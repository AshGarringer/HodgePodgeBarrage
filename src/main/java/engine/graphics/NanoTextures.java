package engine.graphics;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.nanovg.NanoVG;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryStack.*;

public class NanoTextures {
    
    private static long vg; // NanoVG context
    private static Map<String, Integer> imageCache = new HashMap<>();
    private static NVGColor tempColor = NVGColor.create();
    private static NVGPaint tempPaint = NVGPaint.create();
    
    // Initialize NanoVG (call once after OpenGL context is created)
    public static void init(long vgContext) {
        vg = vgContext;
    }
    
    // Load image and return NanoVG image handle
    public static int loadImage(String path) {
        // Check cache first
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            
            // Clean up path (same logic as your old Textures class)
            if (path.startsWith("/")) path = path.substring(1);
            if (path.startsWith("resources/")) path = path.substring(10);
            if (path.startsWith("textures/")) path = path.substring(9);
            
            String fullPath = "resources/textures/" + path;
            
            System.out.println(fullPath);
            
            ByteBuffer imageData = STBImage.stbi_load(fullPath, width, height, channels, 4);
            if (imageData == null) {
                System.err.println("Failed to load image: " + fullPath + " - " + STBImage.stbi_failure_reason());
                return -1;
            }
            
            // Create NanoVG image
            int imageHandle = nvgCreateImageRGBA(vg, width.get(0), height.get(0), 0, imageData);
            
            // Cache and cleanup
            imageCache.put(path, imageHandle);
            STBImage.stbi_image_free(imageData);
            
            return imageHandle;
        }
    }
    
    // Load animation (returns array of image handles)
    public static int[] loadAnimation(String path, int length) {
        int[] images = new int[length];
        for (int i = 0; i < length; i++) {
            String framePath = path + "/" + i + ".png";
            images[i] = loadImage(framePath);
        }
        return images;
    }
    
    public static int[] loadAnimation(String path, int length, String extension) {
        int[] images = new int[length];
        for (int i = 0; i < length; i++) {
            String framePath = path + "/" + i + extension;
            images[i] = loadImage(framePath);
        }
        return images;
    }
    
    // Drawing methods (Graphics2D-like API)
    
    public static void drawImage(int imageHandle, float x, float y) {
        if (imageHandle == -1) return;
        
        // Get image size
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            nvgImageSize(vg, imageHandle, w, h);
            
            drawImage(imageHandle, x, y, w.get(0), h.get(0));
        }
    }
    
    public static void drawImage(int imageHandle, float x, float y, float width, float height) {
        if (imageHandle == -1) return;
        
        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);
        nvgFillPaint(vg, nvgImagePattern(vg, x, y, width, height, 0, imageHandle, 1.0f, tempPaint));
        nvgFill(vg);
    }
    
    // Draw with color tint
    public static void drawImage(int imageHandle, float x, float y, Color tint) {
        if (imageHandle == -1) return;
        
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            nvgImageSize(vg, imageHandle, w, h);
            
            float alpha = tint.getAlpha() / 255.0f;
            
            nvgBeginPath(vg);
            nvgRect(vg, x, y, w.get(0), h.get(0));
            nvgFillPaint(vg, nvgImagePattern(vg, x, y, w.get(0), h.get(0), 0, imageHandle, alpha, tempPaint));
            nvgFill(vg);
            
            // Apply color tint as overlay if needed
            nvgBeginPath(vg);
            nvgRect(vg, x, y, w.get(0), h.get(0));
            nvgFillColor(vg, nvgRGBA((byte)tint.getRed(), (byte)tint.getGreen(), (byte)tint.getBlue(), 
                                    (byte)(alpha * 128), tempColor)); // 50% blend
            nvgFill(vg);
        }
    }
    
    // Fill rectangle (replaces Graphics2D fillRect)
    public static void fillRect(float x, float y, float width, float height, Color color) {
        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);
        nvgFillColor(vg, nvgRGBA((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue(), 
                                (byte)color.getAlpha(), tempColor));
        nvgFill(vg);
    }
    
    public static int getWidth(int imageHandle){
        
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            nvgImageSize(vg, imageHandle, w, h);
            
            return w.get(0);
        }
    }
    
    public static int getHeight(int imageHandle){
        
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            nvgImageSize(vg, imageHandle, w, h);
            
            return h.get(0);
        }
    }
    
    // Transform methods (similar to Graphics2D)
    public static void save() {
        nvgSave(vg);
    }
    
    public static void resetTransform(){
        nvgResetTransform(vg);
    }
    
    public static void restore() {
        nvgRestore(vg);
    }
    
    public static void translate(float x, float y) {
        nvgTranslate(vg, x, y);
    }
    
    public static void scale(float x, float y) {
        nvgScale(vg, x, y);
    }
    
    public static void rotate(double angle) {
        nvgRotate(vg, (float)angle);
    }
    
    // Frame management
    public static void beginFrame(int width, int height) {
        nvgBeginFrame(vg, width, height, 1.0f);
    }
    
    public static void endFrame() {
        nvgEndFrame(vg);
    }
    
    // Cleanup
    public static void cleanup() {
        for (int imageHandle : imageCache.values()) {
            nvgDeleteImage(vg, imageHandle);
        }
        imageCache.clear();
        
        tempColor.free();
        tempPaint.free();
    }
}
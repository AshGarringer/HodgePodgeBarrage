package engine.framework;

import org.lwjgl.glfw.*;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class NewWindow {
    
    private long window;
    private String title;
    private int width, height;
    private boolean fullscreen;
    private boolean wasResized = false;
    public boolean initialized = false;
    
    // NanoVG context
    private long vg;
    
    public NewWindow(String title, int width, int height, int maxSize, boolean fullscreen) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.fullscreen = fullscreen;
    }
    
    public void init() {
        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();
        
        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        
        // Create window
        long monitor = fullscreen ? glfwGetPrimaryMonitor() : NULL;
        window = glfwCreateWindow(width, height, title, monitor, NULL);
        
        if (window == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }
        
        // Setup callbacks
        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            this.width = w;
            this.height = h;
            this.wasResized = true;
            glViewport(0, 0, w, h);
        });
        
        // Center window if not fullscreen
        if (!fullscreen) {
            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);
                
                glfwGetWindowSize(window, pWidth, pHeight);
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
                
                glfwSetWindowPos(window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
                );
            }
        }
        
        // Make context current
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window);
        
        // Initialize OpenGL
        GL.createCapabilities();
        
        // Initialize NanoVG using NanoVGGL3 (correct way)
        vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);
        if (vg == NULL) {
            throw new RuntimeException("Could not init NanoVG");
        }
        
        // Setup OpenGL state
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        initialized = true;
    }
    
    public void update() {
        glfwPollEvents();
        glfwSwapBuffers(window);
        glClear(GL_COLOR_BUFFER_BIT);
    }
    
    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }
    
    public boolean wasResized() {
        boolean result = wasResized;
        wasResized = false;
        return result;
    }
    
    public void cleanup() {
        if (vg != NULL) {
            NanoVGGL3.nvgDelete(vg);  // Use NanoVGGL3.nvgDelete
        }
        
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    
    // Getters
    public long getHandle() { return window; }
    public long getNanoVGContext() { return vg; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getTitle() { return title; }
}
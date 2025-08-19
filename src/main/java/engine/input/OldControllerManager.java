/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package engine.input;

import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cookiebot
 */
public class OldControllerManager {
//    
//    // Button constants - maintain compatibility with ControllerHandler
//    public static int A = 0;
//    public static int B = 1;
//    public static int X = 2;
//    public static int Y = 3;
//    public static int UP = 4;
//    public static int DOWN = 5;
//    public static int LEFT = 6;
//    public static int RIGHT = 7;
//    public static int LTRIGGER = 8;
//    public static int RTRIGGER = 9;
//    public static int SELECT = 10;
//    public static int START = 11;
//    
//    public ArrayList<LwjController> list = new ArrayList<>();
//    private boolean initialized = false;
//    
//    // Store stable IDs to maintain controller assignment across reconnections
//    private long lastDiscoveryTime = 0;
//    private static final long DISCOVERY_INTERVAL = 1000; // Check for new controllers every 1 second
//    
//    public OldControllerManager(){
//        
//        // Discover available controllers
//        refreshControllers();
//    }
//    
//    /**
//     * Refresh the list of available controllers with persistence support
//     */
//    public void refreshControllers() {
//        
//        list = new ArrayList<>();
//        // Scan for all available gamepads
//        for(int jid = GLFW.GLFW_JOYSTICK_1; jid <= GLFW.GLFW_JOYSTICK_LAST; jid++){
//            if(GLFW.glfwJoystickPresent(jid) && GLFW.glfwJoystickIsGamepad(jid)){
//                list.add(new LwjController(jid));
//            }
//        }
//        
//        System.out.println("Total active controllers: " + list.size());
//    }
//    
//    public ArrayList<LwjController> getControllers() {
//        
//        list = new ArrayList<>();
//        ArrayList<LwjController> controllers = new ArrayList<>();
//        // Scan for all available gamepads
//        for(int jid = GLFW.GLFW_JOYSTICK_1; jid <= GLFW.GLFW_JOYSTICK_LAST; jid++){
//            if(GLFW.glfwJoystickPresent(jid) && GLFW.glfwJoystickIsGamepad(jid)){
//                controllers.add(new LwjController(jid));
//                System.out.println(jid);
//            }
//        }
//        System.out.println("Total active controllers: " + controllers.size());
////        while(list.size() < controllers.size()){
////            
////        }
//        return new ArrayList<>(list); // Return copy to prevent external modification
//    }
//    
//    
//    /**
//     * Update all controllers - call this every frame
//     */
//    public void updateControllers() {
//        if(!initialized) return;
//        
//        // CRITICAL: Poll GLFW events to process controller connect/disconnect events
//        // This is necessary because we're using AWT for windowing, not GLFW windowing
//        GLFW.glfwPollEvents();
//        
//        // Update all controllers
//        for(LwjController controller : list) {
//            controller.update();
//        }
//        
//        // Remove inactive controllers (disconnected ones)
//        list.removeIf(controller -> {
//            if(!controller.isActive()) {
//                System.out.println("Removing inactive controller: " + controller.getName());
//                controller.dispose();
//                return true;
//            }
//            return false;
//        });
//        
//        // Periodically check for new controllers or reconnections
//        long currentTime = System.currentTimeMillis();
//        if(currentTime - lastDiscoveryTime > DISCOVERY_INTERVAL) {
//            refreshControllers();
//            lastDiscoveryTime = currentTime;
//        }
//    }
//    
//    /**
//     * Get controller by index
//     */
//    public LwjController getController(int index) {
//        if(index >= 0 && index < list.size()) {
//            return list.get(index);
//        }
//        return null;
//    }
//    
//    /**
//     * Get number of active controllers
//     */
//    public int getControllerCount() {
//        return list.size();
//    }
//    
//    /**
//     * Get all controllers
//     */
//    /**
//     * Check if any controller has pressed the specified button
//     */
//    public boolean anyControllerPressed(int button) {
//        for(LwjController controller : list) {
//            if(controller.pressed(button)) {
//                return true;
//            }
//        }
//        return false;
//    }
//    
//    /**
//     * Cleanup resources
//     */
//    public void dispose() {
//        for(LwjController controller : list) {
//            controller.dispose();
//        }
//        list.clear();
//        
//        if(initialized) {
//            LwjController.terminateGLFW();
//            initialized = false;
//        }
//    }
}

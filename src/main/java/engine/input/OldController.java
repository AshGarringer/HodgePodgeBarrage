/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package engine.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author cookiebot
 */
public class OldController {
//    
//    // SNES Button constants - maintain same order as SnesController
//    public static int X = 0;
//    public static int A = 1;
//    public static int B = 2;
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
//    public boolean active = true;
//    
//    private final Boolean[] pressed = new Boolean[12];
//    private final Boolean[] released = new Boolean[12];
//    private final Boolean[] held = new Boolean[12];
//    private final Boolean[] previousHeld = new Boolean[12];
//    
//    private int joystickId;
//    private final GLFWGamepadState gamepadState;
//    
//    // Deadzone for analog sticks when used as digital input
//    private static final float ANALOG_DEADZONE = 0.5f;
//
//    public OldController(int joystickId) {
//        this.joystickId = joystickId;
//        this.gamepadState = GLFWGamepadState.malloc();
//        
//        // Initialize all button states
//        for(int i = 0; i < 12; i ++){
//            pressed[i] = false;
//            released[i] = false;
//            held[i] = false;
//            previousHeld[i] = false;
//        }
//    }
//    
//    public LwjController(){
//        active = false;
//        this.gamepadState = GLFWGamepadState.malloc();
//        
//        for(int i = 0; i < 12; i ++){
//            pressed[i] = false;
//            released[i] = false;
//            held[i] = false;
//            previousHeld[i] = false;
//        }
//    }
//    
//    public void update(){
//        
//        // Check if joystick is still present and is a gamepad
//        if(!GLFW.glfwJoystickPresent(joystickId) || !GLFW.glfwJoystickIsGamepad(joystickId)) {
//            active = false;
//            return;
//        }
//        else{
//            active = true;
//        }
//        
//        // Store previous frame's held states
//        System.arraycopy(held, 0, previousHeld, 0, 12);
//        
//        // Get current gamepad state
//        if(!GLFW.glfwGetGamepadState(joystickId, gamepadState)) {
//            active = false;
//            return;
//        }
//        
//        // Map GLFW gamepad buttons to SNES controller layout
//        // Note: GLFW uses Xbox controller mapping, so we need to remap for SNES feel
//        
//        // Face buttons (swap A/B and X/Y to match SNES layout)
//        set(A, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_X) == GLFW.GLFW_PRESS);      // GLFW X -> SNES A
//        set(B, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_A) == GLFW.GLFW_PRESS);      // GLFW A -> SNES B
//        set(X, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_B) == GLFW.GLFW_PRESS);      // GLFW B -> SNES X
//        set(Y, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_Y) == GLFW.GLFW_PRESS);      // GLFW Y -> SNES Y
//        
//        // D-pad buttons
//        set(UP, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP) == GLFW.GLFW_PRESS);
//        set(DOWN, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN) == GLFW.GLFW_PRESS);
//        set(LEFT, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT) == GLFW.GLFW_PRESS);
//        set(RIGHT, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT) == GLFW.GLFW_PRESS);
//        
//        // Shoulder buttons - Your "Retro Controller" has unusual mapping:
//        // Left physical trigger -> RIGHT_BUMPER button
//        // Right physical trigger -> RIGHT_TRIGGER axis (1.0 when pressed)
//        boolean leftShoulder = gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == GLFW.GLFW_PRESS;
//        boolean rightShoulder = gamepadState.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) == 1.0f;
//        
//        set(LTRIGGER, leftShoulder);
//        set(RTRIGGER, rightShoulder);
//        
//        // Start/Select buttons
//        set(SELECT, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_BACK) == GLFW.GLFW_PRESS);
//        set(START, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_START) == GLFW.GLFW_PRESS);
//        
//        // Also check analog stick as fallback for D-pad if D-pad buttons aren't working
//        if(!held[UP] && !held[DOWN] && !held[LEFT] && !held[RIGHT]) {
//            float leftY = gamepadState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);
//            float leftX = gamepadState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X);
//            
//            // Convert analog stick to digital D-pad input
//            if(leftY < -ANALOG_DEADZONE) {
//                set(UP, true);
//            }
//            if(leftY > ANALOG_DEADZONE) {
//                set(DOWN, true);
//            }
//            if(leftX < -ANALOG_DEADZONE) {
//                set(LEFT, true);
//            }
//            if(leftX > ANALOG_DEADZONE) {
//                set(RIGHT, true);
//            }
//        }
//    }
//    
//    public void set(int i, boolean b){
//        if(!Objects.equals(held[i], b)){
//            if(b) pressed[i] = true;
//            else released[i] = true;
//            held[i] = b;
//        }
//    }
//    
//    public boolean pressed(int button_code){
//        return pressed(button_code, false);
//    }
//    
//    public boolean pressed(int button_code, boolean clear){
//        if(!clear) return pressed[button_code];
//        boolean holder = pressed[button_code];
//        pressed[button_code] = false;
//        return holder;
//    }
//    
//    public boolean released(int button_code){
//        return released(button_code, false);
//    }
//    
//    public boolean released(int button_code, boolean clear){
//        if(!clear) return released[button_code];
//        boolean holder = released[button_code];
//        released[button_code] = false;
//        return holder;
//    }
//    
//    public boolean held(int button_code){
//        return held[button_code];
//    }
//    
//    public void clearPressed(){
//        for(int i = 0; i < 12; i++){
//            pressed[i] = false;
//        }
//    }
//    
//    public void clearReleased(){
//        for(int i = 0; i < 12; i++){
//            released[i] = false;
//        }
//    }
//    
//    public void clearHeld(){
//        for(int i = 0; i < 12; i++){
//            held[i] = false;
//        }
//    }
//    
//    public void clearChanges(){
//        clearPressed();
//        clearReleased();
//        clearHeld();
//    }
//    
//    public static ArrayList<LwjController> getControllers(){
//        
//        ArrayList<LwjController> list = new ArrayList<>();
//        
//        // Check all possible joystick IDs (GLFW supports up to 16)
//        for(int jid = GLFW.GLFW_JOYSTICK_1; jid <= GLFW.GLFW_JOYSTICK_LAST; jid++){
//            if(GLFW.glfwJoystickPresent(jid) && GLFW.glfwJoystickIsGamepad(jid)){
//                list.add(new LwjController(jid));
//            }
//        }
//        
//        return list;
//    }
//    
//    public String getName(){
//        if(!active) return "Inactive Controller";
//        return GLFW.glfwGetGamepadName(joystickId);
//    }
//    
//    public int getJoystickId(){
//        return joystickId;
//    }
//    
//    public boolean isActive(){
//        return active;
//    }
//    
//    public boolean equalsController(LwjController other){
//        return other != null && this.joystickId == other.joystickId;
//    }
//    
//    // Get a more stable identifier for this controller
//    // Combines name + GUID for better identification across reconnections
//    public String getStableId(){
//        if(!active) return "inactive";
//        String name = GLFW.glfwGetGamepadName(joystickId);
//        String guid = GLFW.glfwGetJoystickGUID(joystickId);
//        return (name != null ? name : "Unknown") + "_" + (guid != null ? guid : "NoGUID");
//    }
//    
//    // Check if this controller matches a stable ID (for reconnection detection)
//    public boolean matchesStableId(String stableId){
//        return getStableId().equals(stableId);
//    }
//    
//    // Update the joystick ID (useful when a controller reconnects with a different ID)
//    public void updateJoystickId(int newJoystickId){
//        this.joystickId = newJoystickId;
//        this.active = GLFW.glfwJoystickPresent(newJoystickId) && GLFW.glfwJoystickIsGamepad(newJoystickId);
//    }
//    
//    // Clean up resources
//    public void dispose(){
//        if(gamepadState != null){
//            gamepadState.free();
//        }
//    }
//    
//    // Static method to initialize GLFW - should be called once at startup
//    public static boolean initializeGLFW(){
//        return GLFW.glfwInit();
//    }
//    
//        // Static method to terminate GLFW - should be called at shutdown
//    public static void terminateGLFW(){
//        GLFW.glfwTerminate();
//    }
}

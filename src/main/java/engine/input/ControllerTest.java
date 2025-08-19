/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package engine.input;

import engine.framework.BasicEngine;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

/**
 *
 * @author cookiebot
 */
public class ControllerTest extends BasicEngine{

    ArrayList<FriendlyController> controllers = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();
    
    public ControllerTest(){
        super();
        GLFW.glfwInit();
        this.start();
    }
    
    @Override
    public void tick() {
    
        GLFW.glfwPollEvents();
        
        for(int jid = GLFW.GLFW_JOYSTICK_1; jid <= GLFW.GLFW_JOYSTICK_LAST; jid++){
            if(GLFW.glfwJoystickPresent(jid) && GLFW.glfwJoystickIsGamepad(jid)){
                if(!ids.contains((Integer)jid)){
                    ids.add(jid);
                    controllers.add(new FriendlyController(jid));
                }
            }
        }
        for (FriendlyController controller : controllers) {
            controller.update();
        }
    }
    
    
    public static void main(String[] args) {
        ControllerTest game = new ControllerTest();
    }
    
}
class FriendlyController{
    
    public static int X = 0;
    public static int A = 1;
    public static int B = 2;
    public static int Y = 3;
    public static int UP = 4;
    public static int DOWN = 5;
    public static int LEFT = 6;
    public static int RIGHT = 7;
    public static int LTRIGGER = 8;
    public static int RTRIGGER = 9;
    public static int SELECT = 10;
    public static int START = 11;
    
    private static final float ANALOG_DEADZONE = 0.5f;
    
    public boolean active = true;
    
    private final Boolean[] pressed = new Boolean[12];
    private final Boolean[] released = new Boolean[12];
    private final Boolean[] held = new Boolean[12];
    private final Boolean[] previousHeld = new Boolean[12];
    
    public static ArrayList<String> names = new ArrayList<>();
    
    String name;
    Integer controllerID;
    private final GLFWGamepadState gamepadState;
    
    public FriendlyController(int id){
        if(names.isEmpty()){
            names.add("Jeremy");
            names.add("Tim");
            names.add("Jim");
            names.add("Christopher");
            names.add("Tomathy");
            names.add("Wesley");
            names.add("Lucille");
            names.add("Fanny");
            names.add("Helga");
            names.add("Jamey");
            names.add("Kimberleighghe");
            names.add("Sally");
        }
        Random r = new Random();
        int num = r.nextInt(names.size());
        name = names.get(num);
        names.remove(num);
        controllerID = id;
        
        this.gamepadState = GLFWGamepadState.malloc();
        
        for(int i = 0; i < 12; i ++){
            pressed[i] = false;
            released[i] = false;
            held[i] = false;
            previousHeld[i] = false;
        }
    }
    
    public void update(){
        
        // Check if joystick is still present and is a gamepad
        if(!GLFW.glfwJoystickPresent(controllerID) || !GLFW.glfwJoystickIsGamepad(controllerID)) {
            active = false;
            
            System.out.println(name+" disconnected.");
            return;
        }
        else{
            active = true;
        }
        
        // Store previous frame's held states
        System.arraycopy(held, 0, previousHeld, 0, 12);
        
        // Get current gamepad state
        if(!GLFW.glfwGetGamepadState(controllerID, gamepadState)) {
            active = false;
            return;
        }
        
        // Map GLFW gamepad buttons to SNES controller layout
        // Note: GLFW uses Xbox controller mapping, so we need to remap for SNES feel
        
        // Face buttons (swap A/B and X/Y to match SNES layout)
        set(A, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_X) == GLFW.GLFW_PRESS);      // GLFW X -> SNES A
        set(B, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_A) == GLFW.GLFW_PRESS);      // GLFW A -> SNES B
        set(X, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_B) == GLFW.GLFW_PRESS);      // GLFW B -> SNES X
        set(Y, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_Y) == GLFW.GLFW_PRESS);      // GLFW Y -> SNES Y
        
        // D-pad buttons
        set(UP, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP) == GLFW.GLFW_PRESS);
        set(DOWN, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN) == GLFW.GLFW_PRESS);
        set(LEFT, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT) == GLFW.GLFW_PRESS);
        set(RIGHT, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT) == GLFW.GLFW_PRESS);
        
        // Shoulder buttons - Your "Retro Controller" has unusual mapping:
        // Left physical trigger -> RIGHT_BUMPER button
        // Right physical trigger -> RIGHT_TRIGGER axis (1.0 when pressed)
        boolean leftShoulder = gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == GLFW.GLFW_PRESS;
        boolean rightShoulder = gamepadState.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) == 1.0f;
        
        set(LTRIGGER, leftShoulder);
        set(RTRIGGER, rightShoulder);
        
        // Start/Select buttons
        set(SELECT, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_BACK) == GLFW.GLFW_PRESS);
        set(START, gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_START) == GLFW.GLFW_PRESS);
        
        // Also check analog stick as fallback for D-pad if D-pad buttons aren't working
        if(!held[UP] && !held[DOWN] && !held[LEFT] && !held[RIGHT]) {
            float leftY = gamepadState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);
            float leftX = gamepadState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X);
            
            // Convert analog stick to digital D-pad input
            if(leftY < -ANALOG_DEADZONE) {
                set(UP, true);
            }
            if(leftY > ANALOG_DEADZONE) {
                set(DOWN, true);
            }
            if(leftX < -ANALOG_DEADZONE) {
                set(LEFT, true);
            }
            if(leftX > ANALOG_DEADZONE) {
                set(RIGHT, true);
            }
        }
        System.out.println("Player " + controllerID + " | " + name+" pressed: ");
        if(held[0])System.out.print("X");
        else System.out.print("_");
        if(held[1])System.out.print("A");
        else System.out.print("_");
        if(held[2])System.out.print("B");
        else System.out.print("_");
        if(held[3])System.out.print("Y");
        else System.out.print("_");
        System.out.println("");
    }
    
    public void set(int i, boolean b){
        if(!Objects.equals(held[i], b)){
            if(b) pressed[i] = true;
            else released[i] = true;
            held[i] = b;
        }
    }
    
    
}
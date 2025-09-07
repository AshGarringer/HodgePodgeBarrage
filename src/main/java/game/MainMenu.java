package game;

import engine.graphics.NanoTextures;
import engine.graphics.SimpleAnimation;
import engine.graphics.NanoTextures;
import engine.input.SnesController;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 *
 * @author cookiebot
 */
public class MainMenu {
    
    public static Integer background;
    public static Integer[] words;
    public static Integer logo;
    public static Integer start;
    
    static SimpleAnimation animation;
    private static SimpleAnimation logoX1;
    private static SimpleAnimation logoX2;
    private static SimpleAnimation logoY1;
    private static SimpleAnimation logoScale;
    private static SimpleAnimation startBob;
    private static SimpleAnimation backgroundScale;
    
    private static Integer composite;
    
    public static void load(){
        background = NanoTextures.loadImage("/textures/mainMenu/background.png");
        words = new Integer[4];
        words[0] = NanoTextures.loadImage("/textures/mainMenu/hodge.png");
        words[1] = NanoTextures.loadImage("/textures/mainMenu/podge.png");
        words[2] = NanoTextures.loadImage("/textures/mainMenu/robot.png");
        words[3] = NanoTextures.loadImage("/textures/mainMenu/barrage.png");
        start = NanoTextures.loadImage("/textures/mainMenu/start.png");
        logo = NanoTextures.loadImage("/textures/mainMenu/logo.png");
        
        animation = new SimpleAnimation(3500,false);
        animation.addHold(45);
        animation.addStateChange();
        animation.addStateChange();
        for(int i = 0; i < 4; i ++){
            animation.addMotion(3500, 0, 26, SimpleAnimation.SLOW_TOWARDS,20);
            animation.addStateChange();
        }
        animation.addMotion(255, 0, 80, SimpleAnimation.MOVE_EVEN);
        
        logoX1 = new SimpleAnimation(true);
        logoX1.addMotion(-5, 5, 103, SimpleAnimation.SMOOTH);
        logoX1.addMotion(5, -5, 103, SimpleAnimation.SMOOTH);
        logoX2 = new SimpleAnimation(true);
        logoX2.addMotion(5, -5, 211, SimpleAnimation.SMOOTH);
        logoX2.addMotion(-5, 5, 211, SimpleAnimation.SMOOTH);
        logoY1 = new SimpleAnimation(true);
        logoY1.addMotion(15, -15, 244, SimpleAnimation.SMOOTH);
        logoY1.addMotion(-15, 15, 244, SimpleAnimation.SMOOTH);
        logoScale = new SimpleAnimation(true);
        logoScale.addMotion(1, 1.01f, 300, SimpleAnimation.SMOOTH);
        logoScale.addMotion(1.01f, 1, 300, SimpleAnimation.SMOOTH);
        startBob = new SimpleAnimation(true);
        startBob.addMotion(8, -8, 244, SimpleAnimation.SMOOTH);
        startBob.addMotion(-8, 8, 244, SimpleAnimation.SMOOTH);
        backgroundScale = new SimpleAnimation(true);
        backgroundScale.addMotion(1, 1.04f, 488, SimpleAnimation.SMOOTH);
        backgroundScale.addMotion(1.04f, 1, 488, SimpleAnimation.SMOOTH);
    }
    
    public static void render(int width, int height){
        
        if(!animation.isFinished()){
            NanoTextures.fillRect(0, 0, width, height,Color.BLACK);
        }
        
        float scale1 = height / 1400f;
        float offset1 = (width / scale1 - 3500) / 2;
        
        NanoTextures.scale(scale1, scale1);
        NanoTextures.translate(offset1, 0);

        int logoX = 797;
        int logoY = 325;
        animation.tick();
        
        if(animation.getState() < 5){
            NanoTextures.drawImage(composite, 797, 325);
        }
        
        switch(animation.getState()){
            case 0: 
                return;
            case 1:
                NanoTextures.drawImage(MainMenu.words[0], logoX - animation.value(), logoY, null);
                return;
            case 2:
                NanoTextures.drawImage(MainMenu.words[0], logoX, logoY, null);
                NanoTextures.drawImage(MainMenu.words[1], logoX, logoY - animation.value(), null);
                return;
            case 3:
                NanoTextures.drawImage(MainMenu.words[0], logoX, logoY, null);
                NanoTextures.drawImage(MainMenu.words[1], logoX, logoY, null);
                NanoTextures.drawImage(MainMenu.words[2], logoX, logoY + animation.value(), null);
                return;
            case 4:
                NanoTextures.drawImage(MainMenu.words[0], logoX, logoY, null);
                NanoTextures.drawImage(MainMenu.words[1], logoX, logoY, null);
                NanoTextures.drawImage(MainMenu.words[2], logoX, logoY, null);
                NanoTextures.drawImage(MainMenu.words[3], logoX + animation.value(), logoY, null);
                return;
        }
        
        logoX1.tick();
        logoY1.tick();
        logoX2.tick();
        logoScale.tick();
        startBob.tick();
        backgroundScale.tick();

        NanoTextures.translate(1750, 700);
        NanoTextures.scale(backgroundScale.valueFloat(), backgroundScale.valueFloat());
        NanoTextures.drawImage(MainMenu.background,-1750, -700, null);
        NanoTextures.scale(1/backgroundScale.valueFloat(), 1/backgroundScale.valueFloat());
        NanoTextures.translate(-1750, -700);
        
        float logoFinalX = 1749 + logoX1.valueFloat() + logoX2.valueFloat();
        float logoFinalY = 584 + logoY1.valueFloat();
        NanoTextures.translate(logoFinalX, logoFinalY);
        NanoTextures.scale(logoScale.valueFloat(), logoScale.valueFloat());
        
        NanoTextures.drawImage(MainMenu.logo, -1905/2, -509/2, null);
        
        NanoTextures.scale(1/logoScale.valueFloat(), 1/logoScale.valueFloat());
        NanoTextures.translate(-logoFinalX, -logoFinalY);

        NanoTextures.translate(1530, 1000 + startBob.value());
        NanoTextures.drawImage(MainMenu.start, 0, 0, null);
        NanoTextures.translate(-1530, -1000 - startBob.value());

        if(!animation.isFinished()){
            NanoTextures.fillRect(0, 0, 3500, 1400, new Color(255, 255, 255, animation.value()));
        }
        
        ArrayList<Integer> controllerIds = SnesController.updateControllers();
        NanoTextures.resetTransform();
    }
}
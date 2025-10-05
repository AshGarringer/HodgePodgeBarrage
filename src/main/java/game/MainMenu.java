package game;

import engine.graphics.SimpleAnimation;
import engine.graphics.Textures;
import engine.input.SnesController;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author cookiebot
 */
public class MainMenu {
    
    public static BufferedImage background;
    public static BufferedImage[] words;
    public static BufferedImage logo;
    public static BufferedImage start;
    
    static SimpleAnimation animation;
    private static SimpleAnimation logoX1;
    private static SimpleAnimation logoX2;
    private static SimpleAnimation logoY1;
    private static SimpleAnimation logoScale;
    private static SimpleAnimation startBob;
    private static boolean notStarted = true;
    private static SimpleAnimation backgroundScale;
    private static SimpleAnimation startVibrate;
    private static SimpleAnimation startDrop;
    
    private static BufferedImage composite;
    
    public static void load(){
        background = Textures.loadImage("/textures/mainMenu/background.png");
        words = new BufferedImage[4];
        words[0] = Textures.loadImage("/textures/mainMenu/hodge.png");
        words[1] = Textures.loadImage("/textures/mainMenu/podge.png");
        words[2] = Textures.loadImage("/textures/mainMenu/robot.png");
        words[3] = Textures.loadImage("/textures/mainMenu/barrage.png");
        start = Textures.loadImage("/textures/mainMenu/start.png");
        logo = Textures.loadImage("/textures/mainMenu/logo.png");
        
        animation = new SimpleAnimation(3500,false);
        animation.addHold(45);
        animation.addStateChange();
        animation.addStateChange();
        for(int i = 0; i < 4; i ++){
            animation.addMotion(3500, 0, 26, SimpleAnimation.SLOW_TOWARDS,20);
            animation.addStateChange();
        }
        animation.addMotion(255, 0, 80, SimpleAnimation.MOVE_EVEN);
        
        startVibrate  = new SimpleAnimation(false);
        startVibrate.AddVibrate(15, 3f, 25);
        startDrop  = new SimpleAnimation(0,false);
        startDrop.addHold(40);
        startDrop.addMotion(0, -50, 15, SimpleAnimation.SMOOTH);
        startDrop.addMotion(-50, 500, 25, SimpleAnimation.SPEED_TOWARDS,2/3f);
        
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
    
    public static void render(Graphics2D g, Game game, int width, int height, float transit){
        game.setHints(g);
        
        AffineTransform originalTransform = g.getTransform();
        
        if(composite == null){
            composite = new BufferedImage(1905,509,BufferedImage.TYPE_INT_ARGB);
        }
        
        if(!animation.isFinished()){
            Textures.fillRect(0, 0, width, height, Color.black, g);
        }
        
        float scale1 = height / 1400f;
        float offset1 = (width / scale1 - 3500) / 2;
        
        AffineTransform mainTransform = new AffineTransform();
        mainTransform.scale(scale1, scale1);
        mainTransform.translate(offset1, 0);
        g.setTransform(mainTransform);
        
        g.setClip(new Rectangle(-(int)offset1, 0, 3500, 1400));

        if(transit > 0){
            startVibrate.tick();
            startDrop.tick();
            
            Textures.fillRect(0, 0, 3500, 1400, Color.black, g);
            g.drawImage(MainMenu.start, 1530+startVibrate.value(), 1000+startDrop.value(), null);
            return;
        }
        
        int logoX = 797;
        int logoY = 325;
        animation.tick();
        
        if(animation.getState() < 5){
            g.drawImage(composite, 797, 325, game);
        }
        
        switch(animation.getState()){
            case 0: 
                g.setTransform(originalTransform);
                return;
            case 1:
                g.drawImage(MainMenu.words[0], logoX - animation.value(), logoY, null);
                g.setTransform(originalTransform);
                return;
            case 2:
                if(animation.stateChanged()){
                    Graphics cg = composite.getGraphics();
                    cg.drawImage(MainMenu.words[0], 0, 0, null);
                    g.drawImage(MainMenu.words[0], logoX, logoY, null);
                }
                g.drawImage(MainMenu.words[1], logoX, logoY - animation.value(), null);
                g.setTransform(originalTransform);
                return;
            case 3:
                if(animation.stateChanged()){
                    Graphics cg = composite.getGraphics();
                    cg.drawImage(MainMenu.words[1], 0, 0, null);
                    g.drawImage(MainMenu.words[1], logoX, logoY, null);
                }
                g.drawImage(MainMenu.words[2], logoX, logoY + animation.value(), null);
                g.setTransform(originalTransform);
                return;
            case 4:
                if(animation.stateChanged()){
                    Graphics cg = composite.getGraphics();
                    cg.drawImage(MainMenu.words[2], 0, 0, null);
                    g.drawImage(MainMenu.words[2], logoX, logoY, null);
                }
                g.drawImage(MainMenu.words[3], logoX + animation.value(), logoY, null);
                g.setTransform(originalTransform);
                return;
        }
        
        logoX1.tick();
        logoY1.tick();
        logoX2.tick();
        logoScale.tick();
        startBob.tick();
        backgroundScale.tick();

        AffineTransform backgroundTransform = new AffineTransform(mainTransform);
        backgroundTransform.translate(1750, 700);
        backgroundTransform.scale(backgroundScale.valueFloat(), backgroundScale.valueFloat());
        backgroundTransform.translate(-1750, -700);
        g.setTransform(backgroundTransform);
        g.drawImage(MainMenu.background, 0, 0, null);

        AffineTransform logoTransform = new AffineTransform(mainTransform);
        float logoFinalX = 1749 + logoX1.valueFloat() + logoX2.valueFloat();
        float logoFinalY = 584 + logoY1.valueFloat();
        logoTransform.translate(logoFinalX, logoFinalY);
        logoTransform.scale(logoScale.valueFloat(), logoScale.valueFloat());
        logoTransform.translate(-MainMenu.logo.getWidth()/2, -MainMenu.logo.getHeight()/2);
        g.setTransform(logoTransform);
        g.drawImage(MainMenu.logo, 0, 0, null);

        AffineTransform startTransform = new AffineTransform(mainTransform);
        startTransform.translate(1530, 1000 + startBob.value());
        g.setTransform(startTransform);
        g.drawImage(MainMenu.start, 0, 0, null);

        if(!animation.isFinished()){
            g.setTransform(mainTransform);
            Textures.fillRect(0, 0, 3500, 1400, new Color(255, 255, 255, animation.value()), g,true);
        }

        g.setTransform(originalTransform);
        
        ArrayList<Integer> controllerIds = SnesController.updateControllers();

//        if(game.state.isTransit()){
//            g.setColor(new Color(0, 0, 0, (int)(255 * game.state.getTransit())));
//            g.fillRect(0, 0, width, height);
//        }
    }
}
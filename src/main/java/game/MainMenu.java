package game;

import engine.graphics.SimpleAnimation;
import engine.graphics.Textures;
import engine.input.SnesController;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static javax.swing.Spring.height;

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
    private static SimpleAnimation backgroundScale;
    
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
        animation.addHold(30);
        animation.addStateChange();
        animation.addStateChange();
        for(int i = 0; i < 4; i ++){
            animation.addMotion(3500, 0, 30, SimpleAnimation.SLOW_TOWARDS,20);
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
//        logoScale.addMotion(1.005f, 1, 30, SimpleAnimation.SMOOTH);
        logoScale.addMotion(1.01f, 1, 30, SimpleAnimation.SPEED_TOWARDS,20);
        startBob = new SimpleAnimation(true);
        startBob.addMotion(8, -8, 244, SimpleAnimation.SMOOTH);
        startBob.addMotion(-8, 8, 244, SimpleAnimation.SMOOTH);
        backgroundScale = new SimpleAnimation(true);
        backgroundScale.addMotion(1, 1.02f, 244, SimpleAnimation.SMOOTH);
        backgroundScale.addMotion(1.02f, 1, 244, SimpleAnimation.SMOOTH);
        
    }
    public static void render(Graphics2D g, Game game, int width, int height){

        // main menu
        game.setHints(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        float scale1 = height / 1400f;
        float offset1 = (width / scale1 - 3500) / 2;

        g.scale(scale1, scale1);
        g.translate(offset1, 0);

        int logoX = 797;
        int logoY = 325;
        animation.tick();

        switch(animation.getState()){
            case 0: return;
            case 1:
                g.drawImage(MainMenu.words[0],logoX - animation.value(),logoY,null);
                return;
            case 2:
                g.drawImage(MainMenu.words[0],logoX,logoY,null);
                g.drawImage(MainMenu.words[1],logoX,logoY - animation.value(),null);
                return;
            case 3:
                g.drawImage(MainMenu.words[0],logoX,logoY,null);
                g.drawImage(MainMenu.words[1],logoX,logoY,null);
                g.drawImage(MainMenu.words[2],logoX,logoY + animation.value(),null);
                return;
            case 4:
                g.drawImage(MainMenu.words[0],logoX,logoY,null);
                g.drawImage(MainMenu.words[1],logoX,logoY,null);
                g.drawImage(MainMenu.words[2],logoX,logoY,null);
                g.drawImage(MainMenu.words[3],logoX + animation.value(),logoY,null);
                return;
        }
        logoX1.tick();
        logoY1.tick();
        logoX2.tick();
        logoScale.tick();
        startBob.tick();
        backgroundScale.tick();

        g.translate(1750,700);
        g.scale(backgroundScale.valueFloat(),backgroundScale.valueFloat());
        g.drawImage(MainMenu.background,-1750,-700,null);
        g.scale(1f/backgroundScale.valueFloat(),1f/backgroundScale.valueFloat());
        g.translate(-1750,-700);
        
        g.translate(1749 + logoX1.valueFloat()+logoX2.valueFloat(), 584 + logoY1.valueFloat());
        g.scale(logoScale.valueFloat(),logoScale.valueFloat());
        g.drawImage(MainMenu.logo,-MainMenu.logo.getWidth()/2,-MainMenu.logo.getHeight()/2,null);
        g.scale(1f/logoScale.valueFloat(),1f/logoScale.valueFloat());
        g.translate(-1749-logoX1.valueFloat()-logoX2.valueFloat(), -584 - logoY1.valueFloat());

        g.translate(0,startBob.value());
        g.drawImage(MainMenu.start,1530,1000,null);
        g.translate(0,-startBob.value());

        if(!animation.isFinished()){
            g.setColor(new Color(255,255,255,animation.value()));
            g.fillRect(0, 0, 3500, 1400);
        }

        ArrayList<Integer> controllerIds = SnesController.updateControllers();

        if(game.state.isTransit()){
            g.setColor(new Color(0,0,0,(int)(255*game.state.getTransit())));

            g.fillRect(0, 0, width, height);
        }
    }
    
}

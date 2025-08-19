package game;

import engine.graphics.Textures;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class MainMenu {
    
    public static BufferedImage logo;
    
    public static void load(){
        logo = Textures.loadImage("/textures/mainMenu/logo.png");
                
    }
    
}

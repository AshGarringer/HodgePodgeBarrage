package game;

import engine.graphics.Textures;
import engine.logic.Calcs;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class ModuleSelect {
    
    public static BufferedImage background;
    public static BufferedImage overlay;
    public static BufferedImage unroll;
    public static BufferedImage[] cursors;
    
    public static BufferedImage[] slotPreviews;
    public static BufferedImage[] eqippedPreviews;
    public static BufferedImage[] playerPreviews;
    
    public static BufferedImage playerShadow;
    
    public static void loadImages(){
        
        background = Textures.loadImage("/textures/moduleSelect/background.png");
        unroll = Textures.loadImage("/textures/moduleSelect/paperUnroll.png");
        overlay = Textures.loadImage("/textures/moduleSelect/overlay.png");
        cursors = new BufferedImage[4];
        for(int i = 0; i < 4; i ++){
            cursors[i] = Textures.loadImage("/textures/moduleSelect/cursors/p" + (i+1) + ".png");
        }
        
        slotPreviews = new BufferedImage[Modules.NUM_MODULES];
        eqippedPreviews = new BufferedImage[Modules.NUM_MODULES];
        for(int i = 0; i < Modules.NUM_MODULES; i ++){
            slotPreviews[i] = Textures.loadImage("/textures/moduleSelect/modulePreviews/"+i+".png");
            eqippedPreviews[i] = Textures.loadImage("/textures/moduleSelect/equippedModules/"+i+".png");
        }
        
        String[] centerNames = new String[]{"smiley","grin","dismay","stress","cyclops","drool"};
        playerPreviews = new BufferedImage[centerNames.length];
        for(int i = 0; i < centerNames.length; i ++){
            playerPreviews[i] = Textures.loadImage("/textures/moduleSelect/players/"+centerNames[i]+".png");
        }
        
        playerShadow = Textures.loadImage("/textures/moduleSelect/playerShadow.png");
        
    }
    
}

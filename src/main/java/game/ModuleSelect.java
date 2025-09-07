package game;

import engine.graphics.NanoTextures;

/**
 *
 * @author cookiebot
 */
public class ModuleSelect {
    
    public static Integer background;
    public static Integer overlay;
    public static Integer unroll;
    public static Integer[] cursors;
    
    public static Integer[] slotPreviews;
    public static Integer[] eqippedPreviews;
    public static Integer[] playerPreviews;
    
    public static Integer playerShadow;
    
    public static void loadImages(){
        
        background = NanoTextures.loadImage("/textures/moduleSelect/background.png");
        unroll = NanoTextures.loadImage("/textures/moduleSelect/paperUnroll.png");
        overlay = NanoTextures.loadImage("/textures/moduleSelect/overlay.png");
        cursors = new Integer[4];
        for(int i = 0; i < 4; i ++){
            cursors[i] = NanoTextures.loadImage("/textures/moduleSelect/cursors/p" + (i+1) + ".png");
        }
        
        slotPreviews = new Integer[Modules.NUM_MODULES];
        eqippedPreviews = new Integer[Modules.NUM_MODULES];
        for(int i = 0; i < Modules.NUM_MODULES; i ++){
            slotPreviews[i] = NanoTextures.loadImage("/textures/moduleSelect/modulePreviews/"+i+".png");
            eqippedPreviews[i] = NanoTextures.loadImage("/textures/moduleSelect/equippedModules/"+i+".png");
        }
        
        String[] centerNames = new String[]{"smiley","grin","dismay","stress","cyclops","drool"};
        playerPreviews = new Integer[centerNames.length];
        for(int i = 0; i < centerNames.length; i ++){
            playerPreviews[i] = NanoTextures.loadImage("/textures/moduleSelect/players/"+centerNames[i]+".png");
        }
        
        playerShadow = NanoTextures.loadImage("/textures/moduleSelect/playerShadow.png");
        
    }
    
}

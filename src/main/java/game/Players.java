/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.graphics.Textures;
import engine.logic.Calcs;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author cookiebot
 */
public class Players {
    
    public static Random random;
    
    public static BufferedImage[][] damaged;
    public static BufferedImage[][] flashes;
    public static BufferedImage[] sparks;
    public static BufferedImage[][] mash;
    public static BufferedImage[][] skillCheck;
    public static BufferedImage[] skillCheckNext;
    public static BufferedImage skillCheckBack;
    public static BufferedImage[] explosion;
    public static BufferedImage charredRemains;
    
    public static BufferedImage[] centers;
    public static BufferedImage[] centerOverlays;
    public static BufferedImage highlight;
    public static Boolean[] centersTilted;
    public static BufferedImage shadow;
    
    public static void loadImages(){
        
        random = new Random();
        
        
        String[] centerNames = new String[]{"smiley","grin","dismay","stress","cyclops","drool"};
        centersTilted = new Boolean[]{
                false, false, true, true, false, true
                };
        centers = new BufferedImage[centerNames.length];
        centerOverlays = new BufferedImage[centerNames.length];
        for(int i = 0; i < centerNames.length; i ++){
            centers[i] = Textures.loadImage("/textures/players/centers/"+centerNames[i]+".png");
            centerOverlays[i] = Textures.loadImage("/textures/players/centers/"+centerNames[i]+"Overlay.png");
        }
        highlight = Textures.loadImage("/textures/players/centers/highlight.png");
        
        damaged = new BufferedImage[5][];
        
        String[] damageLevels = new String[]{"little","some","much","max","goku"};
        for(int i = 0; i < damageLevels.length; i ++){
            damaged[i] = new BufferedImage[24];
            for(int j = 0; j < 24; j ++){
                damaged[i][j] = Textures.loadImage("/textures/players/damage/"+damageLevels[i]+"/"+Calcs.fillInt(j)+".png");
            }
            
        }
        
        flashes = new BufferedImage[5][];
        
        for(int i = 0; i < damageLevels.length; i ++){
            flashes[i] = new BufferedImage[24];
            for(int j = 0; j < 24; j ++){
                flashes[i][j] = Textures.loadImage("/textures/players/damage/"+damageLevels[i]+"/flash"+Calcs.fillInt(j)+".png");
            }
            
        }
        
        sparks = new BufferedImage[50];
        for(int i = 0; i < 50; i ++){
            sparks[i] = Textures.loadImage("/textures/players/sparks/"+Calcs.fillInt(i)+".png");
        }
        
        mash = new BufferedImage[4][];
        
        String[] names = new String[]{"x","a","b","y"};
        
        for(int j = 0; j < 4; j ++){
            mash[j] = new BufferedImage[9];
            for(int i = 0; i < 9; i ++){
                mash[j][i] = Textures.loadImage("/textures/players/button/"+names[j]+"/"+Calcs.fillInt(i)+".png");
            }
        }
        
        skillCheckBack = Textures.loadImage("/textures/players/skillCheck/base.png");
        skillCheck = new BufferedImage[4][];
        skillCheckNext = new BufferedImage[4];
        
        for(int j = 0; j < 4; j ++){
            skillCheck[j] = new BufferedImage[8];
            for(int i = 0; i < 8; i ++){
                skillCheck[j][i] = Textures.loadImage("/textures/players/skillCheck/"+names[j]+"/"+Calcs.fillInt(i)+".png");
            }
        }
        
        for(int j = 0; j < 4; j ++){
            skillCheckNext[j] = Textures.loadImage("/textures/players/skillCheck/"+names[j]+"/next.png");
        }
                
        explosion = new BufferedImage[40];
        for(int i = 0; i < 40; i ++){
            explosion[i] = Textures.loadImage("/textures/players/explosion/"+Calcs.fillInt(i)+".png");
        }
        
        charredRemains = Textures.loadImage("/textures/players/CharredRemains.png");
        shadow = Textures.loadImage("/textures/players/shadow.png");
    }
    
}

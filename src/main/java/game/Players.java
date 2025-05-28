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
    public static BufferedImage[] explosion;
    public static BufferedImage charredRemains;
    
    public static BufferedImage[] centers;
    public static Boolean[] centersTilted;
    
    public static void loadImages(){
        
        random = new Random();
        
        
        String[] centerNames = new String[]{"smiley","drool"};
        centersTilted = new Boolean[]{
                false, true
                };
        centers = new BufferedImage[centerNames.length];
        for(int i = 0; i < centerNames.length; i ++){
            centers[i] = Textures.loadImage("/textures/players/centers/"+centerNames[i]+".png");
        }
        
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
                
        explosion = new BufferedImage[40];
        for(int i = 0; i < 40; i ++){
            explosion[i] = Textures.loadImage("/textures/players/explosion/"+Calcs.fillInt(i)+".png");
        }
        
        charredRemains = Textures.loadImage("/textures/players/CharredRemains.png");
    }
    
}

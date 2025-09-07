/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.graphics.NanoTextures;
import engine.logic.Calcs;
import java.util.Random;

/**
 *
 * @author cookiebot
 */
public class Players {
    
    public static Random random;
    
    public static Integer[][] damaged;
    public static Integer[][] flashes;
    public static Integer[] sparks;
    public static Integer[][] mash;
    public static Integer[][] skillCheck;
    public static Integer[] skillCheckNext;
    public static Integer skillCheckBack;
    public static Integer[] explosion;
    public static Integer charredRemains;
    
    public static Integer[] centers;
    public static Integer[] centerOverlays;
    public static Integer highlight;
    public static Boolean[] centersTilted;
    public static Integer shadow;
    
    public static void loadImages(){
        
        random = new Random();
        
        
        String[] centerNames = new String[]{"smiley","grin","dismay","stress","cyclops","drool"};
        centersTilted = new Boolean[]{
                false, false, true, true, false, true
                };
        centers = new Integer[centerNames.length];
        centerOverlays = new Integer[centerNames.length];
        for(int i = 0; i < centerNames.length; i ++){
            centers[i] = NanoTextures.loadImage("/textures/players/centers/"+centerNames[i]+".png");
            centerOverlays[i] = NanoTextures.loadImage("/textures/players/centers/"+centerNames[i]+"Overlay.png");
        }
        highlight = NanoTextures.loadImage("/textures/players/centers/highlight.png");
        
        damaged = new Integer[5][];
        
        String[] damageLevels = new String[]{"little","some","much","max","goku"};
        for(int i = 0; i < damageLevels.length; i ++){
            damaged[i] = new Integer[24];
            for(int j = 0; j < 24; j ++){
                damaged[i][j] = NanoTextures.loadImage("/textures/players/damage/"+damageLevels[i]+"/"+Calcs.fillInt(j)+".png");
            }
            
        }
        
        flashes = new Integer[5][];
        
        for(int i = 0; i < damageLevels.length; i ++){
            flashes[i] = new Integer[24];
            for(int j = 0; j < 24; j ++){
                flashes[i][j] = NanoTextures.loadImage("/textures/players/damage/"+damageLevels[i]+"/flash"+Calcs.fillInt(j)+".png");
            }
            
        }
        
        sparks = new Integer[50];
        for(int i = 0; i < 50; i ++){
            sparks[i] = NanoTextures.loadImage("/textures/players/sparks/"+Calcs.fillInt(i)+".png");
        }
        
        mash = new Integer[4][];
        
        String[] names = new String[]{"x","a","b","y"};
        
        for(int j = 0; j < 4; j ++){
            mash[j] = new Integer[9];
            for(int i = 0; i < 9; i ++){
                mash[j][i] = NanoTextures.loadImage("/textures/players/button/"+names[j]+"/"+Calcs.fillInt(i)+".png");
            }
        }
        
        skillCheckBack = NanoTextures.loadImage("/textures/players/skillCheck/base.png");
        skillCheck = new Integer[4][];
        skillCheckNext = new Integer[4];
        
        for(int j = 0; j < 4; j ++){
            skillCheck[j] = new Integer[8];
            for(int i = 0; i < 8; i ++){
                skillCheck[j][i] = NanoTextures.loadImage("/textures/players/skillCheck/"+names[j]+"/"+Calcs.fillInt(i)+".png");
            }
        }
        
        for(int j = 0; j < 4; j ++){
            skillCheckNext[j] = NanoTextures.loadImage("/textures/players/skillCheck/"+names[j]+"/next.png");
        }
                
        explosion = new Integer[40];
        for(int i = 0; i < 40; i ++){
            explosion[i] = NanoTextures.loadImage("/textures/players/explosion/"+Calcs.fillInt(i)+".png");
        }
        
        charredRemains = NanoTextures.loadImage("/textures/players/CharredRemains.png");
        shadow = NanoTextures.loadImage("/textures/players/shadow.png");
    }
    
}

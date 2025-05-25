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
    
    public static BufferedImage[] sparks;
    public static BufferedImage[][] mash;
    public static BufferedImage[] explosion;
    
    public static BufferedImage[] faces;
    
    
    public static void loadImages(){
        
        random = new Random();
        
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
    }
    
}

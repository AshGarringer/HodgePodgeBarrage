/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.graphics.Textures;
import engine.logic.Calcs;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class Module {
    
    public BufferedImage[] animation;
    
    public Hitbox hitbox;
    
    public Module(String path, String hitbox_string, int length, int damage){
        
        animation = new BufferedImage[length];
        
        for(int i = 1; i < length; i ++){
            animation[i] = Textures.loadImage("/textures/"+path+"/"+Calcs.fillInt(i) + ".png");
        }
        
        hitbox = new Hitbox(hitbox_string, length, damage);
        
    }
}

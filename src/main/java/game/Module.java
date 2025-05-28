/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.graphics.Textures;
import engine.logic.Calcs;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class Module {
    
    public BufferedImage[] animation;
    
    public Hitbox hitbox;
    
    int frame = 0;
    int frameTimer = 0;
    int length;
    private boolean buffered = false;
    
    public Module(String path, String hitbox_string, int length, int damage){
        
        this.length = length;
        
        animation = new BufferedImage[length];
        
        for(int i = 0; i < length; i ++){
            animation[i] = Textures.loadImage("/textures/"+path+"/"+Calcs.fillInt(i) + ".png");
        }
         
        hitbox = new Hitbox(hitbox_string, length, damage, this);
    }
    
    public void tick(){
        frameTimer ++;
        if(frameTimer == 3){
            if(frame > 0){
                frame ++;
            }
            if(frame >= animation.length){
                frame = 0;
                if(buffered)activate();
            }
            frameTimer = 0;
        }
    }
    
    public void activate(){
        buffered = false;
        if(frame == 0){
            frame ++;
        }
        else if(Math.abs(animation.length -frame) < animation.length/4){
            buffered = true;
        }
    }
    
    public BufferedImage getImage(){
        return animation[frame];
    }
    
    public HitboxPoint[] getHitbox(){
        return hitbox.animation[frame];
    }
    
    private Module(BufferedImage[] animation, Hitbox hitbox, int index){
         this.animation = animation;
         this.hitbox = hitbox;
         this.hitbox.parent = this;
    }
    
    public Module duplicate(int index){
        return new Module(animation,hitbox,index);
    }
}

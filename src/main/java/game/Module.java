/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.graphics.Textures;
import engine.logic.Calcs;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class Module {
    
    public BufferedImage[] animation;
    
    public Hitbox hitbox;
    
    int startup = -1;
    
    int frame = 0;
    int frameTimer = 0;
    int length;
    
    public boolean loop;
    int loop_start = 0;
    int loop_length = 0;
    boolean held = false;
    
    BufferedImage projectile;
    
    private boolean buffered = false;
    
    public Module(String path, String hitbox_string, int length, int damage, int type){
        this(path,hitbox_string,length,0,0,damage,type);
    }
    public Module(String path, String hitbox_string, int length, int loop_start, int loop_length, int damage, int type){
        
        this.length = length;
        
        if(loop_length != 0){
            loop = true;
            this.loop_start = loop_start;
            this.loop_length = loop_length;
        }
        
        animation = new BufferedImage[length];
        
        for(int i = 0; i < length; i ++){
            animation[i] = Textures.loadImage("/textures/modules/"+path+"/"+Calcs.fillInt(i) + ".png");
        }
         
        hitbox = new Hitbox(hitbox_string, length, damage, this);
        
        if(type == 5){
            projectile = Textures.loadImage("/textures/modules/"+path+"/projectile.png");
        }
        
        for(int i = 0; i < hitbox.animation.length && startup == -1; i ++){
            HitboxPoint[] arr = hitbox.animation[i];
            for(int j = 0; j < arr.length; j ++){
                if(arr[j].type > 0){
                    startup = i;
                    break;
                }
            }
        }
    }
    
    public void tick(){
        frameTimer ++;
        if(frameTimer == 3){
            if(frame > 0){
                frame ++;
            }
            if(loop && held && frame == loop_start + loop_length){
                frame = loop_start;
                held = false;
            }
            else if (loop && !held && frame >= loop_start && frame < loop_start + loop_length){
                frame = loop_start + loop_length;
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
    
    public void hold(){
        held = true;
    }
    
    public BufferedImage getImage(){
        return animation[frame];
    }
    
    public HitboxPoint[] getHitbox(){
        return hitbox.animation[frame];
    }
    
    private Module(BufferedImage[] animation, int loop_start, int loop_length, Hitbox hitbox, int index, int startup, BufferedImage projectile){
         this.animation = animation;
         this.hitbox = hitbox;
         this.loop_start = loop_start;
         this.loop_length = loop_length;
         if(loop_start != 0)loop = true;
         this.hitbox.parent = this;
         this.startup = startup;
         this.projectile = projectile;
    }
    
    public Module duplicate(int index){
        return new Module(animation,loop_start,loop_length,hitbox,index,startup,projectile);
    }
}

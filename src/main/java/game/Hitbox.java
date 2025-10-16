/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import java.awt.Point;

/**
 *
 * @author cookiebot
 */
public class Hitbox {
    
    public HitboxPoint[][] hurtboxes;
    public HitboxPoint[][] hitboxes;
    public Module parent;
    public int damage;
    
    public Hitbox(String hitboxString, int length, int damage, Module parent){
        
        this.damage = damage;
        this.parent = parent;
        
        String[] frames = hitboxString.split(" ");
        
        hurtboxes = new HitboxPoint[length][];
        hitboxes = new HitboxPoint[length][];
        
        for(int i = 0; i < length; i ++){
            if(frames.length > i){
                String[] individual_hitboxes = frames[i].split("-");
                int numHurtboxes = 0;
                int numHitboxes = 0;
               
                for(int j = 0; j < individual_hitboxes.length; j ++){
                    if(HitboxPoint.isHurtbox(individual_hitboxes[j]))numHurtboxes ++;
                    else numHitboxes ++;
                }
                
                hurtboxes[i] = new HitboxPoint[numHurtboxes];
                hitboxes[i] = new HitboxPoint[numHitboxes];
                
                for(int j = 0; j < individual_hitboxes.length; j ++){
                    if(HitboxPoint.isHurtbox(individual_hitboxes[j])){
                        hurtboxes[i][hurtboxes[i].length - numHurtboxes] = new HitboxPoint(individual_hitboxes[j], this);
                        numHurtboxes --;
                    }
                    else{
                        hitboxes[i][hitboxes[i].length - numHitboxes] = new HitboxPoint(individual_hitboxes[j], this);
                        numHitboxes --;
                    }
                }
            }else{
                hurtboxes[i] = new HitboxPoint[0];
                hitboxes[i] = new HitboxPoint[0];
            }
        }
    }
    // Explosion
    public Hitbox(int damage, HitboxPoint point){
        
        this.damage = damage;
        hitboxes = new HitboxPoint[1][];
        hitboxes[0] = new HitboxPoint[]{new HitboxPoint(point.x,point.y,point.radius,point.type,point.intensity,this)};
    }
}

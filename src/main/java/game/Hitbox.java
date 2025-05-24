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
    
    public HitboxPoint[][] animation;
    public Module parent;
    public int damage;
    
    public Hitbox(String hitboxes, int length, int damage, Module parent){
        
        this.damage = damage;
        this.parent = parent;
        
        String[] frames = hitboxes.split(" ");
        
        animation = new HitboxPoint[length][];
        
        for(int i = 0; i < length; i ++){
            if(frames.length > i){
                String[] individual_hitboxes = frames[i].split("-");
                animation[i] = new HitboxPoint[individual_hitboxes.length];
                for(int j = 0; j < individual_hitboxes.length; j ++){
                    animation[i][j] = new HitboxPoint(individual_hitboxes[j], damage, this);
                }
            }else{
                animation[i] = new HitboxPoint[1];
                animation[i][0] = new HitboxPoint();
            }
        }
        
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.graphics.NanoTextures;

/**
 *
 * @author cookiebot
 */
public class Projectile {
    
    float x;
    float y;
    float xVel;
    float yVel;
    Integer image;
    double rotation;
    int timer;
    int parent;
    int damage;
    
    public Projectile(float x, float y, float xVel, float yVel, Integer image, double rotation, int parent){
        this.x = x;
        this.y = y;
        this.xVel = xVel;
        this.yVel = yVel;
        this.image = image;
        this.rotation = rotation;
        timer = 120;
        this.parent = parent;
        this.damage = 4;
    }
    
    public void tick(){
        x += xVel;
        y += yVel;
        timer --;
    }
    
    public HitboxPoint getHitbox(){
        return new HitboxPoint((int)x,(int)y,NanoTextures.getWidth(image),1,damage,this, true);
    }
    
    public void reflect(int newParent){
        timer = 120;
        damage *= 1.5f;
        this.parent = newParent;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class Projectile {
    
    float x;
    float y;
    float xVel;
    float yVel;
    BufferedImage[] image;
    double rotation;
    int timer;
    int parent;
    int damage;
    int radius;
    int type;
    
    public Projectile(float x, float y, float xVel, float yVel, BufferedImage[] image, double rotation, int parent, int type){
        this.x = x;
        this.y = y;
        this.xVel = xVel;
        this.yVel = yVel;
        this.image = image;
        this.rotation = rotation;
        timer = 120;
        this.parent = parent;
        this.type = type;
        switch(type){
            default:
                this.damage = 1;
                this.radius = 5;
                break;
            case 5:
                this.radius = 10;
                this.damage = 4;
                break;
            case 6:
                this.radius = 15;
                this.damage = 0;
                break;
        }
    }
    
    public void tick(){
        x += xVel;
        y += yVel;
        timer --;
        if(type == 6){
            yVel *= 0.97f;
            xVel *= 0.97f;
            if(timer == 36){
                damage = 10;
                radius = 40;
            }
            if(timer == 10){
                damage = 0;
                radius = 0;
            }
        }
        
    }
    
    public void render(Graphics2D g){
        if(type != 6){
            g.drawImage(image[0], (int)x-10,(int)y-10, null);
        }
        else{
            g.drawImage(image[Math.min((int)((120-timer)/3f),39)],(int)x-40,(int)y-40,null);
        }
    }
    
    public HitboxPoint getHitbox(){
        return new HitboxPoint((int)x,(int)y,radius,1,damage,this, true);
    }
    
    public Projectile reflect(int newParent){
        if(type != 6)timer = 120;
        xVel *= -1;
        xVel *= -1;
        this.parent = newParent;
        return this;
    }
}

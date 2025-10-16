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
                radius = 50;
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
    
    public void checkCollisions(Map map){
        switch(type){
            case 6:
                if(timer <= 36)return;
                double collisionAngle = map.intersects(x+xVel, y+yVel,10);
                if(collisionAngle != 0){
                    double velocity = Math.sqrt(xVel*xVel +yVel*yVel);
                    double angle = Math.atan2(yVel,xVel);
                    double parallel = Math.cos(angle - collisionAngle)*(velocity);
                    double perpendicular = Math.sin(angle - collisionAngle)*velocity;
                    double newAngle = Math.atan2(perpendicular, Math.abs(parallel*0.6f)) + collisionAngle;
                    xVel = (float)(Math.cos(newAngle)*velocity);
                    yVel = (float)(Math.sin(newAngle)*velocity);
                }
                break;
            default:
                if(map.intersects(x, y, 3) != 0)this.timer = -1;
                break;
                
        }
    }
    public void checkCollisions(Player player, int playerIndex){
        
        if (Math.sqrt(Math.pow(player.x - x, 2) + Math.pow(player.y-y, 2)) > 200) return;
        
        if(!player.alive || player.hurtboxes == null)return;
        for(int i = 0; i < player.hitboxes.length; i ++){
            HitboxPoint h1 = player.hitboxes[i];
            if(hitboxesCollide(h1, this.getHitbox())){
                if(h1.isReflecting()){
                    this.reflect(playerIndex);
                }
                else if(h1.isAttacking()){
                    if(type == 6){
                        timer = 37;
                    }
                    else{
                        timer = -1;
                    }
                }
                else if(h1.isShielding()){
                    if(type == 6){
                        float holderX = xVel;
                        float holderY = yVel;
                        xVel = player.xVel;
                        yVel = player.yVel;
                        player.xVel += 0.9*holderX;
                        player.yVel += 0.9*holderY;
                    }
                    else {
                        timer = -1;
                    }
                }
                return;
            }
        }
        
        if(type != 6)
            for(int i = 0; i < player.hurtboxes.length; i ++){
                HitboxPoint h1 = player.hurtboxes[i];
                if(hitboxesCollide(h1, this.getHitbox())){
                    player.takeDamage((int)(damage * h1.intensity / 100), 1,
                            Math.atan2(player.y - y,  player.x - x));
                    this.timer = -1;
                    return;
                }
            }
        else{
            for(int i = 0; i < player.hurtboxes.length; i ++){
                HitboxPoint h1 = player.hurtboxes[i];
                if(hitboxesCollide(h1, this.getHitbox())){
                    if(timer <= 36){
                        player.takeDamage((int)(damage * h1.intensity / 100), 1,
                                Math.atan2(player.y - y,  player.x - x));
                    }
                    else{
                        float holderX = xVel;
                        float holderY = yVel;
                        xVel = player.xVel;
                        yVel = player.yVel;
                        player.xVel += 0.9*holderX;
                        player.yVel += 0.9*holderY;
                    }
                    return;
                }
            }
        }
    }
    
    private boolean hitboxesCollide(HitboxPoint a, HitboxPoint b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double radSum = a.radius + b.radius;
        return dx * dx + dy * dy < radSum * radSum;
    }
    
    public Projectile reflect(int newParent){
        if(type != 6)timer = 120;
        xVel *= -1;
        yVel *= -1;
        this.parent = newParent;
        return this;
    }
}

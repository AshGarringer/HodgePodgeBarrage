/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.graphics.Textures;
import engine.input.SnesController;
import engine.logic.Calcs;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class Player {
    
    Integer[] selected;
    Module[] modules;
    BufferedImage body;
    HitboxPoint[] hitboxes;
    
    int playerNum;
    
    float x,y,rotation;
    float xAcc,yAcc,rAcc; 
    
    public void tickMenu(SnesController controller){
        
    }
    
    public void init(Module[] modules, Integer playerNum){
        this.modules = modules;
        this.playerNum = playerNum;
        x = -200 + 400*(playerNum%2);
        y = -200 + 400*(playerNum/2);
        body = Textures.loadImage("/textures/players/smiley.png");
    }
    
    public void tickGame(SnesController controller){
        
        for(int i = SnesController.X; i <= SnesController.Y; i ++){
            if(controller.pressed(i)){
                modules[i].activate();
            }
        }
        for(int i = 0; i < 4; i ++){
            modules[i].tick();
        }
        
        hitboxes = new HitboxPoint[modules[0].getHitbox().length + modules[1].getHitbox().length+
            modules[2].getHitbox().length + modules[2].getHitbox().length + 1];
        hitboxes[0] = new HitboxPoint((int)x,(int)y,43,0,100);
        int hitboxNum = 1;
        for(int i = 0; i < 4; i ++){
            HitboxPoint[] hitboxFrame = modules[i].getHitbox();
            for(int j = 0; j < hitboxFrame.length; j ++){
                Point rotatedPoint = Calcs.rotatePoint(hitboxFrame[j].x + x-40,hitboxFrame[j].y + y-120,x,y,rotation+i*90);
                hitboxes[hitboxNum] = new HitboxPoint(rotatedPoint.x,rotatedPoint.y,hitboxFrame[j].radius,
                        hitboxFrame[j].type,hitboxFrame[j].intensity);
                hitboxNum ++;
            }
        }
    }
    
    public void renderGame(Graphics2D g){
        
        g.setColor(new Color(255,0,0,100));
        g.translate(x,y);
        g.rotate(rotation);
        
        for(int i = 0; i < 4; i ++){
            BufferedImage image = modules[i].getImage();
            g.drawImage(image,-40,-120,null);
            g.rotate(Math.PI/2);
        }
        g.drawImage(body, -40,-40, null);
        g.rotate(-rotation);
        g.translate(-x,-y);
        for(int i = 0; i < hitboxes.length; i ++){
            HitboxPoint hp = hitboxes[i];
            if(hp != null)
            g.fillOval(hp.x-hp.radius,hp.y-hp.radius,hp.radius*2,hp.radius*2);
        }
    }
}

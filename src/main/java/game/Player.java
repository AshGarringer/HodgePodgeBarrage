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
import java.util.ArrayList;

/**
 *
 * @author cookiebot
 */
public class Player {
    
    private static final Color HITBOX0 = new Color(255,0,0,100);
    private static final Color HITBOX1 = new Color(0,255,0,100);
    private static final Color HITBOX2 = new Color(0,0,255,100);
    private static final Color HITBOX3 = new Color(255,255,0,100);
    private static final Color HITBOX4 = new Color(50,50,50,100);
    
    Integer[] selected;
    Module[] modules;
    BufferedImage body;
    HitboxPoint[] hitboxes;
    
    int playerNum;
    
    float lastX, lastY;
    
    float x,y,rotation;
    float xVel,yVel,rVel; 
    float maxSpeed = 7;
    float maxRotSpeed = 0.08f;
    
    float jitterX;
    float jitterY;

    int damage;

    int maxPause;
    int pause;
    
    int deathAcc = 0;
    int deathVelocity = 0;
    int deathLevel = 0;
    
    ArrayList<Player> intersecting;

    public Player(){
        x = 0;
        y = 0;
        rotation = 0;
        xVel = 0;
        yVel = 0;
        rVel = 0;
        damage = 0;
        maxPause = 0;
        intersecting = new ArrayList<>();
        damage = 0;
    }
    
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
        
        if(pause > 0){
            jitterX *= -1;
            jitterY *= -1;
            pause --;
            return;
        }
        
        int xintent = 0;
        int yintent = 0;

        if(controller.held(SnesController.UP)){
            yintent --;
        }
        if(controller.held(SnesController.DOWN)){
            yintent ++;
        }
        if(controller.held(SnesController.LEFT)){
            xintent --;
        }
        if(controller.held(SnesController.RIGHT)){
            xintent ++;
        }
        double sp = maxSpeed;
        if(xintent != 0 && yintent != 0){
            sp = maxSpeed/Math.sqrt(2);
        }
        if(controller.pressed(SnesController.UP)){
            xVel -= sp/5;
        }
        if(controller.pressed(SnesController.DOWN)){
            xVel += sp/5;
        }
        if(controller.pressed(SnesController.LEFT)){
            xVel -= sp/5;
        }
        if(controller.pressed(SnesController.RIGHT)){
            xVel += sp/5;
        }
        xVel += (sp*xintent - xVel)*0.03;
        yVel += (sp*yintent - yVel)*0.03;
        lastX = x;
        lastY = y;
        x += xVel;
        y += yVel;

        int rotIntent = 0;
        if(controller.held(SnesController.LTRIGGER)){
            rotIntent --;
        }
        if(controller.held(SnesController.RTRIGGER)){
            rotIntent ++;
        }
        rVel += (maxRotSpeed*rotIntent - rVel)*0.1;
        rotation = (float)((rotation+rVel)%(Math.PI*2));

        for(int i = SnesController.X; i <= SnesController.Y; i ++){
            if(controller.pressed(i)){
                modules[i].activate();
                rVel *= 0.5;
                if(rotIntent == 0){
                    rVel *= 0.5;
                }
            }
        }
        for(int i = 0; i < 4; i ++){
            modules[i].tick();
        }

        controller.clearPressed();

        hitboxes = new HitboxPoint[modules[0].getHitbox().length + modules[1].getHitbox().length+
        modules[2].getHitbox().length + modules[3].getHitbox().length + 1];
        hitboxes[0] = new HitboxPoint((int)x,(int)y,43,0,100,null);
        int hitboxNum = 1;
        for(int i = 0; i < 4; i ++){
            HitboxPoint[] hitboxFrame = modules[i].getHitbox();
            for(int j = 0; j < hitboxFrame.length; j ++){
                Point rotatedPoint = Calcs.rotatePoint(hitboxFrame[j].x + x-40,hitboxFrame[j].y + y-120,x,y,Math.toDegrees(rotation)+i*90);
                hitboxes[hitboxNum] = new HitboxPoint(rotatedPoint.x,rotatedPoint.y,hitboxFrame[j].radius,
                        hitboxFrame[j].type,hitboxFrame[j].intensity,hitboxFrame[j].parent);
                hitboxNum ++;
            }
        }
    }

    public void takeDamage(int damage, int type, double direction){
        pause = damage*2;
        maxPause = damage*2;
        
        //calculate direction
        
        xVel += (float)Math.cos(direction)*damage/2;
        yVel += (float)Math.sin(direction)*damage/2;
        
        direction = Math.atan2(yVel, xVel);
        jitterX = (float)Math.cos(direction)*damage/2;
        jitterY = (float)Math.sin(direction)*damage/2;

        this.damage += damage;
    }
    
    public void addPause(int damageDealt){
        pause = damageDealt*2;
        maxPause = damageDealt*2;
        
        jitterX = 0;
        jitterY = 0;
    }
    
    public void revertPos(){
        x = lastX;
        y = lastY;
    }
    
    public void renderGame(Graphics2D g){
        
        float x2 = this.x;
        float y2 = this.y;
        
        if(pause > 0 && (jitterX > 0 || jitterY > 0)){
            x2 += jitterX*((float)pause/maxPause);
            y2 += jitterY*((float)pause/maxPause);
        }
        
        g.setColor(new Color(255,0,0,100));
        g.translate(x2,y2);
        g.rotate(rotation);
        
        for(int i = 0; i < 4; i ++){
            BufferedImage image = modules[i].getImage();
            g.drawImage(image,-40,-120,null);
            g.rotate(Math.PI/2);
        }
        g.drawImage(body, -40,-40, null);
        g.rotate(-rotation);
        g.translate(-x2,-y2);
        int lastType = -1;
        for(int i = 0; i < hitboxes.length; i ++){
            HitboxPoint hp = hitboxes[i];
            if(hp.type != lastType){
                lastType = hp.type;
                switch(hp.type){
                    case 0 -> g.setColor(HITBOX0);
                    case 1 -> g.setColor(HITBOX1);
                    case 2 -> g.setColor(HITBOX2);
                    case 3 -> g.setColor(HITBOX3);
                    default -> g.setColor(HITBOX4);
                }
            }
            g.fillOval(hp.x-hp.radius,hp.y-hp.radius,hp.radius*2,hp.radius*2);
        }
    }
}

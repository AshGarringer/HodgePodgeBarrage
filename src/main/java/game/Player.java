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
    
    public static boolean DRAW_HITBOXES = false;
    
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
    
    float deathVelocity = 0;
    float deathLevel = 0;
    
    int mashButton = 0;
    int spinDirection = 0;
    
    int mashFrame = 0;
    int sparkFrame = 0;
    
    int explosionFrame = 0;
    
    boolean alive = true;
    
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
        
        
        if(explosionFrame > 0){
            
            hitboxes = new HitboxPoint[1];
            hitboxes[0] = new HitboxPoint((int)x,(int)y,100,10,100,null);
            return;
        }
        
        if(pause > 0){
            jitterX *= -1;
            jitterY *= -1;
            pause --;
            return;
        }
        else if (deathVelocity > 0){
            deathLevel += deathVelocity;

            if(controller.pressed(SnesController.X + mashButton)){
                deathVelocity -= 0.1;
            }
            controller.clearPressed();

            if(deathLevel >= 100){
                deathVelocity = 0;
                deathLevel = 0;
                explosionFrame ++;
            }
            else if(deathLevel <= 0){
                deathLevel = 0;
                deathVelocity = 0;
                explosionFrame = 0;
            }

            lastX = x;
            lastY = y;

            xVel *= 0.97;
            yVel *= 0.97;
            x += xVel;
            y += yVel;
            rVel = (rotation + spinDirection*deathLevel/300f)%(float)(Math.PI*2)-rotation;
            rotation = (rotation + spinDirection*deathLevel/300f)%(float)(Math.PI*2);
            hitboxes = new HitboxPoint[1];
            hitboxes[0] = new HitboxPoint((int)x,(int)y,50,0,100,null);
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
        if(rVel <= maxRotSpeed)
            rVel += (maxRotSpeed*rotIntent - rVel)*0.1;
        else
            rVel += (maxRotSpeed*rotIntent - rVel)*0.03;
        rotation = (float)((rotation+rVel)%(Math.PI*2));

        for(int i = SnesController.X; i <= SnesController.Y; i ++){
            if(controller.pressed(i) && rVel <= maxRotSpeed){
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
        
        if(this.damage >= 100){
            deathVelocity = (float)(Math.sqrt(this.damage-60)*damage/(float)300);
            if(deathVelocity < 0.1)deathVelocity = 0;
            else{
                mashButton = Players.random.nextInt(4);
            }
            if(rVel > 0)spinDirection = 1;
            else spinDirection = -1;
        }

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
        
        if(explosionFrame > 0){
            
            g.drawImage(Players.explosion[explosionFrame], (int)x - 120,(int)y-120,null);
            
            explosionFrame ++;
            if(explosionFrame > 39)alive = false;
            return;
        }
        
        float x2 = this.x;
        float y2 = this.y;
        
        if(pause > 0 && (jitterX > 0 || jitterY > 0)){
            x2 += jitterX*((float)pause/maxPause);
            y2 += jitterY*((float)pause/maxPause);
        }
        if(deathVelocity > 0){
            float dist = deathLevel*8/100f + Players.random.nextFloat((deathLevel+0.01f)*2/100);
            double angle = Players.random.nextDouble(Math.PI*2);
            x2 += Math.cos(angle)*dist;
            y2 += Math.sin(angle)*dist;
            sparkFrame ++;
            if(sparkFrame >= 50)sparkFrame = 0;
            mashFrame ++;
            if(mashFrame >= 9)mashFrame = 0;
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
        
        if(deathVelocity>0 && pause == 0){
            x2 += (x-x2)*0.6f;
            y2 += (y-y2)*0.6f;
            
            g.translate(x2,y2);
            g.drawImage(Players.mash[mashButton][mashFrame], -20,-20, null);
            if(deathLevel > 40)
                g.drawImage(Players.sparks[sparkFrame], -60,-60, null);
            g.translate(-x2,-y2);
        }
        if(DRAW_HITBOXES){
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
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.graphics.Textures;
import engine.input.SnesController;
import engine.logic.Calcs;
import java.awt.AlphaComposite;
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
    
    public static double HIGHLIGHT_ANGLE = -Math.PI/4;
    
    Game game;
    
    Integer[] selected;
    Module[] modules;
    Integer center;
    HitboxPoint[] hitboxes;
    
    int playerNum;
    
    float lastX, lastY, lastR;
    
    float x,y,rotation;
    float xVel,yVel,rVel; 
    float maxSpeed = 7;
    float maxRotSpeed = 0.08f;
    
    float jitterX;
    float jitterY;

    int damage;

    int maxPause;
    int pause;
    
    int damageFrame = 0;
    
    float deathVelocity = 0;
    float deathLevel = 0;
    
    int mashButton = 0;
    int spinDirection = 0;
    
    int mashFrame = 0;
    int sparkFrame = 0;
    
    int explosionFrame = 0;
    
    boolean alive = true;
    
    ArrayList<Player> intersecting;

    public Player(Game game){
        x = 0;
        y = 0;
        rotation = 0;
        xVel = 0;
        yVel = 0;
        rVel = 0;
        damage = 0;
        maxPause = 0;
        intersecting = new ArrayList<>();
        this.game = game;
    }
    
    public void tickMenu(SnesController controller){
        
    }
    
    public void init(Module[] modules, int center, Integer playerNum){
        this.modules = modules;
        this.center = center;
        this.playerNum = playerNum;
        if(Players.centersTilted[center])rotation -= (float)Math.PI/4f;
        x = -200 + 400*(playerNum%2);
        y = -200 + 400*(playerNum/2);
    }
    
    public void tickGame(SnesController controller){
        
        if(!alive){
            hitboxes = null;
            return;
        }
        
        if(explosionFrame > 0){
            if(explosionFrame > 3 && explosionFrame < 15){
                hitboxes = new HitboxPoint[1];
                hitboxes[0] = new HitboxPoint((int)x,(int)y,100,10,100,null);
            }
            else{
                hitboxes = null;
            }
            return;
        }
        
        if(pause > 0){
            pause --;
            if(deathVelocity > 0 || deathLevel > 0){
                if(controller.pressed(SnesController.X + mashButton)){
                    deathVelocity -= 0.1;
                }
            }
            return;
        }
        else if (deathVelocity > 0 || deathLevel > 0){
            deathLevel += deathVelocity;
            if(deathLevel < 90 && deathVelocity <0)
                deathLevel = 0;

            if(controller.pressed(SnesController.X + mashButton)){
                deathVelocity -= 0.1;
            }
            controller.clearPressed();

            if(deathLevel >= 100){
                deathVelocity = 0;
                deathLevel = 0;
                explosionFrame ++;
                return;
            }
            else if(deathLevel <= 0){
                deathLevel = 0;
                deathVelocity = 0;
                explosionFrame = 0;
                return;
            }

            lastX = x;
            lastY = y;

            xVel *= 0.97;
            yVel *= 0.97;
            tryToMove(xVel,yVel);
            lastR = rotation;
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
        tryToMove(xVel,yVel);

        int rotIntent = 0;
        if(controller.held(SnesController.LTRIGGER)){
            rotIntent --;
        }
        if(controller.held(SnesController.RTRIGGER)){
            rotIntent ++;
        }
        float maxRotSpeed2 = maxRotSpeed;
        for(int i = 0; i < 4; i ++){
            if(modules[i].frame <= modules[i].startup && modules[i].frame > 0){
                maxRotSpeed2 /= 2;
                break;
            }
        }
        if(rVel <= maxRotSpeed2)
            rVel += (maxRotSpeed2*rotIntent - rVel)*0.1;
        else
            rVel += (maxRotSpeed2*rotIntent - rVel)*0.03;
        lastR = rotation;
        rotation = (float)((rotation+rVel)%(Math.PI*2));

        for(int i = SnesController.X; i <= SnesController.Y; i ++){
            if(controller.pressed(i) && rVel <= maxRotSpeed){
                modules[i].activate();
                if(rVel <= maxRotSpeed){
                    rVel *= 0.5;
                    if(rotIntent == 0){
                        rVel *= 0.5;
                    }
                }
                if(modules[i].projectile != null){
                    xVel *= 0.9;
                    yVel *= 0.9;
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
                
                if(hitboxFrame[j].type == 3 && modules[i].frameTimer == 1){
                    
                    double projectileRotation = rotation + i * Math.PI / 2 - Math.PI / 2;
                    
                    float velocityX = (float)Math.cos(projectileRotation)*7;
                    float velocityY = (float)Math.sin(projectileRotation)*7;
                    
                    game.projectiles.add(new Projectile(rotatedPoint.x,rotatedPoint.y,velocityX + xVel,
                            velocityY + yVel,modules[i].projectile,projectileRotation, playerNum));
                }
                    hitboxes[hitboxNum] = new HitboxPoint(rotatedPoint.x,rotatedPoint.y,hitboxFrame[j].radius,
                            hitboxFrame[j].type,hitboxFrame[j].intensity,hitboxFrame[j].parent);
                hitboxNum ++;
            }
        }
    }
    
    public void tryToMove(float moveX, float moveY){
        if(x + moveX >= Game.WINDOW_WIDTH/2 - 40){
            if(xVel > 0)xVel = -xVel;
        }
        else if(x + moveX <= - Game.WINDOW_WIDTH/2 + 40){
            if(xVel < 0)xVel = -xVel;
        }
        if(y + moveY >= Game.WINDOW_HEIGHT/2- 40){
            if(yVel > 0)yVel = -yVel;
        }
        else if(y + moveY <= - Game.WINDOW_HEIGHT/2 + 40){
            if(yVel < 0)yVel = -yVel;
        }
        else{
            x += moveX;
            y += moveY; 
        }
    }

    public void takeDamage(int damage, int type, double direction){
        
        if(deathLevel > 0 || deathVelocity > 0){
            addPause(damage);
            return;
        }
        double knockback = Math.pow(damage,3/4f)*1.2f*Math.pow(this.damage+100, 1/4f)/Math.pow(100,1/4f);
        
        pause = damage*2;
        maxPause = damage*2;
        
        //calculate direction
        
        xVel += (float)(Math.cos(direction)*knockback);
        yVel += (float)(Math.sin(direction)*knockback);
        
        direction = Math.atan2(yVel, xVel);
        jitterX = (float)Math.abs(Math.cos(direction)*knockback);
        jitterY = (float)Math.abs(Math.sin(direction)*knockback);
        
        if(type != 2){
            if(this.damage > 60){
                deathVelocity = (float)(Math.pow((this.damage-60)*Math.pow(damage,1/2f),1/2f)/12);
                System.out.println(deathVelocity);
                if(deathVelocity < 0.3)deathVelocity = 0;
                else{
                    mashButton = Players.random.nextInt(4);
                }
                if(rVel > 0)spinDirection = 1;
                else spinDirection = -1;
            }

            this.damage += damage;
        }
        else{
            int dir = Math.round(rotation/Math.abs(rotation));
            rVel = maxRotSpeed*3*dir;
        }
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
        rotation = lastR;
    }
    
    public void renderGame(Graphics2D g){
        
        if(!alive){
//            g.drawImage(Players.charredRemains, (int)x-60,(int)y-60,120,120,null);
            return;
        }
        
        if(explosionFrame > 0){
//            g.drawImage(Players.charredRemains, (int)x-60,(int)y-60,120,120,null);
            g.drawImage(Players.explosion[explosionFrame], (int)x - 120,(int)y-120,null);
            
            
            explosionFrame ++;
            if(explosionFrame > 39)alive = false;
            return;
        }
        
        float x2 = this.x;
        float y2 = this.y;
        
        if(pause > 0 && (Math.abs(jitterX) > 0 || Math.abs(jitterY) > 0)){
            
            float dir = Players.random.nextFloat(2f);
            x2 += (jitterX - dir * jitterX)*((float)pause/maxPause);
            y2 += (jitterY - dir * jitterY)*((float)pause/maxPause);
        }
        else if(deathVelocity > 0|| deathLevel > 0){
            float dist = deathLevel*8/100f + Players.random.nextFloat((deathLevel+0.01f)*2/100);
            double angle = Players.random.nextDouble(Math.PI*2);
            x2 += Math.cos(angle)*dist;
            y2 += Math.sin(angle)*dist;
            sparkFrame ++;
            if(sparkFrame >= 50)sparkFrame = 0;
            mashFrame ++;
            if(mashFrame >= 9)mashFrame = 0;
        }else if(damage > 34 && damage < 200){
            float dist = Players.random.nextFloat((damage-33.999f)*2/100);
            double angle = Players.random.nextDouble(Math.PI*2);
            x2 += Math.cos(angle)*dist;
            y2 += Math.sin(angle)*dist;
        }
        
        if(damage > 34){
            int level = (damage-34)/33;
            if(level > 3)level = 3;
            if(damage >= 200 && deathVelocity <= 0 && deathLevel <= 0)level = 4;

            // 1. Create an offscreen buffer (now 240x240)
            BufferedImage playerBuffer = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gPlayer = playerBuffer.createGraphics();

            // 2. Draw player and modules to the buffer, centered
            gPlayer.translate(120, 120); // Center the drawing in the larger buffer
            gPlayer.rotate(rotation);
            for (int i = 0; i < 4; i++) {
                BufferedImage image = modules[i].getImage();
                gPlayer.drawImage(image, -40, -120, null);
                gPlayer.rotate(Math.PI / 2);
            }
            if(Players.centersTilted[center])gPlayer.rotate((float)Math.PI/4f);
            gPlayer.drawImage(Players.centers[center], -40, -40, null);
            if(Players.centersTilted[center])g.rotate(-(float)Math.PI/4f);
            gPlayer.rotate(HIGHLIGHT_ANGLE - rotation);
            gPlayer.drawImage(Players.highlight, -40, -40, 80, 80, null);
            gPlayer.rotate(-HIGHLIGHT_ANGLE + rotation);
            if(Players.centersTilted[center])g.rotate((float)Math.PI/4f);
            gPlayer.drawImage(Players.centerOverlays[center], -40, -40, null);
            if(Players.centersTilted[center])gPlayer.rotate(-(float)Math.PI/4f);
            gPlayer.dispose();

            // 3. Draw the flash animation using SRC_ATOP
            Graphics2D gFlash = playerBuffer.createGraphics();
            gFlash.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.7f));
            g.translate(x2-x, y2-x);
            gFlash.drawImage(Players.flashes[level][damageFrame], 40, 40, null); // Center the 160x160 flash in 240x240
            g.translate(-x2+x, -y2+x);
            gFlash.dispose();

            g.drawImage(Players.shadow, (int)x2-50,(int)y2-50, null);
            // 4. Draw the result to the main graphics context
            g.drawImage(playerBuffer, (int)x2 - 120, (int)y2 - 120, null);

            // 5. Draw the damage overlay as before (optional)
            
            g.translate(x2, y2);
            g.rotate(rotation);
            if(Players.centersTilted[center])g.rotate((float)Math.PI/4f);
            g.drawImage(Players.damaged[level][damageFrame], -50, -50, null);
            if(Players.centersTilted[center])g.rotate(-(float)Math.PI/4f);
            g.rotate(-rotation);
            g.translate(-x2, -y2);
            damageFrame++;
            if (damageFrame >= 24) damageFrame = 0;
        } else {
            // Draw player and modules as before if not flashing
            g.translate(x2, y2);
            g.drawImage(Players.shadow, -50,-50, null);
            g.rotate(rotation);
            for (int i = 0; i < 4; i++) {
                BufferedImage image = modules[i].getImage();
                g.drawImage(image, -40, -120, null);
                g.rotate(Math.PI / 2);
            }
            if(Players.centersTilted[center])g.rotate((float)Math.PI/4f);
            g.drawImage(Players.centers[center], -40, -40, null);
            if(Players.centersTilted[center])g.rotate(-(float)Math.PI/4f);
            g.rotate(HIGHLIGHT_ANGLE - rotation);
            g.drawImage(Players.highlight, -40, -40, 80, 80, null);
            g.rotate(-HIGHLIGHT_ANGLE + rotation);
            if(Players.centersTilted[center])g.rotate((float)Math.PI/4f);
            g.drawImage(Players.centerOverlays[center], -40, -40, null);
            if(Players.centersTilted[center])g.rotate(-(float)Math.PI/4f);
            g.rotate(-rotation);
            g.translate(-x2, -y2);
        }
        if((deathVelocity>0 || deathLevel > 0)&& pause == 0){
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
                        case 0:g.setColor(HITBOX0);break;
                        case 1:g.setColor(HITBOX1);break;
                        case 2:g.setColor(HITBOX2);break;
                        case 3:g.setColor(HITBOX3);break;
                        default:g.setColor(HITBOX4);break;
                    }
                }
                g.fillOval(hp.x-hp.radius,hp.y-hp.radius,hp.radius*2,hp.radius*2);
            }
        }
    }
}

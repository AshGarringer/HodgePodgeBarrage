/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.input.SnesController;
import engine.logic.Calcs;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
    int iframes;
    
    int damageFrame = 0;
    
    boolean mash = false;
    // skill check
    int numButtons = 0;
    int nextMashButton = 0;
    // mash & skill check
    float deathVelocity = 0;
    float deathLevel = 0;
    int mashButton = 0;
    double lastPressTime = 0;
    
    int spinDirection = 0;
    
    int mashFrame = 0;
    int sparkFrame = 0;
    
    int explosionFrame = 0;
    
    boolean alive = true;
    
    ArrayList<Player> intersecting;
    ArrayList<Integer> intersectingMap;
    ArrayList<Integer> intersectingZone;
    
    SnesController controller;
    
    Integer[] moduleSelections;
    Float[] mouse;
    Integer playerHover;

    public Player(Game game, int controllerId, Integer playerNum){
        x = 0;
        y = 0;
        rotation = 0;
        xVel = 0;
        yVel = 0;
        rVel = 0;
        damage = 0;
        maxPause = 0;
        iframes = 0;
        intersecting = new ArrayList<>();
        intersectingMap = new ArrayList<>();
        intersectingZone = new ArrayList<>();
        controller = new SnesController(controllerId);
        this.playerNum = playerNum;
        this.game = game;
        initMenu();
    }
    
    public final void initMenu(){
        mouse = new Float[]{1220f + (playerNum % 2) * 1160,375f + (playerNum / 2) * 650};
        moduleSelections = new Integer[]{-1,-1,-1,-1};
        playerHover = -1;
        center = 0;
    }
    
    public final void initGame(){
        this.modules = Modules.loadModuleSelection(moduleSelections);
        x = -200 + 400*(playerNum%2);
        y = -200 + 400*(playerNum/2);
    }
    
    public void tickMenu(){
        
        controller.clearPressed();
        controller.update();

        if(controller.pressed(SnesController.LTRIGGER)){
            center = (center -1 + Players.centers.length)%Players.centers.length;
        }
        if(controller.pressed(SnesController.RTRIGGER)){
            center = (center +1 + Players.centers.length)%Players.centers.length;;
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
        mouse[0] += 10*xintent;
        mouse[1] += 10*yintent;

        // Get mouse position for this player
        float mouseX = mouse[0];
        float mouseY = mouse[1];

        // Determine which module (if any) is hovered
        int hoveredModule = -1;
        for (int m = 0; m < game.modules.length; m++) {
            int col = m % 2;
            int row = m / 2;
            int x = 1530 + col * 270;
            int y = 100 + row * 200;
            if (mouseX >= x && mouseX < x + 270 && mouseY >= y && mouseY < y + 200) {
                hoveredModule = m;
                break;
            }
        }
        playerHover = hoveredModule;

        // Handle selection
        if (hoveredModule != -1 && controller.pressed(SnesController.A)) {
            // Find the first empty slot (-1)
            for (int s = 0; s < moduleSelections.length; s++) {
                if (moduleSelections[s] == -1) {
                    moduleSelections[s] = hoveredModule;
                    break;
                }
            }
        }
        else if (controller.pressed(SnesController.B)){
            // Remove the last selected module
            for (int s = moduleSelections.length - 1; s >= 0; s--) {
                if (moduleSelections[s] != -1) {
                    moduleSelections[s] = -1;
                    break;
                }
            }
        }
    }
    
    public void drawPlayerPreview(Graphics2D g, boolean drawMouse) {
        
        int col = playerNum % 2;
        int row = playerNum / 2;
        int x = 1220 + col * 1160;
        int y = 375 + row * 650;
        
        g.drawImage(ModuleSelect.playerShadow, x-225,y-225,null);
        
        g.translate(x, y);

        int hovered = playerHover;
        boolean previewDrawn = false;
        
        for (int i = 0; i < 4; i++) {
            if (moduleSelections[i] >= 0)
                g.drawImage(ModuleSelect.eqippedPreviews[moduleSelections[i]], -160, -330, null);
            else if(!previewDrawn && hovered >= 0 ){
                previewDrawn = true;
                Composite oldComp = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g.drawImage(ModuleSelect.eqippedPreviews[hovered], -160, -330, null);
                g.setComposite(oldComp);
            }
            g.rotate(Math.PI / 2);
        }
        
        g.drawImage(ModuleSelect.playerPreviews[center], -160, -160, null);

        g.translate(-x, -y);
        if (drawMouse)
            g.drawImage(ModuleSelect.cursors[playerNum], Math.round(mouse[0]) - 100,
                    Math.round(mouse[1]) - 100, 200, 200, null);
    }
    
    int pressButtonTimer = 10;
    
    public void tickGame(){
        
        controller.clearPressed();
        controller.update();
        
        if(!alive){
            hitboxes = null;
            return;
        }
        
        if(explosionFrame > 0){
            if(explosionFrame > 3 && explosionFrame < 15){
                hitboxes = new HitboxPoint[1];
                hitboxes[0] = new HitboxPoint((int)x,(int)y,200,10,100,null);
            }
            else{
                hitboxes = null;
            }
            return;
        }
        
        if(pause > 0){
            pause --;
            if(mash && deathVelocity > 0 || deathLevel > 0 &&
                    controller.pressed(SnesController.X + mashButton, false)){
                deathVelocity -= 0.1;
            }
            else if(!mash && numButtons > 0 && controller.pressed(SnesController.X + mashButton, false)){
                numButtons --;
                deathLevel -= 0;
                if(numButtons > 0)mashButton = Players.random.nextInt(4);
            }
            return;
        }
        if(numButtons > 0 || deathVelocity > 0 || deathLevel > 0){
            if (mash){
                deathLevel += deathVelocity;
                if(deathLevel < 90 && deathVelocity < 0)
                    deathLevel = 0;

                if(controller.pressed(SnesController.X + mashButton, false)){
                    deathVelocity -= 0.1;
                }
            }
            if(!mash){
                deathLevel += deathVelocity;
                pressButtonTimer --;
                if(controller.pressed(SnesController.X + mashButton, false)){
                    pressButtonTimer = 10;
                    numButtons --;
                    
                    deathLevel -= 10;
                    if(deathLevel < 0)deathLevel = 0;
                    if(numButtons > 0){
                        mashButton = nextMashButton;
                        nextMashButton = Players.random.nextInt(3);
                        if(nextMashButton >= mashButton)nextMashButton ++;
                    }
                    else{
                        deathLevel = 0;
                        deathVelocity = 0;
                        iframes = 90;
                    }
//                    if(lastPressTime != 0){
//                        System.out.println(System.currentTimeMillis() - lastPressTime);
//                    }
//                    lastPressTime = System.currentTimeMillis();
                }
            }
            controller.clearPressed();

            if(deathLevel >= 100){
                deathVelocity = 0;
                deathLevel = 0;
                explosionFrame ++;
                return;
            }
            if(mash){
                if(deathLevel <= 0){
                    deathLevel = 0;
                    deathVelocity = 0;
                    explosionFrame = 0;
                    return;
                }
            }

            lastX = x;
            lastY = y;

            xVel *= 0.97;
            yVel *= 0.97;
            tryToMove(xVel,yVel);
            lastR = rotation;
            rVel = (rotation + spinDirection*(deathLevel)/300f)%(float)(Math.PI*2)-rotation;
            rotation = (rotation + spinDirection*(deathLevel)/300f)%(float)(Math.PI*2);
            hitboxes = new HitboxPoint[1];
            hitboxes[0] = new HitboxPoint((int)x,(int)y,50,0,100,null);
            return;
        }
        
        if(iframes > 0)iframes --;
        
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
            if(modules[i].loop && controller.held(i) && rVel <= maxRotSpeed){
                modules[i].hold();
            }
        }
        for(int i = 0; i < 4; i ++){
            modules[i].tick();
        }
        
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
        MapHitbox hitbox;
        float translateX;
        float translateY;
        boolean collided = false;
        for(int i = 0; i < game.map.hitboxes.size(); i ++){
            hitbox = game.map.hitboxes.get(i);
            translateX = x + moveX;
            translateY = y + moveY;
            switch(hitbox.type){
                case 2:
                    Point new_pos = Calcs.rotatePoint(translateX, translateY, hitbox.x, hitbox.y, -hitbox.rotation);
                    translateX = new_pos.x;
                    translateY = new_pos.y;
                case 0:
                    if(translateX > hitbox.x)translateX = Math.max(hitbox.x+1, translateX-hitbox.width/2);
                    else translateX = Math.min(hitbox.x-1, translateX+hitbox.width/2);
                    if(translateY > hitbox.y)translateY = Math.max(hitbox.y+1, translateY-hitbox.height/2);
                    else translateY = Math.min(hitbox.y-1, translateY+hitbox.height/2);
                    break;
            }
            
            if(Math.pow(translateX-hitbox.x, 2) + Math.pow(translateY-hitbox.y, 2) < Math.pow(40+hitbox.diameter/2+1, 2)){
                
                if(!intersectingMap.contains(i)){
                    collided = true;
                    intersectingMap.add(i);
                }
                double angle = Math.atan2(translateY-hitbox.y, translateX-hitbox.x);
                if(hitbox.type == 2)angle += Math.toRadians(hitbox.rotation);
                collideWithMapHitbox(angle);
            }
            else{
                if(intersectingMap.contains((Integer)i))
                    intersectingMap.remove((Integer)i);
            }
            
        }
        
        if(!collided){
            x += moveX;
            y += moveY;
        }
        
        for (int i = 0; i < game.map.zones.size(); i ++) {
            
            Rectangle zone = game.map.zones.get(i);
            if(!zone.contains(new Point((int)x,(int)y)));
            float oldXVel = xVel;
            float oldYVel = yVel;
            xVel *= -0.6f;
            yVel *= -0.6f;
            if(x < zone.x + 40)x = zone.x + 40;
            else if(x > zone.x + zone.width - 40)x = zone.x + zone.width - 40;
            else {xVel = oldXVel;}
            if(y < zone.y + 40)y = zone.y + 40;
            else if(y > zone.y + zone.height - 40)y = zone.y + zone.height - 40;
            else {yVel = oldYVel;} 
        }
    }
    
    public void collideWithMapHitbox(double collisionAngle){
        double velocity = Math.sqrt(xVel*xVel +yVel*yVel);
        double angle = Math.atan2(yVel,xVel);
        double parallel = Math.cos(angle - collisionAngle)*(velocity);
        double perpendicular = Math.sin(angle - collisionAngle)*velocity;
        double newAngle = Math.atan2(perpendicular, Math.abs(parallel*0.6f)) + collisionAngle;
//        double newAngle = collisionAngle - (angle - collisionAngle)*2;
        xVel = (float)(Math.cos(newAngle)*velocity);
        yVel = (float)(Math.sin(newAngle)*velocity);
        x += xVel;
        y += yVel;
    }

    public void takeDamage(int damage, int type, double direction){
        
        if(deathLevel > 0 || deathVelocity > 0 || iframes > 0){
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
            if(this.damage > 60 && mash){
                deathVelocity = (float)(Math.pow((this.damage-60)*Math.pow(damage,1/2f),1/2f)/12);
                System.out.println(deathVelocity);
                if(deathVelocity < 0.3)deathVelocity = 0;
                else{
                    mashButton = Players.random.nextInt(4);
                }
                if(rVel > 0)spinDirection = 1;
                else spinDirection = -1;
            }
            
            if(this.damage >= 80 && !mash){
                numButtons = 2 + (this.damage - 80 + damage * 2)/10;
                
//                deathVelocity = 1.6f;
                
//                deathVelocity = 1.37f;
                deathVelocity = 0.37f+(this.damage)/220f;
                
                mashButton = Players.random.nextInt(4);
                nextMashButton = Players.random.nextInt(3);
                if(nextMashButton >= mashButton)nextMashButton ++;
                
                if(rVel > 0)spinDirection = 1;
                else spinDirection = -1;
                
                // the fastest I ever got was 176
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
            g.drawImage(Players.charredRemains, (int)x-60,(int)y-60,120,120,null);
            return;
        }
        
        if(explosionFrame > 0){
            g.drawImage(Players.charredRemains, (int)x-60,(int)y-60,120,120,null);
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
        else if(numButtons > 0 || deathVelocity > 0|| deathLevel > 0){
            float dist = deathLevel*8/100f + Players.random.nextFloat((deathLevel+0.01f)*2/100);
            double angle = Players.random.nextDouble(Math.PI*2);
            x2 += Math.cos(angle)*dist;
            y2 += Math.sin(angle)*dist;
            sparkFrame ++;
            if(sparkFrame >= 50)sparkFrame = 0;
            mashFrame ++;
            if(!mash && mashFrame >= 8)mashFrame = 0;
            if(mashFrame >= 9)mashFrame = 0;
        }else if(damage > 34 && damage < 200){
            float dist = Players.random.nextFloat((damage-33.999f)*2/100);
            double angle = Players.random.nextDouble(Math.PI*2);
            x2 += Math.cos(angle)*dist;
            y2 += Math.sin(angle)*dist;
        }
        
        int level = (int)((float)damage/33) - 1;
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
        gPlayer.drawImage(Players.centers[center], -40, -40, null);
        gPlayer.rotate(HIGHLIGHT_ANGLE - rotation);
        gPlayer.drawImage(Players.highlight, -40, -40, 80, 80, null);
        gPlayer.rotate(-HIGHLIGHT_ANGLE + rotation);
        gPlayer.drawImage(Players.centerOverlays[center], -40, -40, null);

        if(level >= 0){
            gPlayer.drawImage(Players.damaged[level][damageFrame], -50, -50, null);
            // 3. Draw the flash animation using SRC_ATOP
            gPlayer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.7f));
            g.translate(x2-x, y2-x);
            float alpha = (float)(Math.sin(System.currentTimeMillis() * 0.01*(level+1)) * 0.35 + 0.35); // Oscillates between 0 and 0.7
            if(iframes > 0)
                alpha = (float)(Math.sin(System.currentTimeMillis() * 0.05) * 0.15 + 0.3);
            gPlayer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            gPlayer.setColor(new Color(255,0,0,Math.min(255,2*(damage-20))));
            if(iframes > 0)
                gPlayer.setColor(new Color(255,255,255));
            
            gPlayer.fillRect(-120, -120, 240, 240); // Fill the entire buffer with red
            g.translate(-x2+x, -y2+x);
            
            damageFrame++;
            if (damageFrame >= 24) damageFrame = 0;
        }
        gPlayer.dispose();

        g.drawImage(Players.shadow, (int)x2-50,(int)y2-50, null);
        // 4. Draw the result to the main graphics context
        g.drawImage(playerBuffer, (int)x2 - 120, (int)y2 - 120, null);

        // 5. Draw the damage overlay as before (optional)
//        if(numButtons > 0 || deathVelocity > 0 || deathLevel > 0){
//            x2 += (x-x2)*0.6f;
//            y2 += (y-y2)*0.6f;
//            
//            g.translate(x2,y2);
//            if(mash)
//                g.drawImage(Players.mash[mashButton][mashFrame], -20,-20, null);
//            else{
//                g.drawImage(Players.skillCheckBack, -35,-35, null);
//                g.drawImage(Players.skillCheck[mashButton][mashFrame], -35,-35, null);
//                if(numButtons > 1)
//                    g.drawImage(Players.skillCheckNext[nextMashButton], -35,-35, null);
//            }
//            if(deathLevel > 40)
//                g.drawImage(Players.sparks[sparkFrame], -60,-60, null);
//            g.translate(-x2,-y2);
//        }
            
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
    public void renderForeground(Graphics2D g){
        
        if(!alive || explosionFrame > 0){
            return;
        }
        if(numButtons > 0 || deathVelocity > 0 || deathLevel > 0){
            
            g.translate(x,y);
            if(mash)
                g.drawImage(Players.mash[mashButton][mashFrame], -20,-20, null);
            else{
                g.drawImage(Players.skillCheckBack, -35,-35, null);
                g.drawImage(Players.skillCheck[mashButton][mashFrame], -35,-35, null);
                if(numButtons > 1)
                    g.drawImage(Players.skillCheckNext[nextMashButton], -35,-35, null);
            }
            if(deathLevel > 40)
                g.drawImage(Players.sparks[sparkFrame], -60,-60, null);
            g.translate(-x,-y);
        }
    }
}

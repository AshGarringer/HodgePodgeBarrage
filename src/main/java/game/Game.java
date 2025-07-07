package game;

import engine.framework.Engine;
import engine.framework.MultiState;
import engine.graphics.Text;
import engine.graphics.Textures;
import engine.input.ControllerHandler;
import engine.input.SnesController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author cookibot
 */
public class Game extends Engine {
    
    public static int WINDOW_WIDTH = 1600;
    public static int WINDOW_HEIGHT = 900;
    
    Module[] modules;
    MultiState state;

    ControllerHandler controllerHandler;

    ArrayList<SnesController> controllers;
    ArrayList<Player> players;
    ArrayList<Integer[]> playerSelections;
    ArrayList<Integer> centerSelections;
    ArrayList<Float> playerMiceAcc;
    ArrayList<Float[]> playerMice;
    ArrayList<Integer> playerHover;
    ArrayList<Projectile> projectiles;

    Text text;
    Text text2;
    Text text3;

    int timer;
    
    int winner;
    
    Random random;
    
    BufferedImage map;

    public Game(){
        init();
        map =  Textures.loadImage("/textures/maps/demo.png");
        this.start("HodgePodgeRobotBarrage", 1600, 900, true);
    }
    
    public void init(){
        text = new Text("Regular",40,Color.white);
        text2 = new Text("Regular2",30,Color.white, 5, Color.black, true);
        text3 = new Text("Fancy",80,Color.black,5, Color.white, true);
        random = new Random();
        state = new MultiState(0);
        modules = Modules.getModules();
        Players.loadImages();
        ModuleSelect.loadImages();
        players = new ArrayList<>();
        playerSelections = new ArrayList<>();
        centerSelections = new ArrayList<>();
        playerMiceAcc = new ArrayList<>();
        playerMice = new ArrayList<>();
        playerHover = new ArrayList<>();
        controllers = new ArrayList<>();
        projectiles = new ArrayList<>();
        timer = 1;
        winner = -1;
    }

    @Override
    public void tick() {
        state.update();
        if(state.state() <= 1 || state.state() == 4){
            timer --;
            if(timer == 0){
                getPlayers();
                while(controllers.size() > players.size()){
                    players.add(new Player(this));
                    playerSelections.add(new Integer[]{-1,-1,-1,-1});
                    centerSelections.add(0);
                    playerMiceAcc.add(0f);
                    playerMice.add(new Float[]{1220f + (playerMice.size() % 2) * 1160, 375f + (playerMice.size() / 2) * 650});
                    playerHover.add(-1);
                }
                timer = 20;
            }
        }
        for (SnesController controller : controllers) {
            controller.update();
        }
        switch (state.state()) {
            case 0:
                // main menu
                if(!state.isTransit()){
                    for (SnesController controller : controllers) {
                        if (controller.pressed(SnesController.START)) {
                            state.transition(20, 2, 60);
                        }
                        if (controller.pressed(SnesController.A)) {
                            state.transition(20, 2, 60);
                        }
                        controller.clearPressed();
                    }
                }
                break;
            case 2:
                if(state.isTransit())break;
                if(checkKonami(controllers)!= -1){
                    for(int i = 0; i < controllers.size(); i ++){
                        for(int j = 0; j < 4; j ++){
                            if(playerSelections.get(i)[j] < 0)playerSelections.get(i)[j] = random.nextInt(Modules.NUM_MODULES);
                        }
                    }
                }
                for (int i = 0; i < controllers.size(); i ++) {
                    SnesController controller = controllers.get(i);
                    if (controller.pressed(SnesController.START)) {
                        boolean allSelected = true;
                        for(int k = 0; k < controllers.size(); k ++){
                            for(int j = 0; j < 4; j ++){
                                if(playerSelections.get(k)[j] < 0)allSelected = false;
                            }
                        }
                        if(allSelected){
                            for(int j = 0; j < controllers.size(); j ++){
                                players.get(j).init(Modules.loadModuleSelection(playerSelections.get(j)),
                                        centerSelections.get(j), j);
                                
                            System.out.println(Modules.loadModuleSelection(playerSelections.get(j)).length);
                            }
                            state.transition(0, 3, 0);
                            return;
                        }
                    }
                    
                    if(controller.pressed(SnesController.LTRIGGER)){
                        centerSelections.set(i, (centerSelections.get(i) -1 + Players.centers.length)%Players.centers.length);
                    }
                    if(controller.pressed(SnesController.RTRIGGER)){
                        centerSelections.set(i, (centerSelections.get(i) +1)%Players.centers.length);
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
                    if(controller.held(SnesController.LEFT) != controller.held(SnesController.RIGHT) ||
                            controller.held(SnesController.UP) != controller.held(SnesController.DOWN)){
                        playerMiceAcc.set(i,playerMiceAcc.get(i)+(10-playerMiceAcc.get(i))*0.05f);
                    }
                    else{
                        playerMiceAcc.set(i,playerMiceAcc.set(i,0f));
                    }
                    float sp = playerMiceAcc.get(i);
                    if(xintent != 0 && yintent != 0){
                        sp = Math.round(playerMiceAcc.get(i)/Math.sqrt(2));
                    }
                    playerMice.get(i)[0] += sp*xintent;
                    playerMice.get(i)[1] += sp*yintent;

                    // Get mouse position for this player
                    float mouseX = playerMice.get(i)[0];
                    float mouseY = playerMice.get(i)[1];

                    // Determine which module (if any) is hovered
                    int hoveredModule = -1;
                    for (int m = 0; m < modules.length; m++) {
                        int col = m % 2;
                        int row = m / 2;
                        int x = 1530 + col * 270;
                        int y = 100 + row * 200;
                        if (mouseX >= x && mouseX < x + 270 && mouseY >= y && mouseY < y + 200) {
                            hoveredModule = m;
                            break;
                        }
                    }
                    playerHover.set(i, hoveredModule);

                    // Handle selection
                    if (hoveredModule != -1 && controller.pressed(SnesController.A)) {
                        Integer[] selections = playerSelections.get(i);
                        // Find the first empty slot (-1)
                        for (int s = 0; s < selections.length; s++) {
                            if (selections[s] == -1) {
                                selections[s] = hoveredModule;
                                break;
                            }
                        }
                        // Optionally, update the array in the list (not needed if it's the same object)
                        playerSelections.set(i, selections);
                    }
                    else if (controller.pressed(SnesController.B)){
                        Integer[] selections = playerSelections.get(i);
                        // Remove the last selected module
                        for (int s = selections.length - 1; s >= 0; s--) {
                            if (selections[s] != -1) {
                                selections[s] = -1;
                                break;
                            }
                        }
                        playerSelections.set(i, selections);
                    }
                    controller.clearPressed();
                }
                break;
            case 3:
                if(checkKonami(controllers)!= -1){
                    Player.DRAW_HITBOXES = !Player.DRAW_HITBOXES;
                }
                int playersRemaining = 0;
                boolean restart = false;
                for (int i = 0; i < players.size(); i++) {
                    if(i == winner && controllers.get(i).pressed(SnesController.START))restart = true;
                    players.get(i).tickGame(controllers.get(i));
                    if(players.get(i).alive)playersRemaining++;
                }
                if(winner == -1 && playersRemaining == 1){
                    for (int i = 0; i < players.size(); i++) {
                        if(players.get(i).alive)winner = i;
                    }
                }
                int tbr = -1;
                for(int i = 0; i < projectiles.size(); i ++){
                    projectiles.get(i).tick();
                    if(projectiles.get(i).timer <= 0)tbr = i;
                }
                if(tbr >= 0)projectiles.remove(tbr);
                if(restart){
                    init();
                }
                break;
            case 4:
                // pause game
                break;
            case 5:
                // aftermath
                break;
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = window.getWidth();
        int height = window.getHeight();

        switch (state.state()) {
            case 0:
                // main menu
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, width, height);
                text3.drawString(width/2, height/2 - 130, "HODGE PODGE", g);
                text3.drawString(width/2, height/2 - 30, "ROBOT BARRAGE", g);
                text.drawString(width/2, height/2 + 50, "(temporary menu)", g);

                for(int i = 0; i < players.size(); i ++){
                    text.drawString((int)((width/5f) *(i+1)), height - 40, "Player "+Integer.toString(i+1), g);
                }
                if(state.isTransit()){
                    g.setColor(new Color(0,0,0,(int)(255*state.getTransit())));
                    
                    g.fillRect(0, 0, width, height);
                }
                break;
            case 2:
                this.setHints(g);
                float scale = window.getHeight() / 1400f;
                float offset = (window.getWidth() / scale - 3600) / 2;

                g.scale(scale, scale);
                g.translate(offset, 0);

                g.drawImage(ModuleSelect.background, 0, 0, null);

                for (int i = 0; i < modules.length; i++) {
                    int col = i % 2;
                    int row = i / 2;
                    int x = 1530 + col * 270;
                    int y = 100 + row * 200;   

                    BufferedImage preview = ModuleSelect.slotPreviews[i];
                    g.drawImage(preview, x, y, 270, 200, null);
                }
               
                for (int i = 0; i < players.size(); i++) {
                    int col = i % 2;
                    int row = i / 2;

                    int x = 1220 + col * 1160;
                    int y = 375 + row * 650;

                    g.drawImage(ModuleSelect.playerShadow, x-225,y-225,null);
                    drawPlayerPreview(g, x, y, i, state.getTransit() >= 0);
                }
                g.drawImage(ModuleSelect.overlay,0,0,null);

                if(state.getTransit() < 0){
                    g.drawImage(ModuleSelect.unroll, -400 + (int)(Math.cos((-Math.PI/2)*state.getTransit())*4000), 0, 4000, 1400, this);
                }
                g.translate(offset, 0);
                g.scale(scale, scale);
                // 1220, 375

                // module select
                break;
            case 3:

                g.setColor(Color.white);
                g.fillRect(0, 0, width, height);
                
                g.translate(width / 2, height / 2);
                g.drawImage(map, -WINDOW_WIDTH/2 -50, -WINDOW_HEIGHT/2-50,null);
                drawStats(g);
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).renderGame(g);
                }
                checkCollisions();
                if(winner >= 0){
                    text2.drawString(0, 0, "PLAYER "+(winner+1)+" WINS!", g);
                }
                
                for (Projectile projectile : projectiles) {
                    g.drawImage(projectile.image, (int)projectile.x,(int)projectile.y, null);
                }
                break;
            case 4:
                // aftermath
                break;
        }
    }
    public void drawPlayerPreview(Graphics2D g, int centerX, int centerY, int playerNum, boolean drawMouse) {
        g.translate(centerX, centerY);

        if (Players.centersTilted[centerSelections.get(playerNum)])
            g.rotate(-Math.PI / 4f);

        int hovered = playerHover.get(playerNum);
        boolean previewDrawn = false;
        
        for (int i = 0; i < 4; i++) {
            if (playerSelections.get(playerNum)[i] >= 0)
                g.drawImage(ModuleSelect.eqippedPreviews[playerSelections.get(playerNum)[i]], -160, -330, null);
            else if(!previewDrawn && hovered >= 0 ){
                previewDrawn = true;
                Composite oldComp = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g.drawImage(ModuleSelect.eqippedPreviews[hovered], -160, -330, null);
                g.setComposite(oldComp);
            }
            g.rotate(Math.PI / 2);
        }
        if (Players.centersTilted[centerSelections.get(playerNum)])
            g.rotate(Math.PI / 4f);

        g.drawImage(ModuleSelect.playerPreviews[centerSelections.get(playerNum)], -160, -160, null);

        g.translate(-centerX, -centerY);
        if (drawMouse)
            g.drawImage(ModuleSelect.cursors[playerNum], Math.round(playerMice.get(playerNum)[0]) - 100,
                    Math.round(playerMice.get(playerNum)[1]) - 100, 200, 200, null);
    }

    public void drawStats(Graphics g) {

        int width = window.getWidth();
        int height = window.getHeight();
        g.translate(-(int)(width/2f), -(int)(height/2f));

        text2.drawString(10, 10, (String) ("Player 1: " + players.get(0).damage).replace('0', 'O'), 1, -1, g);
        if(players.size() > 1){
            text2.drawString(width -10, 10, (String) ("Player 2: " + players.get(1).damage).replace('0', 'O'), -1, -1, g);
            if(players.size() > 2){
                text2.drawString(10, height-10, (String) ("Player 3: " + players.get(2).damage).replace('0', 'O'), 1, 1, g);
                if(players.size() > 3)
                text2.drawString(width-10, height-10, (String) ("Player 4: " + players.get(3).damage).replace('0', 'O'), -1, 1, g);
            }
        }
        
        g.translate((int)(width/2f),(int)(height/2f));

    }
//
//    public void checkCollisions() {
//
//        for (int i = 0; i < players.size(); i++) {
//            Player p1 = players.get(i);
//            if (p1.hitboxes == null || !p1.alive)
//                continue;
//            for (int j = i + 1; j < players.size(); j++) {
//                Player p2 = players.get(j);
//                if (p2.hitboxes == null || !p1.alive)
//                    continue;
//
//                boolean playersCollided = false;
//                HitboxPoint p1defend = null;
//                HitboxPoint p2attack = null;
//                HitboxPoint p2defend = null;
//                HitboxPoint p1attack = null;
//                boolean noCollision = false;
//
//                for (int h1 = 0; h1 < p1.hitboxes.length; h1++) {
//                    HitboxPoint hb1;
//                    if(h1 < p1.hitboxes.length) hb1 = p1.hitboxes[h1];
//                    else hb1 = p1.projectiles.get(h1-p1.hitboxes.length).getHitbox();
//                    for (int h2 = 0; h2 < p2.hitboxes.length + p2.projectiles.size(); h2++) {
//                        HitboxPoint hb2;
//                        if(h2 < p2.hitboxes.length)hb2 = p2.hitboxes[h2];
//                        else hb2 = p2.projectiles.get(h2-p2.hitboxes.length).getHitbox();
//
//                        // Check collision
//                        double dx = hb1.x - hb2.x;
//                        double dy = hb1.y - hb2.y;
//                        double distSq = dx * dx + dy * dy;
//                        double radSum = hb1.radius + hb2.radius;
//                        if (distSq < radSum * radSum) {
//
//                            if((hb1.type == 0 || hb1.type == 4 )&& hb2.projectileParent != null){
//                                if(hb1.type == 0){
//                                    p1.takeDamage((int) (hb2.intensity * hb1.intensity/ 100),
//                                            6, hb2.projectileParent.rotation);
//                                    hb2.projectileParent.timer = -1;
//                                }
//                                else{
//                                    hb2.projectileParent.timer = -1;
//                                    p1.projectiles.add(hb2.projectileParent.reflect());
//                                }
//                            }
//                            else if((hb2.type == 0  || hb2.type == 4 )&& hb1.projectileParent != null){
//                                if(hb2.type == 0){
//                                    p2.takeDamage((int) (hb1.intensity * hb2.intensity/ 100),
//                                            6, hb1.projectileParent.rotation);
//                                    hb1.projectileParent.timer = -1;
//                                }
//                                else{
//                                    hb1.projectileParent.timer = -1;
//                                    p2.projectiles.add(hb1.projectileParent.reflect());
//                                }
//                            }
//                            else{
//                                playersCollided = true;
//                                
//                                if ((hb1.type == 1 || hb1.type == 10) && (hb2.type == 0 || hb2.type == 2)) {
//                                    if (p1attack == null || ((hb1.intensity > p1attack.intensity || hb2.type == 2) && p2defend.type != 2)) {
//                                        p1attack = hb1;
//                                        p2defend = hb2;
//                                    }
//                                }
//                                if ((hb1.type == 0|| hb1.type == 2) && (hb2.type == 1 || hb2.type == 10)) {
//                                    if (p2attack == null || ((hb2.intensity > p2attack.intensity || hb1.type == 2) && p1defend.type != 2)) {
//                                        p2attack = hb2;
//                                        p1defend = hb1;
//                                    }
//                                }
//                            }
//                            // collide
//                        }
//                    }
//                }
//
//                if (playersCollided) {
//
//                    if (!p1.intersecting.contains(p2)) {
//                        p1.intersecting.add(p2);
//                    }
//                    if (!p2.intersecting.contains(p1)) {
//
//                        p2.intersecting.add(p1);
//
//                        
//                        if (p1attack != null && p2defend != null && p1attack.type == 10) {
//                            double rotation = Math.atan2(p2.y - p1.y, p2.x - p1.x);
//                            int damage = 5;
//                            if (p1attack.radius > 90)
//                                damage = 20;
//                            p2.takeDamage((int) (p1attack.intensity * p2defend.intensity * damage / 10000),
//                                    p1attack.type, rotation);
//                            p2.rVel = -p1.rVel / 3;
//                            continue;
//                        }
//                        if (p2attack != null && p1defend != null && p2attack.type == 10) {
//                            double rotation = Math.atan2(p1.y - p2.y, p1.x - p2.x);
//                            int damage = 5;
//                            if (p2attack.radius > 90)
//                                damage = 15;
//                            p1.takeDamage((int) (p2attack.intensity * p1defend.intensity * damage / 10000),
//                                    p2attack.type, rotation);
//                            p1.rVel = -p2.rVel / 3;
//                            continue;
//                        }
//
//                        p1.revertPos();
//                        p2.revertPos();
//                        float p1x = p1.xVel;
//                        float p1y = p1.yVel;
//                        p1.xVel = p2.xVel;
//                        p1.yVel = p2.yVel;
//                        p2.xVel = p1x;
//                        p2.yVel = p1y;
//                        float p1r = p1.rVel;
//                        p1.rVel = -p2.rVel;
//                        p2.rVel = -p1r;
//                        
//                        if (p1attack != null && p2defend != null) {
//                            if(p2defend.type == 2){
//                                int moduleNum = getModuleIndexForHitbox(p2, p2defend);
//                                double rotation = p2.rotation + moduleNum * Math.PI / 2 - Math.PI / 2;
//                                p1.takeDamage(
//                                        (int) (p1attack.intensity  * p1attack.parent.damage / 100),
//                                        p2defend.type, rotation);
//                            }
//                            else{
//                                int moduleNum = getModuleIndexForHitbox(p1, p1attack);
//                                double rotation = p1.rotation + moduleNum * Math.PI / 2 - Math.PI / 2;
//                                p2.takeDamage(
//                                        (int) (p1attack.intensity * p2defend.intensity * p1attack.parent.damage / 10000),
//                                        p1attack.type, rotation);
//                                p1.addPause(
//                                        (int) (p1attack.intensity * p2defend.intensity * p1attack.parent.damage / 10000));
//                            }
//                        }
//                        if (p2attack != null && p1defend != null) {
//                            if(p1defend.type == 2){
//                                int moduleNum = getModuleIndexForHitbox(p1, p1defend);
//                                double rotation = p1.rotation + moduleNum * Math.PI / 2 - Math.PI / 2;
//                                p2.takeDamage(
//                                        (int) (p2attack.intensity  * p2attack.parent.damage / 100),
//                                        p1defend.type, rotation);
//                            }
//                            else{
//                                int moduleNum = getModuleIndexForHitbox(p2, p2attack);
//                                double rotation = p2.rotation + moduleNum * Math.PI / 2 - Math.PI / 2;
//                                p1.takeDamage(
//                                        (int) (p2attack.intensity * p1defend.intensity * p2attack.parent.damage / 10000),
//                                        p2attack.type, rotation);
//                                p2.addPause(
//                                        (int) (p2attack.intensity * p1defend.intensity * p2attack.parent.damage / 10000));
//                            }
//                        }
//                    }
//
//                } else {
//                    if (p1.intersecting.contains(p2)) {
//                        p1.intersecting.remove(p2);
//                    }
//                    if (p2.intersecting.contains(p1)) {
//                        p2.intersecting.remove(p1);
//                    }
//                }
//            }
//        }
//    }
    
    private boolean hitboxesCollide(HitboxPoint a, HitboxPoint b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double radSum = a.radius + b.radius;
        return dx * dx + dy * dy < radSum * radSum;
    }

    private void handleProjectileCollision(Player player, HitboxPoint hb, Projectile proj) {
        // Hurtbox (0) takes damage, Reflector (4) reflects
        if (hb.type == 0) {
            player.takeDamage((int)(proj.damage * hb.intensity / 100), 6, proj.rotation);
            proj.timer = -1;
        } else if (hb.type == 4) {
            proj.timer = -1;
            projectiles.add(new Projectile(proj.x, proj.y, -proj.xVel, -proj.yVel, proj.image, proj.rotation + Math.PI, player.playerNum));
        }
    }

    private void handlePlayerCollision(Player attacker, Player defender, HitboxPoint atk, HitboxPoint def) {
        int atkModule = getModuleIndexForHitbox(attacker, atk);
        int defModule = getModuleIndexForHitbox(defender, def);
        double atkDir = attacker.rotation + atkModule * Math.PI / 2 - Math.PI / 2;
        double defDir = defender.rotation + defModule * Math.PI / 2 - Math.PI / 2;

        if (def.type == 2) { // Shield
            attacker.takeDamage((int)(atk.intensity * atk.parent.damage / 100), def.type, defDir);
        } else {
            int dmg = (int)(atk.intensity * def.intensity * atk.parent.damage / 10000);
            defender.takeDamage(dmg, atk.type, atkDir);
            attacker.addPause(dmg);
        }
    }

    private boolean isAttack(HitboxPoint hb) {
        return hb.type == 1 || hb.type == 10;
    }
    public void checkCollisions() {
    // Player vs Player
        for (int i = 0; i < players.size(); i++) {
            Player p1 = players.get(i);
            if (p1.hitboxes == null || !p1.alive) continue;
            for (int j = i + 1; j < players.size(); j++) {
                Player p2 = players.get(j);
                if (p2.hitboxes == null || !p2.alive) continue;

                boolean collided = false;
                HitboxPoint p1Attack = null, p1Defend = null, p2Attack = null, p2Defend = null;

                for (HitboxPoint hb1 : p1.hitboxes) {
                    for (HitboxPoint hb2 : p2.hitboxes) {
                        if (!hitboxesCollide(hb1, hb2)) continue;

                        // Attack/Defend logic
                        if ((hb1.type == 1 || hb1.type == 10) && (hb2.type == 0 || hb2.type == 2)) {
                            if (p1Attack == null || hb1.intensity > p1Attack.intensity || hb2.type == 2) {
                                p1Attack = hb1;
                                p2Defend = hb2;
                            }
                        }
                        if ((hb2.type == 1 || hb2.type == 10) && (hb1.type == 0 || hb1.type == 2)) {
                            if (p2Attack == null || hb2.intensity > p2Attack.intensity || hb1.type == 2) {
                                p2Attack = hb2;
                                p1Defend = hb1;
                            }
                        }
                        collided = true;
                    }
                }

                if (collided) {
                    if (!p1.intersecting.contains(p2)) p1.intersecting.add(p2);
                    if (!p2.intersecting.contains(p1)) p2.intersecting.add(p1);
                    else continue;
                    // Special attack type 10
                    if (p1Attack != null && p2Defend != null && p1Attack.type == 10) {
                        double rot = Math.atan2(p2.y - p1.y, p2.x - p1.x);
                        int dmg = (p1Attack.radius > 90) ? 20 : 5;
                        p2.takeDamage((int)(p1Attack.intensity * p2Defend.intensity * dmg / 10000), p1Attack.type, rot);
                        p2.rVel = -p1.rVel / 3;
                        continue;
                    }
                    if (p2Attack != null && p1Defend != null && p2Attack.type == 10) {
                        double rot = Math.atan2(p1.y - p2.y, p1.x - p2.x);
                        int dmg = (p2Attack.radius > 90) ? 15 : 5;
                        p1.takeDamage((int)(p2Attack.intensity * p1Defend.intensity * dmg / 10000), p2Attack.type, rot);
                        p1.rVel = -p2.rVel / 3;
                        continue;
                    }

                    // Swap velocities and revert positions
                    p1.revertPos(); p2.revertPos();
                    float tmpX = p1.xVel, tmpY = p1.yVel, tmpR = p1.rVel;
                    p1.xVel = p2.xVel; p1.yVel = p2.yVel; p1.rVel = -p2.rVel;
                    p2.xVel = tmpX; p2.yVel = tmpY; p2.rVel = -tmpR;

                    // Handle attack/defend
                    if (p1Attack != null && p2Defend != null) handlePlayerCollision(p1, p2, p1Attack, p2Defend);
                    if (p2Attack != null && p1Defend != null) handlePlayerCollision(p2, p1, p2Attack, p1Defend);
                } else {
                    p1.intersecting.remove(p2);
                    p2.intersecting.remove(p1);
                }
            }
        }
        for (Projectile proj : new ArrayList<>(projectiles)) {
            if (proj.timer <= 0) continue;
            for (int i = 0; i < players.size(); i++) {
                if (i == proj.parent) continue; // Immune to own projectiles
                Player player = players.get(i);
                if (player.hitboxes == null || !player.alive) continue;

                boolean attackCollision = false;

                // First pass: check for attack hitbox collision
                for (HitboxPoint hb : player.hitboxes) {
                    if (hitboxesCollide(hb, proj.getHitbox()) && isAttack(hb)) {
                        attackCollision = true;
                        break;
                    }
                }

                if (attackCollision) {
                    proj.timer = -1;
                    players.get(i).xVel += proj.xVel * 0.5f; // Apply some knockback
                    players.get(i).yVel += proj.yVel * 0.5f; // Apply some knockback
                    continue; // Don't apply damage or reflect
                }

                // Second pass: check for other collisions (vulnerable, reflector, etc)
                for (HitboxPoint hb : player.hitboxes) {
                    if (hitboxesCollide(hb, proj.getHitbox())) {
                        handleProjectileCollision(player, hb, proj);
                        break;
                    }
                }
            }
        }
    }
    
    public boolean handleError(Exception e){
        init();
        return true;
    }

    private int getModuleIndexForHitbox(Player player, HitboxPoint hitbox) {
        int offset = 1; // skip the main body hitbox at index 0
        for (int m = 0; m < player.modules.length; m++) {
            HitboxPoint[] frame = player.modules[m].getHitbox();
            for (int j = 0; j < frame.length; j++) {
                if (offset < player.hitboxes.length && player.hitboxes[offset] == hitbox) {
                    return m;
                }
                offset++;
            }
        }
        return 0; // fallback to 0 if not found
    }

    public void getPlayers() {
        // load new controllers
        controllers = SnesController.getControllers();
    }
    
    private final ArrayList<Integer> KonamiCodes = new ArrayList<Integer>();
    
    private static final Integer[] KONAMI_CODE = new Integer[]{
        4,4,5,5,6,7,6,7,2,1
    };
    private int checkKonami(ArrayList<SnesController> controllers){
        
        while(KonamiCodes.size() < controllers.size()){
            KonamiCodes.add(0);
        }
        for(int i = 0; i < controllers.size(); i ++){
            if(controllers.get(i).pressed(KONAMI_CODE[KonamiCodes.get(i)])){
                KonamiCodes.set(i, KonamiCodes.get(i)+1);
            }
            else{
                for(int j = 0; j <= 3; j ++){
                    if(controllers.get(i).pressed(j))KonamiCodes.set(i, 0);
                }
                for(int j = 8; j <= 11; j ++){
                    if(controllers.get(i).pressed(j))KonamiCodes.set(i, 0);
                }
            }
            if(KonamiCodes.get(i) == 10){
                KonamiCodes.set(i, 0);
                return i;
            }
            
        }
        
        return -1;
    }
}
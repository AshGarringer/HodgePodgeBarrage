package game;

import game.maps.Map;
import engine.framework.Engine;
import engine.framework.MultiState;
import engine.graphics.Text;
import engine.graphics.Textures;
import engine.input.SnesController;
import engine.sound.LwjglAudioManager;
import engine.sound.SoundPlayer;
import static game.MainMenu.animation;

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
    
    ArrayList<Player> players;
    ArrayList<Projectile> projectiles;
    Map map;

    Text text;
    Text text2;
    Text text3;
    
    boolean restart = false;
    int winner;
    
    Random random;
    
    private LwjglAudioManager audioManager;
    Thread load;
    boolean loaded;

    public Game(){
        init();
        this.start("HodgePodgeRobotBarrage", 1600, 900, true);
    }
    
    public final void init(){
        state = new MultiState(0);
        text = new Text("Regular",40,Color.white);
        load = new Thread(){
            @Override
            public void run(){
                SnesController.init();
                text = new Text("Regular",40,Color.white);
                text2 = new Text("Regular2",30,Color.white, 5, Color.black, true);
                text3 = new Text("Fancy",80,Color.black,5, Color.white, true);
                random = new Random();
                modules = Modules.getModules();
                Players.loadImages();
                ModuleSelect.loadImages();
                MapSelect.loadImages();
                MainMenu.load();
                players = new ArrayList<>();
                projectiles = new ArrayList<>();
                winner = -1;
                audioManager = new LwjglAudioManager();
                loaded = true;
            }
        };
        load.start();
    }

    @Override
    public void tick() {
        
        ArrayList<Integer> controllerIds = new ArrayList<>();
        if(loaded){
            audioManager.update();
            controllerIds = SnesController.updateControllers();
        }
        if(restart){
            players = new ArrayList<>();
            state.transition(0, 2,60);
            restart = false;
            while(controllerIds.size() > players.size()){
                players.add(new Player(this, controllerIds.get(players.size()),players.size()));
                players.get(players.size() -1).initMenu();
            }
            winner = -1;
            MapSelect.reset();
        }
        state.update();
        
        if(consecutiveErrors > 0)consecutiveErrors --;
        
        switch (state.state()) {
            case 0:
                if(loaded)state.transition(0, 0);
                break;
            case 1:
                // main menu
                if(state.isTransit())break;
//                if(MainMenu.animation.getFrame() == 60)
//                    audioManager.playMusic("ThemeIntro.ogg", "ThemeLoop.ogg");
                for(Integer id : controllerIds){
                    if(SnesController.getButtonHeld(id, SnesController.START)){
                        state.transition(80, 2,60);
                        while(controllerIds.size() > players.size()){
                            players.add(new Player(this, controllerIds.get(players.size()),players.size()));
                            players.get(players.size() -1).initMenu();
                        }
                    }
                }
                break;
            case 2:
                while(controllerIds.size() > players.size()){
                    players.add(new Player(this, controllerIds.get(players.size()),players.size()));
                    players.get(players.size() -1).initMenu();
                }
//                if(state.getTransit() == -1)
//                    audioManager.playMusic("HPRBMenuIntro.ogg", "HPRBMenuLoop.ogg");
                if(state.isTransit())break;
                if(checkKonami() == true){
                    for (Player player : players) {
                        for(int j = 0; j < 4; j ++){
                            if(player.moduleSelections[j] < 0)
                                player.moduleSelections[j] = random.nextInt(Modules.NUM_MODULES);
                        }
                    }
                }
                for (int i = 0; i < players.size(); i ++) {
                    SnesController controller = players.get(i).controller;
                    players.get(i).tickModule();
                    if (controller.pressed(SnesController.START)) {
                        boolean allSelected = true;
                        for (Player player : players) {
                            for(int j = 0; j < 4; j ++){
                                if(player.moduleSelections[j] < 0)allSelected = false;
                            }
                        }
                        if(allSelected){
                            state.transition(60, 3,30);
                            return;
                        }
                    }
                }
                break;
            case 3:
                for (int i = 0; i < players.size(); i ++) {
                    SnesController controller = players.get(i).controller;
                    controller.clearPressed();
                    controller.update();
                    if(controller.held(SnesController.UP))
                        MapSelect.mice[i].y -= 12;
                    if(controller.held(SnesController.DOWN))
                        MapSelect.mice[i].y += 12;
                    if(controller.held(SnesController.LEFT))
                        MapSelect.mice[i].x -= 12;
                    if(controller.held(SnesController.RIGHT))
                        MapSelect.mice[i].x += 12;
                    
                    if (controller.pressed(SnesController.A))
                        for(int j = 0; j < Map.NUM_MAPS; j ++)
                            if(new Rectangle(1565,290 + 280* j,470,270).contains(MapSelect.mice[i]))
                                MapSelect.selected = j;
                    
                    if (controller.pressed(SnesController.START)) {
                        if(MapSelect.selected != -1){
                            for (Player player : players) {
                                player.initGame();
                            }
                            map = Map.getMaps()[MapSelect.selected];
                            state.transition(20, 4,30);
                            return;
                        }
                    }
                }
                break;
            case 4:
                if(state.isTransit())break;
                if(checkKonami()){
                    Player.DRAW_HITBOXES = !Player.DRAW_HITBOXES;
                }
                int playersRemaining = 0;
                for (int i = 0; i < players.size(); i++) {
                    if((i == winner || winner == 5 ) && players.get(i).controller.pressed(SnesController.START))restart = true;
                    players.get(i).tickGame();
                    if(players.get(i).alive)playersRemaining++;
                }
                if(winner == -1 && playersRemaining == 1){
                    for (int i = 0; i < players.size(); i++) {
                        if(players.get(i).alive)winner = i;
                    }
                }
                if(playersRemaining == 0)winner = 5;
                int tbr = -1;
                for(int i = 0; i < projectiles.size(); i ++){
                    Projectile proj = projectiles.get(i);
                    proj.tick();
                    if(projectiles.get(i).timer <= 0)tbr = i;
                }
                if(tbr >= 0)projectiles.remove(tbr);
                map.tick();
                break;
            case 5:
                // pause game
                break;
            case 6:
                // aftermath
                break;
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = window.getWidth();
        int height = window.getHeight();

        g.setClip(0, 0, width, height);
            
        switch (state.state()) {
            case 0:
                g.setColor(Color.black);
                g.fillRect(0, 0, width, height);
                text.drawString(width/2, height/2, "Loading", g);
                break;
            case 1:
                // main menu
                this.setHints(g);
                MainMenu.render(g,this,width,height, state.getTransit());
                break;

            case 2:
                this.setHints(g);
                float scale2 = window.getHeight() / 1400f;
                float offset2 = (window.getWidth() / scale2 - 3600) / 2;

                g.scale(scale2, scale2);
                g.translate(offset2, 0);

                g.drawImage(ModuleSelect.background, 0, 0, null);

                for (int i = 0; i < modules.length; i++) {
                    int col = i % 2;
                    int row = i / 2;
                    int x = 1530 + col * 270;
                    int y = 100 + row * 200;   

                    BufferedImage preview = ModuleSelect.slotPreviews[i];
                    g.drawImage(preview, x, y, 270, 200, null);
                }
               
                for (Player player : players) {
                    player.drawPlayerPreview(g, state.getTransit() >= 0);
                }
                g.drawImage(ModuleSelect.overlay,0,0,null);

                if(state.getTransit() < 0){
                    g.drawImage(ModuleSelect.unroll, -400 + (int)(Math.cos((-Math.PI/2)*state.getTransit())*4000), 0, 4000, 1400, this);
                }
                if(state.getTransit() > 0){
                    g.drawImage(ModuleSelect.unroll, -400 + (int)(Math.cos((-Math.PI/2)*state.getTransit())*4000), 0, 4000, 1400, this);
                }
                g.translate(offset2, 0);
                g.scale(scale2, scale2);
                // 1220, 375

                // module select
                break;
            case 3:
                this.setHints(g);
                float scale3 = window.getHeight() / 1400f;
                float offset3 = (window.getWidth() / scale3 - 3600) / 2;

                g.scale(scale3, scale3);
                g.translate(offset3, 0);
                
                g.drawImage(MapSelect.background, 0, 0, null);
                
                for(int i = 0; i < Map.NUM_MAPS; i ++){
                    g.drawImage(MapSelect.slotPreviews[i], 1565,290 + 280* i, this);
                    if(MapSelect.selected == i){
                        g.drawImage(MapSelect.slotPreviews[i], 1565 - 20,290 + 280* i - 20,510,310, this);
                    }
                }
                
                for(int i = 0; i < players.size(); i ++){
                    g.drawImage(MapSelect.cursors[i],MapSelect.mice[i].x - 100,MapSelect.mice[i].y - 100,200,200,null);
                }
                
                if(state.getTransit() != 0){
                    Textures.fillRect(0, 0, 3500, 1400, new Color(0, 0, 0, Math.abs((int)(state.getTransit()*255f))), g,true);
                }
                
                g.translate(offset3, 0);
                g.scale(scale3, scale3);
                break;
            case 4:
                
                g.setColor(Color.white);
                g.fillRect(0, 0, width, height);
                
                g.translate(width / 2, height / 2);
                map.drawBackground(g);
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).renderBackground(g);
                }
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).renderGame(g);
                }
                checkCollisions();
                
                for (Projectile projectile : projectiles) {
                    projectile.render(g);
                }
                
                map.drawForeground(g);
                
                drawStats(g);
                if(winner == 5){
                    text2.drawString(0, 0, "NOBODY WINS!", g);
                }
                else if(winner >= 0){
                    text2.drawString(0, 0, "PLAYER "+(winner+1)+" WINS!", g);
                }
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).renderForeground(g);
                }
                g.translate(-width / 2, -height / 2);
                
                if(state.getTransit() != 0){
                    Textures.fillRect(0, 0, 3500, 1400, new Color(0, 0, 0, Math.abs((int)(state.getTransit()*255f))), g,true);
                }
                break;
            case 5:
                // aftermath
                break;
        }
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
    
    public void checkCollisions(){
        for(int i = 0; i < players.size(); i ++){
            Player player1 = players.get(i);
            if (!player1.alive || player1.hitboxes == null) continue;
            for (int j = i + 1; j < players.size(); j++) {
                Player player2 = players.get(j);
                if (!player2.alive || player2.hitboxes == null) continue;
                // too far apart for any meelee attacks
                if (Math.sqrt(Math.pow(player2.x - player1.x, 2) + Math.pow(player2.y-player1.y,2)) > 350) continue;
                
                for(int k = 0; k < player1.hitboxes.length; k ++){
                    HitboxPoint h1 = player1.hitboxes[k];
                    for(int l = 0; l < player2.hitboxes.length; l ++){
                        HitboxPoint h2 = player2.hitboxes[l];
                        if(h2.isShielding() && hitboxesCollide(h1, h2)){
                            handlePlayerCollision(player1,player2,h1,h2);
                        }
                    }
                }
                
                if(player2.hurtboxes != null)
                    for(int k = 0; k < player1.hitboxes.length; k ++){
                        HitboxPoint h1 = player1.hitboxes[k];
                        for(int l = 0; l < player2.hurtboxes.length; l ++){
                            HitboxPoint h2 = player2.hurtboxes[l];
                            if(hitboxesCollide(h1, h2) && h1.isAttacking()){
                                handlePlayerCollision(player1,player2,h1,h2);
                            }
                        }
                    }
                
                for(int k = 0; k < player2.hitboxes.length; k ++){
                    HitboxPoint h2 = player2.hitboxes[k];
                    for(int l = 0; l < player1.hitboxes.length; l ++){
                        HitboxPoint h1 = player1.hitboxes[l];
                        if(h1.isShielding() && hitboxesCollide(h2, h1)){
                            handlePlayerCollision(player2,player1,h2,h1);
                        }
                    }
                }
                
                if(player1.hurtboxes != null)
                    for(int k = 0; k < player2.hitboxes.length; k ++){
                        HitboxPoint h2 = player2.hitboxes[k];
                        for(int l = 0; l < player1.hurtboxes.length; l ++){
                            HitboxPoint h1 = player1.hurtboxes[l];
                            if(hitboxesCollide(h2, h1) && h2.isAttacking()){
                                handlePlayerCollision(player2,player1,h2,h1);
                            }
                        }
                    }
                
                if(player1.hurtboxes == null)continue;
                if(player2.hurtboxes == null)continue;
                
                if(Math.sqrt(Math.pow(player2.x+player2.xVel - player1.x-player1.xVel, 2)+
                                Math.pow(player2.y+player2.yVel - player1.y-player1.yVel,2)) <= 86){
                    if(Math.sqrt(Math.pow(player2.x - player1.x, 2) + Math.pow(player2.y-player1.y,2)) > 86){
                    
                        float holder = player1.rVel;
                        player1.rVel = player2.rVel;
                        player2.rVel = holder;
                        // returns the respective direction of player1 (to be transfered to p2)
                        double cdir1 = Math.atan2(player2.y - player1.y, player2.x-player1.x);
                        // returns the respective direction of player2 (to be transfered to p1)
                        double cdir2 = Math.atan2(player1.y - player2.y, player1.x-player2.x);

                        double p1dir = Math.atan2(player1.yVel, player1.xVel);
                        double p1Vel = Math.sqrt(player1.xVel*player1.xVel + player1.yVel*player1.yVel);

                        double p2dir = Math.atan2(player2.yVel, player2.xVel);
                        double p2Vel = Math.sqrt(player2.xVel*player2.xVel + player2.yVel*player2.yVel);

                        double cveloc1 = Math.cos(p1dir-cdir1)*p1Vel;
                        double cveloc2 = Math.cos(p2dir-cdir2)*p2Vel;

                        player1.xVel -= Math.cos(cdir1)*cveloc1;
                        player1.yVel -= Math.sin(cdir1)*cveloc1;
                        player1.xVel += Math.cos(cdir2)*cveloc2;
                        player1.yVel += Math.sin(cdir2)*cveloc2;

                        player2.xVel -= Math.cos(cdir2)*cveloc2;
                        player2.yVel -= Math.sin(cdir2)*cveloc2;
                        player2.xVel += Math.cos(cdir1)*cveloc1;
                        player2.yVel += Math.sin(cdir1)*cveloc1;
                    }
                    else {
                        // returns the respective direction of player1 (to be transfered to p2)
                        double cdir1 = Math.atan2(player2.y - player1.y, player2.x-player1.x);
                        // returns the respective direction of player2 (to be transfered to p1)
                        double cdir2 = Math.atan2(player1.y - player2.y, player1.x-player2.x);

                        double cveloc1 = 0.5f;
                        double cveloc2 = 0.5f;

                        player1.xVel -= Math.cos(cdir1)*cveloc1;
                        player1.yVel -= Math.sin(cdir1)*cveloc1;
                        player1.xVel += Math.cos(cdir2)*cveloc2;
                        player1.yVel += Math.sin(cdir2)*cveloc2;

                        player2.xVel -= Math.cos(cdir2)*cveloc2;
                        player2.yVel -= Math.sin(cdir2)*cveloc2;
                        player2.xVel += Math.cos(cdir1)*cveloc1;
                        player2.yVel += Math.sin(cdir1)*cveloc1;
                    }
                }
            }
        }
        
        for (Projectile proj : new ArrayList<>(projectiles)) {
            if (proj.timer <= 0) continue;
            proj.checkCollisions(map);
            for (int i = 0; i < players.size(); i++) {
                proj.checkCollisions(players.get(i),i);
            }
        }
    }
    
    private boolean hitboxesCollide(HitboxPoint a, HitboxPoint b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double radSum = a.radius + b.radius;
        return dx * dx + dy * dy < radSum * radSum;
    }
    private void handleProjectileCollision(Player player, HitboxPoint hb, Projectile proj) {
        // Hurtbox (0) takes damage, Reflector (4) reflects
        if (hb.type == 0) {
            if(proj.type == 6 && proj.damage == 0){
                if(proj.xVel + proj.yVel > 0.5){
                    
                    float holderx = proj.xVel;
                    float holdery = proj.yVel;
                    proj.xVel = player.xVel;
                    proj.yVel = player.yVel;
                    player.xVel = holderx;
                    player.yVel = holdery;
                }
            }
            else{
                player.takeDamage((int)(proj.damage * hb.intensity / 100), 1,
                        Math.atan2(player.y - proj.y,  player.x - proj.x));
            }
            if(proj.type != 6){
                proj.timer = -1;
            }
            else{
                if(proj.timer < 36){
                    proj.damage = 0;
                    proj.radius = 0;
                }
            }
        } else if (hb.type == 4 || hb.type == 6) {
            proj.reflect(player.playerNum);
        }
    }
    private void handlePlayerCollision(Player attacker, Player defender, HitboxPoint atk, HitboxPoint def) {
        int atkModule = getModuleIndexForHitbox(attacker, atk);
        int defModule = getModuleIndexForHitbox(defender, def);
        double atkDir = attacker.rotation + atkModule * Math.PI / 2 - Math.PI / 2;
        double defDir = defender.rotation + defModule * Math.PI / 2 - Math.PI / 2;

        if(def.isShielding()){
            if(def.isAttacking()){
                int dmg = (int)(atk.intensity * def.intensity * def.parent.damage / 10000);
                attacker.takeDamage(dmg, def.type, defDir);
            }
            else{
                attacker.takeDamage((int)(atk.intensity * atk.parent.damage / 100), def.type, defDir);
            }
        }
        else if(def.isHurtbox()){
            int dmg = (int)(atk.intensity * def.intensity * atk.parent.damage / 10000);
            defender.takeDamage(dmg, atk.type, atkDir);
            attacker.addPause(dmg, false); 
        }
    }
    private boolean isAttack(HitboxPoint hb) {
        return hb.type == 1 || hb.type == 10;
    }
    public void handleCollisions() {
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
                        if (isAttacking(hb1) && isDefending(hb2)) {
                            if (p1Attack == null || hb1.intensity > p1Attack.intensity || hb2.type == 2 || hb2.type == 6) {
                                p1Attack = hb1;
                                p2Defend = hb2;
                            }
                        }
                        if (isAttacking(hb2) && isDefending(hb1)) {
                            if (p2Attack == null || hb2.intensity > p2Attack.intensity || hb1.type == 2 || hb1.type == 6) {
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
                if (i == proj.parent && proj.type != 6) continue; // Immune to own projectiles
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
    private boolean isAttacking(HitboxPoint hb1){
        return hb1.type == 1 || hb1.type == 10 || hb1.type == 5;
    }
    private boolean isDefending(HitboxPoint hb1){
        return hb1.type == 0 || hb1.type == 2 || hb1.type == 5 || hb1.type == 6;
    }
    
    private int getModuleIndexForHitbox(Player player, HitboxPoint hitbox) {
        int offset = 1; // skip the main body hitbox at index 0
        for (int m = 0; m < player.modules.length; m++) {
            HitboxPoint[] frame1 = player.modules[m].getHurtbox();
            for (int j = 0; j < frame1.length; j++) {
                if(player.hurtboxes == null)break;
                if (offset < player.hurtboxes.length && player.hurtboxes[offset] == hitbox) {
                    return m;
                }
                offset++;
            }
            HitboxPoint[] frame2 = player.modules[m].getHitbox();
            for (int j = 0; j < frame2.length; j++) {
                if(player.hitboxes == null)break;
                if (player.hitboxes[j] == hitbox) {
                    return m;
                }
            }
        }
        return 0; // fallback to 0 if not found
    }
    
    int consecutiveErrors = 0;
    @Override
    public boolean handleError(Exception e){
        consecutiveErrors += 2;
        if(consecutiveErrors > 240){
            consecutiveErrors = 0;
            loaded = false;
            init();
        }
        return true;
    }
    
    private Integer KonamiCode = 0;
    
    private static final Integer[] KONAMI_CODE = new Integer[]{
        4,4,5,5,6,7,6,7,2,1,11
    };
    private boolean checkKonami(){
        
        for(int i = 0; i < players.size(); i ++){
            if(players.get(i).controller.pressed(KONAMI_CODE[KonamiCode])){
                KonamiCode = KonamiCode+1;
            }
            else{
                for(int j = 0; j <= 11; j ++){
                    if(KonamiCode > 0 && j != KONAMI_CODE[KonamiCode-1] && players.get(i).controller.held(j))KonamiCode = 0;
                }
            }
            if(KonamiCode == 11){
                KonamiCode = 0;
                System.out.println("K O N A M I      C O D E ! ! !");
                return true;
            }
            
        }
        return false;
    }
}
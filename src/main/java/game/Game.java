package game;

import engine.framework.Engine;
import engine.framework.MultiState;
import engine.graphics.Text;
import engine.graphics.Textures;
import engine.input.SnesController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import org.lwjgl.glfw.GLFW;

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

    Text text;
    Text text2;
    Text text3;
    
    int winner;
    
    Random random;
    
    BufferedImage map;

    public Game(){
        init();
        map =  Textures.loadImage("/textures/maps/demo.png");
        this.start("HodgePodgeRobotBarrage", 1600, 900, true);
    }
    
    public final void init(){
        SnesController.init();
        text = new Text("Regular",40,Color.white);
        text2 = new Text("Regular2",30,Color.white, 5, Color.black, true);
        text3 = new Text("Fancy",80,Color.black,5, Color.white, true);
        random = new Random();
        state = new MultiState(0);
        modules = Modules.getModules();
        Players.loadImages();
        ModuleSelect.loadImages();
        MainMenu.load();
        players = new ArrayList<>();
        projectiles = new ArrayList<>();
        winner = -1;
    }

    @Override
    public void tick() {
        state.update();
        
        ArrayList<Integer> controllerIds = SnesController.updateControllers();
        
        switch (state.state()) {
            case 0:
                // main menu
                
                for(Integer id : controllerIds){
                    if(SnesController.getButtonHeld(id, SnesController.START)){
                        state.transition(20, 2,60);
                        while(controllerIds.size() > players.size()){
                            players.add(new Player(this, controllerIds.get(players.size()),players.size()));
                            players.get(players.size() -1).initMenu();
                        }
                    }
                }
                break;
            case 2:
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
                    players.get(i).tickMenu();
                    if (controller.pressed(SnesController.START)) {
                        boolean allSelected = true;
                        for (Player player : players) {
                            for(int j = 0; j < 4; j ++){
                                if(player.moduleSelections[j] < 0)allSelected = false;
                            }
                        }
                        if(allSelected){
                            for (Player player : players) {
                                player.initGame();
                                System.out.println(Modules.loadModuleSelection(player.moduleSelections).length);
                            }
                            state.transition(0, 3, 0);
                            return;
                        }
                    }
                }
                break;
            case 3:
                if(checkKonami()){
                    Player.DRAW_HITBOXES = !Player.DRAW_HITBOXES;
                }
                int playersRemaining = 0;
                boolean restart = false;
                for (int i = 0; i < players.size(); i++) {
                    if(i == winner && players.get(i).controller.pressed(SnesController.START))restart = true;
                    players.get(i).tickGame();
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
                
                this.setHints(g);
                float scale1 = window.getHeight() / 700f;
                float offset1 = (window.getWidth() / scale1 - 1800) / 2;

                g.setColor(Color.BLACK);
                g.fillRect(0, 0, width, height);
                
                g.drawImage(MainMenu.logo, (int)(offset1*scale1), 0,(int)(MainMenu.logo.getWidth()*scale1),
                        (int)(MainMenu.logo.getHeight()*scale1),null);
                
                ArrayList<Integer> controllerIds = SnesController.updateControllers();
                text.drawString(width/2, height/2 + 50, "(unfinished menu)", g);
                
                
                for(int i = 0; i < controllerIds.size(); i ++){
                    text.drawString((int)((width/5f) *(i+1)), height - 40, "Player "+Integer.toString(i+1), g);
                }
                if(state.isTransit()){
                    g.setColor(new Color(0,0,0,(int)(255*state.getTransit())));
                    
                    g.fillRect(0, 0, width, height);
                }
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
                g.translate(offset2, 0);
                g.scale(scale2, scale2);
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
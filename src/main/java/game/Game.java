package game;

import engine.framework.Engine;
import engine.framework.MultiState;
import engine.graphics.Text;
import engine.input.ControllerHandler;
import engine.input.SnesController;

import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author cookibot
 */
public class Game extends Engine{
    
    Module[] modules;
    MultiState state;
    
    ControllerHandler controllerHandler;
    
    ArrayList<SnesController> controllers;
    ArrayList<Player> players;
    
    Text text;
    Text text2;
    
    int timer = 1;
    
    public Game(){
        text = new Text("Regular",40,Color.white);
        text2 = new Text("Regular2",30,Color.white, 5, Color.black, true);
        state = new MultiState(0);
        modules = Modules.getModules();
        players = new ArrayList<>();
        controllers = new ArrayList<>();
        this.start("HodgePodgeRobotBarrage", 1600, 900, false);
    }
    
    @Override
    public void tick() {
        state.update();
        if(state.state() <= 1 || state.state() == 4){
            timer --;
            if(timer == 0){
                getPlayers();
                while(controllers.size() > players.size()){
                     players.add(new Player());
                }
                timer = 20;
            }
        }
        for(SnesController controller : controllers){
            controller.update();
        }
        switch(state.state()){
            case 0:
                //main menu
                
                for(SnesController controller : controllers){
                    if(controller.pressed(SnesController.A)){
                        state.transition(1, 3, 1);
                        for(int i = 0; i < players.size(); i ++){
                            players.get(i).init(Modules.loadModuleSelection(new Integer[]{2,0,1,0}),i);
                        }
                    }
                    controller.clearPressed();
                }
                if(controllers.size() == 4){
                    
                }
                break;
            case 1:
                //body select (might not use)
                break;
            case 2:
                //module select
                break;
            case 3:
                for(int i = 0; i < players.size(); i ++){
                    players.get(i).tickGame(controllers.get(i));
                }
                break;
            case 4:
                //pause game
                break;
            case 5:
                //aftermath
                break;
        }
    }

    @Override
    public void render(Graphics2D g) {
        setHints(g);
        
        int width = window.getWidth();
        int height = window.getHeight();
        
        switch(state.state()){
            case 0: 
                //main menu
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, width, height);
                text.drawString(width/2, height/2, "Main Menu", g);
                
                for(int i = 0; i < players.size(); i ++){
                    text.drawString((int)((width/5f) *(i+1)), height - 40, "Player "+Integer.toString(i+1), g);
                }
                break;
            case 1:
                //body select (might not use)
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, width, height);
                text.drawString(width/2, height/2, "Body Select", g);
                
                for(int i = 0; i < players.size(); i ++){
                    text.drawString((int)((width/5f) *(i+1)), height - 40, "Player "+Integer.toString(i+1), g);
                }
                break;
            case 2:
                //module select
                break;
            case 3:
                
                g.setColor(Color.white);
                g.fillRect(0,0,width,height);
                
                drawStats(g);
                g.translate(width/2,height/2);
                for(int i = 0; i < players.size(); i ++){
                    players.get(i).renderGame(g);
                }
                checkCollisions();
                break;
            case 4:
                //aftermath
                break;
        }
    }
    
    public void drawStats(Graphics g){
        
        int width = window.getWidth();
        int height = window.getHeight();
        
        text2.drawString(0, 0, (String)("Player 1: "+players.get(0).damage).replace('0', 'O'),1,-1, g);
        text2.drawString(width, 0, (String)("Player 2: "+players.get(1).damage).replace('0', 'O'),-1,-1, g);
        text2.drawString(0, height, (String)("Player 3: "+players.get(2).damage).replace('0', 'O'),1,1, g);
        text2.drawString(width, height, (String)("Player 4: "+players.get(3).damage).replace('0', 'O'),-1,1, g);
                
    }
    
    public void checkCollisions() {

        for (int i = 0; i < players.size(); i++) {
            Player p1 = players.get(i);
            if (p1.hitboxes == null) continue;
            for (int j = i + 1; j < players.size(); j++) {
                Player p2 = players.get(j);
                if (p2.hitboxes == null) continue;

                boolean playersCollided = false;
                HitboxPoint p1defend = null;
                HitboxPoint p2attack = null;
                boolean invincibleP1 = false;
                HitboxPoint p2defend = null;
                HitboxPoint p1attack = null;
                boolean invincibleP2 = false;
                
                for (int h1 = 0; h1 < p1.hitboxes.length; h1++) {
                    HitboxPoint hb1 = p1.hitboxes[h1];
                    for (int h2 = 0; h2 < p2.hitboxes.length; h2++) {
                        HitboxPoint hb2 = p2.hitboxes[h2];

                        // Check collision
                        double dx = hb1.x - hb2.x;
                        double dy = hb1.y - hb2.y;
                        double distSq = dx * dx + dy * dy;
                        double radSum = hb1.radius + hb2.radius;
                        if (distSq < radSum * radSum) {
                            
                            playersCollided = true;
                            
                            if(hb1.type == 1 && hb2.type == 0){
                                if(p1attack == null || hb1.intensity > p1attack.intensity){
                                    p1attack = hb1;
                                    p2defend = hb2;
                                }
                            }
                            if(hb1.type == 0 && hb2.type == 1){
                                if(p2attack == null || hb2.intensity > p2attack.intensity){
                                    p2attack = hb2;
                                    p1defend = hb1;
                                }
                            }
                            //collide
                        }
                    }
                }
                
                if(playersCollided){
                    
                    if(!p1.intersecting.contains(p2)){
                        p1.intersecting.add(p2);
                    }
                    if(!p2.intersecting.contains(p1)){
                        p2.intersecting.add(p1);
                        p1.revertPos();
                        p2.revertPos();
                        float p1x = p1.xVel;
                        float p1y = p1.yVel;
                        p1.xVel = p2.xVel;
                        p1.yVel = p2.yVel;
                        p2.xVel = p1x;
                        p2.yVel = p1y;
                        float p1r = p1.rVel;
                        p1.rVel = -p2.rVel;
                        p2.rVel = -p1r;

                        if(p1attack != null && p2defend != null){
                            int moduleNum = getModuleIndexForHitbox(p1, p1attack);
                            double rotation = p1.rotation + moduleNum * Math.PI / 2 - Math.PI/2;
                            p2.takeDamage((int)(p1attack.intensity*p2defend.intensity*p1attack.parent.damage/10000), p1attack.type, rotation);
                            p1.addPause((int)(p1attack.intensity*p2defend.intensity*p1attack.parent.damage/10000));
                        }
                        if(p2attack != null && p1defend != null){
                            int moduleNum = getModuleIndexForHitbox(p2, p2attack);
                            double rotation = p2.rotation + moduleNum * Math.PI / 2 - Math.PI/2;
                            System.out.println(getModuleIndexForHitbox(p2, p1attack));
                            p1.takeDamage((int)(p2attack.intensity*p1defend.intensity*p2attack.parent.damage/10000), p2attack.type, rotation);
                            p2.addPause((int)(p2attack.intensity*p1defend.intensity*p2attack.parent.damage/10000));
                        }
                    }
                    
                }
                else{
                    if(p1.intersecting.contains(p2)){
                        p1.intersecting.remove(p2);
                    }
                    if(p2.intersecting.contains(p1)){
                        p2.intersecting.remove(p1);
                    }
                }
            }
        }
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
    public void getPlayers(){
        //load new controllers
        controllers = SnesController.getControllers();
    }
}
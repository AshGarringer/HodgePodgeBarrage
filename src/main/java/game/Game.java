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
    
    int timer = 1;

    public Game(){
        text = new Text("Regular",40,Color.white);
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
                            players.get(i).init(Modules.loadModuleSelection(new Integer[]{0,0,1,2}),i);
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
                g.translate(width/2,height/2);
                for(int i = 0; i < players.size(); i ++){
                    players.get(i).renderGame(g);
                }
                break;
            case 4:
                //aftermath
                break;
        }
    }
    

    public void getPlayers(){
        //load new controllers
        controllers = SnesController.getControllers();
    }
    
    
}
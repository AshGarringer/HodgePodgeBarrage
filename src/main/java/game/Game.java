package game;

import engine.framework.Engine;

import java.awt.*;

/**
 *
 * @author cookibot
 */
public class Game extends Engine{
    
    Module[] modules;

    public Game(){
        modules = Modules.getModules();
    }
    
    @Override
    public void tick() {
    }

    @Override
    public void render(Graphics2D g) {
        setHints(g);
    }
}
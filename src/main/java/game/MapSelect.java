/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.graphics.Textures;
import game.maps.Map;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class MapSelect { 
    
    public static BufferedImage background;
    public static BufferedImage[] cursors;
    
    public static BufferedImage[] slotPreviews;
    public static BufferedImage holder;
    
    public static Point[] mice;
    public static int selected = -1;
    
    public static void loadImages(){
        
        Map.load();
        mice = new Point[4];
        for(int i = 0; i < 4; i ++){
            mice[i] = new Point(1220 + (i % 2) * 1160,375 + (i / 2) * 650);
        }
        
        background = Textures.loadImage("/textures/mapSelect/background.png");
        cursors = new BufferedImage[4];
        for(int i = 0; i < 4; i ++){
            cursors[i] = Textures.loadImage("/textures/mapSelect/p" + (i+1) + ".png");
        }
        holder = Textures.loadImage("/textures/mapSelect/holder.png");
        
        slotPreviews = new BufferedImage[Map.NUM_MAPS];
        for(int i = 0; i < Map.NUM_MAPS; i ++){
            slotPreviews[i] = new BufferedImage(470,270,2);
            Graphics2D g = (Graphics2D)slotPreviews[i].createGraphics();
            
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setClip(10, 10, 450, 250);
            BufferedImage image = Map.getMaps()[i].background;
            int width = (int)(450f * 1.2);
            int height = (int)((float) image.getHeight()/image.getWidth() * 450f * 1.2);
            g.drawImage(Map.getMaps()[i].background, 225 - width/2, 125 - height/2, width, height, null);
            
            g.setClip(0, 0, 470, 270);
            g.drawImage(holder, 0, 0, null);
            g.dispose();
        }
    }
    public static void reset(){
        
        mice = new Point[4];
        for(int i = 0; i < 4; i ++){
            mice[i] = new Point(1220 + (i % 2) * 1160,375 + (i / 2) * 650);
        }
        
        selected = -1;
    }
}

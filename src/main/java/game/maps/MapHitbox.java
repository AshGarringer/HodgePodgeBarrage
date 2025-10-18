/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.maps;

import engine.logic.Calcs;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author cookiebot
 */
public class MapHitbox {
    
    public static final Integer RECTANGLE = 0;
    public static final Integer ROTATED_RECTANGLE = 1;
    public static final Integer CIRCLE = 2;
    
    public int x, y;
    public int type;
    public int width;
    public int height;
    public double rotation;
    public int diameter;
    public int raduis;
    
    public MapHitbox (int type, int x, int y, int width, int height){
        this.type = type;
        this.x = x;
        this.y = y;
        if(type == 1){
            diameter = width;
            raduis = width/2;
        }
        this.width = width;
        this.height = height;
        this.rotation = 0;
    }
    public MapHitbox (int type, int x, int y, int width, int height, double rotation){
        this.type = type;
        this.x = x;
        this.y = y;
        diameter = 0;
        raduis = 0;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }
    public MapHitbox (int type, int x, int y, int diameter){
        this.type = type;
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        raduis = diameter/2;
    }
    
    public void render(Graphics2D g, boolean selected){
        switch(type){
            case 0:
                g.setColor(Color.red);
                g.drawRect(x-width/2, y-height/2, width,height);
                if(selected){
                g.setColor(new Color(255,0,0,100));
                    g.fillRect(x-width/2, y-height/2, width,height);
                }
                break;
            case 1:
                g.setColor(Color.red);
                g.drawOval(x-raduis, y-raduis, diameter,diameter);
                if(selected){
                g.setColor(new Color(255,0,0,100));
                    g.fillOval(x-raduis, y-raduis, diameter,diameter);
                }
                break;
            case 2:
                g.setColor(Color.red);
                g.translate(x,y);
                g.rotate(Math.toRadians(rotation));
                g.drawRect(-width/2,-height/2, width,height);
                if(selected){
                g.setColor(new Color(255,0,0,100));
                    g.fillRect(-width/2, -height/2, width,height);
                }
                g.rotate(-Math.toRadians(rotation));
                g.translate(-x,-y);
                break;
        }
    }
    public boolean contains(Point p){
        switch(type){
            case 0:
                return (new Rectangle(x-width/2,y-height/2,width,height)).contains(p);
            case 1:
                p = Calcs.rotatePoint(p, x, y, -rotation);
                return (new Rectangle(x-width/2,y-height/2,width,height)).contains(p);
            case 2:
                return (p.x-x)*(p.x-x) + (p.y-y)*(p.y-y) < diameter*diameter; 
        }
        return false;
    }
    public void shift(int x, int y, int width, int height){
        this.x += x;
        this.y += y;
        this.height += height;
        this.width += width;
        if(type == 2)diameter += width+height;
    }
    public void rotate(int rot){
        this.rotation = (360 + rotation +rot)%360;
    }
}

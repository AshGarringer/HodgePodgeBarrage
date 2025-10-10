/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.files.FileLoader;
import engine.graphics.Textures;
import engine.logic.Calcs;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author cookiebot
 */
public class Map {
    
    public ArrayList<MapHitbox> hitboxes;
    public ArrayList<Rectangle> zones;
    BufferedImage background;
    BufferedImage foreground;
    String path;
    
    int width, height;
    
    public Map(String folder){
        
        path = folder;
        String s = FileLoader.separator();
        ArrayList<String> hitboxesString = FileLoader.readLocalFile(s+ "maps" + s + folder + s +"hitboxes");
        
        String[] justHitboxes = new String[0];
        String[] zoneHitboxes = new String[0];
        if(!hitboxesString.isEmpty())
            justHitboxes = hitboxesString.get(0).split("=");
        
        if(hitboxesString.size() > 1)
            zoneHitboxes = hitboxesString.get(1).split("=");
        
        hitboxes = new ArrayList<>();
        zones = new ArrayList<>();
        
        for(int i = 0; i < justHitboxes.length; i ++){
            String[] vals = justHitboxes[i].split(",");
            switch(vals.length){
                case 4:
                    hitboxes.add(new MapHitbox(0,parse(vals[0]),parse(vals[1]),parse(vals[2]),parse(vals[3])));
                    break;
                case 3:
                    hitboxes.add(new MapHitbox(1,parse(vals[0]),parse(vals[1]),parse(vals[2])));
                    break;
                case 5:
                    hitboxes.add(new MapHitbox(2,parse(vals[0]),parse(vals[1]),parse(vals[2]),parse(vals[3]),(double)parse(vals[4])));
                    break;
            }
        }
        for(int i = 0; i < zoneHitboxes.length; i ++){
            String[] vals = zoneHitboxes[i].split(",");
            if(vals.length == 4){
                zones.add(new Rectangle(parse(vals[0]),parse(vals[1]),parse(vals[2]),parse(vals[3])));
            }
        }
        
        System.out.println(hitboxes.size());
        background = Textures.loadPng(s + "maps" + s + folder + s + "background");
        foreground = Textures.loadPng(s + "maps" + s + folder + s + "foreground");
        width = background.getWidth();
        height = background.getHeight();
    }
    public void addHitbox(int type, int x, int y, int width, int height){
        if(type == -1){
            addZone(x,y,width,height);
            return;
        }
        hitboxes.add(new MapHitbox(type,x,y,width,height));
    }
    public void addZone(int x, int y, int width, int height){
        zones.add(new Rectangle(x,y,width,height));
    }
    
    public int checkMouseIntersection(int x, int y){
        
        for(int i = 0; i < hitboxes.size(); i ++){
            if(hitboxes.get(i).contains(new Point(x,y)))return i;
        }
        return -1;
    }
    
    public int checkMouseIntersectionZone(int x, int y){
        for(int i = 0; i < zones.size(); i ++){
            if(zones.get(i).contains(new Point(x,y)))return i;
        }
        return -1;
    }
    
    public String writeToFile(){
        String s = "";
        
        for(int i = 0; i < hitboxes.size(); i ++){
            String hitboxString = "";
            MapHitbox hitbox = hitboxes.get(i);
            switch (hitbox.type){
                case 0:
                    hitboxString = hitbox.x+","+hitbox.y+","+hitbox.width+","+hitbox.height+"=";
                    break;
                case 1:
                    hitboxString = hitbox.x+","+hitbox.y+","+hitbox.diameter+"=";
                    break;
                case 2:
                    hitboxString = hitbox.x+","+hitbox.y+","+hitbox.width+","+hitbox.height+","+(int)hitbox.rotation+"=";
                    break;
            }
            s = s + hitboxString;
        }
        
        if(s.endsWith("="))s = s.substring(0,s.length()-1);
        s = s +"\n";
        
        for(int i = 0; i < zones.size(); i ++){
            Rectangle r = zones.get(i);
            s = s + r.x+","+r.y+","+r.width+","+r.height+"=";
        }
        
        if(s.endsWith("="))s = s.substring(0,s.length()-1);
        
        FileLoader.reWriteLocalFile(s, "resources" + FileLoader.separator()+ "maps" + FileLoader.separator() + path + FileLoader.separator() +"hitboxes");
        
        System.out.println(s);
        return s;
    }
    
    public static String getValueIfExists(String[] arr, int index){
        if(arr.length > index)return arr[index];
        return "";
    }
    
    public static int parse(String s){
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e){
            return 0;
        }
    }
    
    public static int safelyGetValue(String[] arr, int index){
        try {
            return Integer.parseInt(getValueIfExists(arr, index));
        }
        catch (NumberFormatException e){
            return 0;
        }
    }
    public static String safelyGetString(ArrayList<String> arr, int index){
        try {
            return arr.get(index);
        }
        catch (Exception e){
            return "";
        }
    }
    
    // static
    
    public static Map[] maps;
    
    public static void load(){
        
        maps = new Map[1];
        
        maps[0] = new Map("house");
        
    }
    
    public static Map[] getMaps(){
        if(maps == null)
            load();
        return maps;
    }
    
    public double intersects(float x, float y, int radius){
        
        MapHitbox hitbox;
        float translateX;
        float translateY;
        for(int i = 0; i < hitboxes.size(); i ++){
            hitbox = hitboxes.get(i);
            translateX = x;
            translateY = y;
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
            
            if(Math.pow(translateX-hitbox.x, 2) + Math.pow(translateY-hitbox.y, 2) < Math.pow(radius+hitbox.diameter/2+1, 2)){
                
                double angle = Math.atan2(translateY-hitbox.y, translateX-hitbox.x);
                if(hitbox.type == 2)angle += Math.toRadians(hitbox.rotation);
                return angle;
            }
            
        }
        return 0;
    }
    
}
class MapHitbox {
    
    public static final Integer RECTANGLE = 0;
    public static final Integer ROTATED_RECTANGLE = 1;
    public static final Integer CIRCLE = 2;
    
    int x, y;
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
        diameter = 0;
        raduis = 0;
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
                g.drawOval(x-diameter/2, y-diameter/2, diameter,diameter);
                if(selected){
                g.setColor(new Color(255,0,0,100));
                    g.fillOval(x-diameter/2, y-diameter/2, diameter,diameter);
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

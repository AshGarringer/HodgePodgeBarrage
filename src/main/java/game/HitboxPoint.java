/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import java.awt.Point;

/**
 *
 * @author cookiebot
 */
public class HitboxPoint {
    
    int x;
    int y;
    int radius;
    int type;
    int intensity;
    
    public HitboxPoint(String string, int damage){
        String[] arr = string.split(",");
        try{
            x = Integer.parseInt(arr[0]);
            y = Integer.parseInt(arr[1]);
            radius = Integer.parseInt(arr[2]);
            type = Integer.parseInt(arr[3]);
            intensity = Integer.parseInt(arr[4]);
        }
        catch (Exception e){
            x = 0;
            y = 0;
            radius = 0;
            type = 0;
            intensity = 0;
        }
    }
    
    public HitboxPoint() {
        this(0, 0, 0, 0, 0);
    }
    public HitboxPoint(Point p, int radius, int type, int intensity) {
        this(p.x, p.y, radius, type, intensity);
    }
    public HitboxPoint(int x, int y, int radius, int type, int intensity) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.type = type;
        this.intensity = intensity;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getRadius(){
        return radius;
    }
    public double getType() {
        return type;
    }
    public double getdamage() {
        return intensity;
    }
}

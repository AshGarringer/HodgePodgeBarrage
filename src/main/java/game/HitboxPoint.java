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
    public Hitbox parent;
    public Projectile projectileParent;
    
    // 0: Damageable hitbox
    // 1: Attacking hitbox
    // 2: Shield hitbox
    // 3: Projectile spawnpoint
    // 4: Reflector hitbox
    // 5: Electric counter hitbox
    // 6: Shield AND reflector hitbox
    
    public HitboxPoint(String string, Hitbox parent){
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
        this.parent = parent;
    }
    
    public HitboxPoint() {
        this(0, 0, 0, 0, 0, null);
    }
    public HitboxPoint(int x, int y, int radius, int type, int intensity, Projectile parent, boolean isProjectile) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.type = type;
        this.intensity = intensity;
        this.projectileParent = parent;
    }
    public HitboxPoint(int x, int y, int radius, int type, int intensity, Hitbox parent) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.type = type;
        this.intensity = intensity;
        this.parent = parent;
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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.maps;

import engine.files.FileLoader;
import engine.graphics.Textures;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class MapUnderground extends Map{
    
    BufferedImage[] foregroundArr;
    int timer = 0;
    
    public MapUnderground() {
        super("underground");
        foregroundArr = new BufferedImage[2];
        String s = FileLoader.separator();
        foregroundArr[0] = foreground;
        foregroundArr[1] = Textures.loadPng(s + "maps" + s + "underground" + s + "foreground2");
    }
    
    @Override
    public void tick(){
        timer ++;
        if(timer == 60){
            timer = 0;
        }
    }
    
    @Override
    public void drawForeground(Graphics2D g){
        g.drawImage(foregroundArr[timer/30], -width/2, -height/2,null);
    }
}

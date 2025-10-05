/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.files.FileLoader;
import engine.graphics.Textures;
import engine.logic.Calcs;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author cookiebot
 */
public class Modules {
    
    public static Module[] modules;
    
    
    public static final int NUM_MODULES = 7;
    
    public static Module[] getModules(){
        
        if(modules != null)return modules;
        
        modules = new Module[7];
        
        String s = FileLoader.separator();
        ArrayList<String> hitboxes = FileLoader.readLocalFile(s+"hitboxes");
        
        String[] damages;
        if(!hitboxes.isEmpty()) damages = hitboxes.get(0).split(",");
        else damages = new String[0];
        
        modules[0] = new Module("spike", safelyGetString(hitboxes,1), 9, safelyGetValue(damages,0),0);
        modules[1] = new Module("spear", safelyGetString(hitboxes,2), 23, safelyGetValue(damages,1),1);
        modules[2] = new Module("saw", safelyGetString(hitboxes,3), 8, 2, 4, safelyGetValue(damages,2),2);
        modules[3] = new Module("shield", safelyGetString(hitboxes,4), 5,4,1, safelyGetValue(damages,3),3);
        modules[4] = new Module("quickshield", safelyGetString(hitboxes,5), 12, safelyGetValue(damages,4),4);
        modules[5] = new Module("shooter", safelyGetString(hitboxes,6), 10, safelyGetValue(damages,5),5);
        modules[6] = new Module("reflector", safelyGetString(hitboxes,7), 13, safelyGetValue(damages,6),6);
        
        return modules;
    }
    
    public static Module[] loadModuleSelection(Integer[] selected){
        Module[] returnModules = new Module[4];
        if(selected.length < 4)return null;
        returnModules[0] = modules[selected[0]].duplicate(0);
        returnModules[1] = modules[selected[1]].duplicate(1);
        returnModules[2] = modules[selected[2]].duplicate(2);
        returnModules[3] = modules[selected[3]].duplicate(3);
        return returnModules;
    }
    
    public static Module[] resetModules(){
        modules = null;
        return getModules();
    }
    
    public static void writeHitboxTXT() {
        String s = FileLoader.separator();
        ArrayList<String> hitboxes = FileLoader.readLocalFile(s + "hitboxes");
    
        StringBuilder hitboxTXT = new StringBuilder();
    
        // Handle the first line: damage values
        String[] damages;
        if (!hitboxes.isEmpty()) {
            damages = hitboxes.get(0).split(",");
        } else {
            damages = new String[0];
        }
    
        // Ensure the number of damage values matches the number of modules
        if (damages.length < modules.length) {
            String[] newDamages = new String[modules.length];
            for (int i = 0; i < newDamages.length; i++) {
                if (i < damages.length) {
                    newDamages[i] = damages[i];
                } else {
                    newDamages[i] = "10"; // Default damage value
                }
            }
            damages = newDamages;
        }
    
        // Append the damage values to the first line
        hitboxTXT.append(damages[0]);
        for (int i = 1; i < damages.length; i++) {
            hitboxTXT.append(",").append(damages[i]);
        }
    
        
        // Write the modules to the hitbox string
        for (Module module : modules) {
            
            boolean frameAppended = false;
            hitboxTXT.append("\n");
            
            for(int i = 0; i < module.hitbox.animation.length; i ++){
                
                boolean hitboxAppended = false;
                HitboxPoint[] frame = module.hitbox.animation[i];
                
                if(frameAppended)hitboxTXT.append(" ");
                else frameAppended = true;
                
                for(int j = 0; j < frame.length; j ++){
                    
                    HitboxPoint point = frame[j];
                    
                    if(point.radius != 0){
                        
                        if(hitboxAppended)
                            hitboxTXT.append("-");
                        hitboxAppended = true;
                        
                        hitboxTXT.append(point.x).append(",").append(point.y).append(",")
                                .append(point.radius).append(",").append(point.type).append(",").append(point.intensity);
                        // append hitbox values
                        
                    }
                    
                }
                
            }
            
        }
    
        // Write the hitbox data to the file
        FileLoader.reWriteLocalFile(hitboxTXT.toString(), "resources"+s+"hitboxes");
    }
    
    public static String getValueIfExists(String[] arr, int index){
        if(arr.length > index)return arr[index];
        return "";
    }
    
    public static int safelyParse(String s){
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
}

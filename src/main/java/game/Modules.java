/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import engine.files.FileLoader;
import java.util.ArrayList;

/**
 *
 * @author cookiebot
 */
public class Modules {
    
    public static Module[] getModules(){
        
        Module[] modules = new Module[1];
        
        String s = FileLoader.separator();
        ArrayList<String> hitboxes = FileLoader.readLocalFile(s+"hitboxes");
        
        String[] damages;
        if(!hitboxes.isEmpty()) damages = hitboxes.get(0).split(",");
        else damages = new String[0];
        
        modules[0] = new Module("spear", hitboxes.get(1), 23, safelyGetValue(damages,0));
        
        return modules;
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
}

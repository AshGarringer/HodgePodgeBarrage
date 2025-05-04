package engine.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class RescaledImage {
    
    private final BufferedImage[] imageMipMaps;
    
    private int nextIndexPad = -1;
    private int scaleIndex = 0;
    
    public RescaledImage(BufferedImage image){
        
        imageMipMaps = new BufferedImage[3];
        
        imageMipMaps[0] = image;
        imageMipMaps[1] = Textures.scaleImage(image, image.getWidth()/2,image.getHeight()/2);
        imageMipMaps[2] = Textures.scaleImage(image, image.getWidth()/4,image.getHeight()/4);
    }
    
    public void drawImage(float x, float y, float width, float height, Graphics2D g){
        updateIndex(width,height,g);
        g.drawImage(imageMipMaps[scaleIndex],Math.round(x),Math.round(y),Math.round(width),Math.round(height),null);
    }
    
    public void drawRotated(float x, float y, int width, int height, double rotation, Graphics2D g){
        AffineTransform old = g.getTransform();
        g.translate(x,y);
        g.rotate(rotation);
        double scale = old.getScaleX()*(width/(float)imageMipMaps[0].getWidth());
        if(scale > 0.55f){
            g.translate(-width/2f, -height/2f);
            g.scale(width/(float)imageMipMaps[0].getWidth(), height/(float)imageMipMaps[0].getHeight());
            g.drawImage(imageMipMaps[0],0,0,null);
        }
        else if(scale > 0.30f){
            g.translate(-width/2f, -height/2f);
            g.scale(width/(float)imageMipMaps[1].getWidth(), height/(float)imageMipMaps[1].getHeight());
            g.drawImage(imageMipMaps[1],0,0,null);
        }
        else {
            g.translate(-width/2f, -height/2f);
            g.scale(width/(float)imageMipMaps[2].getWidth(), height/(float)imageMipMaps[2].getHeight());
            g.drawImage(imageMipMaps[2],0,0,null);
        }
        g.setTransform(old);
    }
    
    public boolean updateIndex(float width, float height, Graphics2D g){
        double scale = g.getTransform().getScaleX()*(width/(float)imageMipMaps[0].getWidth());
        int newindex;
        if(scale > 0.55f)newindex = 0;
        else if(scale > 0.30f)newindex = 1;
        else newindex = 2;
        
        if(nextIndexPad == -1){
            scaleIndex = newindex;
            nextIndexPad = newindex;
        }
        
        if(newindex != scaleIndex){
            if(nextIndexPad == newindex)scaleIndex = newindex;
            nextIndexPad = newindex;
            return true;
        }
        return false;
    }
}

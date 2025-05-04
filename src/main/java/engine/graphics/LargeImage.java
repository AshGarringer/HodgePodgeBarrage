package engine.graphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author cookiebot
 */
public class LargeImage {
    
    BufferedImage imageFull;
    BufferedImage imageHalf;
    BufferedImage imageQuarter;
    BufferedImage[] imageSubsectionsFull;
    BufferedImage[] imageSubsectionsHalf;
    BufferedImage[] imageSubsectionsQuarter;
    int subWidth = 1000;
    int subHeight = 1000;
    
    public LargeImage(BufferedImage image){
        imageFull = image;
        imageHalf = Textures.scaleImage(image, 0.5f);
        imageQuarter = Textures.scaleImage(image, 0.25f);
        imageSubsectionsFull = createSubsections(image, 1000,1000);
        imageSubsectionsHalf = createSubsections(imageHalf, 1000,1000);
        imageSubsectionsQuarter = createSubsections(imageQuarter, 1000,1000);
    }
    
    public LargeImage(BufferedImage image, int subWidth, int subHeight){
        imageFull = image;
        imageHalf = Textures.scaleImage(image, 0.5f);
        imageQuarter = Textures.scaleImage(image, 0.25f);
        this.subWidth = subWidth;
        this.subHeight = subHeight;
        imageSubsectionsFull = createSubsections(image, subWidth,subHeight);
        imageSubsectionsHalf = createSubsections(imageHalf, subWidth,subHeight);
        imageSubsectionsQuarter = createSubsections(imageQuarter, subWidth,subHeight);
    }
    
    private BufferedImage[] createSubsections(BufferedImage image, int subWidth, int subHeight){
        
        int numSubsX = image.getWidth()/subWidth;
        if(image.getWidth()%1000 != 0)numSubsX += 1;
        
        int numSubsY = image.getHeight()/subHeight;
        if(image.getHeight()%1000 != 0)numSubsY += 1;
        
        BufferedImage[] imageSubsections = new BufferedImage[numSubsX*numSubsY];
        
        for(int x = 0; x < numSubsX; x ++){
            for(int y = 0; y < numSubsY; y ++){
                imageSubsections[x*numSubsY + y] = image.getSubimage(x*subWidth,y*subHeight,
                        Math.min(subWidth,image.getWidth() - subWidth*x),Math.min(subHeight,image.getHeight() - subHeight*y));
            }
        }
        return imageSubsections;
    }
    
    public void draw(float x, float y, float width, float height,
            float screenX, float screenY, float screenWidth, float screenHeight, Graphics2D g){
        
        
    }
    
}

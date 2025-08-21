package engine.graphics;

import java.util.ArrayList;

/**
 *
 * @author cookiebot
 */
public class SimpleAnimation {
    
    public static final int HOLD = 0;
    public static final int SLOW_TOWARDS = 1;
    public static final int SPEED_TOWARDS = 2;
    public static final int SMOOTH = 3;
    public static final int MOVE_EVEN = 4;
    
    private final ArrayList<Float> animation;
    private final ArrayList<Integer> state_increments;
    
    private int frame = 0;
    private final boolean loop;
    private boolean finished;
    private int state;
    private boolean state_changed;
    
    public SimpleAnimation(float start, boolean loop){
        animation = new ArrayList<>();
        state_increments = new ArrayList<>();
        animation.add(start);
        this.loop = loop;
        this.finished = false;
    }
    public SimpleAnimation(boolean loop){
        animation = new ArrayList<>();
        state_increments = new ArrayList<>();
        this.loop = loop;
        this.finished = false;
    }
    
    public void tick(){
        if(state_changed)state_changed = false;
        if(state_increments.contains((Integer)frame)){
            state_changed = true;
            state ++;
        }
        if(frame == animation.size()-1){
            if(loop)frame = 0;
            else finished = true;
        }
        else{
            frame ++;
        }
    }
    
    public void addHold(int frames){
        Float point = animation.get(animation.size()-1);
        for(int i = 1; i < frames+1; i ++){
            animation.add(point);
        }
    }
    public void addMotion(float val, int frames, int type){
        addMotion( animation.get(animation.size()-1), val, frames, type);
    }
    public void addMotion(float startval, float endval, int frames, int type){
        addMotion(startval,endval,frames,type,1f);
    }
    public void addMotion(float startval, float endval, int frames, int type, float extremity){
        float start = startval;
        float end = endval;
        float distance = end-start;
        
        switch(type){
            case HOLD:
                for(int i = 1; i < frames+1; i ++){
                    animation.add(end);
                }
                break;
            case SLOW_TOWARDS:
                
                for(int i = 1; i < frames+1; i ++){
                    animation.add(start + (float)(Math.pow(Math.sin(((float)i/frames)*Math.PI/2),1f/extremity)*distance));
                }
                break;
            case SPEED_TOWARDS:
                
                for(int i = 1; i < frames+1; i ++){
                    animation.add(start + (float)(Math.pow(Math.sin(((float)i/frames)*Math.PI/2-Math.PI/2)+1,1f/extremity)*distance));
                }
                
                break;
            case SMOOTH:
                for(int i = 1; i < frames+1; i ++){
                    animation.add( start + (float)(Math.pow((-Math.cos(((float)i/frames)*Math.PI)+1f)/2f,1/extremity)*distance));
                }
                
                break;
            case MOVE_EVEN:
                
                for(int i = 1; i < frames+1; i ++){
                    animation.add(start + ((float)i/frames)*distance);
                }
                
                break;
        }
    }
    public void addStateChange(){
        state_increments.add(animation.size()-1);
    }
    
    public int value(){
        return Math.round(animation.get(frame));
    }
    public float valueFloat(){
        return animation.get(frame);
    }
    
    public boolean isFinished(){
        return finished;
    }
    public void setFrame(int frame){
        this.frame = frame;
    }
    public int getFrame(){
        return frame;
    }
    public void restart(){
        frame = 0;
        finished = false;
    }
    public int getState(){
        return state;
    }
    public void setState(int state){
        this.state = state;
    }
    public boolean stateChanged(){
        return state_changed;
    }
}

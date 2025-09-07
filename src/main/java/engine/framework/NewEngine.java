package engine.framework;

import engine.graphics.NanoTextures;

public abstract class NewEngine implements Runnable {
    
    private Thread thread;
    public NewWindow window;
    
    public int state = 0;
    public boolean multi_state = true;
    
    private boolean running = false;
    
    public void start(String name, int width, int height, boolean full) {
        window = new NewWindow(name, width, height, 5000, full);
        window.init();        
        // Initialize NanoVG textures
        NanoTextures.init(window.getNanoVGContext());
        loadLinear();
        // Load resources in separate thread
        new Thread(() -> {
            load();
        }).start();
            
        running = true;
        thread = new Thread(this);
        thread.start();
    }
    
    @Override
    public void run() {
        int tps = 60;
        long timePerTick = (long) (1000.0 / tps);
        
        int tickTimer = 0;
        long sumTime = 0;
        boolean dropFrame = false;
        
        while (running && !window.shouldClose()) {
            try {
                long startTime = System.currentTimeMillis();
                
                tick();
                
                if (!dropFrame) {
                    renderEngine();
                }
                
                tickTimer++;
                if (tickTimer > tps) {
                    tickTimer = 0;
                }
                
                long frameTime = System.currentTimeMillis() - startTime;
                long sleepTime = timePerTick - frameTime;
                sumTime += frameTime;
                
                dropFrame = sleepTime < 0;
                
                if (tickTimer == 0) {
                    System.out.println(((sumTime / (float) tps) / timePerTick) * 100 + "%");
                    sumTime = 0;
                }
                
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                if (!handleError(e)) {
                    break;
                }
            }
        }
        
        cleanup();
    }
    
    private void renderEngine() {
        window.update();
        
        // Begin NanoVG frame
        NanoTextures.beginFrame(getWidth(), getHeight());
        
        // Call your render method
        render();
        
        // End NanoVG frame
        NanoTextures.endFrame();
    }
    
    // Abstract methods for subclasses
    // loaded in separate thread
    public void load(){}
    // always loaded before game starts
    public void loadLinear(){}
    public void tick(){}
    public void render(){} // No more Graphics2D parameter!
    
    public boolean handleError(Exception e) {
        return false;
    }
    
    public void stop() {
        running = false;
    }
    
    private void cleanup() {
        NanoTextures.cleanup();
        window.cleanup();
    }
    
    // Window access methods
    public int getWidth() { return window.getWidth(); }
    public int getHeight() { return window.getHeight(); }
    public boolean wasResized() { return window.wasResized(); }
}
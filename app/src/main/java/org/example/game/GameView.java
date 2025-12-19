package org.example.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.example.entities.Player;
import org.example.entities.Teacher;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameEngine gameEngine;
    private GameThread gameThread;
    private float lastTouchX;
    private float lastTouchY;
    private long lastTapTime = 0;
    private static final long DOUBLE_TAP_TIME_DELTA = 300; // milliseconds
    private boolean isPanning = false;
    private float panStartX;
    private float panStartY;
    
    public GameView(Context context) {
        super(context);
        init();
    }
    
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        getHolder().addCallback(this);
        setFocusable(true);
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (gameEngine == null) {
            int width = getWidth();
            int height = getHeight();
            if (width == 0 || height == 0) {
                // Use default size if not measured yet
                width = 1080;
                height = 1920;
            }
            gameEngine = new GameEngine(width, height, getContext());
        }
        if (gameThread == null) {
            gameThread = new GameThread(getHolder(), this);
            gameThread.setRunning(true);
            gameThread.start();
        }
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (gameEngine != null && width > 0 && height > 0) {
            // Update camera viewport when screen size changes
            gameEngine.updateViewport(width, height);
        }
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        gameThread.setRunning(false);
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void update() {
        if (gameEngine != null) {
            gameEngine.update();
        }
    }
    
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (gameEngine != null) {
            gameEngine.draw(canvas);
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameEngine == null || gameEngine.getState() != org.example.game.GameState.PLAYING) {
            return super.onTouchEvent(event);
        }
        
        float x = event.getX();
        float y = event.getY();
        float screenWidth = getWidth();
        
        int action = event.getAction();
        
        // Check if touch is on a bonus icon first
        if (action == MotionEvent.ACTION_DOWN) {
            // Check for double-tap to toggle vision cone visualization (debug mode)
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTapTime < DOUBLE_TAP_TIME_DELTA) {
                // Double tap detected - toggle vision cone visualization
                Teacher.toggleVisionConeVisualization();
                lastTapTime = 0; // Reset to prevent triple-tap
                return true; // Consume the event
            }
            lastTapTime = currentTime;
            
            // Check if touch is on a bonus icon
            if (gameEngine.handleBonusTouch(x, y, screenWidth)) {
                return true; // Bonus was activated
            }
            
            // Check if we should start panning (not on joystick area)
            // Joystick is typically in bottom-right corner, so we'll let it handle its own touches
            // For now, we'll handle panning for touches that aren't on bonus icons
            // and let joystick handle its area
            isPanning = false; // Will be set to true on ACTION_MOVE if movement detected
            panStartX = x;
            panStartY = y;
            lastTouchX = x;
            lastTouchY = y;
        } else if (action == MotionEvent.ACTION_MOVE) {
            // Check if this is a pan gesture (significant movement)
            float dx = x - panStartX;
            float dy = y - panStartY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            // Start panning if moved more than 10 pixels
            if (!isPanning && distance > 10) {
                isPanning = true;
                // Switch to manual pan mode if we were following player
                if (gameEngine.getCameraMode() == CameraMode.FOLLOW_PLAYER) {
                    gameEngine.setCameraMode(CameraMode.MANUAL_PAN);
                }
            }
            
            // Pan the camera if we're panning
            if (isPanning) {
                float deltaX = x - lastTouchX;
                float deltaY = y - lastTouchY;
                gameEngine.panCamera(deltaX, deltaY);
                lastTouchX = x;
                lastTouchY = y;
                return true; // Consume the event
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            // End panning
            if (isPanning) {
                isPanning = false;
                return true;
            } else {
                // It was a tap (not a drag)
                // In MANUAL_PAN mode, tap switches back to FOLLOW_PLAYER
                if (gameEngine.getCameraMode() == CameraMode.MANUAL_PAN) {
                    float dx = x - panStartX;
                    float dy = y - panStartY;
                    float distance = (float) Math.sqrt(dx * dx + dy * dy);
                    if (distance < 20) {
                        // It was a tap, switch back to follow player
                        gameEngine.setCameraMode(CameraMode.FOLLOW_PLAYER);
                        return true;
                    }
                }
            }
            isPanning = false;
        }
        
        // Let joystick handle touch events if it's in the joystick area
        // Otherwise, let parent handle it (for other UI interactions)
        return super.onTouchEvent(event);
    }
    
    public void handleJoystickInput(float xPercent, float yPercent) {
        if (gameEngine == null || gameEngine.getState() != GameState.PLAYING) {
            return;
        }
        
        Player player = gameEngine.getPlayer();
        if (player == null) {
            return;
        }
        
        // Use a threshold to prevent tiny movements
        float threshold = 0.1f;
        
        player.setMovingUp(yPercent < -threshold);
        player.setMovingDown(yPercent > threshold);
        player.setMovingLeft(xPercent < -threshold);
        player.setMovingRight(xPercent > threshold);
    }
    
    public void handleJoystickRelease() {
        if (gameEngine == null) {
            return;
        }
        
        Player player = gameEngine.getPlayer();
        if (player != null) {
            player.stopMovement();
        }
    }
    
    public GameEngine getGameEngine() {
        return gameEngine;
    }
    
    private class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private GameView gameView;
        private boolean running;
        
        public GameThread(SurfaceHolder holder, GameView view) {
            surfaceHolder = holder;
            gameView = view;
        }
        
        public void setRunning(boolean running) {
            this.running = running;
        }
        
        @Override
        public void run() {
            Canvas canvas;
            while (running) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        if (canvas != null) {
                            gameView.update();
                            gameView.draw(canvas);
                        }
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                
                // Control frame rate (target 60 FPS)
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


package org.example.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.example.entities.Player;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameEngine gameEngine;
    private GameThread gameThread;
    private float lastTouchX;
    private float lastTouchY;
    
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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Check if touch is on a bonus icon
            if (gameEngine != null && gameEngine.getState() == org.example.game.GameState.PLAYING) {
                float x = event.getX();
                float y = event.getY();
                float screenWidth = getWidth();
                if (gameEngine.handleBonusTouch(x, y, screenWidth)) {
                    return true; // Bonus was activated
                }
            }
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


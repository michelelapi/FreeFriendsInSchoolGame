package org.example.game;

import android.graphics.RectF;

public class Camera {
    private float x;
    private float y;
    private float viewportWidth;
    private float viewportHeight;
    private float worldWidth;
    private float worldHeight;
    
    public Camera(float viewportWidth, float viewportHeight, float worldWidth, float worldHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.x = 0;
        this.y = 0;
    }
    
    public void update(float targetX, float targetY) {
        // Center camera on target (player)
        x = targetX - viewportWidth / 2;
        y = targetY - viewportHeight / 2;
        
        // Clamp camera to world boundaries
        x = Math.max(0, Math.min(x, worldWidth - viewportWidth));
        y = Math.max(0, Math.min(y, worldHeight - viewportHeight));
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getViewportWidth() {
        return viewportWidth;
    }
    
    public float getViewportHeight() {
        return viewportHeight;
    }
    
    public RectF getViewport() {
        return new RectF(x, y, x + viewportWidth, y + viewportHeight);
    }
    
    public void setWorldSize(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }
    
    public void setViewportSize(float viewportWidth, float viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        // Re-clamp camera position with new viewport size
        x = Math.max(0, Math.min(x, worldWidth - viewportWidth));
        y = Math.max(0, Math.min(y, worldHeight - viewportHeight));
    }
    
    /**
     * Pans the camera by the specified amount
     * @param dx Amount to pan in X direction (positive = right, negative = left)
     * @param dy Amount to pan in Y direction (positive = down, negative = up)
     */
    public void panBy(float dx, float dy) {
        x += dx;
        y += dy;
        // Clamp camera to world boundaries
        x = Math.max(0, Math.min(x, worldWidth - viewportWidth));
        y = Math.max(0, Math.min(y, worldHeight - viewportHeight));
    }
    
    /**
     * Sets the camera position directly
     * @param x New X position
     * @param y New Y position
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        // Clamp camera to world boundaries
        this.x = Math.max(0, Math.min(this.x, worldWidth - viewportWidth));
        this.y = Math.max(0, Math.min(this.y, worldHeight - viewportHeight));
    }
}


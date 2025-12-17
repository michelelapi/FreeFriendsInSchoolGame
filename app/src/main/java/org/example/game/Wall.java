package org.example.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Wall {
    private float x;
    private float y;
    private float width;
    private float height;
    private Paint brickPaint;
    private Paint mortarPaint;
    private SpriteManager spriteManager;
    private static final float BRICK_WIDTH = 40f;
    private static final float BRICK_HEIGHT = 20f;
    private static final float MORTAR_WIDTH = 2f;
    
    public Wall(float x, float y, float width, float height, SpriteManager spriteManager) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.spriteManager = spriteManager;
        
        // Brick color (reddish-brown like Minecraft)
        brickPaint = new Paint();
        brickPaint.setColor(Color.rgb(150, 75, 0));
        brickPaint.setStyle(Paint.Style.FILL);
        
        // Mortar color (gray)
        mortarPaint = new Paint();
        mortarPaint.setColor(Color.rgb(100, 100, 100));
        mortarPaint.setStyle(Paint.Style.FILL);
    }
    
    public RectF getBounds() {
        return new RectF(x, y, x + width, y + height);
    }
    
    public void draw(Canvas canvas, float cameraX, float cameraY) {
        // Draw at world coordinates - camera transform is already applied to canvas
        
        // Try to use sprite tile if available
        if (spriteManager != null && spriteManager.getWallTileSprite() != null) {
            spriteManager.drawTiledSprite(canvas, spriteManager.getWallTileSprite(), x, y, width, height);
        } else {
            // Fallback to brick pattern
            // Draw mortar background
            canvas.drawRect(x, y, x + width, y + height, mortarPaint);
            
            // Draw brick pattern
            float currentY = y;
            boolean offsetRow = false;
            
            while (currentY < y + height) {
                float currentX = x;
                if (offsetRow) {
                    currentX -= BRICK_WIDTH / 2; // Offset every other row
                }
                
                while (currentX < x + width) {
                    float brickEndX = Math.min(currentX + BRICK_WIDTH, x + width);
                    float brickEndY = Math.min(currentY + BRICK_HEIGHT, y + height);
                    
                    // Only draw brick if it's within bounds
                    if (brickEndX > x && brickEndY > y) {
                        canvas.drawRect(
                            Math.max(currentX, x),
                            Math.max(currentY, y),
                            brickEndX,
                            brickEndY,
                            brickPaint
                        );
                    }
                    
                    currentX += BRICK_WIDTH + MORTAR_WIDTH;
                }
                
                currentY += BRICK_HEIGHT + MORTAR_WIDTH;
                offsetRow = !offsetRow;
            }
        }
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
}


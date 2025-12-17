package org.example.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.example.game.SpriteManager;

public class Friend extends Entity {
    private boolean rescued = false;
    private Paint paint;
    private SpriteManager spriteManager;
    
    public Friend(float x, float y, SpriteManager spriteManager) {
        super(x, y, 30, 30);
        this.spriteManager = spriteManager;
        this.paint = new Paint();
        this.paint.setColor(Color.GREEN);
        this.paint.setStyle(Paint.Style.FILL);
        
        // Update size if sprite is available
        if (spriteManager != null) {
            this.width = spriteManager.getFriendWidth();
            this.height = spriteManager.getFriendHeight();
        }
    }
    
    @Override
    public void update(float deltaTime) {
        // Friends don't move
    }
    
    @Override
    public void draw(Canvas canvas, float cameraX, float cameraY) {
        if (!rescued) {
            // Draw at world coordinates - camera transform is already applied to canvas
            if (spriteManager != null && spriteManager.getFriendSprite() != null) {
                // Draw sprite if available
                spriteManager.drawSprite(canvas, spriteManager.getFriendSprite(), x, y, width, height);
            } else {
                // Fallback to colored rectangle
                canvas.drawRect(x, y, x + width, y + height, paint);
            }
        }
    }
    
    public boolean isRescued() {
        return rescued;
    }
    
    public void rescue() {
        this.rescued = true;
    }
}


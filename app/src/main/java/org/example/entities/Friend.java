package org.example.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Friend extends Entity {
    private boolean rescued = false;
    private Paint paint;
    
    public Friend(float x, float y) {
        super(x, y, 30, 30);
        this.paint = new Paint();
        this.paint.setColor(Color.GREEN);
        this.paint.setStyle(Paint.Style.FILL);
    }
    
    @Override
    public void update(float deltaTime) {
        // Friends don't move
    }
    
    @Override
    public void draw(Canvas canvas, float cameraX, float cameraY) {
        if (!rescued) {
            // Draw at world coordinates - camera transform is already applied to canvas
            canvas.drawRect(x, y, x + width, y + height, paint);
        }
    }
    
    public boolean isRescued() {
        return rescued;
    }
    
    public void rescue() {
        this.rescued = true;
    }
}


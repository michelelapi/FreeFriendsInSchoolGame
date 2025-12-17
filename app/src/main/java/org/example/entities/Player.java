package org.example.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player extends Entity {
    private static final float DEFAULT_SPEED = 200f; // pixels per second
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private Paint paint;

    public Player(float x, float y) {
        super(x, y, 40, 40);
        this.speed = DEFAULT_SPEED;
        this.paint = new Paint();
        this.paint.setColor(Color.BLUE);
        this.paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void update(float deltaTime) {
        velocityX = 0;
        velocityY = 0;

        if (movingUp)
            velocityY = -speed;
        if (movingDown)
            velocityY = speed;
        if (movingLeft)
            velocityX = -speed;
        if (movingRight)
            velocityX = speed;

        // Normalize diagonal movement
        if (velocityX != 0 && velocityY != 0) {
            float factor = (float) (speed / Math.sqrt(velocityX * velocityX + velocityY * velocityY));
            velocityX *= factor;
            velocityY *= factor;
        }

        x += velocityX * deltaTime;
        y += velocityY * deltaTime;
    }

    @Override
    public void draw(Canvas canvas, float cameraX, float cameraY) {
        // Draw at world coordinates - camera transform is already applied to canvas
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public void setMovingUp(boolean moving) {
        this.movingUp = moving;
    }

    public void setMovingDown(boolean moving) {
        this.movingDown = moving;
    }

    public void setMovingLeft(boolean moving) {
        this.movingLeft = moving;
    }

    public void setMovingRight(boolean moving) {
        this.movingRight = moving;
    }

    public void stopMovement() {
        movingUp = false;
        movingDown = false;
        movingLeft = false;
        movingRight = false;
    }
}

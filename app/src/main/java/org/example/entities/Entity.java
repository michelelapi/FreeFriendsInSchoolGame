package org.example.entities;

import android.graphics.Canvas;
import android.graphics.RectF;

public abstract class Entity {
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected float velocityX;
    protected float velocityY;
    protected float speed;
    
    public Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityX = 0;
        this.velocityY = 0;
        this.speed = 0;
    }
    
    public abstract void update(float deltaTime);
    
    public abstract void draw(Canvas canvas, float cameraX, float cameraY);
    
    public RectF getBounds() {
        return new RectF(x, y, x + width, y + height);
    }
    
    public boolean intersects(Entity other) {
        return getBounds().intersect(other.getBounds());
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
    
    public float getCenterX() {
        return x + width / 2;
    }
    
    public float getCenterY() {
        return y + height / 2;
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}


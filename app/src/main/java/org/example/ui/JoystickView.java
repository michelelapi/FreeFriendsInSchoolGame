package org.example.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {
    private Paint outerCirclePaint;
    private Paint innerCirclePaint;
    private Paint gearPaint;
    
    private float centerX;
    private float centerY;
    private float outerRadius;
    private float innerRadius;
    private float joystickX;
    private float joystickY;
    private float maxDistance;
    
    private boolean isPressed = false;
    private JoystickListener listener;
    
    public interface JoystickListener {
        void onJoystickMoved(float xPercent, float yPercent);
        void onJoystickReleased();
    }
    
    public JoystickView(Context context) {
        super(context);
        init();
    }
    
    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerCirclePaint.setColor(Color.argb(150, 100, 100, 100));
        outerCirclePaint.setStyle(Paint.Style.FILL);
        
        innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint.setColor(Color.argb(200, 50, 150, 255));
        innerCirclePaint.setStyle(Paint.Style.FILL);
        
        gearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gearPaint.setColor(Color.argb(180, 80, 80, 80));
        gearPaint.setStyle(Paint.Style.STROKE);
        gearPaint.setStrokeWidth(4);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        outerRadius = Math.min(w, h) / 2f - 20;
        innerRadius = outerRadius * 0.4f;
        maxDistance = outerRadius - innerRadius;
        joystickX = centerX;
        joystickY = centerY;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw outer circle (base)
        canvas.drawCircle(centerX, centerY, outerRadius, outerCirclePaint);
        
        // Draw gear icon in the center
        drawGear(canvas, centerX, centerY, outerRadius * 0.6f);
        
        // Draw inner circle (joystick handle)
        canvas.drawCircle(joystickX, joystickY, innerRadius, innerCirclePaint);
    }
    
    private void drawGear(Canvas canvas, float cx, float cy, float radius) {
        int teeth = 8;
        float toothLength = radius * 0.3f;
        float innerRadius = radius * 0.6f;
        
        Path gearPath = new Path();
        
        for (int i = 0; i < teeth; i++) {
            float angle = (float) (i * 2 * Math.PI / teeth);
            float x1 = cx + (float) Math.cos(angle) * innerRadius;
            float y1 = cy + (float) Math.sin(angle) * innerRadius;
            float x2 = cx + (float) Math.cos(angle) * radius;
            float y2 = cy + (float) Math.sin(angle) * radius;
            float x3 = cx + (float) Math.cos(angle + Math.PI / teeth) * radius;
            float y3 = cy + (float) Math.sin(angle + Math.PI / teeth) * radius;
            float x4 = cx + (float) Math.cos(angle + Math.PI / teeth) * innerRadius;
            float y4 = cy + (float) Math.sin(angle + Math.PI / teeth) * innerRadius;
            
            if (i == 0) {
                gearPath.moveTo(x1, y1);
            } else {
                gearPath.lineTo(x1, y1);
            }
            gearPath.lineTo(x2, y2);
            gearPath.lineTo(x3, y3);
            gearPath.lineTo(x4, y4);
        }
        gearPath.close();
        
        canvas.drawPath(gearPath, gearPaint);
        
        // Draw center circle
        Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.argb(180, 80, 80, 80));
        centerPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, innerRadius * 0.5f, centerPaint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Only handle joystick if game is playing
        org.example.game.GameEngine engine = getGameEngine();
        if (engine != null && engine.getState() != org.example.game.GameState.PLAYING) {
            return false;
        }
        
        float touchX = event.getX();
        float touchY = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInBounds(touchX, touchY)) {
                    isPressed = true;
                    updateJoystickPosition(touchX, touchY);
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_MOVE:
                if (isPressed) {
                    updateJoystickPosition(touchX, touchY);
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isPressed) {
                    isPressed = false;
                    resetJoystick();
                    if (listener != null) {
                        listener.onJoystickReleased();
                    }
                    return true;
                }
                break;
        }
        
        return false;
    }
    
    private boolean isInBounds(float x, float y) {
        float dx = x - centerX;
        float dy = y - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance <= outerRadius + 50; // Add some padding for easier touch
    }
    
    private void updateJoystickPosition(float touchX, float touchY) {
        float dx = touchX - centerX;
        float dy = touchY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > maxDistance) {
            dx = (dx / distance) * maxDistance;
            dy = (dy / distance) * maxDistance;
            distance = maxDistance;
        }
        
        joystickX = centerX + dx;
        joystickY = centerY + dy;
        
        // Calculate normalized values (-1 to 1)
        float xPercent = dx / maxDistance;
        float yPercent = dy / maxDistance;
        
        if (listener != null) {
            listener.onJoystickMoved(xPercent, yPercent);
        }
        
        invalidate();
    }
    
    private void resetJoystick() {
        joystickX = centerX;
        joystickY = centerY;
        invalidate();
    }
    
    public void setJoystickListener(JoystickListener listener) {
        this.listener = listener;
    }
    
    private org.example.game.GameEngine gameEngine;
    
    public void setGameEngine(org.example.game.GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    private org.example.game.GameEngine getGameEngine() {
        return gameEngine;
    }
}


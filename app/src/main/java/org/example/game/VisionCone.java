package org.example.game;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a vision cone for the teacher.
 * 45-degree cone (22.5 degrees on each side) with a specified length.
 */
public class VisionCone {
    private static final float CONE_ANGLE = (float) (Math.PI / 4); // 45 degrees
    private static final float HALF_CONE_ANGLE = CONE_ANGLE / 2; // 22.5 degrees
    private static final int SAMPLES_PER_SIDE = 10; // Number of samples per side for collision detection
    
    private float length;
    private SchoolLayout schoolLayout;
    
    public VisionCone(float length, SchoolLayout schoolLayout) {
        this.length = length;
        this.schoolLayout = schoolLayout;
    }
    
    /**
     * Checks if the vision cone collides with any walls
     * @param centerX X position of the teacher's center
     * @param centerY Y position of the teacher's center
     * @param direction Direction the teacher is facing (in radians)
     * @return true if vision cone hits a wall
     */
    public boolean hitsWall(float centerX, float centerY, float direction) {
        List<Wall> walls = schoolLayout.getWalls();
        
        // Sample points along the cone edges and center
        for (int i = 0; i <= SAMPLES_PER_SIDE; i++) {
            // Left edge of cone
            float leftAngle = direction - HALF_CONE_ANGLE;
            float leftProgress = (float) i / SAMPLES_PER_SIDE;
            float leftX = centerX + (float) (Math.cos(leftAngle) * length * leftProgress);
            float leftY = centerY + (float) (Math.sin(leftAngle) * length * leftProgress);
            
            // Right edge of cone
            float rightAngle = direction + HALF_CONE_ANGLE;
            float rightProgress = (float) i / SAMPLES_PER_SIDE;
            float rightX = centerX + (float) (Math.cos(rightAngle) * length * rightProgress);
            float rightY = centerY + (float) (Math.sin(rightAngle) * length * rightProgress);
            
            // Center of cone
            float centerProgress = (float) i / SAMPLES_PER_SIDE;
            float centerXPoint = centerX + (float) (Math.cos(direction) * length * centerProgress);
            float centerYPoint = centerY + (float) (Math.sin(direction) * length * centerProgress);
            
            // Check if any of these points intersect with walls
            for (Wall wall : walls) {
                RectF wallBounds = wall.getBounds();
                
                // Check left edge point
                if (wallBounds.contains(leftX, leftY)) {
                    return true;
                }
                
                // Check right edge point
                if (wallBounds.contains(rightX, rightY)) {
                    return true;
                }
                
                // Check center point
                if (wallBounds.contains(centerXPoint, centerYPoint)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Finds an opening (door/gap) in the wall by rotating the vision cone
     * @param centerX X position of the teacher's center
     * @param centerY Y position of the teacher's center
     * @param startDirection Starting direction to search from
     * @param searchRange Range to search (in radians, e.g., Math.PI * 2 for full circle)
     * @param stepSize Step size for rotation (in radians)
     * @return Direction to an opening, or null if none found
     */
    public Float findOpening(float centerX, float centerY, float startDirection, float searchRange, float stepSize) {
        List<Wall> walls = schoolLayout.getWalls();
        
        // Search in both directions (clockwise and counter-clockwise)
        int steps = (int) (searchRange / stepSize);
        
        for (int i = 0; i <= steps; i++) {
            // Try clockwise rotation
            float clockwiseDir = normalizeAngle(startDirection + i * stepSize);
            if (!hitsWall(centerX, centerY, clockwiseDir)) {
                return clockwiseDir;
            }
            
            // Try counter-clockwise rotation
            float counterClockwiseDir = normalizeAngle(startDirection - i * stepSize);
            if (counterClockwiseDir != clockwiseDir) { // Avoid checking same direction twice
                if (!hitsWall(centerX, centerY, counterClockwiseDir)) {
                    return counterClockwiseDir;
                }
            }
        }
        
        return null; // No opening found
    }
    
    /**
     * Finds the best opening direction toward a target
     * @param centerX X position of the teacher's center
     * @param centerY Y position of the teacher's center
     * @param targetX Target X position
     * @param targetY Target Y position
     * @param currentDirection Current facing direction
     * @return Direction to an opening that's closest to the target direction
     */
    public Float findBestOpeningTowardTarget(float centerX, float centerY, float targetX, float targetY, 
                                             float currentDirection) {
        // Calculate direction to target
        float targetDirection = (float) Math.atan2(targetY - centerY, targetX - centerX);
        
        // Search for openings, prioritizing directions closer to target
        float searchRange = (float) (Math.PI * 2); // Full circle
        float stepSize = (float) (Math.PI / 18); // 10 degree steps
        
        Float bestOpening = null;
        float bestScore = Float.NEGATIVE_INFINITY;
        
        int steps = (int) (searchRange / stepSize);
        for (int i = 0; i <= steps; i++) {
            // Try direction
            float testDir = normalizeAngle(targetDirection + (i - steps/2) * stepSize);
            
            if (!hitsWall(centerX, centerY, testDir)) {
                // Calculate score: prefer directions closer to target
                float angleDiff = Math.abs(normalizeAngle(testDir - targetDirection));
                if (angleDiff > Math.PI) {
                    angleDiff = (float) (Math.PI * 2 - angleDiff);
                }
                float score = (float) Math.PI - angleDiff; // Closer = higher score
                
                if (score > bestScore) {
                    bestScore = score;
                    bestOpening = testDir;
                }
            }
        }
        
        return bestOpening;
    }
    
    /**
     * Normalizes an angle to 0-2π range
     */
    private float normalizeAngle(float angle) {
        while (angle < 0) {
            angle += Math.PI * 2;
        }
        while (angle >= Math.PI * 2) {
            angle -= Math.PI * 2;
        }
        return angle;
    }
    
    /**
     * Gets the length of the vision cone
     */
    public float getLength() {
        return length;
    }
    
    /**
     * Sets the length of the vision cone
     */
    public void setLength(float length) {
        this.length = length;
    }
}


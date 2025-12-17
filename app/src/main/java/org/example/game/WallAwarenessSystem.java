package org.example.game;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * System that caches wall positions and provides spatial queries to avoid getting stuck.
 * Uses spatial hashing for efficient wall lookups.
 */
public class WallAwarenessSystem {
    private static final float CELL_SIZE = 100f; // Size of each spatial hash cell
    private Map<String, List<Wall>> spatialHash; // Spatial hash: "x,y" -> List of walls in that cell
    private List<Wall> allWalls; // All walls for fallback
    private float worldWidth;
    private float worldHeight;
    
    // Problematic areas where entities got stuck (for learning)
    private Map<String, Integer> problematicAreas; // "x,y" -> count of times stuck here
    
    public WallAwarenessSystem(SchoolLayout schoolLayout, float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.allWalls = new ArrayList<>(schoolLayout.getWalls());
        this.spatialHash = new HashMap<>();
        this.problematicAreas = new HashMap<>();
        
        buildSpatialHash();
    }
    
    /**
     * Builds a spatial hash for efficient wall lookups
     */
    private void buildSpatialHash() {
        for (Wall wall : allWalls) {
            RectF bounds = wall.getBounds();
            
            // Calculate which cells this wall occupies
            int minCellX = (int) (bounds.left / CELL_SIZE);
            int maxCellX = (int) (bounds.right / CELL_SIZE);
            int minCellY = (int) (bounds.top / CELL_SIZE);
            int maxCellY = (int) (bounds.bottom / CELL_SIZE);
            
            // Add wall to all cells it occupies
            for (int x = minCellX; x <= maxCellX; x++) {
                for (int y = minCellY; y <= maxCellY; y++) {
                    String key = x + "," + y;
                    if (!spatialHash.containsKey(key)) {
                        spatialHash.put(key, new ArrayList<>());
                    }
                    spatialHash.get(key).add(wall);
                }
            }
        }
    }
    
    /**
     * Gets the spatial hash key for a position
     */
    private String getCellKey(float x, float y) {
        int cellX = (int) (x / CELL_SIZE);
        int cellY = (int) (y / CELL_SIZE);
        return cellX + "," + cellY;
    }
    
    /**
     * Checks if a position would collide with any wall
     */
    public boolean wouldCollide(float x, float y, float width, float height) {
        RectF testBounds = new RectF(x, y, x + width, y + height);
        
        // Get cells that might contain walls
        int minCellX = (int) (x / CELL_SIZE);
        int maxCellX = (int) ((x + width) / CELL_SIZE);
        int minCellY = (int) (y / CELL_SIZE);
        int maxCellY = (int) ((y + height) / CELL_SIZE);
        
        // Check walls in relevant cells
        for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
            for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                String key = cellX + "," + cellY;
                List<Wall> walls = spatialHash.get(key);
                if (walls != null) {
                    for (Wall wall : walls) {
                        if (testBounds.intersect(wall.getBounds())) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Checks if there's a wall ahead in a given direction
     */
    public boolean hasWallAhead(float fromX, float fromY, float direction, float checkDistance, float entityWidth, float entityHeight) {
        float checkX = fromX + (float) (Math.cos(direction) * checkDistance);
        float checkY = fromY + (float) (Math.sin(direction) * checkDistance);
        
        return wouldCollide(checkX - entityWidth / 2, checkY - entityHeight / 2, entityWidth, entityHeight);
    }
    
    /**
     * Checks if there's a wall to the left
     */
    public boolean hasWallLeft(float fromX, float fromY, float direction, float checkDistance, float entityWidth, float entityHeight) {
        float leftDirection = direction + (float) (Math.PI / 2);
        float checkX = fromX + (float) (Math.cos(leftDirection) * checkDistance);
        float checkY = fromY + (float) (Math.sin(leftDirection) * checkDistance);
        
        return wouldCollide(checkX - entityWidth / 2, checkY - entityHeight / 2, entityWidth, entityHeight);
    }
    
    /**
     * Checks if there's a wall to the right
     */
    public boolean hasWallRight(float fromX, float fromY, float direction, float checkDistance, float entityWidth, float entityHeight) {
        float rightDirection = direction - (float) (Math.PI / 2);
        float checkX = fromX + (float) (Math.cos(rightDirection) * checkDistance);
        float checkY = fromY + (float) (Math.sin(rightDirection) * checkDistance);
        
        return wouldCollide(checkX - entityWidth / 2, checkY - entityHeight / 2, entityWidth, entityHeight);
    }
    
    /**
     * Finds the best direction to avoid walls, preferring directions that don't lead to walls
     */
    public float findBestDirection(float fromX, float fromY, float desiredDirection, float entityWidth, float entityHeight) {
        float checkDistance = 80f; // How far ahead to check
        
        // Check desired direction first
        if (!hasWallAhead(fromX, fromY, desiredDirection, checkDistance, entityWidth, entityHeight)) {
            return desiredDirection;
        }
        
        // Try 8 directions around the desired direction
        float[] testDirections = {
            desiredDirection,                           // 0 degrees
            desiredDirection + (float) (Math.PI / 4),   // 45 degrees
            desiredDirection + (float) (Math.PI / 2),   // 90 degrees
            desiredDirection + (float) (3 * Math.PI / 4), // 135 degrees
            desiredDirection + (float) Math.PI,         // 180 degrees
            desiredDirection - (float) (3 * Math.PI / 4), // -135 degrees
            desiredDirection - (float) (Math.PI / 2),   // -90 degrees
            desiredDirection - (float) (Math.PI / 4)    // -45 degrees
        };
        
        // Find the best direction (closest to desired that doesn't hit a wall)
        float bestDirection = desiredDirection;
        float bestScore = Float.NEGATIVE_INFINITY;
        
        for (float dir : testDirections) {
            if (!hasWallAhead(fromX, fromY, dir, checkDistance, entityWidth, entityHeight)) {
                // Calculate score: prefer directions closer to desired direction
                float angleDiff = Math.abs(normalizeAngle(dir - desiredDirection));
                if (angleDiff > Math.PI) {
                    angleDiff = (float) (Math.PI * 2 - angleDiff);
                }
                float score = (float) Math.PI - angleDiff; // Closer = higher score
                
                if (score > bestScore) {
                    bestScore = score;
                    bestDirection = dir;
                }
            }
        }
        
        // If all directions have walls, try perpendicular directions
        if (bestScore == Float.NEGATIVE_INFINITY) {
            float perp1 = desiredDirection + (float) (Math.PI / 2);
            float perp2 = desiredDirection - (float) (Math.PI / 2);
            
            if (!hasWallAhead(fromX, fromY, perp1, checkDistance, entityWidth, entityHeight)) {
                return perp1;
            } else if (!hasWallAhead(fromX, fromY, perp2, checkDistance, entityWidth, entityHeight)) {
                return perp2;
            }
        }
        
        return bestDirection;
    }
    
    /**
     * Marks an area as problematic (where entity got stuck)
     */
    public void markProblematicArea(float x, float y) {
        String key = getCellKey(x, y);
        problematicAreas.put(key, problematicAreas.getOrDefault(key, 0) + 1);
    }
    
    /**
     * Checks if an area is problematic (has been stuck before)
     */
    public boolean isProblematicArea(float x, float y) {
        String key = getCellKey(x, y);
        return problematicAreas.containsKey(key) && problematicAreas.get(key) > 2;
    }
    
    /**
     * Gets alternative directions when stuck, avoiding problematic areas
     */
    public List<Float> getAlternativeDirections(float fromX, float fromY, float entityWidth, float entityHeight) {
        List<Float> alternatives = new ArrayList<>();
        float checkDistance = 100f;
        
        // Test 8 directions
        for (int i = 0; i < 8; i++) {
            float dir = (float) (i * Math.PI / 4);
            float testX = fromX + (float) (Math.cos(dir) * checkDistance);
            float testY = fromY + (float) (Math.sin(dir) * checkDistance);
            
            // Check if this direction is clear and not problematic
            if (!hasWallAhead(fromX, fromY, dir, checkDistance, entityWidth, entityHeight) &&
                !isProblematicArea(testX, testY)) {
                alternatives.add(dir);
            }
        }
        
        return alternatives;
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
}


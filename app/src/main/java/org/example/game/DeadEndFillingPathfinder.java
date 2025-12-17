package org.example.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the Dead-End Filling algorithm for maze navigation.
 * This algorithm identifies and marks dead ends in the maze, leaving only valid paths.
 */
public class DeadEndFillingPathfinder {
    private static final float GRID_CELL_SIZE = 40f; // Size of each grid cell
    private boolean[][] walkableGrid; // true = walkable, false = wall/obstacle
    private boolean[][] deadEndGrid; // true = dead end (filled), false = valid path
    private int gridWidth;
    private int gridHeight;
    private float worldWidth;
    private float worldHeight;
    private SchoolLayout schoolLayout;

    public DeadEndFillingPathfinder(SchoolLayout schoolLayout, float worldWidth, float worldHeight) {
        this.schoolLayout = schoolLayout;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.gridWidth = (int) Math.ceil(worldWidth / GRID_CELL_SIZE);
        this.gridHeight = (int) Math.ceil(worldHeight / GRID_CELL_SIZE);
        
        initializeGrid();
        fillDeadEnds();
    }

    /**
     * Initializes the grid by marking walkable and non-walkable cells
     */
    private void initializeGrid() {
        walkableGrid = new boolean[gridHeight][gridWidth];
        deadEndGrid = new boolean[gridHeight][gridWidth];

        // Mark all cells as walkable initially
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                walkableGrid[y][x] = true;
                deadEndGrid[y][x] = false;
            }
        }

        // Mark walls and obstacles as non-walkable
        for (Wall wall : schoolLayout.getWalls()) {
            markWallInGrid(wall);
        }

        // Mark rooms as walkable (they can be entered)
        // But we'll treat corridors as the main navigation paths
    }

    /**
     * Marks wall cells as non-walkable in the grid
     */
    private void markWallInGrid(Wall wall) {
        int startX = worldToGridX(wall.getX());
        int startY = worldToGridY(wall.getY());
        int endX = worldToGridX(wall.getX() + wall.getWidth());
        int endY = worldToGridY(wall.getY() + wall.getHeight());

        // Clamp to grid bounds
        startX = Math.max(0, Math.min(startX, gridWidth - 1));
        startY = Math.max(0, Math.min(startY, gridHeight - 1));
        endX = Math.max(0, Math.min(endX, gridWidth - 1));
        endY = Math.max(0, Math.min(endY, gridHeight - 1));

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                // Check if this grid cell center is inside the wall
                float cellCenterX = gridXToWorld(x) + GRID_CELL_SIZE / 2;
                float cellCenterY = gridYToWorld(y) + GRID_CELL_SIZE / 2;
                
                if (wall.getBounds().contains(cellCenterX, cellCenterY)) {
                    walkableGrid[y][x] = false;
                }
            }
        }
    }

    /**
     * Fills dead ends using the Dead-End Filling algorithm
     */
    private void fillDeadEnds() {
        boolean changed = true;
        
        // Iterate until no more dead ends are found
        while (changed) {
            changed = false;
            
            for (int y = 0; y < gridHeight; y++) {
                for (int x = 0; x < gridWidth; x++) {
                    // Skip if already marked as dead end or not walkable
                    if (deadEndGrid[y][x] || !walkableGrid[y][x]) {
                        continue;
                    }
                    
                    // Check if this is a dead end (only one walkable neighbor)
                    int walkableNeighbors = countWalkableNeighbors(x, y);
                    
                    if (walkableNeighbors <= 1) {
                        // This is a dead end, mark it
                        deadEndGrid[y][x] = true;
                        changed = true;
                    }
                }
            }
        }
    }

    /**
     * Counts walkable neighbors (not including dead ends) for a cell
     */
    private int countWalkableNeighbors(int x, int y) {
        int count = 0;
        int[] dx = {0, 1, 0, -1}; // N, E, S, W
        int[] dy = {-1, 0, 1, 0};
        
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            
            if (isValidCell(nx, ny) && 
                walkableGrid[ny][nx] && 
                !deadEndGrid[ny][nx]) {
                count++;
            }
        }
        
        return count;
    }

    /**
     * Checks if cell coordinates are valid
     */
    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < gridWidth && y >= 0 && y < gridHeight;
    }

    /**
     * Converts world X coordinate to grid X index
     */
    private int worldToGridX(float worldX) {
        return (int) (worldX / GRID_CELL_SIZE);
    }

    /**
     * Converts world Y coordinate to grid Y index
     */
    private int worldToGridY(float worldY) {
        return (int) (worldY / GRID_CELL_SIZE);
    }

    /**
     * Converts grid X index to world X coordinate
     */
    private float gridXToWorld(int gridX) {
        return gridX * GRID_CELL_SIZE;
    }

    /**
     * Converts grid Y index to world Y coordinate
     */
    private float gridYToWorld(int gridY) {
        return gridY * GRID_CELL_SIZE;
    }

    /**
     * Checks if a world position is in a dead end
     */
    public boolean isInDeadEnd(float worldX, float worldY) {
        int gridX = worldToGridX(worldX);
        int gridY = worldToGridY(worldY);
        
        if (!isValidCell(gridX, gridY)) {
            return true; // Out of bounds is considered a dead end
        }
        
        return deadEndGrid[gridY][gridX];
    }

    /**
     * Gets the best direction to move towards a target, avoiding dead ends
     * Returns an angle in radians
     */
    public float getDirectionToTarget(float fromX, float fromY, float toX, float toY) {
        int fromGridX = worldToGridX(fromX);
        int fromGridY = worldToGridY(fromY);
        
        // Check all 8 directions (N, NE, E, SE, S, SW, W, NW)
        float[] directions = {
            0f,                    // N
            (float) (Math.PI / 4), // NE
            (float) (Math.PI / 2), // E
            (float) (3 * Math.PI / 4), // SE
            (float) Math.PI,       // S
            (float) (-3 * Math.PI / 4), // SW
            (float) (-Math.PI / 2), // W
            (float) (-Math.PI / 4)  // NW
        };
        
        float bestDirection = (float) Math.atan2(toY - fromY, toX - fromX);
        float bestScore = Float.NEGATIVE_INFINITY;
        
        // Try each direction and score it
        for (float dir : directions) {
            float testX = fromX + (float) (Math.cos(dir) * GRID_CELL_SIZE * 2);
            float testY = fromY + (float) (Math.sin(dir) * GRID_CELL_SIZE * 2);
            
            // Check if this direction leads to a dead end
            if (isInDeadEnd(testX, testY)) {
                continue; // Skip dead ends
            }
            
            // Calculate score: prefer directions closer to target and not in dead ends
            float dx = toX - testX;
            float dy = toY - testY;
            float distanceToTarget = (float) Math.sqrt(dx * dx + dy * dy);
            
            // Score: closer to target is better, but avoid dead ends
            float score = -distanceToTarget;
            
            if (score > bestScore) {
                bestScore = score;
                bestDirection = dir;
            }
        }
        
        // If all directions lead to dead ends, use direct path (might be going to a room)
        if (bestScore == Float.NEGATIVE_INFINITY) {
            return (float) Math.atan2(toY - fromY, toX - fromX);
        }
        
        return bestDirection;
    }

    /**
     * Checks if a position is walkable (not a wall and not a dead end)
     */
    public boolean isWalkable(float worldX, float worldY) {
        int gridX = worldToGridX(worldX);
        int gridY = worldToGridY(worldY);
        
        if (!isValidCell(gridX, gridY)) {
            return false;
        }
        
        return walkableGrid[gridY][gridX] && !deadEndGrid[gridY][gridX];
    }

    /**
     * Gets a list of valid directions (not leading to dead ends) from a position
     */
    public List<Float> getValidDirections(float worldX, float worldY) {
        List<Float> validDirs = new ArrayList<>();
        float[] directions = {
            0f,                    // N
            (float) (Math.PI / 4), // NE
            (float) (Math.PI / 2), // E
            (float) (3 * Math.PI / 4), // SE
            (float) Math.PI,       // S
            (float) (-3 * Math.PI / 4), // SW
            (float) (-Math.PI / 2), // W
            (float) (-Math.PI / 4)  // NW
        };
        
        for (float dir : directions) {
            float testX = worldX + (float) (Math.cos(dir) * GRID_CELL_SIZE * 2);
            float testY = worldY + (float) (Math.sin(dir) * GRID_CELL_SIZE * 2);
            
            if (!isInDeadEnd(testX, testY)) {
                validDirs.add(dir);
            }
        }
        
        return validDirs;
    }
}


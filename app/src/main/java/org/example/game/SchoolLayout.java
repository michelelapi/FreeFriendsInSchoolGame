package org.example.game;

import java.util.ArrayList;
import java.util.List;

public class SchoolLayout {
    private List<Wall> walls;
    private List<Room> rooms;
    private float worldWidth;
    private float worldHeight;
    private float principalOfficeX;
    private float principalOfficeY;
    private static final float WALL_THICKNESS = 20f;
    private static final float CORRIDOR_WIDTH = 220f; // Increased from 150f for much better navigation
    private static final float ROOM_SIZE = 300f; // Increased from 200f to make rooms larger

    private SpriteManager spriteManager;

    public SchoolLayout(float worldWidth, float worldHeight, SpriteManager spriteManager) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.spriteManager = spriteManager;
        this.walls = new ArrayList<>();
        this.rooms = new ArrayList<>();
        generateLayout();
    }

    private void generateLayout() {
        // Create outer walls
        walls.add(new Wall(0, 0, worldWidth, WALL_THICKNESS, spriteManager)); // Top
        walls.add(new Wall(0, worldHeight - WALL_THICKNESS, worldWidth, WALL_THICKNESS, spriteManager)); // Bottom
        walls.add(new Wall(0, 0, WALL_THICKNESS, worldHeight, spriteManager)); // Left
        walls.add(new Wall(worldWidth - WALL_THICKNESS, 0, WALL_THICKNESS, worldHeight, spriteManager)); // Right

        // Create a grid of rooms with corridors
        float startX = 100;
        float startY = 100;
        int roomsPerRow = 4;
        int roomsPerCol = 3;
        float roomSpacing = ROOM_SIZE + CORRIDOR_WIDTH;

        // Create rooms
        for (int row = 0; row < roomsPerCol; row++) {
            for (int col = 0; col < roomsPerRow; col++) {
                float roomX = startX + col * roomSpacing;
                float roomY = startY + row * roomSpacing;

                // Room bounds
                Room room = new Room(roomX, roomY, ROOM_SIZE, ROOM_SIZE);
                rooms.add(room);

                float doorWidth = 150f; // Increased from 100f to make doorways much wider and easier to navigate
                float doorCenterX = roomX + ROOM_SIZE / 2;
                float doorCenterY = roomY + ROOM_SIZE / 2;

                // Top wall (with doorway if not first row)
                if (row > 0) {
                    // Left segment
                    walls.add(
                            new Wall(roomX, roomY, doorCenterX - doorWidth / 2 - roomX, WALL_THICKNESS, spriteManager));
                    // Right segment
                    walls.add(new Wall(doorCenterX + doorWidth / 2, roomY,
                            (roomX + ROOM_SIZE) - (doorCenterX + doorWidth / 2), WALL_THICKNESS, spriteManager));
                } else {
                    // Full top wall for first row
                    walls.add(new Wall(roomX, roomY, ROOM_SIZE, WALL_THICKNESS, spriteManager));
                }

                // Bottom wall (with doorway if not last row)
                if (row < roomsPerCol - 1) {
                    // Left segment
                    walls.add(new Wall(roomX, roomY + ROOM_SIZE - WALL_THICKNESS,
                            doorCenterX - doorWidth / 2 - roomX, WALL_THICKNESS, spriteManager));
                    // Right segment
                    walls.add(new Wall(doorCenterX + doorWidth / 2, roomY + ROOM_SIZE - WALL_THICKNESS,
                            (roomX + ROOM_SIZE) - (doorCenterX + doorWidth / 2), WALL_THICKNESS, spriteManager));
                } else {
                    // Full bottom wall for last row
                    walls.add(new Wall(roomX, roomY + ROOM_SIZE - WALL_THICKNESS, ROOM_SIZE, WALL_THICKNESS,
                            spriteManager));
                }

                // Left wall (with doorway if not first column)
                if (col > 0) {
                    // Top segment
                    walls.add(
                            new Wall(roomX, roomY, WALL_THICKNESS, doorCenterY - doorWidth / 2 - roomY, spriteManager));
                    // Bottom segment
                    walls.add(new Wall(roomX, doorCenterY + doorWidth / 2, WALL_THICKNESS,
                            (roomY + ROOM_SIZE) - (doorCenterY + doorWidth / 2), spriteManager));
                } else {
                    // Full left wall for first column
                    walls.add(new Wall(roomX, roomY, WALL_THICKNESS, ROOM_SIZE, spriteManager));
                }

                // Right wall (with doorway if not last column)
                if (col < roomsPerRow - 1) {
                    // Top segment
                    walls.add(new Wall(roomX + ROOM_SIZE - WALL_THICKNESS, roomY, WALL_THICKNESS,
                            doorCenterY - doorWidth / 2 - roomY, spriteManager));
                    // Bottom segment
                    walls.add(new Wall(roomX + ROOM_SIZE - WALL_THICKNESS, doorCenterY + doorWidth / 2,
                            WALL_THICKNESS, (roomY + ROOM_SIZE) - (doorCenterY + doorWidth / 2), spriteManager));
                } else {
                    // Full right wall for last column
                    walls.add(new Wall(roomX + ROOM_SIZE - WALL_THICKNESS, roomY, WALL_THICKNESS, ROOM_SIZE,
                            spriteManager));
                }
            }
        }

        // Place principal's office in the top-right corner area
        principalOfficeX = worldWidth - 300f;
        principalOfficeY = 700f;
    }

    /**
     * Gets the principal's office location [x, y]
     */
    public float[] getPrincipalOfficeLocation() {
        return new float[] { principalOfficeX, principalOfficeY };
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public boolean isInCorridor(float x, float y) {
        // Check if position is in a corridor (not inside a room)
        for (Room room : rooms) {
            if (room.contains(x, y)) {
                return false;
            }
        }
        return true;
    }

    public static class Room {
        private float x;
        private float y;
        private float width;
        private float height;

        public Room(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean contains(float px, float py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
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
}

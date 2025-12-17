package org.example.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.example.bonuses.Bonus;
import org.example.bonuses.PrincipalBonus;
import org.example.bonuses.ParentBonus;
import org.example.bonuses.AnotherTeacherBonus;
import org.example.entities.Friend;
import org.example.entities.Player;
import org.example.entities.Teacher;
import org.example.physics.CollisionDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {
    private GameState state;
    private Player player;
    private List<Teacher> teachers;
    private List<Friend> friends;
    private Camera camera;
    private SchoolLayout schoolLayout;
    private float worldWidth = 2000;
    private float worldHeight = 2000;
    private long lastUpdateTime;
    private int score = 0;
    private int friendsRescued = 0;
    private Paint backgroundPaint;
    private Paint corridorPaint;
    private Paint roomPaint;
    private static final float SQUARE_SIZE = 20f; // Size of each square in the pattern
    private Teacher teacherThatCaughtPlayer = null; // Track which teacher caught the player

    // Bonus system
    private List<Bonus> availableBonuses;
    private List<Bonus> collectedBonuses; // Bonuses collected by player
    private Random random;
    private static final float BONUS_CHANCE = 0.6f; // 60% chance to get a bonus when rescuing a friend
    private String activeBonusMessage = null;
    private float bonusMessageTimer = 0f;
    private static final float BONUS_MESSAGE_DURATION = 2f; // Show message for 2 seconds
    private boolean principalBonusActive = false; // Track if principal bonus is active

    // Bonus UI
    private static final float BONUS_ICON_SIZE = 60f;
    private static final float BONUS_ICON_SPACING = 70f;
    private static final float BONUS_ICON_MARGIN = 20f;

    public GameEngine(float viewportWidth, float viewportHeight) {
        this.state = GameState.PLAYING;
        this.camera = new Camera(viewportWidth, viewportHeight, worldWidth, worldHeight);
        this.teachers = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.lastUpdateTime = System.currentTimeMillis();
        this.random = new Random();

        // Initialize bonus system
        this.availableBonuses = new ArrayList<>();
        this.availableBonuses.add(new PrincipalBonus());
        this.availableBonuses.add(new ParentBonus());
        this.availableBonuses.add(new AnotherTeacherBonus());
        this.collectedBonuses = new ArrayList<>();
        this.collectedBonuses.add(new PrincipalBonus());
        this.collectedBonuses.add(new ParentBonus());
        this.collectedBonuses.add(new AnotherTeacherBonus());

        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(Color.rgb(240, 240, 240)); // Light gray floor

        // Paint for corridor squares (light gray)
        this.corridorPaint = new Paint();
        this.corridorPaint.setColor(Color.rgb(220, 220, 220)); // Light gray
        this.corridorPaint.setStyle(Paint.Style.FILL);

        // Paint for room squares (darker gray)
        this.roomPaint = new Paint();
        this.roomPaint.setColor(Color.rgb(180, 180, 180)); // Darker gray for rooms
        this.roomPaint.setStyle(Paint.Style.FILL);

        // Create school layout
        this.schoolLayout = new SchoolLayout(worldWidth, worldHeight);

        initializeLevel();
    }

    private void initializeLevel() {
        // Initialize player at starting position (in a corridor)
        player = new Player(150, 150);

        // Add friends in rooms first
        List<SchoolLayout.Room> rooms = schoolLayout.getRooms();
        for (int i = 0; i < Math.min(5, rooms.size()); i++) {
            SchoolLayout.Room room = rooms.get(i);
            // Place friend in center of room
            float friendX = room.getX() + room.getWidth() / 2 - 15;
            float friendY = room.getY() + room.getHeight() / 2 - 15;
            friends.add(new Friend(friendX, friendY));
        }

        // Add teachers and assign them to guard friends
        // Place teachers in corridors near their assigned friends, but distant from
        // player
        float playerStartX = 150f;
        float playerStartY = 150f;
        float minDistanceFromPlayer = 600f; // Minimum distance from player starting position

        for (int i = 0; i < friends.size() && i < 5; i++) {
            Friend friend = friends.get(i);
            SchoolLayout.Room friendRoom = rooms.get(i);

            float roomCenterX = friendRoom.getX() + friendRoom.getWidth() / 2;
            float roomCenterY = friendRoom.getY() + friendRoom.getHeight() / 2;

            // Calculate direction away from player
            float dirX = roomCenterX - playerStartX;
            float dirY = roomCenterY - playerStartY;
            float dirLength = (float) Math.sqrt(dirX * dirX + dirY * dirY);

            float teacherX = 0;
            float teacherY = 0;
            boolean foundGoodPosition = false;

            // Try different positions around the room (north, south, east, west)
            float[] offsets = {
                    // North of room
                    0f, -120f,
                    // South of room
                    0f, 120f,
                    // East of room
                    120f, 0f,
                    // West of room
                    -120f, 0f
            };

            // Try to find a good position that's far enough from player
            for (int offsetIdx = 0; offsetIdx < offsets.length; offsetIdx += 2) {
                float candidateX = roomCenterX + offsets[offsetIdx];
                float candidateY = roomCenterY + offsets[offsetIdx + 1];

                // Check distance from player
                float dx = candidateX - playerStartX;
                float dy = candidateY - playerStartY;
                float distanceFromPlayer = (float) Math.sqrt(dx * dx + dy * dy);

                // Check if position is in a corridor (not in a room) and far enough from player
                if (distanceFromPlayer >= minDistanceFromPlayer &&
                        schoolLayout.isInCorridor(candidateX, candidateY)) {
                    teacherX = candidateX;
                    teacherY = candidateY;
                    foundGoodPosition = true;
                    break;
                }
            }

            // If no good position found, place further away from player
            if (!foundGoodPosition) {
                if (dirLength > 0) {
                    // Normalize direction away from player
                    dirX /= dirLength;
                    dirY /= dirLength;

                    // Start from room center and move away from player until we're far enough
                    float baseDistance = 200f; // Start 200 pixels from room center
                    teacherX = roomCenterX + dirX * baseDistance;
                    teacherY = roomCenterY + dirY * baseDistance;

                    // Check if this position is far enough from player
                    float dx = teacherX - playerStartX;
                    float dy = teacherY - playerStartY;
                    float distanceFromPlayer = (float) Math.sqrt(dx * dx + dy * dy);

                    // If not far enough, move further away
                    if (distanceFromPlayer < minDistanceFromPlayer) {
                        float additionalDistance = minDistanceFromPlayer - distanceFromPlayer + 50f;
                        teacherX += dirX * additionalDistance;
                        teacherY += dirY * additionalDistance;
                    }

                    // Try to find a nearby corridor position if current position is not in corridor
                    if (!schoolLayout.isInCorridor(teacherX, teacherY)) {
                        // Try positions in a spiral pattern around the calculated position
                        float[] spiralOffsets = { 0f, -40f, 40f, 0f, 0f, 40f, -40f, 0f,
                                0f, -80f, 80f, 0f, 0f, 80f, -80f, 0f };
                        boolean foundCorridor = false;
                        for (int offsetIdx = 0; offsetIdx < spiralOffsets.length; offsetIdx += 2) {
                            float testX = teacherX + spiralOffsets[offsetIdx];
                            float testY = teacherY + spiralOffsets[offsetIdx + 1];
                            dx = testX - playerStartX;
                            dy = testY - playerStartY;
                            distanceFromPlayer = (float) Math.sqrt(dx * dx + dy * dy);

                            if (distanceFromPlayer >= minDistanceFromPlayer &&
                                    schoolLayout.isInCorridor(testX, testY)) {
                                teacherX = testX;
                                teacherY = testY;
                                foundCorridor = true;
                                break;
                            }
                        }
                        // If still no corridor found, keep the calculated position
                        // (it will be adjusted by collision detection)
                    }
                } else {
                    // Fallback: place far east of room (away from player at 150, 150)
                    teacherX = friendRoom.getX() + friendRoom.getWidth() + 500f;
                    teacherY = friendRoom.getY() + friendRoom.getHeight() / 2;
                }
            }

            // Final check: ensure teacher is at least minDistanceFromPlayer away
            float dx = teacherX - playerStartX;
            float dy = teacherY - playerStartY;
            float distanceFromPlayer = (float) Math.sqrt(dx * dx + dy * dy);

            if (distanceFromPlayer < minDistanceFromPlayer) {
                // Force placement further away
                if (dirLength > 0) {
                    float dirXNorm = dirX / dirLength;
                    float dirYNorm = dirY / dirLength;
                    float neededDistance = minDistanceFromPlayer - distanceFromPlayer + 100f;
                    teacherX += dirXNorm * neededDistance;
                    teacherY += dirYNorm * neededDistance;
                } else {
                    // Move east (away from player at 150, 150)
                    teacherX = playerStartX + minDistanceFromPlayer + 100f;
                }
            }

            Teacher teacher = new Teacher(teacherX, teacherY);
            teacher.setGuardedFriend(friend);
            teachers.add(teacher);
        }

        friendsRescued = 0;
        score = 0;
    }

    public void update() {
        if (state != GameState.PLAYING) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;

        // Cap deltaTime to prevent large jumps
        if (deltaTime > 0.1f) {
            deltaTime = 0.1f;
        }

        // Update player
        player.update(deltaTime);

        // Check wall collisions for player (boundary walls)
        if (CollisionDetector.checkWallCollision(player, worldWidth, worldHeight)) {
            CollisionDetector.resolveWallCollision(player, worldWidth, worldHeight);
        }
        // Check brick wall collisions for player
        if (CollisionDetector.checkWallCollision(player, schoolLayout.getWalls())) {
            CollisionDetector.resolveWallCollision(player, schoolLayout.getWalls());
        }

        // Update teachers
        for (Teacher teacher : teachers) {
            // Update teacher with player and friends information for guard/chase behavior
            teacher.update(deltaTime, schoolLayout, player, friends);

            // Check boundary walls
            boolean hitBoundary = CollisionDetector.checkWallCollision(teacher, worldWidth, worldHeight);
            if (hitBoundary) {
                CollisionDetector.resolveWallCollision(teacher, worldWidth, worldHeight);
                teacher.onWallCollision();
            }
            // Check brick wall collisions
            boolean hitWall = CollisionDetector.checkWallCollision(teacher, schoolLayout.getWalls());
            if (hitWall) {
                CollisionDetector.resolveWallCollision(teacher, schoolLayout.getWalls());
                teacher.onWallCollision();
            }
        }

        // Check collisions
        Teacher collidingTeacher = CollisionDetector.getCollidingTeacher(player, teachers);
        if (collidingTeacher != null) {
            teacherThatCaughtPlayer = collidingTeacher;
            state = GameState.QUESTION;
        }

        Friend rescuedFriend = CollisionDetector.checkPlayerFriendCollision(player, friends);
        if (rescuedFriend != null) {
            rescuedFriend.rescue();
            friendsRescued++;
            score += 100;

            // Randomly give a bonus (collect it, don't activate automatically)
            if (random.nextFloat() < BONUS_CHANCE && !availableBonuses.isEmpty()) {
                // Create a new instance of the bonus type
                Bonus bonusTemplate = availableBonuses.get(random.nextInt(availableBonuses.size()));
                Bonus collectedBonus = createBonusInstance(bonusTemplate);
                collectedBonuses.add(collectedBonus);
                score += 50; // Bonus points for getting a bonus

                // Show bonus message
                activeBonusMessage = "Collected: " + collectedBonus.getName() + "!";
                bonusMessageTimer = BONUS_MESSAGE_DURATION;
            }
        }

        // Update bonus message timer
        if (bonusMessageTimer > 0) {
            bonusMessageTimer -= deltaTime;
            if (bonusMessageTimer <= 0) {
                activeBonusMessage = null;
            }
        }

        // Check if principal bonus is active and all non-ignored teachers have arrived
        if (principalBonusActive) {
            boolean allTeachersArrived = true;
            for (Teacher teacher : teachers) {
                // Only check teachers that are going to office, haven't reached it, and aren't
                // ignored
                if (teacher.isGoingToPrincipalOffice() &&
                        !teacher.hasReachedPrincipalOffice() &&
                        !teacher.isIgnoredForPrincipalBonus()) {
                    allTeachersArrived = false;
                    break;
                }
            }

            if (allTeachersArrived) {
                // All non-ignored teachers have arrived, signal them to return
                for (Teacher teacher : teachers) {
                    if (teacher.isGoingToPrincipalOffice()) {
                        teacher.returnFromPrincipalOffice();
                    }
                }
                principalBonusActive = false;
            }
        }

        // Check win condition
        boolean allRescued = true;
        for (Friend friend : friends) {
            if (!friend.isRescued()) {
                allRescued = false;
                break;
            }
        }
        if (allRescued && friends.size() > 0) {
            score += 500; // Bonus for completing level
            state = GameState.GAME_OVER;
        }

        // Update camera
        camera.update(player.getCenterX(), player.getCenterY());
    }

    public void draw(Canvas canvas) {
        // Clear screen with floor color
        canvas.drawColor(backgroundPaint.getColor());

        // Apply camera transform - this moves the world so camera position becomes
        // (0,0)
        canvas.save();
        canvas.translate(-camera.getX(), -camera.getY());

        // Draw background pattern with small squares
        drawBackgroundPattern(canvas);

        // Draw brick walls - draw at world coordinates (canvas is already translated)
        for (Wall wall : schoolLayout.getWalls()) {
            wall.draw(canvas, 0, 0);
        }

        // Draw friends - draw at world coordinates
        for (Friend friend : friends) {
            friend.draw(canvas, 0, 0);
        }

        // Draw teachers - draw at world coordinates
        for (Teacher teacher : teachers) {
            teacher.draw(canvas, 0, 0);
        }

        // Draw player - draw at world coordinates
        player.draw(canvas, 0, 0);

        canvas.restore();

        // Draw HUD (score, etc.) - HUD is drawn in screen space
        drawHUD(canvas);
    }

    public SchoolLayout getSchoolLayout() {
        return schoolLayout;
    }

    private void drawBackgroundPattern(Canvas canvas) {
        // Get camera bounds to optimize drawing (only draw visible squares)
        float cameraX = camera.getX();
        float cameraY = camera.getY();
        float viewportWidth = camera.getViewportWidth();
        float viewportHeight = camera.getViewportHeight();

        // Calculate visible area with some padding
        float startX = Math.max(0, cameraX - SQUARE_SIZE);
        float endX = Math.min(worldWidth, cameraX + viewportWidth + SQUARE_SIZE);
        float startY = Math.max(0, cameraY - SQUARE_SIZE);
        float endY = Math.min(worldHeight, cameraY + viewportHeight + SQUARE_SIZE);

        // Align to grid to avoid floating point precision issues
        float alignedStartX = (float) (Math.floor(startX / SQUARE_SIZE) * SQUARE_SIZE);
        float alignedStartY = (float) (Math.floor(startY / SQUARE_SIZE) * SQUARE_SIZE);

        // First, draw all corridor squares (light gray) for the entire visible area
        for (float y = alignedStartY; y < endY; y += SQUARE_SIZE) {
            for (float x = alignedStartX; x < endX; x += SQUARE_SIZE) {
                canvas.drawRect(x, y, x + SQUARE_SIZE, y + SQUARE_SIZE, corridorPaint);
            }
        }

        // Then, draw room squares (darker gray) on top of rooms
        for (SchoolLayout.Room room : schoolLayout.getRooms()) {
            float roomStartX = room.getX();
            float roomStartY = room.getY();
            float roomEndX = roomStartX + room.getWidth();
            float roomEndY = roomStartY + room.getHeight();

            // Only draw if room is visible
            if (roomEndX < startX || roomStartX > endX || roomEndY < startY || roomStartY > endY) {
                continue;
            }

            // Align room drawing to grid
            float alignedRoomStartX = (float) (Math.floor(roomStartX / SQUARE_SIZE) * SQUARE_SIZE);
            float alignedRoomStartY = (float) (Math.floor(roomStartY / SQUARE_SIZE) * SQUARE_SIZE);
            float alignedRoomEndX = (float) (Math.ceil(roomEndX / SQUARE_SIZE) * SQUARE_SIZE);
            float alignedRoomEndY = (float) (Math.ceil(roomEndY / SQUARE_SIZE) * SQUARE_SIZE);

            // Clamp to visible area
            alignedRoomStartX = Math.max(alignedRoomStartX, alignedStartX);
            alignedRoomStartY = Math.max(alignedRoomStartY, alignedStartY);
            alignedRoomEndX = Math.min(alignedRoomEndX, endX);
            alignedRoomEndY = Math.min(alignedRoomEndY, endY);

            // Draw squares for this room
            for (float y = alignedRoomStartY; y < alignedRoomEndY; y += SQUARE_SIZE) {
                for (float x = alignedRoomStartX; x < alignedRoomEndX; x += SQUARE_SIZE) {
                    // Only draw if square center is actually in the room
                    float squareCenterX = x + SQUARE_SIZE / 2;
                    float squareCenterY = y + SQUARE_SIZE / 2;
                    if (room.contains(squareCenterX, squareCenterY)) {
                        canvas.drawRect(x, y, x + SQUARE_SIZE, y + SQUARE_SIZE, roomPaint);
                    }
                }
            }
        }
    }

    private void drawHUD(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);

        canvas.drawText("Score: " + score, 20, 60, textPaint);
        canvas.drawText("Rescued: " + friendsRescued + "/" + friends.size(), 20, 110, textPaint);

        // Show active bonus effect indicator
        int teachersAway = 0;
        for (Teacher teacher : teachers) {
            if (teacher.isAway()) {
                teachersAway++;
            }
        }
        if (teachersAway > 0) {
            Paint bonusEffectPaint = new Paint();
            bonusEffectPaint.setColor(Color.rgb(0, 200, 0)); // Green for positive effect
            bonusEffectPaint.setTextSize(35);
            bonusEffectPaint.setAntiAlias(true);
            canvas.drawText("Teachers away: " + teachersAway, 20, 160, bonusEffectPaint);
        }

        // Draw collected bonus icons on the right/top
        drawBonusIcons(canvas);

        // Draw bonus message if active
        if (activeBonusMessage != null && bonusMessageTimer > 0) {
            Paint bonusPaint = new Paint();
            bonusPaint.setColor(Color.rgb(255, 215, 0)); // Gold color
            bonusPaint.setTextSize(50);
            bonusPaint.setAntiAlias(true);
            bonusPaint.setStyle(Paint.Style.FILL);
            bonusPaint.setFakeBoldText(true);

            // Draw background for better visibility
            Paint bgPaint = new Paint();
            bgPaint.setColor(Color.argb(200, 0, 0, 0)); // Semi-transparent black
            bgPaint.setStyle(Paint.Style.FILL);

            float textWidth = bonusPaint.measureText(activeBonusMessage);
            float padding = 20f;
            float bgLeft = canvas.getWidth() / 2 - textWidth / 2 - padding;
            float bgRight = canvas.getWidth() / 2 + textWidth / 2 + padding;
            float bgTop = 200f;
            float bgBottom = 280f;

            canvas.drawRect(bgLeft, bgTop - 50, bgRight, bgBottom, bgPaint);
            canvas.drawText(activeBonusMessage, canvas.getWidth() / 2 - textWidth / 2, 250, bonusPaint);

            // Show what the bonus does
            String bonusDescription = getBonusDescription(activeBonusMessage);
            if (bonusDescription != null) {
                Paint descPaint = new Paint();
                descPaint.setColor(Color.WHITE);
                descPaint.setTextSize(30);
                descPaint.setAntiAlias(true);

                float descWidth = descPaint.measureText(bonusDescription);
                canvas.drawText(bonusDescription, canvas.getWidth() / 2 - descWidth / 2, 280, descPaint);
            }
        }
    }

    private String getBonusDescription(String bonusMessage) {
        if (bonusMessage.contains("Principal")) {
            return "All teachers leave to see principal!";
        } else if (bonusMessage.contains("Parent")) {
            return "One teacher leaves to talk with parent!";
        } else if (bonusMessage.contains("Another Teacher")) {
            return "One teacher leaves to talk with colleague!";
        }
        return null;
    }

    private void drawBonusIcons(Canvas canvas) {
        if (collectedBonuses.isEmpty()) {
            return;
        }

        float screenWidth = canvas.getWidth();
        float startX = screenWidth - BONUS_ICON_MARGIN - BONUS_ICON_SIZE;
        float startY = BONUS_ICON_MARGIN;

        Paint iconBgPaint = new Paint();
        iconBgPaint.setStyle(Paint.Style.FILL);

        Paint iconBorderPaint = new Paint();
        iconBorderPaint.setStyle(Paint.Style.STROKE);
        iconBorderPaint.setStrokeWidth(3f);
        iconBorderPaint.setColor(Color.BLACK);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < collectedBonuses.size(); i++) {
            Bonus bonus = collectedBonuses.get(i);
            float iconY = startY + i * BONUS_ICON_SPACING;

            // Draw icon background
            int color = getBonusColor(bonus);
            iconBgPaint.setColor(color);
            canvas.drawRect(startX, iconY, startX + BONUS_ICON_SIZE, iconY + BONUS_ICON_SIZE, iconBgPaint);

            // Draw border
            canvas.drawRect(startX, iconY, startX + BONUS_ICON_SIZE, iconY + BONUS_ICON_SIZE, iconBorderPaint);

            // Draw bonus letter/icon
            String iconText = getBonusIconText(bonus);
            float textX = startX + BONUS_ICON_SIZE / 2;
            float textY = iconY + BONUS_ICON_SIZE / 2 + 7; // Center vertically
            textPaint.setColor(Color.WHITE);
            textPaint.setFakeBoldText(true);
            canvas.drawText(iconText, textX, textY, textPaint);

            // Draw bonus name below icon
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(18);
            textPaint.setFakeBoldText(false);
            String bonusName = bonus.getName();
            if (bonusName.length() > 8) {
                bonusName = bonusName.substring(0, 8);
            }
            canvas.drawText(bonusName, textX, iconY + BONUS_ICON_SIZE + 20, textPaint);
        }
    }

    private int getBonusColor(Bonus bonus) {
        String name = bonus.getName();
        if (name.contains("Principal")) {
            return Color.rgb(255, 165, 0); // Orange
        } else if (name.contains("Parent")) {
            return Color.rgb(0, 150, 255); // Blue
        } else if (name.contains("Another Teacher")) {
            return Color.rgb(150, 0, 255); // Purple
        }
        return Color.rgb(200, 200, 200); // Default gray
    }

    private String getBonusIconText(Bonus bonus) {
        String name = bonus.getName();
        if (name.contains("Principal")) {
            return "P";
        } else if (name.contains("Parent")) {
            return "Pa";
        } else if (name.contains("Another Teacher")) {
            return "T";
        }
        return "?";
    }

    /**
     * Creates a new instance of a bonus based on the template
     */
    private Bonus createBonusInstance(Bonus template) {
        String name = template.getName();
        if (name.contains("Principal")) {
            return new PrincipalBonus();
        } else if (name.contains("Parent")) {
            return new ParentBonus();
        } else if (name.contains("Another Teacher")) {
            return new AnotherTeacherBonus();
        }
        return template; // Fallback
    }

    /**
     * Handles touch event to activate bonuses
     * 
     * @param x Screen X coordinate
     * @param y Screen Y coordinate
     * @return true if a bonus was activated
     */
    public boolean handleBonusTouch(float x, float y, float screenWidth) {
        if (collectedBonuses.isEmpty()) {
            return false;
        }

        float startX = screenWidth - BONUS_ICON_MARGIN - BONUS_ICON_SIZE;
        float startY = BONUS_ICON_MARGIN;

        // Check if touch is in bonus icon area
        if (x < startX || x > startX + BONUS_ICON_SIZE) {
            return false;
        }

        // Find which bonus was clicked
        for (int i = 0; i < collectedBonuses.size(); i++) {
            float iconY = startY + i * BONUS_ICON_SPACING;
            if (y >= iconY && y <= iconY + BONUS_ICON_SIZE) {
                // Activate this bonus
                Bonus bonus = collectedBonuses.remove(i);
                bonus.activate(this);

                // Show activation message
                activeBonusMessage = "Used: " + bonus.getName() + "!";
                bonusMessageTimer = BONUS_MESSAGE_DURATION;

                return true;
            }
        }

        return false;
    }

    public List<Bonus> getCollectedBonuses() {
        return collectedBonuses;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Player getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public int getFriendsRescued() {
        return friendsRescued;
    }

    public int getTotalFriends() {
        return friends.size();
    }

    public Teacher getTeacherThatCaughtPlayer() {
        return teacherThatCaughtPlayer;
    }

    public void freezeTeacherThatCaughtPlayer() {
        if (teacherThatCaughtPlayer != null) {
            teacherThatCaughtPlayer.freeze();
            teacherThatCaughtPlayer = null; // Reset after freezing
        }
    }

    public void updateViewport(float viewportWidth, float viewportHeight) {
        if (camera != null) {
            // Update camera viewport size
            camera.setViewportSize(viewportWidth, viewportHeight);
            // Immediately update camera to current player position
            if (player != null) {
                camera.update(player.getCenterX(), player.getCenterY());
            }
        }
    }

    // Getters for bonus system
    public List<Teacher> getTeachers() {
        return teachers;
    }

    /**
     * Adds a new bonus type to the available bonuses list
     * This allows for easy extension with new bonus types
     */
    public void addBonus(Bonus bonus) {
        availableBonuses.add(bonus);
    }

    /**
     * Sets the principal bonus active state (called by PrincipalBonus)
     */
    public void setPrincipalBonusActive(boolean active) {
        this.principalBonusActive = active;
    }
}

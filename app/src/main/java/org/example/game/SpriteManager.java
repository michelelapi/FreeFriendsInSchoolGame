package org.example.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Manages loading and caching of sprite bitmaps for the game.
 * This class loads sprites from the drawable resources folder.
 */
public class SpriteManager {
    private static SpriteManager instance;
    private Context context;
    
    // Cached bitmaps
    private Bitmap playerSprite;
    private Bitmap teacherSprite;
    private Bitmap friendSprite;
    private Bitmap wallTileSprite;
    private Bitmap floorTileSprite;
    private Bitmap corridorTileSprite;
    private Bitmap roomTileSprite;
    
    // Sprite dimensions (will be set when sprites are loaded)
    private int playerWidth = 40;
    private int playerHeight = 40;
    private int teacherWidth = 40;
    private int teacherHeight = 40;
    private int friendWidth = 30;
    private int friendHeight = 30;
    private int tileSize = 20;
    
    private SpriteManager(Context context) {
        this.context = context;
        loadSprites();
    }
    
    public static SpriteManager getInstance(Context context) {
        if (instance == null) {
            instance = new SpriteManager(context);
        }
        return instance;
    }
    
    /**
     * Loads all sprites from drawable resources.
     * If a sprite resource doesn't exist, it will be null and the game will fall back to colored rectangles.
     */
    private void loadSprites() {
        try {
            // Try to load player sprite
            int playerResId = context.getResources().getIdentifier("sprite_player", "drawable", context.getPackageName());
            if (playerResId != 0) {
                playerSprite = BitmapFactory.decodeResource(context.getResources(), playerResId);
                if (playerSprite != null) {
                    playerWidth = playerSprite.getWidth();
                    playerHeight = playerSprite.getHeight();
                }
            }
        } catch (Exception e) {
            // Sprite not found, will use fallback
        }
        
        try {
            // Try to load teacher sprite
            int teacherResId = context.getResources().getIdentifier("sprite_teacher", "drawable", context.getPackageName());
            if (teacherResId != 0) {
                teacherSprite = BitmapFactory.decodeResource(context.getResources(), teacherResId);
                if (teacherSprite != null) {
                    teacherWidth = teacherSprite.getWidth();
                    teacherHeight = teacherSprite.getHeight();
                }
            }
        } catch (Exception e) {
            // Sprite not found, will use fallback
        }
        
        try {
            // Try to load friend sprite
            int friendResId = context.getResources().getIdentifier("sprite_friend", "drawable", context.getPackageName());
            if (friendResId != 0) {
                friendSprite = BitmapFactory.decodeResource(context.getResources(), friendResId);
                if (friendSprite != null) {
                    friendWidth = friendSprite.getWidth();
                    friendHeight = friendSprite.getHeight();
                }
            }
        } catch (Exception e) {
            // Sprite not found, will use fallback
        }
        
        try {
            // Try to load wall tile sprite
            int wallResId = context.getResources().getIdentifier("tile_wall", "drawable", context.getPackageName());
            if (wallResId != 0) {
                wallTileSprite = BitmapFactory.decodeResource(context.getResources(), wallResId);
                if (wallTileSprite != null) {
                    tileSize = wallTileSprite.getWidth();
                }
            }
        } catch (Exception e) {
            // Sprite not found, will use fallback
        }
        
        try {
            // Try to load floor tile sprite
            int floorResId = context.getResources().getIdentifier("tile_floor", "drawable", context.getPackageName());
            if (floorResId != 0) {
                floorTileSprite = BitmapFactory.decodeResource(context.getResources(), floorResId);
            }
        } catch (Exception e) {
            // Sprite not found, will use fallback
        }
        
        try {
            // Try to load corridor tile sprite
            int corridorResId = context.getResources().getIdentifier("tile_corridor", "drawable", context.getPackageName());
            if (corridorResId != 0) {
                corridorTileSprite = BitmapFactory.decodeResource(context.getResources(), corridorResId);
            }
        } catch (Exception e) {
            // Sprite not found, will use fallback
        }
        
        try {
            // Try to load room tile sprite
            int roomResId = context.getResources().getIdentifier("tile_room", "drawable", context.getPackageName());
            if (roomResId != 0) {
                roomTileSprite = BitmapFactory.decodeResource(context.getResources(), roomResId);
            }
        } catch (Exception e) {
            // Sprite not found, will use fallback
        }
    }
    
    /**
     * Draws a sprite at the specified position and size.
     * If the sprite is null, this method does nothing (fallback to colored rectangles).
     */
    public void drawSprite(Canvas canvas, Bitmap sprite, float x, float y, float width, float height) {
        if (sprite != null) {
            Rect srcRect = new Rect(0, 0, sprite.getWidth(), sprite.getHeight());
            Rect dstRect = new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));
            canvas.drawBitmap(sprite, srcRect, dstRect, null);
        }
    }
    
    /**
     * Draws a tile sprite repeatedly to fill a rectangular area.
     */
    public void drawTiledSprite(Canvas canvas, Bitmap tileSprite, float x, float y, float width, float height) {
        if (tileSprite == null) {
            return;
        }
        
        int tileWidth = tileSprite.getWidth();
        int tileHeight = tileSprite.getHeight();
        
        for (float tileY = y; tileY < y + height; tileY += tileHeight) {
            for (float tileX = x; tileX < x + width; tileX += tileWidth) {
                float drawWidth = Math.min(tileWidth, (x + width) - tileX);
                float drawHeight = Math.min(tileHeight, (y + height) - tileY);
                
                Rect srcRect = new Rect(0, 0, (int)drawWidth, (int)drawHeight);
                Rect dstRect = new Rect((int)tileX, (int)tileY, (int)(tileX + drawWidth), (int)(tileY + drawHeight));
                canvas.drawBitmap(tileSprite, srcRect, dstRect, null);
            }
        }
    }
    
    // Getters for sprites
    public Bitmap getPlayerSprite() {
        return playerSprite;
    }
    
    public Bitmap getTeacherSprite() {
        return teacherSprite;
    }
    
    public Bitmap getFriendSprite() {
        return friendSprite;
    }
    
    public Bitmap getWallTileSprite() {
        return wallTileSprite;
    }
    
    public Bitmap getFloorTileSprite() {
        return floorTileSprite;
    }
    
    public Bitmap getCorridorTileSprite() {
        return corridorTileSprite;
    }
    
    public Bitmap getRoomTileSprite() {
        return roomTileSprite;
    }
    
    // Getters for sprite dimensions
    public int getPlayerWidth() {
        return playerWidth;
    }
    
    public int getPlayerHeight() {
        return playerHeight;
    }
    
    public int getTeacherWidth() {
        return teacherWidth;
    }
    
    public int getTeacherHeight() {
        return teacherHeight;
    }
    
    public int getFriendWidth() {
        return friendWidth;
    }
    
    public int getFriendHeight() {
        return friendHeight;
    }
    
    public int getTileSize() {
        return tileSize;
    }
    
    /**
     * Checks if sprites are loaded (at least one sprite exists)
     */
    public boolean hasSprites() {
        return playerSprite != null || teacherSprite != null || friendSprite != null || 
               wallTileSprite != null || floorTileSprite != null || corridorTileSprite != null || roomTileSprite != null;
    }
    
    /**
     * Clean up resources when done
     */
    public void cleanup() {
        if (playerSprite != null) {
            playerSprite.recycle();
            playerSprite = null;
        }
        if (teacherSprite != null) {
            teacherSprite.recycle();
            teacherSprite = null;
        }
        if (friendSprite != null) {
            friendSprite.recycle();
            friendSprite = null;
        }
        if (wallTileSprite != null) {
            wallTileSprite.recycle();
            wallTileSprite = null;
        }
        if (floorTileSprite != null) {
            floorTileSprite.recycle();
            floorTileSprite = null;
        }
        if (corridorTileSprite != null) {
            corridorTileSprite.recycle();
            corridorTileSprite = null;
        }
        if (roomTileSprite != null) {
            roomTileSprite.recycle();
            roomTileSprite = null;
        }
    }
}


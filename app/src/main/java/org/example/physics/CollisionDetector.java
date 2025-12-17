package org.example.physics;

import org.example.entities.Entity;
import org.example.entities.Friend;
import org.example.entities.Player;
import org.example.entities.Teacher;
import org.example.game.Wall;

import java.util.List;

public class CollisionDetector {
    
    public static boolean checkPlayerTeacherCollision(Player player, List<Teacher> teachers) {
        for (Teacher teacher : teachers) {
            // Skip frozen teachers - they can't catch the player
            if (!teacher.isFrozen() && player.intersects(teacher)) {
                return true;
            }
        }
        return false;
    }
    
    public static Teacher getCollidingTeacher(Player player, List<Teacher> teachers) {
        for (Teacher teacher : teachers) {
            // Skip frozen teachers - they can't catch the player
            if (!teacher.isFrozen() && player.intersects(teacher)) {
                return teacher;
            }
        }
        return null;
    }
    
    public static Friend checkPlayerFriendCollision(Player player, List<Friend> friends) {
        for (Friend friend : friends) {
            if (!friend.isRescued() && player.intersects(friend)) {
                return friend;
            }
        }
        return null;
    }
    
    public static boolean checkWallCollision(Entity entity, float worldWidth, float worldHeight) {
        return entity.getX() < 0 || 
               entity.getY() < 0 || 
               entity.getX() + entity.getWidth() > worldWidth ||
               entity.getY() + entity.getHeight() > worldHeight;
    }
    
    public static void resolveWallCollision(Entity entity, float worldWidth, float worldHeight) {
        if (entity.getX() < 0) {
            entity.setPosition(0, entity.getY());
        }
        if (entity.getY() < 0) {
            entity.setPosition(entity.getX(), 0);
        }
        if (entity.getX() + entity.getWidth() > worldWidth) {
            entity.setPosition(worldWidth - entity.getWidth(), entity.getY());
        }
        if (entity.getY() + entity.getHeight() > worldHeight) {
            entity.setPosition(entity.getX(), worldHeight - entity.getHeight());
        }
    }
    
    public static boolean checkWallCollision(Entity entity, List<Wall> walls) {
        for (Wall wall : walls) {
            if (entity.getBounds().intersect(wall.getBounds())) {
                return true;
            }
        }
        return false;
    }
    
    public static void resolveWallCollision(Entity entity, List<Wall> walls) {
        float prevX = entity.getX();
        float prevY = entity.getY();
        
        // Try to resolve collision by moving back
        for (Wall wall : walls) {
            if (entity.getBounds().intersect(wall.getBounds())) {
                // Calculate overlap
                float overlapLeft = (entity.getX() + entity.getWidth()) - wall.getX();
                float overlapRight = (wall.getX() + wall.getWidth()) - entity.getX();
                float overlapTop = (entity.getY() + entity.getHeight()) - wall.getY();
                float overlapBottom = (wall.getY() + wall.getHeight()) - entity.getY();
                
                // Find minimum overlap
                float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), 
                                           Math.min(overlapTop, overlapBottom));
                
                // Resolve collision based on minimum overlap direction
                if (minOverlap == overlapLeft) {
                    entity.setPosition(wall.getX() - entity.getWidth(), entity.getY());
                } else if (minOverlap == overlapRight) {
                    entity.setPosition(wall.getX() + wall.getWidth(), entity.getY());
                } else if (minOverlap == overlapTop) {
                    entity.setPosition(entity.getX(), wall.getY() - entity.getHeight());
                } else if (minOverlap == overlapBottom) {
                    entity.setPosition(entity.getX(), wall.getY() + wall.getHeight());
                }
                
                // If still colliding, try the previous position
                if (entity.getBounds().intersect(wall.getBounds())) {
                    entity.setPosition(prevX, prevY);
                }
            }
        }
    }
}


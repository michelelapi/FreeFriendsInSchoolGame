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
        float margin = 3f; // Margin to push entity away from wall to prevent getting stuck
        
        // Try to resolve collision by moving back - iterate multiple times to handle multiple walls
        for (int attempt = 0; attempt < 5; attempt++) {
            boolean stillColliding = false;
            
            for (Wall wall : walls) {
                if (entity.getBounds().intersect(wall.getBounds())) {
                    stillColliding = true;
                    
                    // Calculate center points
                    float entityCenterX = entity.getX() + entity.getWidth() / 2;
                    float entityCenterY = entity.getY() + entity.getHeight() / 2;
                    float wallCenterX = wall.getX() + wall.getWidth() / 2;
                    float wallCenterY = wall.getY() + wall.getHeight() / 2;
                    
                    // Calculate overlaps
                    float overlapLeft = (entity.getX() + entity.getWidth()) - wall.getX();
                    float overlapRight = (wall.getX() + wall.getWidth()) - entity.getX();
                    float overlapTop = (entity.getY() + entity.getHeight()) - wall.getY();
                    float overlapBottom = (wall.getY() + wall.getHeight()) - entity.getY();
                    
                    // Find minimum overlap
                    float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), 
                                               Math.min(overlapTop, overlapBottom));
                    
                    // Resolve collision based on minimum overlap direction with margin
                    if (minOverlap == overlapLeft && overlapLeft < overlapRight) {
                        entity.setPosition(wall.getX() - entity.getWidth() - margin, entity.getY());
                    } else if (minOverlap == overlapRight && overlapRight < overlapLeft) {
                        entity.setPosition(wall.getX() + wall.getWidth() + margin, entity.getY());
                    } else if (minOverlap == overlapTop && overlapTop < overlapBottom) {
                        entity.setPosition(entity.getX(), wall.getY() - entity.getHeight() - margin);
                    } else if (minOverlap == overlapBottom && overlapBottom < overlapTop) {
                        entity.setPosition(entity.getX(), wall.getY() + wall.getHeight() + margin);
                    } else {
                        // If overlaps are similar, push based on center distance
                        float dx = entityCenterX - wallCenterX;
                        float dy = entityCenterY - wallCenterY;
                        
                        if (Math.abs(dx) > Math.abs(dy)) {
                            // Push horizontally
                            if (dx > 0) {
                                entity.setPosition(wall.getX() + wall.getWidth() + margin, entity.getY());
                            } else {
                                entity.setPosition(wall.getX() - entity.getWidth() - margin, entity.getY());
                            }
                        } else {
                            // Push vertically
                            if (dy > 0) {
                                entity.setPosition(entity.getX(), wall.getY() + wall.getHeight() + margin);
                            } else {
                                entity.setPosition(entity.getX(), wall.getY() - entity.getHeight() - margin);
                            }
                        }
                    }
                }
            }
            
            // If no collisions remain, we're done
            if (!stillColliding) {
                break;
            }
        }
        
        // If still colliding after all attempts, try reverting to previous position
        boolean stillColliding = false;
        for (Wall wall : walls) {
            if (entity.getBounds().intersect(wall.getBounds())) {
                stillColliding = true;
                break;
            }
        }
        
        if (stillColliding) {
            // Try moving back along velocity direction if available
            entity.setPosition(prevX, prevY);
        }
    }
}


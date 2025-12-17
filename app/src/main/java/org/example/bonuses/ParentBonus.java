package org.example.bonuses;

import org.example.entities.Teacher;
import org.example.game.GameEngine;

import java.util.List;
import java.util.Random;

/**
 * Parent bonus: One teacher goes to talk with a parent, leaving their kid alone.
 */
public class ParentBonus implements Bonus {
    private static final float TALK_DURATION = 4f; // 4 seconds talking with parent
    
    @Override
    public String getName() {
        return "Parent";
    }
    
    @Override
    public int getPriority() {
        return 2; // Medium priority
    }
    
    @Override
    public void activate(GameEngine gameEngine) {
        List<Teacher> teachers = gameEngine.getTeachers();
        
        // Find teachers that are guarding a friend (not rescued)
        List<Teacher> teachersWithKids = new java.util.ArrayList<>();
        for (Teacher teacher : teachers) {
            if (teacher.getGuardedFriend() != null && 
                !teacher.getGuardedFriend().isRescued() &&
                !teacher.isAway()) {
                teachersWithKids.add(teacher);
            }
        }
        
        if (!teachersWithKids.isEmpty()) {
            // Randomly select one teacher to go talk with parent
            Random random = new Random();
            Teacher selectedTeacher = teachersWithKids.get(random.nextInt(teachersWithKids.size()));
            
            // Place parent at a random location away from the teacher
            float parentX = selectedTeacher.getCenterX() + (random.nextFloat() - 0.5f) * 300f;
            float parentY = selectedTeacher.getCenterY() + (random.nextFloat() - 0.5f) * 300f;
            
            // Ensure parent is in a corridor
            if (!gameEngine.getSchoolLayout().isInCorridor(parentX, parentY)) {
                // Adjust to nearest corridor position
                parentX = selectedTeacher.getCenterX() + 200f;
                parentY = selectedTeacher.getCenterY();
            }
            
            selectedTeacher.goTalkWithParent(parentX, parentY, TALK_DURATION);
        }
    }
}


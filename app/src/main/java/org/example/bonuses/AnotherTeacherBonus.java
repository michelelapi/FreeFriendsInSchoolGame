package org.example.bonuses;

import org.example.entities.Teacher;
import org.example.game.GameEngine;

import java.util.List;
import java.util.Random;

/**
 * Another teacher bonus: One teacher with a kid goes to talk with another teacher without a kid.
 */
public class AnotherTeacherBonus implements Bonus {
    private static final float TALK_DURATION = 3f; // 3 seconds talking with another teacher
    
    @Override
    public String getName() {
        return "Another Teacher";
    }
    
    @Override
    public int getPriority() {
        return 1; // Lowest priority
    }
    
    @Override
    public void activate(GameEngine gameEngine) {
        List<Teacher> teachers = gameEngine.getTeachers();
        
        // Find teachers with kids and teachers without kids
        List<Teacher> teachersWithKids = new java.util.ArrayList<>();
        List<Teacher> teachersWithoutKids = new java.util.ArrayList<>();
        
        for (Teacher teacher : teachers) {
            if (teacher.isAway()) {
                continue; // Skip teachers that are already away
            }
            
            if (teacher.getGuardedFriend() != null && 
                !teacher.getGuardedFriend().isRescued()) {
                teachersWithKids.add(teacher);
            } else {
                teachersWithoutKids.add(teacher);
            }
        }
        
        // If we have both types, make one teacher with kid go talk with one without kid
        if (!teachersWithKids.isEmpty() && !teachersWithoutKids.isEmpty()) {
            Random random = new Random();
            Teacher teacherWithKid = teachersWithKids.get(random.nextInt(teachersWithKids.size()));
            Teacher teacherWithoutKid = teachersWithoutKids.get(random.nextInt(teachersWithoutKids.size()));
            
            // Teacher with kid goes to the teacher without kid
            teacherWithKid.goTalkWithTeacher(teacherWithoutKid, TALK_DURATION);
        }
    }
}


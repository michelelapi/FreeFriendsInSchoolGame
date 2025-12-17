package org.example.bonuses;

import org.example.entities.Teacher;
import org.example.game.GameEngine;

import java.util.List;

/**
 * Principal bonus: All teachers go to the principal's office, leaving kids alone.
 * The bonus ends when all teachers reach the office.
 * This is the highest priority bonus.
 */
public class PrincipalBonus implements Bonus {
    // Duration is not used anymore - bonus ends when all teachers arrive
    
    @Override
    public String getName() {
        return "Principal";
    }
    
    @Override
    public int getPriority() {
        return 3; // Highest priority
    }
    
    @Override
    public void activate(GameEngine gameEngine) {
        // Get principal's office location
        float[] principalOffice = gameEngine.getSchoolLayout().getPrincipalOfficeLocation();
        float officeX = principalOffice[0];
        float officeY = principalOffice[1];
        
        // Make all teachers go to principal's office
        List<Teacher> teachers = gameEngine.getTeachers();
        for (Teacher teacher : teachers) {
            teacher.goToPrincipalOffice(officeX, officeY, 0f); // Duration not used
        }
        
        // Activate principal bonus tracking in GameEngine
        gameEngine.setPrincipalBonusActive(true);
    }
}


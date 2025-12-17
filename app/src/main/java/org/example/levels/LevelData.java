package org.example.levels;

import java.util.ArrayList;
import java.util.List;

public class LevelData {
    private static List<Level> levels;
    
    static {
        levels = new ArrayList<>();
        
        // Elementary levels (Grades 1-5)
        for (int i = 1; i <= 5; i++) {
            levels.add(new Level(i, "Elementary", i, 3 + i, 2 + i, "elementary"));
        }
        
        // Middle School levels (Grades 6-8)
        for (int i = 6; i <= 8; i++) {
            levels.add(new Level(i, "Middle School", i, 5 + i, 3 + i, "middle"));
        }
        
        // High School levels (Grades 9-12)
        for (int i = 9; i <= 12; i++) {
            levels.add(new Level(i, "High School", i, 8 + i, 4 + i, "high"));
        }
        
        // College levels
        for (int i = 13; i <= 16; i++) {
            levels.add(new Level(i, "College", i, 10 + i, 5 + i, "college"));
        }
        
        // PhD level
        levels.add(new Level(17, "PhD", 17, 20, 8, "phd"));
    }
    
    public static List<Level> getAllLevels() {
        return new ArrayList<>(levels);
    }
    
    public static Level getLevel(int levelId) {
        for (Level level : levels) {
            if (level.getLevelId() == levelId) {
                return level;
            }
        }
        return null;
    }
    
    public static int getMaxLevel() {
        return levels.size();
    }
}


package org.example.levels;

import android.content.Context;
import android.content.SharedPreferences;

public class LevelManager {
    private static final String PREFS_NAME = "game_progress";
    private static final String KEY_CURRENT_LEVEL = "current_level";
    private static final String KEY_HIGHEST_LEVEL = "highest_level";
    private static final String KEY_UNLOCKED_LEVELS = "unlocked_levels";
    
    private Context context;
    private SharedPreferences prefs;
    
    public LevelManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public int getCurrentLevel() {
        return prefs.getInt(KEY_CURRENT_LEVEL, 1);
    }
    
    public void setCurrentLevel(int level) {
        prefs.edit().putInt(KEY_CURRENT_LEVEL, level).apply();
    }
    
    public int getHighestLevelReached() {
        return prefs.getInt(KEY_HIGHEST_LEVEL, 1);
    }
    
    public void setHighestLevelReached(int level) {
        int currentHighest = getHighestLevelReached();
        if (level > currentHighest) {
            prefs.edit().putInt(KEY_HIGHEST_LEVEL, level).apply();
        }
    }
    
    public boolean isLevelUnlocked(int level) {
        if (level == 1) {
            return true; // First level always unlocked
        }
        String unlocked = prefs.getString(KEY_UNLOCKED_LEVELS, "1");
        return unlocked.contains(String.valueOf(level));
    }
    
    public void unlockLevel(int level) {
        String unlocked = prefs.getString(KEY_UNLOCKED_LEVELS, "1");
        if (!unlocked.contains(String.valueOf(level))) {
            unlocked += "," + level;
            prefs.edit().putString(KEY_UNLOCKED_LEVELS, unlocked).apply();
        }
    }
    
    public void completeLevel(int level) {
        setHighestLevelReached(level);
        unlockLevel(level + 1); // Unlock next level
    }
    
    public Level getLevel(int levelId) {
        return LevelData.getLevel(levelId);
    }
}


package org.example.data;

import android.content.Context;
import android.content.SharedPreferences;

public class ScoreManager {
    private static final String PREFS_NAME = "game_scores";
    private static final String KEY_HIGH_SCORE = "high_score";
    private static final String KEY_TOTAL_SCORE = "total_score";
    private static final String KEY_LEVEL_HIGH_SCORE_PREFIX = "level_high_score_";
    
    private Context context;
    private SharedPreferences prefs;
    
    public ScoreManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public int getHighScore() {
        return prefs.getInt(KEY_HIGH_SCORE, 0);
    }
    
    public void saveScore(int score) {
        int currentHigh = getHighScore();
        if (score > currentHigh) {
            prefs.edit().putInt(KEY_HIGH_SCORE, score).apply();
        }
        
        int totalScore = getTotalScore();
        prefs.edit().putInt(KEY_TOTAL_SCORE, totalScore + score).apply();
    }
    
    public int getTotalScore() {
        return prefs.getInt(KEY_TOTAL_SCORE, 0);
    }
    
    public int getLevelHighScore(int level) {
        return prefs.getInt(KEY_LEVEL_HIGH_SCORE_PREFIX + level, 0);
    }
    
    public void saveLevelScore(int level, int score) {
        int currentLevelHigh = getLevelHighScore(level);
        if (score > currentLevelHigh) {
            prefs.edit().putInt(KEY_LEVEL_HIGH_SCORE_PREFIX + level, score).apply();
        }
        saveScore(score);
    }
}


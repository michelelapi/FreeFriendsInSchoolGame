package org.example.levels;

public class Level {
    private int levelId;
    private String gradeLevel;
    private int difficulty;
    private int maxFriends;
    private int teacherCount;
    private String questionPool;
    
    public Level(int levelId, String gradeLevel, int difficulty, int maxFriends, int teacherCount, String questionPool) {
        this.levelId = levelId;
        this.gradeLevel = gradeLevel;
        this.difficulty = difficulty;
        this.maxFriends = maxFriends;
        this.teacherCount = teacherCount;
        this.questionPool = questionPool;
    }
    
    public int getLevelId() {
        return levelId;
    }
    
    public String getGradeLevel() {
        return gradeLevel;
    }
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public int getMaxFriends() {
        return maxFriends;
    }
    
    public int getTeacherCount() {
        return teacherCount;
    }
    
    public String getQuestionPool() {
        return questionPool;
    }
}


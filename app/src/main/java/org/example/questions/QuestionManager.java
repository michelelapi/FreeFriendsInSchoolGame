package org.example.questions;

import java.util.List;
import java.util.Random;

public class QuestionManager {
    private QuestionDatabase database;
    private Random random;
    
    public QuestionManager() {
        this.database = QuestionDatabase.getInstance();
        this.random = new Random();
    }
    
    public Question getRandomQuestion(String difficulty) {
        List<Question> questions = database.getQuestionsByDifficulty(difficulty);
        if (questions.isEmpty()) {
            // Fallback to any question if none found for difficulty
            questions = database.getAllQuestions();
        }
        if (questions.isEmpty()) {
            return null;
        }
        return questions.get(random.nextInt(questions.size()));
    }
    
    public Question getRandomQuestionByLevel(int level) {
        String difficulty = getDifficultyForLevel(level);
        return getRandomQuestion(difficulty);
    }
    
    private String getDifficultyForLevel(int level) {
        if (level <= 5) {
            return "elementary";
        } else if (level <= 8) {
            return "middle";
        } else if (level <= 12) {
            return "high";
        } else if (level <= 16) {
            return "college";
        } else {
            return "phd";
        }
    }
}


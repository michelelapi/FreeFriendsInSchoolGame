package org.example.questions;

public class Question {
    private String questionText;
    private String[] options;
    private int correctAnswerIndex;
    private String difficulty;
    private String subject;
    
    public Question(String questionText, String[] options, int correctAnswerIndex, String difficulty, String subject) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.difficulty = difficulty;
        this.subject = subject;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public String[] getOptions() {
        return options;
    }
    
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctAnswerIndex;
    }
}


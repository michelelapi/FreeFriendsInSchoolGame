package org.example.questions;

import java.util.ArrayList;
import java.util.List;

public class QuestionDatabase {
    private static QuestionDatabase instance;
    private List<Question> questions;
    
    private QuestionDatabase() {
        questions = new ArrayList<>();
        initializeQuestions();
    }
    
    public static QuestionDatabase getInstance() {
        if (instance == null) {
            instance = new QuestionDatabase();
        }
        return instance;
    }
    
    private void initializeQuestions() {
        // Elementary level questions (Grades 1-5)
        questions.add(new Question(
            "What is 2 + 2?",
            new String[]{"3", "4", "5", "6"},
            1,
            "elementary",
            "math"
        ));
        
        questions.add(new Question(
            "What is 5 + 3?",
            new String[]{"6", "7", "8", "9"},
            2,
            "elementary",
            "math"
        ));
        
        questions.add(new Question(
            "What is 10 - 4?",
            new String[]{"4", "5", "6", "7"},
            2,
            "elementary",
            "math"
        ));
        
        questions.add(new Question(
            "How many days are in a week?",
            new String[]{"5", "6", "7", "8"},
            2,
            "elementary",
            "general"
        ));
        
        questions.add(new Question(
            "What color do you get when you mix red and blue?",
            new String[]{"Green", "Purple", "Orange", "Yellow"},
            1,
            "elementary",
            "general"
        ));
        
        // Middle School level questions (Grades 6-8)
        questions.add(new Question(
            "What is 12 × 5?",
            new String[]{"50", "60", "70", "80"},
            1,
            "middle",
            "math"
        ));
        
        questions.add(new Question(
            "What is the capital of France?",
            new String[]{"London", "Berlin", "Paris", "Madrid"},
            2,
            "middle",
            "geography"
        ));
        
        questions.add(new Question(
            "What is 15% of 100?",
            new String[]{"10", "15", "20", "25"},
            1,
            "middle",
            "math"
        ));
        
        questions.add(new Question(
            "What is the largest planet in our solar system?",
            new String[]{"Earth", "Mars", "Jupiter", "Saturn"},
            2,
            "middle",
            "science"
        ));
        
        questions.add(new Question(
            "What is 3²?",
            new String[]{"6", "9", "12", "15"},
            1,
            "middle",
            "math"
        ));
        
        // High School level questions (Grades 9-12)
        questions.add(new Question(
            "What is the derivative of x²?",
            new String[]{"x", "2x", "x²", "2x²"},
            1,
            "high",
            "math"
        ));
        
        questions.add(new Question(
            "What is the chemical symbol for water?",
            new String[]{"H2O", "CO2", "O2", "NaCl"},
            0,
            "high",
            "science"
        ));
        
        questions.add(new Question(
            "Who wrote 'Romeo and Juliet'?",
            new String[]{"Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain"},
            1,
            "high",
            "literature"
        ));
        
        questions.add(new Question(
            "What is the square root of 64?",
            new String[]{"6", "7", "8", "9"},
            2,
            "high",
            "math"
        ));
        
        questions.add(new Question(
            "In which year did World War II end?",
            new String[]{"1943", "1944", "1945", "1946"},
            2,
            "high",
            "history"
        ));
        
        // College level questions
        questions.add(new Question(
            "What is the integral of 2x?",
            new String[]{"x²", "2x²", "x² + C", "2x + C"},
            2,
            "college",
            "math"
        ));
        
        questions.add(new Question(
            "What is the speed of light in vacuum?",
            new String[]{"3 × 10⁶ m/s", "3 × 10⁸ m/s", "3 × 10¹⁰ m/s", "3 × 10¹² m/s"},
            1,
            "college",
            "science"
        ));
        
        questions.add(new Question(
            "What is the time complexity of binary search?",
            new String[]{"O(n)", "O(log n)", "O(n log n)", "O(n²)"},
            1,
            "college",
            "computer science"
        ));
        
        // PhD level questions
        questions.add(new Question(
            "What is the Schrödinger equation used for?",
            new String[]{"Describing quantum states", "Calculating energy", "Predicting chemical reactions", "All of the above"},
            3,
            "phd",
            "physics"
        ));
        
        questions.add(new Question(
            "What is the time complexity of quicksort in average case?",
            new String[]{"O(n)", "O(n log n)", "O(n²)", "O(log n)"},
            1,
            "phd",
            "computer science"
        ));
    }
    
    public List<Question> getQuestionsByDifficulty(String difficulty) {
        List<Question> filtered = new ArrayList<>();
        for (Question q : questions) {
            if (q.getDifficulty().equals(difficulty)) {
                filtered.add(q);
            }
        }
        return filtered;
    }
    
    public List<Question> getAllQuestions() {
        return new ArrayList<>(questions);
    }
}


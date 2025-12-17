package org.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.example.MainActivity;
import org.example.R;
import org.example.data.ScoreManager;

public class GameOverActivity extends AppCompatActivity {
    private ScoreManager scoreManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        
        scoreManager = new ScoreManager(this);
        
        Intent intent = getIntent();
        int score = intent.getIntExtra("score", 0);
        int friendsRescued = intent.getIntExtra("friendsRescued", 0);
        int totalFriends = intent.getIntExtra("totalFriends", 0);
        
        TextView scoreText = findViewById(R.id.final_score);
        TextView friendsText = findViewById(R.id.friends_rescued);
        TextView highScoreText = findViewById(R.id.high_score);
        
        if (scoreText != null) {
            scoreText.setText(getString(R.string.final_score_label, score));
        }
        
        if (friendsText != null) {
            friendsText.setText(getString(R.string.friends_rescued_label, friendsRescued, totalFriends));
        }
        
        if (highScoreText != null) {
            int highScore = scoreManager.getHighScore();
            highScoreText.setText(getString(R.string.high_score_label, highScore));
        }
        
        // Save score
        scoreManager.saveScore(score);
        
        Button playAgainButton = findViewById(R.id.play_again_button);
        Button mainMenuButton = findViewById(R.id.main_menu_button);
        
        if (playAgainButton != null) {
            playAgainButton.setOnClickListener(v -> {
                Intent gameIntent = new Intent(this, GameActivity.class);
                startActivity(gameIntent);
                finish();
            });
        }
        
        if (mainMenuButton != null) {
            mainMenuButton.setOnClickListener(v -> {
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            });
        }
    }
}


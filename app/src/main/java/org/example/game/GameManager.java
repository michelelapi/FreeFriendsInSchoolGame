package org.example.game;

import android.content.Context;
import android.content.Intent;

import org.example.ui.GameOverActivity;
import org.example.ui.QuestionDialog;

public class GameManager {
    private Context context;
    private GameEngine gameEngine;
    
    public GameManager(Context context) {
        this.context = context;
    }
    
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    public void handleGameStateChange(GameState newState) {
        if (gameEngine == null) {
            return;
        }
        
        switch (newState) {
            case QUESTION:
                showQuestionDialog();
                break;
            case GAME_OVER:
                showGameOver();
                break;
        }
    }
    
    private void showQuestionDialog() {
        // This will be handled by the activity
    }
    
    private void showGameOver() {
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra("score", gameEngine.getScore());
        intent.putExtra("friendsRescued", gameEngine.getFriendsRescued());
        intent.putExtra("totalFriends", gameEngine.getTotalFriends());
        context.startActivity(intent);
    }
}


package org.example.ui;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.example.R;
import org.example.entities.Teacher;
import org.example.game.GameEngine;
import org.example.game.GameState;
import org.example.game.GameView;
import org.example.questions.Question;
import org.example.questions.QuestionManager;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    private QuestionManager questionManager;
    private Question currentQuestion;
    private volatile boolean questionShown = false;
    private Switch visionConeSwitch;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        questionManager = new QuestionManager();
        
        // Create GameView programmatically
        android.widget.FrameLayout container = findViewById(R.id.game_container);
        gameView = new GameView(this);
        if (container != null) {
            container.addView(gameView);
        }
        
        // Set up vision cone toggle switch
        visionConeSwitch = findViewById(R.id.vision_cone_switch);
        if (visionConeSwitch != null) {
            // Set initial state based on current setting
            visionConeSwitch.setChecked(Teacher.SHOW_VISION_CONE);
            visionConeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Teacher.setVisionConeVisualization(isChecked);
            });
        }
        
        // Set up joystick - wait for GameView to be ready
        JoystickView joystickView = findViewById(R.id.joystick_view);
        if (joystickView != null && gameView != null) {
            // Set up joystick listener after GameView surface is created and GameEngine is initialized
            setupJoystick(joystickView, gameView);
        }
        
        // Set up game state listener
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);
                    if (gameView != null && gameView.getGameEngine() != null) {
                        GameState state = gameView.getGameEngine().getState();
                        if (state == GameState.QUESTION && !questionShown) {
                            questionShown = true;
                            runOnUiThread(() -> {
                                showQuestionDialog();
                            });
                        } else if (state == GameState.GAME_OVER) {
                            runOnUiThread(this::finishGame);
                            break;
                        } else if (state != GameState.QUESTION) {
                            questionShown = false;
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }
    
    private void showQuestionDialog() {
        if (gameView == null || gameView.getGameEngine() == null) {
            questionShown = false;
            return;
        }
        
        // Get a random question based on current level (for now, use elementary)
        currentQuestion = questionManager.getRandomQuestion("elementary");
        
        if (currentQuestion == null) {
            // No question available, continue game
            if (gameView.getGameEngine() != null) {
                gameView.getGameEngine().setState(GameState.PLAYING);
            }
            questionShown = false;
            return;
        }
        
        // Create and show the multiple choice question dialog
        QuestionDialog dialog = new QuestionDialog(this, currentQuestion, new QuestionDialog.OnAnswerSelectedListener() {
            @Override
            public void onAnswerSelected(boolean isCorrect) {
                if (isCorrect) {
                    Toast.makeText(GameActivity.this, "Correct! Run away!", Toast.LENGTH_SHORT).show();
                    if (gameView.getGameEngine() != null) {
                        // Freeze the teacher that caught the player
                        gameView.getGameEngine().freezeTeacherThatCaughtPlayer();
                        gameView.getGameEngine().setState(GameState.PLAYING);
                    }
                    questionShown = false;
                } else {
                    Toast.makeText(GameActivity.this, "Wrong answer! Game Over.", Toast.LENGTH_SHORT).show();
                    questionShown = false;
                    finishGame();
                }
            }
        });
        
        dialog.show();
    }
    
    private void finishGame() {
        GameEngine engine = gameView.getGameEngine();
        if (engine != null) {
            android.content.Intent intent = new android.content.Intent(this, GameOverActivity.class);
            intent.putExtra("score", engine.getScore());
            intent.putExtra("friendsRescued", engine.getFriendsRescued());
            intent.putExtra("totalFriends", engine.getTotalFriends());
            startActivity(intent);
            finish();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null && gameView.getGameEngine() != null) {
            gameView.getGameEngine().setState(GameState.PAUSED);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null && gameView.getGameEngine() != null) {
            if (gameView.getGameEngine().getState() == GameState.PAUSED) {
                gameView.getGameEngine().setState(GameState.PLAYING);
            }
        }
    }
    
    private void setupJoystick(JoystickView joystickView, GameView gameView) {
        // Try to set up joystick, retry if GameEngine is not ready yet
        gameView.post(() -> {
            GameEngine engine = gameView.getGameEngine();
            if (engine != null) {
                joystickView.setGameEngine(engine);
                joystickView.setJoystickListener(new JoystickView.JoystickListener() {
                    @Override
                    public void onJoystickMoved(float xPercent, float yPercent) {
                        gameView.handleJoystickInput(xPercent, yPercent);
                    }
                    
                    @Override
                    public void onJoystickReleased() {
                        gameView.handleJoystickRelease();
                    }
                });
            } else {
                // Retry after a short delay if GameEngine is not ready
                gameView.postDelayed(() -> setupJoystick(joystickView, gameView), 100);
            }
        });
    }
}


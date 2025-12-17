package org.example.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.example.R;
import org.example.questions.Question;

public class QuestionDialog extends Dialog {
    private Question question;
    private OnAnswerSelectedListener listener;
    private TextView questionText;
    private Button[] optionButtons;
    private boolean answerSelected = false;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(boolean isCorrect);
    }

    public QuestionDialog(Context context, Question question, OnAnswerSelectedListener listener) {
        super(context);
        this.question = question;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_question);
        setCancelable(false);

        // Set dialog window size to half the screen width and center it
        Window window = getWindow();
        if (window != null) {
            // Make window background transparent to show rounded corners
            window.setBackgroundDrawableResource(android.R.color.transparent);

            android.view.WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.5f); // 50% of screen
                                                                                                       // width
            params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = android.view.Gravity.CENTER; // Center the dialog on screen
            window.setAttributes(params);
        }

        // Initialize views
        questionText = findViewById(R.id.question_text);
        optionButtons = new Button[] {
                findViewById(R.id.option_button_0),
                findViewById(R.id.option_button_1),
                findViewById(R.id.option_button_2),
                findViewById(R.id.option_button_3)
        };

        // Set button backgrounds programmatically to ensure they're applied
        for (Button button : optionButtons) {
            if (button != null) {
                button.setBackgroundResource(R.drawable.button_background_blue);
                button.setBackgroundTintList(null);
            }
        }

        // Set question text
        if (questionText != null && question != null) {
            questionText.setText(question.getQuestionText());
        }

        // Set up option buttons
        if (question != null && question.getOptions() != null) {
            String[] options = question.getOptions();
            for (int i = 0; i < optionButtons.length && i < options.length; i++) {
                if (optionButtons[i] != null) {
                    optionButtons[i].setText(options[i]);
                    optionButtons[i].setVisibility(View.VISIBLE);

                    final int optionIndex = i;
                    optionButtons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!answerSelected) {
                                answerSelected = true;
                                boolean isCorrect = question.isCorrect(optionIndex);

                                // Highlight the selected button
                                highlightAnswer(optionIndex, isCorrect);

                                // Notify listener after a short delay to show the highlight
                                v.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listener != null) {
                                            listener.onAnswerSelected(isCorrect);
                                        }
                                        dismiss();
                                    }
                                }, 500);
                            }
                        }
                    });
                }
            }

            // Hide unused buttons
            for (int i = options.length; i < optionButtons.length; i++) {
                if (optionButtons[i] != null) {
                    optionButtons[i].setVisibility(View.GONE);
                }
            }
        }
    }

    private void highlightAnswer(int selectedIndex, boolean isCorrect) {
        if (optionButtons == null || selectedIndex < 0 || selectedIndex >= optionButtons.length) {
            return;
        }

        // Change background image based on correctness
        if (isCorrect) {
            // Green for correct answer
            optionButtons[selectedIndex].setBackgroundResource(R.drawable.button_background_green);
        } else {
            // Red for wrong answer
            optionButtons[selectedIndex].setBackgroundResource(R.drawable.button_background_red);

            // Also highlight the correct answer in green
            int correctIndex = question.getCorrectAnswerIndex();
            if (correctIndex >= 0 && correctIndex < optionButtons.length && optionButtons[correctIndex] != null) {
                optionButtons[correctIndex].setBackgroundResource(R.drawable.button_background_green);
            }
        }

        // Disable all buttons
        for (Button button : optionButtons) {
            if (button != null) {
                button.setEnabled(false);
            }
        }
    }
}

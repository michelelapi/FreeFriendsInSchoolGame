package org.example.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.example.R;

public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        
        TextView introText = findViewById(R.id.intro_text);
        if (introText != null) {
            introText.setText(getString(R.string.intro_story));
        }
    }
}


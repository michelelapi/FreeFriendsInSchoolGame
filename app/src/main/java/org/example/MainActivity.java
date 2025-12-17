package org.example;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.example.ui.GameActivity;
import org.example.ui.HelpActivity;
import org.example.ui.IntroActivity;
import org.example.ui.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button playButton = findViewById(R.id.play_button);
        Button introButton = findViewById(R.id.intro_button);
        Button helpButton = findViewById(R.id.help_button);
        Button settingsButton = findViewById(R.id.settings_button);
        
        if (playButton != null) {
            playButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);
            });
        }
        
        if (introButton != null) {
            introButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, IntroActivity.class);
                startActivity(intent);
            });
        }
        
        if (helpButton != null) {
            helpButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
            });
        }
        
        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            });
        }
    }
}


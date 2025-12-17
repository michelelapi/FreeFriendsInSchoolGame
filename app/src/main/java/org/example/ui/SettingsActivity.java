package org.example.ui;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import org.example.R;
import org.example.data.PreferencesManager;

public class SettingsActivity extends AppCompatActivity {
    private PreferencesManager preferencesManager;
    private Switch soundSwitch;
    private Switch musicSwitch;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        preferencesManager = new PreferencesManager(this);
        
        soundSwitch = findViewById(R.id.sound_switch);
        musicSwitch = findViewById(R.id.music_switch);
        
        if (soundSwitch != null) {
            soundSwitch.setChecked(preferencesManager.isSoundEnabled());
            soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                preferencesManager.setSoundEnabled(isChecked);
            });
        }
        
        if (musicSwitch != null) {
            musicSwitch.setChecked(preferencesManager.isMusicEnabled());
            musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                preferencesManager.setMusicEnabled(isChecked);
            });
        }
    }
}


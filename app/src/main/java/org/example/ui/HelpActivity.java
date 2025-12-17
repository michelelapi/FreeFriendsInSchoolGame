package org.example.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.example.R;

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        
        TextView helpText = findViewById(R.id.help_text);
        if (helpText != null) {
            helpText.setText(getString(R.string.help_instructions));
        }
    }
}


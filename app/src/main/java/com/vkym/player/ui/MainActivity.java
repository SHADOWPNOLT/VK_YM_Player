package com.vkym.player.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.vkym.player.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btnTest = findViewById(R.id.btnTest);
        btnTest.setOnClickListener(v -> {
            Toast.makeText(this, "VK & YM Player работает!", Toast.LENGTH_SHORT).show();
        });
    }
}

package com.vkym.player.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.vkym.player.R;
import com.vkym.player.ui.auth.VKAuthActivity;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btnVKAuth = findViewById(R.id.btnVKAuth);
        btnVKAuth.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VKAuthActivity.class);
            startActivity(intent);
        });
    }
}

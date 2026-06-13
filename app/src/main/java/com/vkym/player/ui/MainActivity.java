package com.vkym.player.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vkym.player.R;
import com.vkym.player.utils.SimpleDebug;
import com.vkym.player.ui.library.LibraryFragment;
import com.vkym.player.ui.player.FullscreenPlayerActivity;
import com.vkym.player.ui.search.SearchFragment;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDebug.log("MainActivity onCreate START");
        
        try {
            setContentView(R.layout.activity_main);
            SimpleDebug.log("setContentView OK");
            
            Button btnLogs = findViewById(R.id.btnShowLogs);
            SimpleDebug.log("btnLogs found: " + (btnLogs != null));
            
            if (btnLogs != null) {
                btnLogs.setOnClickListener(v -> {
                    SimpleDebug.log("Logs button clicked - trying to open LogsActivity");
                    try {
                        Intent intent = new Intent(MainActivity.this, LogsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        SimpleDebug.log("Error opening LogsActivity", e);
                        SimpleDebug.showError(MainActivity.this, e.toString());
                    }
                });
            }
            
            BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
            SimpleDebug.log("bottomNav found: " + (bottomNav != null));
            
            // Загружаем фрагмент
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, new LibraryFragment())
                .commit();
            SimpleDebug.log("Fragment loaded");
            
        } catch (Exception e) {
            SimpleDebug.log("CRITICAL ERROR in MainActivity", e);
            SimpleDebug.showError(this, e.toString());
        }
        SimpleDebug.log("MainActivity onCreate END");
    }
}

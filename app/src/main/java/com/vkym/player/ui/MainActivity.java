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
import com.vkym.player.utils.LogCollector;
import com.vkym.player.ui.library.LibraryFragment;
import com.vkym.player.ui.player.FullscreenPlayerActivity;
import com.vkym.player.ui.search.SearchFragment;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogCollector.add(TAG + ": onCreate started");
        
        try {
            setContentView(R.layout.activity_main);
            
            // Кнопка логов
            Button btnLogs = findViewById(R.id.btnShowLogs);
            if (btnLogs != null) {
                btnLogs.setOnClickListener(v -> {
                    LogCollector.add(TAG + ": Logs button clicked");
                    startActivity(new Intent(MainActivity.this, LogsActivity.class));
                });
            }
            
            bottomNavigationView = findViewById(R.id.bottomNavigation);
            if (bottomNavigationView == null) {
                LogCollector.add(TAG + ": bottomNavigationView is null!");
                return;
            }
            
            // Загружаем библиотеку по умолчанию
            try {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, new LibraryFragment())
                    .commit();
                LogCollector.add(TAG + ": Initial fragment loaded");
            } catch (Exception e) {
                LogCollector.add(TAG + ": Error loading initial fragment", e);
            }
            
            bottomNavigationView.setOnItemSelectedListener(item -> {
                try {
                    int itemId = item.getItemId();
                    LogCollector.add(TAG + ": Navigation clicked - " + itemId);
                    
                    if (itemId == R.id.navigation_library) {
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, new LibraryFragment())
                            .commit();
                        return true;
                    } else if (itemId == R.id.navigation_search) {
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, new SearchFragment())
                            .commit();
                        return true;
                    } else if (itemId == R.id.navigation_player) {
                        Intent intent = new Intent(MainActivity.this, FullscreenPlayerActivity.class);
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) {
                    LogCollector.add(TAG + ": Navigation error", e);
                }
                return false;
            });
            
            LogCollector.add(TAG + ": onCreate completed successfully");
        } catch (Exception e) {
            LogCollector.add(TAG + ": Critical error in onCreate", e);
        }
    }
}

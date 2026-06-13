package com.vkym.player.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vkym.player.R;
import com.vkym.player.utils.Logger;
import com.vkym.player.ui.library.LibraryFragment;
import com.vkym.player.ui.player.FullscreenPlayerActivity;
import com.vkym.player.ui.search.SearchFragment;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Logger.d(TAG + ": onCreate started");
            setContentView(R.layout.activity_main);
            
            bottomNavigationView = findViewById(R.id.bottomNavigation);
            if (bottomNavigationView == null) {
                Logger.e(TAG + ": bottomNavigationView is null!");
                return;
            }
            
            Logger.d(TAG + ": Loading initial fragment");
            // Загружаем библиотеку по умолчанию
            try {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, new LibraryFragment())
                    .commit();
            } catch (Exception e) {
                Logger.e(TAG + ": Error loading initial fragment", e);
                Logger.toast(this, "Ошибка загрузки фрагмента: " + e.getMessage());
            }
            
            bottomNavigationView.setOnItemSelectedListener(item -> {
                try {
                    int itemId = item.getItemId();
                    Logger.d(TAG + ": Navigation clicked - " + itemId);
                    
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
                    Logger.e(TAG + ": Navigation error", e);
                    Logger.toast(MainActivity.this, "Ошибка: " + e.getMessage());
                }
                return false;
            });
            
            Logger.d(TAG + ": onCreate completed successfully");
        } catch (Exception e) {
            Logger.e(TAG + ": Critical error in onCreate", e);
            Logger.toast(this, "Критическая ошибка: " + e.getMessage());
        }
    }
}

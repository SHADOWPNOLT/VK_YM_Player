package com.vkym.player.ui;
import android.content.Intent;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vkym.player.R;
import com.vkym.player.ui.library.LibraryFragment;
import com.vkym.player.ui.player.FullscreenPlayerActivity;
import com.vkym.player.ui.search.SearchFragment;
import com.vkym.player.utils.SimpleDebug;

public class MainActivity extends AppCompatActivity {
    
    private BottomNavigationView bottomNavigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDebug.log("MainActivity onCreate started");
        
        try {
            setContentView(R.layout.activity_main);
            SimpleDebug.log("setContentView OK");
            
            bottomNavigationView = findViewById(R.id.bottomNavigation);
            
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, new LibraryFragment())
                    .commit();
                SimpleDebug.log("Initial fragment loaded");
            }
            
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                Fragment selectedFragment = null;
                
                if (itemId == R.id.navigation_library) {
                    selectedFragment = new LibraryFragment();
                } else if (itemId == R.id.navigation_search) {
                    selectedFragment = new SearchFragment();
                } else if (itemId == R.id.navigation_player) {
                    startActivity(new Intent(MainActivity.this, FullscreenPlayerActivity.class));
                    return true;
                }
                
                if (selectedFragment != null) {
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                }
                return true;
            });
            
            SimpleDebug.log("MainActivity onCreate completed");
        } catch (Exception e) {
            SimpleDebug.log("MainActivity critical error", e);
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

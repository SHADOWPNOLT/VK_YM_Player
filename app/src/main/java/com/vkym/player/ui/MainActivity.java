package com.vkym.player.ui;

import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vkym.player.R;
import com.vkym.player.ui.library.LibraryFragment;
import com.vkym.player.ui.player.FullscreenPlayerActivity;
import com.vkym.player.ui.search.SearchFragment;

public class MainActivity extends AppCompatActivity {
    
    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        
        // Загружаем первый фрагмент
        if (savedInstanceState == null) {
            loadFragment(new LibraryFragment());
        }
        
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                
                if (itemId == R.id.navigation_library) {
                    selectedFragment = new LibraryFragment();
                } else if (itemId == R.id.navigation_search) {
                    selectedFragment = new SearchFragment();
                } else if (itemId == R.id.navigation_player) {
                    // Открываем плеер
                    startActivity(new Intent(MainActivity.this, FullscreenPlayerActivity.class));
                    return true;
                }
                
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
                return true;
            }
        });
    }
    
    private void loadFragment(Fragment fragment) {
        try {
            currentFragment = fragment;
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

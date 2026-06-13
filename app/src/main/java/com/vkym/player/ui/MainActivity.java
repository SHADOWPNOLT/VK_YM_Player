package com.vkym.player.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vkym.player.R;

public class MainActivity extends AppCompatActivity {
    
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        
        NavHostFragment navHostFragment = (NavHostFragment) 
            getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_library,
                R.id.navigation_search,
                R.id.navigation_player
            ).build();
            
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }
}

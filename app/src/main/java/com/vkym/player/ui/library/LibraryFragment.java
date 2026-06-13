package com.vkym.player.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vkym.player.R;
import com.vkym.player.utils.Logger;
import com.vkym.player.ui.auth.VKAuthActivity;

public class LibraryFragment extends Fragment {
    
    private static final String TAG = "LibraryFragment";
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnVKAuth;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Logger.d(TAG + ": onCreateView started");
        try {
            View view = inflater.inflate(R.layout.fragment_library, container, false);
            Logger.d(TAG + ": View inflated successfully");
            return view;
        } catch (Exception e) {
            Logger.e(TAG + ": Error inflating layout", e);
            Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d(TAG + ": onViewCreated started");
        
        try {
            viewPager = view.findViewById(R.id.viewPager);
            tabLayout = view.findViewById(R.id.tabLayout);
            btnVKAuth = view.findViewById(R.id.btnVKAuth);
            
            Logger.d(TAG + ": Views found - viewPager=" + (viewPager != null) + 
                   ", tabLayout=" + (tabLayout != null) + 
                   ", btnVKAuth=" + (btnVKAuth != null));
            
            if (btnVKAuth != null) {
                btnVKAuth.setOnClickListener(v -> {
                    Logger.d(TAG + ": VK Auth button clicked");
                    try {
                        Intent intent = new Intent(getActivity(), VKAuthActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Logger.e(TAG + ": Error starting VKAuthActivity", e);
                        Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            
            // Настройка ViewPager
            Logger.d(TAG + ": Setting up ViewPager");
            ViewPagerAdapter adapter = new ViewPagerAdapter(this);
            viewPager.setAdapter(adapter);
            
            new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Мои треки (ВК)");
                    } else {
                        tab.setText("Плейлисты");
                    }
                }
            ).attach();
            
            Logger.d(TAG + ": ViewPager setup completed");
            
        } catch (Exception e) {
            Logger.e(TAG + ": Error in onViewCreated", e);
            Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private static class ViewPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        
        public ViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }
        
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Logger.d("ViewPagerAdapter: createFragment position=" + position);
            if (position == 0) {
                return new VKTracksFragment();
            } else {
                return new PlaylistsFragment();
            }
        }
        
        @Override
        public int getItemCount() {
            return 2;
        }
    }
}

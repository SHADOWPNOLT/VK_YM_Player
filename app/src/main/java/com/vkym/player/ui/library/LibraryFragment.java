package com.vkym.player.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vkym.player.R;
import com.vkym.player.ui.auth.VKAuthActivity;

public class LibraryFragment extends Fragment {
    
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnVKAuth;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            viewPager = view.findViewById(R.id.viewPager);
            tabLayout = view.findViewById(R.id.tabLayout);
            btnVKAuth = view.findViewById(R.id.btnVKAuth);
            
            if (btnVKAuth != null) {
                btnVKAuth.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(getActivity(), VKAuthActivity.class));
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
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
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка инициализации: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private static class ViewPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        
        public ViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }
        
        @NonNull
        @Override
        public Fragment createFragment(int position) {
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

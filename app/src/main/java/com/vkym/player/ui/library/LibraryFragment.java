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
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vkym.player.R;
import com.vkym.player.di.ServiceLocator;
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
        
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        btnVKAuth = view.findViewById(R.id.btnVKAuth);
        
        // Проверяем статус авторизации
        if (ServiceLocator.getInstance().getVKApiClient().isLoggedIn()) {
            btnVKAuth.setText("VK: Авторизован");
            btnVKAuth.setEnabled(false);
        } else {
            btnVKAuth.setText("Авторизация VK");
            btnVKAuth.setEnabled(true);
        }
        
        btnVKAuth.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VKAuthActivity.class);
            startActivityForResult(intent, 100);
        });
        
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("Мои треки (ВК)");
                        break;
                    case 1:
                        tab.setText("Плейлисты");
                        break;
                }
            }
        ).attach();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == getActivity().RESULT_OK) {
            Toast.makeText(getContext(), "VK авторизация выполнена!", Toast.LENGTH_SHORT).show();
            btnVKAuth.setText("VK: Авторизован");
            btnVKAuth.setEnabled(false);
        }
    }
    
    private static class ViewPagerAdapter extends FragmentStateAdapter {
        
        public ViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }
        
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new VKTracksFragment();
                case 1:
                    return new PlaylistsFragment();
                default:
                    return new VKTracksFragment();
            }
        }
        
        @Override
        public int getItemCount() {
            return 2;
        }
    }
}

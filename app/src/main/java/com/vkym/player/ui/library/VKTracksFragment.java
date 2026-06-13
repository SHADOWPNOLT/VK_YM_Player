package com.vkym.player.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import com.vkym.player.ui.adapter.TrackAdapter;
import com.vkym.player.utils.Logger;
import java.util.ArrayList;
import java.util.List;

public class VKTracksFragment extends Fragment implements TrackAdapter.OnTrackClickListener {
    
    private static final String TAG = "VKTracksFragment";
    private RecyclerView recyclerView;
    private TrackAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Logger.d(TAG + ": onCreateView started");
        try {
            View view = inflater.inflate(R.layout.fragment_vk_tracks, container, false);
            Logger.d(TAG + ": View inflated");
            return view;
        } catch (Exception e) {
            Logger.e(TAG + ": Error inflating", e);
            Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d(TAG + ": onViewCreated started");
        
        try {
            recyclerView = view.findViewById(R.id.recyclerView);
            Logger.d(TAG + ": recyclerView found = " + (recyclerView != null));
            
            if (recyclerView == null) {
                Logger.e(TAG + ": recyclerView is null!");
                Toast.makeText(getContext(), "RecyclerView не найден", Toast.LENGTH_LONG).show();
                return;
            }
            
            adapter = new TrackAdapter();
            adapter.setOnTrackClickListener(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            
            // Тестовые треки
            List<Track> testTracks = new ArrayList<>();
            testTracks.add(new Track("1", "vk", "Bohemian Rhapsody", "Queen", 355000, "", ""));
            testTracks.add(new Track("2", "vk", "Billie Jean", "Michael Jackson", 294000, "", ""));
            testTracks.add(new Track("3", "vk", "Imagine", "John Lennon", 183000, "", ""));
            adapter.setTracks(testTracks);
            
            Logger.d(TAG + ": Tracks loaded: " + testTracks.size());
            
        } catch (Exception e) {
            Logger.e(TAG + ": Error in onViewCreated", e);
            Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onTrackClick(Track track, int position) {
        Logger.d(TAG + ": Track clicked - " + track.title);
        try {
            Toast.makeText(getContext(), "Воспроизведение: " + track.title, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Logger.e(TAG + ": Error on track click", e);
        }
    }
    
    @Override
    public void onTrackMenuClick(Track track, int position, View anchor) {
        Logger.d(TAG + ": Menu clicked for - " + track.title);
        try {
            String[] options = {"Добавить в плейлист", "Скачать", "Поделиться"};
            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle(track.title)
                .setItems(options, (dialog, which) -> {
                    if (which == 2) {
                        android.content.Intent shareIntent = new android.content.Intent();
                        shareIntent.setAction(android.content.Intent.ACTION_SEND);
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
                            track.title + " - " + track.artist);
                        shareIntent.setType("text/plain");
                        startActivity(android.content.Intent.createChooser(shareIntent, "Поделиться"));
                    } else {
                        Toast.makeText(getContext(), "Функция в разработке", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
        } catch (Exception e) {
            Logger.e(TAG + ": Error showing menu", e);
            Toast.makeText(getContext(), "Ошибка меню: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

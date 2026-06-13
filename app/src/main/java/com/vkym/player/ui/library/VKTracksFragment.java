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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import com.vkym.player.ui.adapter.TrackAdapter;
import java.util.ArrayList;
import java.util.List;

public class VKTracksFragment extends Fragment implements TrackAdapter.OnTrackClickListener {
    
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private TrackAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vk_tracks, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        
        adapter = new TrackAdapter();
        adapter.setOnTrackClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        // Добавляем тестовые данные
        List<Track> testTracks = new ArrayList<>();
        Track track1 = new Track("1", "vk", "Test Song 1", "Test Artist 1", 180000, "", "");
        Track track2 = new Track("2", "vk", "Test Song 2", "Test Artist 2", 210000, "", "");
        testTracks.add(track1);
        testTracks.add(track2);
        adapter.setTracks(testTracks);
        
        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
            Toast.makeText(getContext(), "Синхронизация VK треков", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onTrackClick(Track track, int position) {
        Toast.makeText(getContext(), "Playing: " + track.title, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onTrackMenuClick(Track track, int position, View anchor) {
        String[] options = {"Добавить в плейлист", "Скачать", "Поделиться"};
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle(track.title)
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        Toast.makeText(getContext(), "Добавлено в плейлист", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "Скачивание: " + track.title, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        android.content.Intent shareIntent = new android.content.Intent();
                        shareIntent.setAction(android.content.Intent.ACTION_SEND);
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
                            track.title + " - " + track.artist);
                        shareIntent.setType("text/plain");
                        startActivity(android.content.Intent.createChooser(shareIntent, "Поделиться"));
                        break;
                }
            })
            .show();
    }
}

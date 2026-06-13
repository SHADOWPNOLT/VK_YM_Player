package com.vkym.player.ui.library;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import com.vkym.player.media.PlaybackService;
import com.vkym.player.ui.adapter.TrackAdapter;
import java.util.ArrayList;
import java.util.List;

public class VKTracksFragment extends Fragment implements TrackAdapter.OnTrackClickListener {
    
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private TrackAdapter adapter;
    private PlaybackService playbackService;
    private boolean isBound = false;
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlaybackService.LocalBinder binder = (PlaybackService.LocalBinder) service;
            playbackService = binder.getService();
            isBound = true;
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            playbackService = null;
            isBound = false;
        }
    };
    
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
        
        // Тестовые треки с реальными URL для воспроизведения
        List<Track> testTracks = new ArrayList<>();
        Track track1 = new Track("1", "vk", "Test Song 1", "Test Artist", 180000, "", 
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
        Track track2 = new Track("2", "vk", "Test Song 2", "Test Artist", 210000, "",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3");
        Track track3 = new Track("3", "vk", "Test Song 3", "Test Artist", 240000, "",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3");
        testTracks.add(track1);
        testTracks.add(track2);
        testTracks.add(track3);
        adapter.setTracks(testTracks);
        
        // Запуск сервиса
        Intent intent = new Intent(getContext(), PlaybackService.class);
        getContext().startService(intent);
        getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        
        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
            Toast.makeText(getContext(), "Синхронизация VK треков", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onTrackClick(Track track, int position) {
        if (isBound && playbackService != null) {
            List<Track> tracks = adapter.getTracks();
            playbackService.playQueue(tracks, position);
            Toast.makeText(getContext(), "Воспроизведение: " + track.title, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Сервис не готов", Toast.LENGTH_SHORT).show();
        }
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
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isBound) {
            getContext().unbindService(serviceConnection);
            isBound = false;
        }
    }
}

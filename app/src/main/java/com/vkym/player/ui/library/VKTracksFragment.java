package com.vkym.player.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import com.vkym.player.di.ServiceLocator;
import com.vkym.player.ui.adapter.TrackAdapter;
import com.vkym.player.ui.viewmodel.SharedPlayerViewModel;

import java.util.ArrayList;
import java.util.List;

public class VKTracksFragment extends Fragment implements TrackAdapter.OnTrackClickListener {
    
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private TrackAdapter adapter;
    private LibraryViewModel viewModel;
    private SharedPlayerViewModel sharedPlayerViewModel;
    
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
        
        // Настройка RecyclerView
        adapter = new TrackAdapter();
        adapter.setOnTrackClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        // Инициализация ViewModels
        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new LibraryViewModel(ServiceLocator.getInstance().getMusicRepository());
            }
        }).get(LibraryViewModel.class);
        
        sharedPlayerViewModel = new ViewModelProvider(requireActivity())
            .get(SharedPlayerViewModel.class);
        
        // Наблюдение за треками
        viewModel.getVkTracks().observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null) {
                adapter.setTracks(tracks);
            }
        });
        
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefresh.setRefreshing(isLoading != null && isLoading);
        });
        
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Pull-to-refresh синхронизация
        swipeRefresh.setOnRefreshListener(() -> {
            // ownerId = 0 значит текущий пользователь
            viewModel.syncVKTracks(0);
        });
        
        // Загрузка треков
        viewModel.loadVKTracks();
    }
    
    @Override
    public void onTrackClick(Track track, int position) {
        // Воспроизведение трека
        List<Track> tracks = adapter.getTracks();
        if (tracks != null && !tracks.isEmpty()) {
            sharedPlayerViewModel.setQueue(tracks, position);
            // Здесь нужно запустить PlaybackService
            startPlaybackService(tracks, position);
        }
    }
    
    @Override
    public void onTrackMenuClick(Track track, int position, View anchor) {
        // Показать меню (добавить в плейлист, скачать, поделиться)
        showTrackMenu(track);
    }
    
    private void startPlaybackService(List<Track> tracks, int startIndex) {
        // Запуск сервиса воспроизведения
        // В реальном проекте здесь будет вызов PlaybackService
        Toast.makeText(getContext(), "Playing: " + tracks.get(startIndex).title, Toast.LENGTH_SHORT).show();
    }
    
    private void showTrackMenu(Track track) {
        // Диалог с опциями
        String[] options = {"Добавить в плейлист", "Скачать", "Поделиться"};
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle(track.title)
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        addToPlaylist(track);
                        break;
                    case 1:
                        downloadTrack(track);
                        break;
                    case 2:
                        shareTrack(track);
                        break;
                }
            })
            .show();
    }
    
    private void addToPlaylist(Track track) {
        Toast.makeText(getContext(), "Add to playlist: " + track.title, Toast.LENGTH_SHORT).show();
    }
    
    private void downloadTrack(Track track) {
        Toast.makeText(getContext(), "Download: " + track.title, Toast.LENGTH_SHORT).show();
    }
    
    private void shareTrack(Track track) {
        android.content.Intent shareIntent = new android.content.Intent();
        shareIntent.setAction(android.content.Intent.ACTION_SEND);
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
            "Check out this song: " + track.title + " by " + track.artist);
        shareIntent.setType("text/plain");
        startActivity(android.content.Intent.createChooser(shareIntent, "Share via"));
    }
}

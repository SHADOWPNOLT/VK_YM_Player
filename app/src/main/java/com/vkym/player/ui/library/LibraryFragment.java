package com.vkym.player.ui.library;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import com.vkym.player.ui.adapter.TrackAdapter;
import com.vkym.player.utils.SimpleDebug;
import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private TrackAdapter adapter;
    private TextView tvTrackCount;
    private MaterialButton btnAddTrack, btnScanFiles;
    private List<Track> tracks = new ArrayList<>();
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            SimpleDebug.log("LibraryFragment: onCreateView started");
            return inflater.inflate(R.layout.fragment_library, container, false);
        } catch (Exception e) {
            SimpleDebug.log("LibraryFragment onCreateView error", e);
            return new View(getContext());
        }
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            recyclerView = view.findViewById(R.id.recyclerView);
            tvTrackCount = view.findViewById(R.id.tvTrackCount);
            btnAddTrack = view.findViewById(R.id.btnAddTrack);
            btnScanFiles = view.findViewById(R.id.btnScanFiles);
            
            adapter = new TrackAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            
            // Тестовые треки
            tracks.add(new Track("1", "local", "Bohemian Rhapsody", "Queen", 355000, "", ""));
            tracks.add(new Track("2", "local", "Billie Jean", "Michael Jackson", 294000, "", ""));
            tracks.add(new Track("3", "local", "Imagine", "John Lennon", 183000, "", ""));
            
            adapter.setTracks(tracks);
            tvTrackCount.setText(tracks.size() + " треков");
            
            if (btnAddTrack != null) {
                btnAddTrack.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Выберите аудиофайл", Toast.LENGTH_SHORT).show();
                });
            }
            
            if (btnScanFiles != null) {
                btnScanFiles.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Сканирование (будет в следующей версии)", Toast.LENGTH_SHORT).show();
                });
            }
            
            SimpleDebug.log("LibraryFragment: initialized successfully");
        } catch (Exception e) {
            SimpleDebug.log("LibraryFragment onViewCreated error", e);
            Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

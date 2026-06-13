package com.vkym.player.ui.library;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.vkym.player.R;
import com.vkym.player.data.TrackManager;
import com.vkym.player.data.model.MyTrack;
import com.vkym.player.ui.adapter.TrackAdapter;
import com.vkym.player.utils.SimpleDebug;
import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private TrackAdapter adapter;
    private TrackManager trackManager;
    private TextView tvTrackCount;
    private MaterialButton btnAddTrack, btnScanFiles;
    private List<MyTrack> tracks = new ArrayList<>();
    
    private final ActivityResultLauncher<String> requestPermissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                scanAudioFiles();
            } else {
                Toast.makeText(getContext(), "Разрешение нужно для сканирования музыки", Toast.LENGTH_LONG).show();
            }
        });
    
    private final ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            if (permissions.values().stream().allMatch(granted -> granted)) {
                scanAudioFiles();
            } else {
                Toast.makeText(getContext(), "Нужны разрешения для доступа к музыке", Toast.LENGTH_LONG).show();
            }
        });
    
    private final ActivityResultLauncher<Intent> pickFileLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    addTrackFromUri(uri);
                }
            }
        });
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.recyclerView);
        tvTrackCount = view.findViewById(R.id.tvTrackCount);
        btnAddTrack = view.findViewById(R.id.btnAddTrack);
        btnScanFiles = view.findViewById(R.id.btnScanFiles);
        
        trackManager = TrackManager.getInstance(getContext());
        
        adapter = new TrackAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        loadTracks();
        
        btnAddTrack.setOnClickListener(v -> pickAudioFile());
        btnScanFiles.setOnClickListener(v -> checkPermissionsAndScan());
    }
    
    private void loadTracks() {
        tracks = trackManager.getAllTracks();
        adapter.setTracks(convertToTrackList(tracks));
        tvTrackCount.setText(tracks.size() + " треков");
    }
    
    private List<com.vkym.player.data.model.Track> convertToTrackList(List<MyTrack> myTracks) {
        List<com.vkym.player.data.model.Track> trackList = new ArrayList<>();
        for (MyTrack t : myTracks) {
            com.vkym.player.data.model.Track track = new com.vkym.player.data.model.Track(
                t.getId(), "local", t.getTitle(), t.getArtist(),
                t.getDuration(), "", t.getFilePath()
            );
            trackList.add(track);
        }
        return trackList;
    }
    
    private void pickAudioFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pickFileLauncher.launch(intent);
    }
    
    private void addTrackFromUri(Uri uri) {
        try {
            String fileName = getFileName(uri);
            String duration = getAudioDuration(uri);
            
            MyTrack track = new MyTrack(
                String.valueOf(System.currentTimeMillis()),
                fileName,
                "Импортировано",
                uri.toString(),
                Long.parseLong(duration)
            );
            trackManager.addTrack(track);
            loadTracks();
            Toast.makeText(getContext(), "Добавлено: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            SimpleDebug.log("Error adding track", e);
            Toast.makeText(getContext(), "Ошибка добавления", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(nameIndex);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "Unknown Track";
    }
    
    private String getAudioDuration(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(getContext(), uri);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return duration != null ? duration : "0";
        } catch (Exception e) {
            return "0";
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {}
        }
    }
    
    private void checkPermissionsAndScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO);
            } else {
                scanAudioFiles();
            }
        } else {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                scanAudioFiles();
            }
        }
    }
    
    private void scanAudioFiles() {
        Toast.makeText(getContext(), "Сканирование аудиофайлов...", Toast.LENGTH_SHORT).show();
        trackManager.scanMediaFiles();
        // Небольшая задержка для завершения сканирования
        recyclerView.postDelayed(this::loadTracks, 2000);
    }
}

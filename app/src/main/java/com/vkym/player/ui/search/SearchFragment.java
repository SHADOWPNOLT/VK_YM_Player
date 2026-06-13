package com.vkym.player.ui.search;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import com.vkym.player.ui.adapter.TrackAdapter;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements TrackAdapter.OnTrackClickListener {
    
    private TextInputEditText searchInput;
    private RecyclerView searchResults;
    private TrackAdapter adapter;
    private List<Track> allTracks = new ArrayList<>();
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        searchInput = view.findViewById(R.id.searchInput);
        searchResults = view.findViewById(R.id.searchResults);
        
        adapter = new TrackAdapter();
        adapter.setOnTrackClickListener(this);
        searchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResults.setAdapter(adapter);
        
        // Добавляем тестовые данные для поиска
        addTestData();
        
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }
    
    private void addTestData() {
        allTracks.add(new Track("1", "vk", "Bohemian Rhapsody", "Queen", 355000, "", ""));
        allTracks.add(new Track("2", "vk", "Billie Jean", "Michael Jackson", 294000, "", ""));
        allTracks.add(new Track("3", "vk", "Imagine", "John Lennon", 183000, "", ""));
        allTracks.add(new Track("4", "vk", "Smells Like Teen Spirit", "Nirvana", 302000, "", ""));
        allTracks.add(new Track("5", "vk", "Hotel California", "Eagles", 391000, "", ""));
        allTracks.add(new Track("6", "ym", "Shape of You", "Ed Sheeran", 233000, "", ""));
        allTracks.add(new Track("7", "ym", "Blinding Lights", "The Weeknd", 200000, "", ""));
        allTracks.add(new Track("8", "ym", "Dance Monkey", "Tones and I", 209000, "", ""));
    }
    
    private void performSearch() {
        String query = searchInput.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Введите поисковый запрос", Toast.LENGTH_SHORT).show();
            adapter.setTracks(new ArrayList<>());
            return;
        }
        
        List<Track> results = new ArrayList<>();
        for (Track track : allTracks) {
            if (track.title.toLowerCase().contains(query) || 
                track.artist.toLowerCase().contains(query)) {
                results.add(track);
            }
        }
        
        adapter.setTracks(results);
        if (results.isEmpty()) {
            Toast.makeText(getContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Найдено: " + results.size(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onTrackClick(Track track, int position) {
        Toast.makeText(getContext(), "Воспроизведение: " + track.title, Toast.LENGTH_SHORT).show();
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

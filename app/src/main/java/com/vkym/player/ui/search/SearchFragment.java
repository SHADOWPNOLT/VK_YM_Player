package com.vkym.player.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import com.vkym.player.data.repository.MusicRepository;
import com.vkym.player.di.ServiceLocator;
import com.vkym.player.ui.adapter.TrackAdapter;
import com.vkym.player.ui.viewmodel.SharedPlayerViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchFragment extends Fragment implements TrackAdapter.OnTrackClickListener {
    
    private TextInputEditText searchInput;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TrackAdapter adapter;
    private MusicRepository repository;
    private SharedPlayerViewModel sharedPlayerViewModel;
    private CompositeDisposable disposables = new CompositeDisposable();
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    
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
        recyclerView = view.findViewById(R.id.searchResults);
        progressBar = new ProgressBar(getContext());
        
        repository = ServiceLocator.getInstance().getMusicRepository();
        sharedPlayerViewModel = new ViewModelProvider(requireActivity())
            .get(SharedPlayerViewModel.class);
        
        adapter = new TrackAdapter();
        adapter.setOnTrackClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        // Обработка поиска с debounce
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }
    
    private void performSearch() {
        String query = searchInput.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Введите поисковый запрос", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading(true);
        
        disposables.add(
            repository.searchBoth(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    tracks -> {
                        showLoading(false);
                        adapter.setTracks(tracks);
                        if (tracks.isEmpty()) {
                            Toast.makeText(getContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        showLoading(false);
                        Toast.makeText(getContext(), "Ошибка поиска: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                )
        );
    }
    
    private void showLoading(boolean show) {
        if (progressBar.getParent() != null) {
            ((ViewGroup) progressBar.getParent()).removeView(progressBar);
        }
        if (show) {
            ((ViewGroup) recyclerView.getParent()).addView(progressBar);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onTrackClick(Track track, int position) {
        List<Track> tracks = adapter.getTracks();
        if (tracks != null && !tracks.isEmpty()) {
            sharedPlayerViewModel.setQueue(tracks, position);
            Toast.makeText(getContext(), "Playing: " + track.title, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Add to playlist: " + track.title, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "Download: " + track.title, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        shareTrack(track);
                        break;
                }
            })
            .show();
    }
    
    private void shareTrack(Track track) {
        android.content.Intent shareIntent = new android.content.Intent();
        shareIntent.setAction(android.content.Intent.ACTION_SEND);
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
            "Check out: " + track.title + " by " + track.artist);
        shareIntent.setType("text/plain");
        startActivity(android.content.Intent.createChooser(shareIntent, "Share via"));
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        if (searchHandler != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}

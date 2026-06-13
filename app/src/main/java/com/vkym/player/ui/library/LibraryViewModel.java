package com.vkym.player.ui.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vkym.player.data.model.Playlist;
import com.vkym.player.data.model.Track;
import com.vkym.player.data.repository.MusicRepository;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LibraryViewModel extends ViewModel {
    private final MusicRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    
    private final MutableLiveData<List<Track>> vkTracks = new MutableLiveData<>();
    private final MutableLiveData<List<Playlist>> playlists = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public LibraryViewModel(MusicRepository repository) {
        this.repository = repository;
    }
    
    public void loadVKTracks() {
        isLoading.setValue(true);
        disposables.add(
            repository.getLocalVKTracks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    tracks -> {
                        vkTracks.setValue(tracks);
                        isLoading.setValue(false);
                    },
                    error -> {
                        errorMessage.setValue("Failed to load VK tracks: " + error.getMessage());
                        isLoading.setValue(false);
                    }
                )
        );
    }
    
    public void syncVKTracks(int ownerId) {
        isLoading.setValue(true);
        disposables.add(
            repository.syncVKTracks(ownerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    tracks -> {
                        vkTracks.setValue(tracks);
                        isLoading.setValue(false);
                    },
                    error -> {
                        errorMessage.setValue("Sync failed: " + error.getMessage());
                        isLoading.setValue(false);
                    }
                )
        );
    }
    
    public void loadPlaylists() {
        disposables.add(
            repository.getAllPlaylists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    playlists::setValue,
                    error -> errorMessage.setValue("Failed to load playlists: " + error.getMessage())
                )
        );
    }
    
    public LiveData<List<Track>> getVkTracks() {
        return vkTracks;
    }
    
    public LiveData<List<Playlist>> getPlaylists() {
        return playlists;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}

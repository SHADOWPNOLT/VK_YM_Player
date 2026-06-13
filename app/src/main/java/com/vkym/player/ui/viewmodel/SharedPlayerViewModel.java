package com.vkym.player.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vkym.player.data.model.Track;

import java.util.List;

public class SharedPlayerViewModel extends ViewModel {
    
    private final MutableLiveData<List<Track>> currentQueue = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentIndex = new MutableLiveData<>(-1);
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Track> currentTrack = new MutableLiveData<>();
    private final MutableLiveData<Long> currentPosition = new MutableLiveData<>(0L);
    private final MutableLiveData<Integer> repeatMode = new MutableLiveData<>(0); // 0=off, 1=all, 2=one
    private final MutableLiveData<Boolean> shuffleEnabled = new MutableLiveData<>(false);
    
    public void setQueue(List<Track> queue, int index) {
        currentQueue.setValue(queue);
        currentIndex.setValue(index);
        if (queue != null && index >= 0 && index < queue.size()) {
            currentTrack.setValue(queue.get(index));
        }
    }
    
    public void setCurrentTrack(Track track) {
        currentTrack.setValue(track);
    }
    
    public void setIsPlaying(boolean playing) {
        isPlaying.setValue(playing);
    }
    
    public void setCurrentPosition(long position) {
        currentPosition.setValue(position);
    }
    
    public void setRepeatMode(int mode) {
        repeatMode.setValue(mode);
    }
    
    public void setShuffleEnabled(boolean enabled) {
        shuffleEnabled.setValue(enabled);
    }
    
    public LiveData<List<Track>> getCurrentQueue() {
        return currentQueue;
    }
    
    public LiveData<Integer> getCurrentIndex() {
        return currentIndex;
    }
    
    public LiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }
    
    public LiveData<Track> getCurrentTrack() {
        return currentTrack;
    }
    
    public LiveData<Long> getCurrentPosition() {
        return currentPosition;
    }
    
    public LiveData<Integer> getRepeatMode() {
        return repeatMode;
    }
    
    public LiveData<Boolean> getShuffleEnabled() {
        return shuffleEnabled;
    }
    
    public void nextTrack() {
        List<Track> queue = currentQueue.getValue();
        Integer index = currentIndex.getValue();
        if (queue != null && index != null && index + 1 < queue.size()) {
            currentIndex.setValue(index + 1);
            currentTrack.setValue(queue.get(index + 1));
        }
    }
    
    public void previousTrack() {
        List<Track> queue = currentQueue.getValue();
        Integer index = currentIndex.getValue();
        if (queue != null && index != null && index - 1 >= 0) {
            currentIndex.setValue(index - 1);
            currentTrack.setValue(queue.get(index - 1));
        }
    }
}

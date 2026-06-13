package com.vkym.player.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import com.vkym.player.data.model.MyTrack;
import com.vkym.player.utils.SimpleDebug;
import java.util.ArrayList;
import java.util.List;

public class TrackManager {
    private static TrackManager instance;
    private List<MyTrack> myTracks = new ArrayList<>();
    private Context context;
    
    public static TrackManager getInstance(Context context) {
        if (instance == null) {
            instance = new TrackManager(context);
        }
        return instance;
    }
    
    private TrackManager(Context context) {
        this.context = context.getApplicationContext();
        loadSavedTracks();
    }
    
    public void loadSavedTracks() {
        // Загружаем из SharedPreferences
        // Пока добавляем тестовые
        if (myTracks.isEmpty()) {
            addDemoTracks();
        }
    }
    
    private void addDemoTracks() {
        myTracks.add(new MyTrack("1", "Bohemian Rhapsody", "Queen", "", 355000));
        myTracks.add(new MyTrack("2", "Billie Jean", "Michael Jackson", "", 294000));
        myTracks.add(new MyTrack("3", "Imagine", "John Lennon", "", 183000));
    }
    
    public void addTrack(MyTrack track) {
        myTracks.add(track);
        SimpleDebug.log("Track added: " + track.getTitle());
    }
    
    public void removeTrack(MyTrack track) {
        myTracks.remove(track);
    }
    
    public List<MyTrack> getAllTracks() {
        return new ArrayList<>(myTracks);
    }
    
    public List<MyTrack> searchTracks(String query) {
        List<MyTrack> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (MyTrack track : myTracks) {
            if (track.getTitle().toLowerCase().contains(lowerQuery) ||
                track.getArtist().toLowerCase().contains(lowerQuery)) {
                results.add(track);
            }
        }
        return results;
    }
    
    public void scanMediaFiles() {
        // Сканируем медиафайлы с телефона
        new Thread(() -> {
            List<MyTrack> audioFiles = getAudioFilesFromDevice();
            for (MyTrack track : audioFiles) {
                if (!myTracks.contains(track)) {
                    myTracks.add(track);
                }
            }
        }).start();
    }
    
    private List<MyTrack> getAudioFilesFromDevice() {
        List<MyTrack> tracks = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        };
        
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                
                if (title != null && filePath != null) {
                    tracks.add(new MyTrack(id, title, artist != null ? artist : "Unknown", filePath, duration));
                }
            }
            cursor.close();
        }
        return tracks;
    }
}

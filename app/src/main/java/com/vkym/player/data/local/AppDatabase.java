package com.vkym.player.data.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.vkym.player.data.local.dao.TrackDao;
import com.vkym.player.data.local.dao.PlaylistDao;
import com.vkym.player.data.model.Track;
import com.vkym.player.data.model.Playlist;
import com.vkym.player.data.model.PlaylistTrack;

@Database(entities = {Track.class, Playlist.class, PlaylistTrack.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract TrackDao trackDao();
    public abstract PlaylistDao playlistDao();
    
    private static volatile AppDatabase instance;
    
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "vkym_music_db"
                    ).build();
                }
            }
        }
        return instance;
    }
}

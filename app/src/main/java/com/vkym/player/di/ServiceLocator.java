package com.vkym.player.di;

import android.content.Context;
import com.vkym.player.VKYMApplication;
import com.vkym.player.data.local.AppDatabase;
import com.vkym.player.data.local.dao.TrackDao;
import com.vkym.player.data.local.dao.PlaylistDao;
import com.vkym.player.data.remote.VKApiClient;
import com.vkym.player.data.remote.YMApiClient;
import com.vkym.player.data.repository.MusicRepository;

public class ServiceLocator {
    private static ServiceLocator instance;
    private final Context context;
    private AppDatabase appDatabase;
    private VKApiClient vkApiClient;
    private YMApiClient ymApiClient;
    private MusicRepository musicRepository;
    
    private ServiceLocator(Context context) {
        this.context = context.getApplicationContext();
    }
    
    public static synchronized ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator(VKYMApplication.getInstance());
        }
        return instance;
    }
    
    public AppDatabase getAppDatabase() {
        if (appDatabase == null) {
            appDatabase = AppDatabase.getInstance(context);
        }
        return appDatabase;
    }
    
    public TrackDao getTrackDao() {
        return getAppDatabase().trackDao();
    }
    
    public PlaylistDao getPlaylistDao() {
        return getAppDatabase().playlistDao();
    }
    
    public VKApiClient getVKApiClient() {
        if (vkApiClient == null) {
            vkApiClient = new VKApiClient(context);
        }
        return vkApiClient;
    }
    
    public YMApiClient getYMApiClient() {
        if (ymApiClient == null) {
            ymApiClient = new YMApiClient(context);
        }
        return ymApiClient;
    }
    
    public MusicRepository getMusicRepository() {
        if (musicRepository == null) {
            musicRepository = new MusicRepository(
                context,
                getVKApiClient(),
                getYMApiClient(),
                getAppDatabase()
            );
        }
        return musicRepository;
    }
}

package com.vkym.player.di;

import android.content.Context;
import com.vkym.player.VKYMApplication;
import com.vkym.player.data.remote.VKApiClient;

public class ServiceLocator {
    private static ServiceLocator instance;
    private final Context context;
    private VKApiClient vkApiClient;
    
    private ServiceLocator(Context context) {
        this.context = context.getApplicationContext();
    }
    
    public static synchronized ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator(VKYMApplication.getInstance());
        }
        return instance;
    }
    
    public VKApiClient getVKApiClient() {
        if (vkApiClient == null) {
            vkApiClient = new VKApiClient(context);
        }
        return vkApiClient;
    }
}

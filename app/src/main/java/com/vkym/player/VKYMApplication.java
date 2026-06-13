package com.vkym.player;

import android.app.Application;
import com.vkym.player.di.ServiceLocator;

public class VKYMApplication extends Application {
    private static VKYMApplication instance;
    private ServiceLocator serviceLocator;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        serviceLocator = new ServiceLocator(this);
    }
    
    public static VKYMApplication getInstance() {
        return instance;
    }
    
    public ServiceLocator getServiceLocator() {
        return serviceLocator;
    }
}

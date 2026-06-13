package com.vkym.player;

import android.app.Application;

public class VKYMApplication extends Application {
    private static VKYMApplication instance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    
    public static VKYMApplication getInstance() {
        return instance;
    }
}

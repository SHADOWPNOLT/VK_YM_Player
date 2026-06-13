package com.vkym.player.data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class VKApiClient {
    private final SharedPreferences securePrefs;
    
    public VKApiClient(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("vk_prefs", Context.MODE_PRIVATE);
        this.securePrefs = prefs;
    }
    
    public void saveAccessToken(String token) {
        securePrefs.edit().putString("access_token", token).apply();
    }
    
    public String getAccessToken() {
        return securePrefs.getString("access_token", "");
    }
    
    public boolean isLoggedIn() {
        return !getAccessToken().isEmpty();
    }
    
    public void logout() {
        securePrefs.edit().remove("access_token").apply();
    }
}

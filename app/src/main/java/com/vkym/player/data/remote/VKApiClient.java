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
        SharedPreferences prefs = null;
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            prefs = EncryptedSharedPreferences.create(
                "vk_secure_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Fallback to regular prefs if encryption fails
            prefs = context.getSharedPreferences("vk_secure_prefs_fallback", Context.MODE_PRIVATE);
        }
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

package com.vkym.player.data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.vkym.player.data.model.Track;
import com.vkym.player.data.remote.vk.VKApiService;
import com.vkym.player.data.remote.vk.VKAudioItem;
import com.vkym.player.data.remote.vk.VKResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class VKApiClient {
    private static final String BASE_URL = "https://api.vk.com/method/";
    private static final String API_VERSION = "5.199";
    
    private final VKApiService apiService;
    private final SharedPreferences securePrefs;
    
    public VKApiClient(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            securePrefs = EncryptedSharedPreferences.create(
                "vk_secure_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to create encrypted prefs", e);
        }
        
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(chain -> {
                String token = securePrefs.getString("access_token", "");
                okhttp3.Request request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
                return chain.proceed(request);
            })
            .build();
        
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build();
        
        apiService = retrofit.create(VKApiService.class);
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
    
    public Single<List<Track>> getMyTracks(int ownerId, int count, int offset) {
        String token = getAccessToken();
        if (token.isEmpty()) {
            return Single.error(new Exception("Not logged in to VK"));
        }
        
        return apiService.getAudioGet(token, API_VERSION, ownerId, count, offset)
            .map(response -> {
                if (!response.isSuccessful() || response.response == null) {
                    throw new Exception("VK API error: " + 
                        (response.error != null ? response.error.error_msg : "Unknown error"));
                }
                
                List<Track> tracks = new ArrayList<>();
                for (VKAudioItem item : response.response.items) {
                    Track track = new Track();
                    track.id = "vk_" + item.ownerId + "_" + item.id;
                    track.source = "vk";
                    track.title = item.title;
                    track.artist = item.artist;
                    track.duration = item.duration * 1000L; // convert to ms
                    track.coverUrl = item.getCoverUrl();
                    // URL может быть прямым или требовать дополнительного резолвинга
                    track.streamUrl = item.url != null ? item.url : "";
                    tracks.add(track);
                }
                return tracks;
            });
    }
}

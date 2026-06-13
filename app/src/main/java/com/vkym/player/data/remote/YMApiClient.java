package com.vkym.player.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

import com.vkym.player.data.model.Track;
import com.vkym.player.data.remote.ym.PersistentCookieJar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class YMApiClient {
    private static final String BASE_URL = "https://music.yandex.ru/";
    
    private final YMApiService apiService;
    private final SharedPreferences prefs;
    
    public interface YMApiService {
        @GET("handlers/music-search.jsx")
        retrofit2.Call<YMSearchResult> searchSync(
            @Query("text") String query,
            @Query("page") int page
        );
    }
    
    public YMApiClient(Context context) {
        this.prefs = context.getSharedPreferences("ym_prefs", Context.MODE_PRIVATE);
        
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .cookieJar(new PersistentCookieJar(context))
            .build();
        
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build();
        
        apiService = retrofit.create(YMApiService.class);
    }
    
    public Single<List<Track>> search(String query) {
        return Single.create(emitter -> {
            try {
                retrofit2.Response<YMSearchResult> response = apiService.searchSync(query, 0).execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<Track> tracks = new ArrayList<>();
                    if (response.body().tracks != null && response.body().tracks.results != null) {
                        for (YMSearchResult.YMTrack ymTrack : response.body().tracks.results) {
                            Track track = new Track();
                            track.id = "ym_" + ymTrack.id;
                            track.source = "ym";
                            track.title = ymTrack.title;
                            track.artist = ymTrack.getArtistName();
                            track.duration = ymTrack.durationMs;
                            if (ymTrack.coverUri != null) {
                                track.coverUrl = "https://" + ymTrack.coverUri.replace("%%", "200x200");
                            }
                            // URL стрима требует отдельного запроса с подписью
                            track.streamUrl = "";
                            tracks.add(track);
                        }
                    }
                    emitter.onSuccess(tracks);
                } else {
                    emitter.onError(new Exception("YM search failed: " + response.code()));
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
    
    public void saveCookies(String cookies) {
        prefs.edit().putString("cookies", cookies).apply();
    }
    
    public boolean hasCookies() {
        return prefs.contains("cookies");
    }
}

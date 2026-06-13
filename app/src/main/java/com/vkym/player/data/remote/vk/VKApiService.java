package com.vkym.player.data.remote.vk;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VKApiService {
    
    @GET("audio.get")
    Single<VKResponse<VKAudioItem>> getAudioGet(
        @Query("access_token") String accessToken,
        @Query("v") String version,
        @Query("owner_id") int ownerId,
        @Query("count") int count,
        @Query("offset") int offset
    );
    
    @GET("execute")
    Single<VKResponse<VKAudioItem>> executeGetAudioWithUrls(
        @Query("access_token") String accessToken,
        @Query("v") String version,
        @Query("code") String code
    );
}

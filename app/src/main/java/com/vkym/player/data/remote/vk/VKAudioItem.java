package com.vkym.player.data.remote.vk;

import com.google.gson.annotations.SerializedName;

public class VKAudioItem {
    public int id;
    @SerializedName("owner_id")
    public int ownerId;
    public String title;
    public String artist;
    public int duration;
    public String url;
    
    @SerializedName("thumb")
    public Thumb thumb;
    
    public static class Thumb {
        @SerializedName("photo_300")
        public String photo300;
    }
    
    public String getCoverUrl() {
        if (thumb != null && thumb.photo300 != null) {
            return thumb.photo300;
        }
        return null;
    }
}

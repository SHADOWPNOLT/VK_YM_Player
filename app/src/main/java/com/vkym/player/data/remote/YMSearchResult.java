package com.vkym.player.data.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class YMSearchResult {
    @SerializedName("tracks")
    public TracksContainer tracks;
    
    public static class TracksContainer {
        public List<YMTrack> results;
    }
    
    public static class YMTrack {
        public String id;
        public String title;
        public List<YMArtist> artists;
        public int durationMs;
        @SerializedName("coverUri")
        public String coverUri;
        
        public String getArtistName() {
            if (artists != null && !artists.isEmpty()) {
                return artists.get(0).name;
            }
            return "Unknown Artist";
        }
    }
    
    public static class YMArtist {
        public String name;
    }
}

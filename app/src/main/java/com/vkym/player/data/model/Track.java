package com.vkym.player.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tracks")
public class Track {
    @PrimaryKey
    public String id;          // source:trackId (vk:123, ym:456)
    public String source;      // "vk" или "ym"
    public String title;
    public String artist;
    public long duration;      // в миллисекундах
    public String coverUrl;
    public String streamUrl;
    public boolean isFavorite;
    public long addedTimestamp;
    
    // Конструктор по умолчанию (нужен для Room)
    public Track() {}
    
    // Конструктор с параметрами
    public Track(String id, String source, String title, String artist, 
                 long duration, String coverUrl, String streamUrl) {
        this.id = id;
        this.source = source;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.coverUrl = coverUrl;
        this.streamUrl = streamUrl;
        this.isFavorite = false;
        this.addedTimestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    
    public String getStreamUrl() { return streamUrl; }
    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }
    
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    
    public long getAddedTimestamp() { return addedTimestamp; }
    public void setAddedTimestamp(long addedTimestamp) { this.addedTimestamp = addedTimestamp; }
}

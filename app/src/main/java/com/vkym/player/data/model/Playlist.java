package com.vkym.player.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlists")
public class Playlist {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public String coverUrl;
    public String source; // "vk", "ym", "local"
    public long createdAt;
    
    public Playlist() {}
    
    public Playlist(String name, String coverUrl, String source) {
        this.name = name;
        this.coverUrl = coverUrl;
        this.source = source;
        this.createdAt = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}

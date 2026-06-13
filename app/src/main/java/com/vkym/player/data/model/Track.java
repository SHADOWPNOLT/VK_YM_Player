package com.vkym.player.data.model;

public class Track {
    public String id;
    public String source;
    public String title;
    public String artist;
    public long duration;
    public String coverUrl;
    public String streamUrl;
    public boolean isFavorite;
    
    public Track() {}
    
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
    }
    
    // Getters
    public String getId() { return id; }
    public String getSource() { return source; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public long getDuration() { return duration; }
    public String getCoverUrl() { return coverUrl; }
    public String getStreamUrl() { return streamUrl; }
    public boolean isFavorite() { return isFavorite; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setSource(String source) { this.source = source; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setDuration(long duration) { this.duration = duration; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}

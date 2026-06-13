package com.vkym.player.data.model;

import java.io.Serializable;

public class MyTrack implements Serializable {
    private String id;
    private String title;
    private String artist;
    private String filePath;
    private long duration;
    private String albumArt;
    private boolean isFavorite;
    private long addedDate;
    
    public MyTrack(String id, String title, String artist, String filePath, long duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.duration = duration;
        this.addedDate = System.currentTimeMillis();
        this.isFavorite = false;
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getFilePath() { return filePath; }
    public long getDuration() { return duration; }
    public String getAlbumArt() { return albumArt; }
    public boolean isFavorite() { return isFavorite; }
    public long getAddedDate() { return addedDate; }
    
    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public void setAlbumArt(String albumArt) { this.albumArt = albumArt; }
}

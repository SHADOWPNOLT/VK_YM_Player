package com.vkym.player.data.model;

import androidx.room.Entity;

@Entity(tableName = "playlist_tracks", primaryKeys = {"playlistId", "trackId"})
public class PlaylistTrack {
    public long playlistId;
    public String trackId;
    
    public PlaylistTrack() {}
    
    public PlaylistTrack(long playlistId, String trackId) {
        this.playlistId = playlistId;
        this.trackId = trackId;
    }
    
    public long getPlaylistId() { return playlistId; }
    public void setPlaylistId(long playlistId) { this.playlistId = playlistId; }
    
    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }
}

package com.vkym.player.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;

import com.vkym.player.data.model.Playlist;
import com.vkym.player.data.model.PlaylistTrack;
import com.vkym.player.data.model.Track;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PlaylistDao {
    
    // Playlist operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPlaylist(Playlist playlist);
    
    @Query("SELECT * FROM playlists")
    Single<List<Playlist>> getAllPlaylists();
    
    @Delete
    Completable deletePlaylist(Playlist playlist);
    
    // PlaylistTrack operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addTrackToPlaylist(PlaylistTrack playlistTrack);
    
    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    Completable removeTrackFromPlaylist(long playlistId, String trackId);
    
    @Query("SELECT t.* FROM tracks t " +
           "INNER JOIN playlist_tracks pt ON t.id = pt.trackId " +
           "WHERE pt.playlistId = :playlistId")
    Single<List<Track>> getTracksInPlaylist(long playlistId);
    
    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    Single<Integer> getPlaylistTrackCount(long playlistId);
}

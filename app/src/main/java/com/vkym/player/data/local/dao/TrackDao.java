package com.vkym.player.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.vkym.player.data.model.Track;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TrackDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<Track> tracks);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Track track);
    
    @Update
    Completable update(Track track);
    
    @Query("SELECT * FROM tracks WHERE source = 'vk'")
    Single<List<Track>> getVKTracks();
    
    @Query("SELECT * FROM tracks WHERE source = 'ym'")
    Single<List<Track>> getYMTracks();
    
    @Query("SELECT * FROM tracks WHERE isFavorite = 1")
    LiveData<List<Track>> getFavoriteTracks();
    
    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    Single<List<Track>> searchTracks(String query);
    
    @Query("DELETE FROM tracks WHERE id = :trackId")
    Completable deleteTrack(String trackId);
    
    @Query("SELECT * FROM tracks WHERE id = :trackId")
    Single<Track> getTrackById(String trackId);
}

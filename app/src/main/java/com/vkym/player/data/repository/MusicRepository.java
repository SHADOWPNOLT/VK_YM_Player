package com.vkym.player.data.repository;

import android.content.Context;

import com.vkym.player.data.local.AppDatabase;
import com.vkym.player.data.local.dao.TrackDao;
import com.vkym.player.data.local.dao.PlaylistDao;
import com.vkym.player.data.model.Track;
import com.vkym.player.data.model.Playlist;
import com.vkym.player.data.model.PlaylistTrack;
import com.vkym.player.data.remote.VKApiClient;
import com.vkym.player.data.remote.YMApiClient;
import com.vkym.player.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicRepository {
    private final Context context;
    private final VKApiClient vkApiClient;
    private final YMApiClient ymApiClient;
    private final TrackDao trackDao;
    private final PlaylistDao playlistDao;
    
    public MusicRepository(Context context, VKApiClient vkApiClient, YMApiClient ymApiClient,
                           AppDatabase database) {
        this.context = context;
        this.vkApiClient = vkApiClient;
        this.ymApiClient = ymApiClient;
        this.trackDao = database.trackDao();
        this.playlistDao = database.playlistDao();
    }
    
    // ============ VK Methods ============
    
    public Single<List<Track>> syncVKTracks(int ownerId) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            return Single.error(new Exception("No network connection"));
        }
        
        return vkApiClient.getMyTracks(ownerId, 200, 0)
            .flatMap(tracks -> {
                if (tracks.isEmpty()) {
                    return Single.just(tracks);
                }
                return trackDao.insertAll(tracks)
                    .andThen(Single.just(tracks));
            })
            .subscribeOn(Schedulers.io());
    }
    
    public Single<List<Track>> getLocalVKTracks() {
        return trackDao.getVKTracks()
            .subscribeOn(Schedulers.io());
    }
    
    // ============ Яндекс Methods ============
    
    public Single<List<Track>> searchYandexMusic(String query) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            return Single.error(new Exception("No network connection"));
        }
        
        return ymApiClient.search(query)
            .subscribeOn(Schedulers.io());
    }
    
    // ============ Unified Search ============
    
    public Single<List<Track>> searchBoth(String query) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            return Single.error(new Exception("No network connection"));
        }
        
        return Single.zip(
            vkApiClient.getMyTracks(0, 50, 0).onErrorReturnItem(new ArrayList<>()),
            ymApiClient.search(query).onErrorReturnItem(new ArrayList<>()),
            (vkTracks, ymTracks) -> {
                List<Track> all = new ArrayList<>();
                all.addAll(vkTracks);
                all.addAll(ymTracks);
                return all;
            }
        ).subscribeOn(Schedulers.io());
    }
    
    // ============ Favorites ============
    
    public Single<List<Track>> getFavoriteTracks() {
        return Single.fromCallable(() -> trackDao.getFavoriteTracks())
            .flatMap(liveData -> Single.just(liveData.getValue()))
            .subscribeOn(Schedulers.io());
    }
    
    public Completable toggleFavorite(Track track) {
        return Single.fromCallable(() -> {
            Track existing = trackDao.getTrackById(track.id).blockingGet();
            if (existing != null) {
                existing.setFavorite(!existing.isFavorite());
                return existing;
            }
            track.setFavorite(true);
            return track;
        }).flatMapCompletable(t -> trackDao.insert(t))
          .subscribeOn(Schedulers.io());
    }
    
    // ============ Playlist Management ============
    
    public Completable createPlaylist(String name, String coverUrl) {
        Playlist playlist = new Playlist(name, coverUrl, "local");
        return playlistDao.insertPlaylist(playlist)
            .subscribeOn(Schedulers.io());
    }
    
    public Single<List<Playlist>> getAllPlaylists() {
        return playlistDao.getAllPlaylists()
            .subscribeOn(Schedulers.io());
    }
    
    public Completable addTrackToPlaylist(long playlistId, Track track) {
        return trackDao.insert(track)
            .andThen(playlistDao.addTrackToPlaylist(new PlaylistTrack(playlistId, track.id)))
            .subscribeOn(Schedulers.io());
    }
    
    public Completable removeTrackFromPlaylist(long playlistId, String trackId) {
        return playlistDao.removeTrackFromPlaylist(playlistId, trackId)
            .subscribeOn(Schedulers.io());
    }
    
    public Single<List<Track>> getTracksInPlaylist(long playlistId) {
        return playlistDao.getTracksInPlaylist(playlistId)
            .subscribeOn(Schedulers.io());
    }
    
    // ============ Copy track from VK to local (без перекачивания) ============
    
    public Completable copyVKTrackToLocalPlaylist(Track vkTrack, long playlistId) {
        // Создаём копию трека с тем же streamUrl
        Track localCopy = new Track(
            vkTrack.id + "_copy",
            "local",
            vkTrack.title,
            vkTrack.artist,
            vkTrack.duration,
            vkTrack.coverUrl,
            vkTrack.streamUrl
        );
        localCopy.setFavorite(vkTrack.isFavorite());
        
        return trackDao.insert(localCopy)
            .andThen(playlistDao.addTrackToPlaylist(new PlaylistTrack(playlistId, localCopy.id)))
            .subscribeOn(Schedulers.io());
    }
    
    // ============ Utility ============
    
    public boolean isVKLoggedIn() {
        return vkApiClient.isLoggedIn();
    }
    
    public void saveVKToken(String token) {
        vkApiClient.saveAccessToken(token);
    }
}

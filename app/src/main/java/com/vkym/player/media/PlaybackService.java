package com.vkym.player.media;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import com.vkym.player.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class PlaybackService extends MediaBrowserServiceCompat {
    
    public static final String CHANNEL_ID = "PlaybackChannel";
    public static final int NOTIFICATION_ID = 1;
    
    private ExoPlayer player;
    private MediaSessionCompat mediaSession;
    private EqualizerManager equalizerManager;
    private List<Track> currentQueue;
    private int currentIndex = -1;
    private boolean isPlaying = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Инициализация ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        
        // Эквалайзер
        equalizerManager = new EqualizerManager();
        equalizerManager.attachToPlayer(player);
        
        // Медиа сессия
        mediaSession = new MediaSessionCompat(this, "PlaybackService");
        mediaSession.setCallback(new MediaSessionCallback());
        setSessionToken(mediaSession.getSessionToken());
        
        // Слушатель плеера
        player.addListener(new PlayerListener());
        
        // Создание канала уведомлений
        createNotificationChannel();
        
        currentQueue = new ArrayList<>();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows when music is playing");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    private Notification buildNotification() {
        if (currentIndex < 0 || currentIndex >= currentQueue.size()) {
            return null;
        }
        
        Track currentTrack = currentQueue.get(currentIndex);
        
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentTrack.title)
            .setContentText(currentTrack.artist)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSession.getSessionToken()))
            .build();
    }
    
    private void updateNotification() {
        Notification notification = buildNotification();
        if (notification != null) {
            startForeground(NOTIFICATION_ID, notification);
        }
    }
    
    private void playTrack(Track track) {
        if (track == null || track.streamUrl == null || track.streamUrl.isEmpty()) {
            return;
        }
        
        MediaItem mediaItem = MediaItem.fromUri(track.streamUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        
        // Обновляем метаданные
        android.support.v4.media.MediaMetadataCompat.Builder metadataBuilder =
            new android.support.v4.media.MediaMetadataCompat.Builder()
                .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist);
        
        if (track.coverUrl != null && !track.coverUrl.isEmpty()) {
            metadataBuilder.putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.coverUrl);
        }
        
        mediaSession.setMetadata(metadataBuilder.build());
        
        // Обновляем состояние
        PlaybackStateCompat state = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
            .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                       PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            .build();
        mediaSession.setPlaybackState(state);
        
        isPlaying = true;
        updateNotification();
    }
    
    public void playQueue(List<Track> queue, int startIndex) {
        currentQueue = new ArrayList<>(queue);
        currentIndex = startIndex;
        if (currentIndex >= 0 && currentIndex < currentQueue.size()) {
            playTrack(currentQueue.get(currentIndex));
        }
    }
    
    public void pause() {
        player.pause();
        isPlaying = false;
        
        PlaybackStateCompat state = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0f)
            .build();
        mediaSession.setPlaybackState(state);
        
        updateNotification();
    }
    
    public void resume() {
        player.play();
        isPlaying = true;
        
        PlaybackStateCompat state = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
            .build();
        mediaSession.setPlaybackState(state);
        
        updateNotification();
    }
    
    public void next() {
        if (currentIndex + 1 < currentQueue.size()) {
            currentIndex++;
            playTrack(currentQueue.get(currentIndex));
        }
    }
    
    public void previous() {
        if (currentIndex - 1 >= 0) {
            currentIndex--;
            playTrack(currentQueue.get(currentIndex));
        }
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public Track getCurrentTrack() {
        if (currentIndex >= 0 && currentIndex < currentQueue.size()) {
            return currentQueue.get(currentIndex);
        }
        return null;
    }
    
    public ExoPlayer getPlayer() {
        return player;
    }
    
    public EqualizerManager getEqualizerManager() {
        return equalizerManager;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        if (equalizerManager != null) {
            equalizerManager.release();
        }
        if (mediaSession != null) {
            mediaSession.release();
        }
    }
    
    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        return new BrowserRoot("root", null);
    }
    
    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(new ArrayList<>());
    }
    
    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            resume();
        }
        
        @Override
        public void onPause() {
            pause();
        }
        
        @Override
        public void onSkipToNext() {
            next();
        }
        
        @Override
        public void onSkipToPrevious() {
            previous();
        }
    }
    
    private class PlayerListener implements Player.Listener {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                next();
            }
        }
        
        @Override
        public void onPlayerError(PlaybackException error) {
            // Обработка ошибки стрима - попытка резервного URL
            android.util.Log.e("PlaybackService", "Playback error: " + error.getMessage());
        }
    }
}

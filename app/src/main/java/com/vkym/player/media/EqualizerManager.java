package com.vkym.player.media;

import android.media.audiofx.Equalizer;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;

public class EqualizerManager {
    private static final String TAG = "EqualizerManager";
    private Equalizer equalizer;
    private int sessionId;
    private boolean isEnabled;
    
    public void attachToPlayer(ExoPlayer player) {
        if (player == null) return;
        
        sessionId = player.getAudioSessionId();
        if (sessionId != 0) {
            try {
                equalizer = new Equalizer(0, sessionId);
                equalizer.setEnabled(true);
                isEnabled = true;
                Log.d(TAG, "Equalizer attached. Band count: " + equalizer.getNumberOfBands());
            } catch (Exception e) {
                Log.e(TAG, "Failed to create equalizer", e);
            }
        } else {
            Log.w(TAG, "Audio session not ready yet");
        }
    }
    
    public void setEnabled(boolean enabled) {
        if (equalizer != null) {
            equalizer.setEnabled(enabled);
            isEnabled = enabled;
        }
    }
    
    public boolean isEnabled() {
        return isEnabled && equalizer != null;
    }
    
    public short getNumberOfBands() {
        return equalizer != null ? equalizer.getNumberOfBands() : 0;
    }
    
    public void setBandLevel(short band, short level) {
        if (equalizer != null) {
            try {
                equalizer.setBandLevel(band, level);
            } catch (Exception e) {
                Log.e(TAG, "Failed to set band level", e);
            }
        }
    }
    
    public short getBandLevel(short band) {
        if (equalizer != null) {
            try {
                return equalizer.getBandLevel(band);
            } catch (Exception e) {
                Log.e(TAG, "Failed to get band level", e);
            }
        }
        return 0;
    }
    
    public short[] getBandLevelRange() {
        if (equalizer != null) {
            return equalizer.getBandLevelRange();
        }
        return new short[]{0, 0};
    }
    
    public void release() {
        if (equalizer != null) {
            equalizer.release();
            equalizer = null;
        }
    }
}

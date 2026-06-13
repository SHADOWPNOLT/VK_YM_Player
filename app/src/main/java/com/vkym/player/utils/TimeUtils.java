package com.vkym.player.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
    
    public static String formatDuration(long milliseconds) {
        if (milliseconds <= 0) return "0:00";
        
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        
        return String.format("%d:%02d", minutes, seconds);
    }
    
    public static String formatProgress(long current, long total) {
        return formatDuration(current) + " / " + formatDuration(total);
    }
}

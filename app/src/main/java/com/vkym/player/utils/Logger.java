package com.vkym.player.utils;

import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Logger {
    private static final String TAG = "VKYM_Player";
    
    public static void e(String message) {
        Log.e(TAG, message);
    }
    
    public static void e(String message, Throwable throwable) {
        Log.e(TAG, message, throwable);
    }
    
    public static void d(String message) {
        Log.d(TAG, message);
    }
    
    public static void toast(AppCompatActivity activity, String message) {
        activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_LONG).show());
    }
    
    public static void toastAndLog(AppCompatActivity activity, String message, Throwable throwable) {
        e(message, throwable);
        activity.runOnUiThread(() -> Toast.makeText(activity, message + ": " + throwable.getMessage(), Toast.LENGTH_LONG).show());
    }
}

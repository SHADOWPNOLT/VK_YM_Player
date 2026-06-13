package com.vkym.player.utils;

import android.app.Activity;
import android.widget.Toast;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;

public class SimpleDebug {
    private static String lastError = null;
    
    public static void log(String msg) {
        android.util.Log.e("SIMPLE_DEBUG", msg);
        lastError = msg;
    }
    
    public static void log(String msg, Throwable t) {
        android.util.Log.e("SIMPLE_DEBUG", msg, t);
        lastError = msg + ": " + t.getMessage();
    }
    
    public static void showError(Activity activity, String error) {
        activity.runOnUiThread(() -> {
            Toast.makeText(activity, "ERROR: " + error, Toast.LENGTH_LONG).show();
            
            // Создаём красный текст на экране
            TextView tv = new TextView(activity);
            tv.setText("❌ ОШИБКА:\n" + error);
            tv.setTextColor(Color.RED);
            tv.setBackgroundColor(Color.BLACK);
            tv.setPadding(20, 20, 20, 20);
            tv.setTextSize(12);
            tv.setGravity(Gravity.CENTER);
            
            ViewGroup root = (ViewGroup) activity.findViewById(android.R.id.content);
            root.addView(tv);
        });
    }
}

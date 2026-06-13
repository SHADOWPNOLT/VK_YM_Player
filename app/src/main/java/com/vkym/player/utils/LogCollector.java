package com.vkym.player.utils;

import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogCollector {
    private static final String TAG = "LogCollector";
    private static final List<String> logs = new ArrayList<>();
    private static final int MAX_LOGS = 500;
    
    public static void add(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
        String logLine = timestamp + " - " + message;
        
        synchronized (logs) {
            logs.add(logLine);
            if (logs.size() > MAX_LOGS) {
                logs.remove(0);
            }
        }
        Log.d("VKYM_App", logLine);
    }
    
    public static void add(String message, Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        add(message + "\n" + sw.toString());
    }
    
    public static List<String> getLogs() {
        synchronized (logs) {
            return new ArrayList<>(logs);
        }
    }
    
    public static void clear() {
        synchronized (logs) {
            logs.clear();
        }
        add("Логи очищены");
    }
    
    public static void saveToFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            synchronized (logs) {
                for (String log : logs) {
                    writer.write(log + "\n");
                }
            }
            writer.write("\n--- End of logs ---\n");
        } catch (Exception e) {
            Log.e(TAG, "Error saving logs", e);
        }
    }
}

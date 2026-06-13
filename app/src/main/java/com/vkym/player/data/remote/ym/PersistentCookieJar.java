package com.vkym.player.data.remote.ym;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class PersistentCookieJar implements CookieJar {
    private final SharedPreferences prefs;
    private final Map<String, List<Cookie>> cache = new HashMap<>();
    
    public PersistentCookieJar(Context context) {
        prefs = context.getSharedPreferences("ym_cookies", Context.MODE_PRIVATE);
        loadCookiesFromPrefs();
    }
    
    private void loadCookiesFromPrefs() {
        // Загружаем сохранённые куки
        String cookiesJson = prefs.getString("cookies", "");
        if (!cookiesJson.isEmpty()) {
            // Здесь нужно парсить JSON в Cookie
            // Упрощённо: оставляем пустым, куки будут сохранены после первого запроса
        }
    }
    
    private void saveCookiesToPrefs() {
        // Сохраняем куки в SharedPreferences
        // В реальном проекте нужно сериализовать Cookie в JSON
    }
    
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cache.put(url.host(), cookies);
        saveCookiesToPrefs();
    }
    
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cache.get(url.host());
        return cookies != null ? cookies : new ArrayList<>();
    }
}

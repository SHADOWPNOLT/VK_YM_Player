package com.vkym.player.ui.auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vkym.player.R;
import com.vkym.player.data.remote.YMApiClient;
import com.vkym.player.di.ServiceLocator;
import com.vkym.player.ui.MainActivity;

public class YandexAuthWebViewActivity extends AppCompatActivity {
    
    private static final String YANDEX_AUTH_URL = "https://passport.yandex.ru/auth/";
    private static final String REDIRECT_URL = "https://music.yandex.ru";
    
    private WebView webView;
    private YMApiClient ymApiClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yandex_auth);
        
        ymApiClient = ServiceLocator.getInstance().getYMApiClient();
        webView = findViewById(R.id.webView);
        
        // Настройка WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        
        // Очистка старых кук
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        
        webView.setWebViewClient(new YandexWebViewClient());
        webView.loadUrl(YANDEX_AUTH_URL);
    }
    
    private class YandexWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            
            // После успешного входа сохраняем куки
            if (url.startsWith(REDIRECT_URL) || url.contains("music.yandex.ru")) {
                String cookies = CookieManager.getInstance().getCookie(url);
                if (cookies != null && !cookies.isEmpty()) {
                    ymApiClient.saveCookies(cookies);
                    Toast.makeText(YandexAuthWebViewActivity.this, 
                        "Яндекс авторизация успешна!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(YandexAuthWebViewActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }
        
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(YandexAuthWebViewActivity.this, 
                "Ошибка: " + description, Toast.LENGTH_SHORT).show();
        }
    }
}

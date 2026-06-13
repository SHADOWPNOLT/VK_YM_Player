package com.vkym.player.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vk.id.VKID;
import com.vk.id.auth.AuthParams;
import com.vk.id.auth.AuthResult;
import com.vk.id.onetap.OneTapProvider;
import com.vkym.player.R;
import com.vkym.player.di.ServiceLocator;
import com.vkym.player.ui.MainActivity;

public class VKAuthActivity extends AppCompatActivity {
    
    private static final String VK_CLIENT_ID = "54635330"; // Замените на ваш ID
    private static final String REDIRECT_URI = "https://vk.com/blank.html";
    private static final String SCOPE = "audio,offline";
    
    private WebView webView;
    private ServiceLocator serviceLocator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk_auth);
        
        serviceLocator = ServiceLocator.getInstance();
        webView = findViewById(R.id.webView);
        
        // Настройка WebView для OAuth
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new VKWebViewClient());
        
        // Формируем URL для авторизации
        String authUrl = "https://oauth.vk.com/authorize?" +
            "client_id=" + VK_CLIENT_ID +
            "&redirect_uri=" + REDIRECT_URI +
            "&scope=" + SCOPE +
            "&response_type=token" +
            "&v=5.199" +
            "&display=mobile";
        
        webView.loadUrl(authUrl);
    }
    
    private class VKWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            
            // Обработка редиректа с access_token
            if (url.contains("access_token=") && url.contains(REDIRECT_URI)) {
                extractTokenAndFinish(url);
            }
        }
        
        private void extractTokenAndFinish(String url) {
            try {
                // Извлечение токена из URL
                String token = null;
                String[] parts = url.split("#");
                if (parts.length > 1) {
                    String[] params = parts[1].split("&");
                    for (String param : params) {
                        if (param.startsWith("access_token=")) {
                            token = param.substring("access_token=".length());
                            break;
                        }
                    }
                }
                
                if (token != null && !token.isEmpty()) {
                    serviceLocator.getVKApiClient().saveAccessToken(token);
                    Toast.makeText(VKAuthActivity.this, "VK авторизация успешна!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(VKAuthActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VKAuthActivity.this, "Не удалось получить токен", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(VKAuthActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(VKAuthActivity.this, "Ошибка загрузки: " + description, Toast.LENGTH_SHORT).show();
        }
    }
}

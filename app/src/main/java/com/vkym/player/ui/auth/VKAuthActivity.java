package com.vkym.player.ui.auth;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.vkym.player.R;
import com.vkym.player.di.ServiceLocator;
import com.vkym.player.utils.SimpleDebug;

public class VKAuthActivity extends AppCompatActivity {
    
    private static final String VK_CLIENT_ID = "54635330";
    private static final String REDIRECT_URI = "https://vk.com/blank.html";
    private static final String SCOPE = "audio,offline";
    
    private WebView webView;
    private ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDebug.log("VKAuthActivity onCreate START");
        
        try {
            setContentView(R.layout.activity_vk_auth);
            SimpleDebug.log("setContentView OK");
            
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
            
            webView = findViewById(R.id.webView);
            progressBar = findViewById(R.id.progressBar);
            SimpleDebug.log("Views initialized");
            
            setupWebView();
            loadAuthUrl();
            SimpleDebug.log("onCreate END");
            
        } catch (Exception e) {
            SimpleDebug.log("VKAuthActivity onCreate ERROR", e);
            SimpleDebug.showError(this, e.toString());
        }
    }
    
    private void setupWebView() {
        try {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setWebViewClient(new VKWebViewClient());
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress < 100) {
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                    } else {
                        progressBar.setVisibility(ProgressBar.GONE);
                    }
                }
            });
            SimpleDebug.log("WebView configured");
        } catch (Exception e) {
            SimpleDebug.log("setupWebView ERROR", e);
        }
    }
    
    private void loadAuthUrl() {
        try {
            String authUrl = "https://oauth.vk.com/authorize?" +
                "client_id=" + VK_CLIENT_ID +
                "&redirect_uri=" + REDIRECT_URI +
                "&scope=" + SCOPE +
                "&response_type=token" +
                "&v=5.131" +
                "&display=mobile";
            
            SimpleDebug.log("Loading URL: " + authUrl);
            webView.loadUrl(authUrl);
        } catch (Exception e) {
            SimpleDebug.log("loadAuthUrl ERROR", e);
        }
    }
    
    private class VKWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            SimpleDebug.log("Page finished: " + url);
            
            if (url.contains("access_token=") && url.contains(REDIRECT_URI)) {
                SimpleDebug.log("Token detected!");
                extractTokenAndFinish(url);
            }
        }
        
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            SimpleDebug.log("WebView error: " + errorCode + " - " + description);
            Toast.makeText(VKAuthActivity.this, "Ошибка загрузки: " + description, Toast.LENGTH_SHORT).show();
        }
        
        private void extractTokenAndFinish(String url) {
            try {
                SimpleDebug.log("Extracting token from: " + url);
                String token = null;
                String userId = null;
                
                String[] parts = url.split("#");
                if (parts.length > 1) {
                    String[] params = parts[1].split("&");
                    for (String param : params) {
                        if (param.startsWith("access_token=")) {
                            token = param.substring("access_token=".length());
                            SimpleDebug.log("Token extracted: " + token.substring(0, Math.min(20, token.length())) + "...");
                        } else if (param.startsWith("user_id=")) {
                            userId = param.substring("user_id=".length());
                            SimpleDebug.log("User ID: " + userId);
                        }
                    }
                }
                
                if (token != null && !token.isEmpty()) {
                    ServiceLocator.getInstance().getVKApiClient().saveAccessToken(token);
                    SimpleDebug.log("Token saved successfully");
                    Toast.makeText(VKAuthActivity.this, "Авторизация успешна!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    SimpleDebug.log("Token not found in URL");
                    Toast.makeText(VKAuthActivity.this, "Не удалось получить токен", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                SimpleDebug.log("extractTokenAndFinish ERROR", e);
                SimpleDebug.showError(VKAuthActivity.this, e.toString());
            }
        }
    }
}

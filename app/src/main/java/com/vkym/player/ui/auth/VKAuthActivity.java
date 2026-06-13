package com.vkym.player.ui.auth;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.vkym.player.R;

public class VKAuthActivity extends AppCompatActivity {
    private WebView webView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk_auth);
        
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("vk.com")) {
                    Toast.makeText(VKAuthActivity.this, "Загрузка...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        webView.loadUrl("https://vk.com");
    }
}

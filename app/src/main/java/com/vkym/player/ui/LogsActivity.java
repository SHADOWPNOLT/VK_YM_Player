package com.vkym.player.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.vkym.player.R;
import com.vkym.player.utils.SimpleDebug;
import com.vkym.player.utils.LogCollector;

public class LogsActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDebug.log("LogsActivity onCreate START");
        
        try {
            setContentView(R.layout.activity_logs);
            SimpleDebug.log("LogsActivity setContentView OK");
            
            TextView textView = findViewById(R.id.textViewLogs);
            SimpleDebug.log("TextView found: " + (textView != null));
            
            if (textView != null) {
                StringBuilder sb = new StringBuilder();
                for (String log : LogCollector.getLogs()) {
                    sb.append(log).append("\n");
                }
                textView.setText(sb.toString());
                SimpleDebug.log("Logs displayed, count: " + LogCollector.getLogs().size());
            }
            
        } catch (Exception e) {
            SimpleDebug.log("LogsActivity CRASH", e);
            SimpleDebug.showError(this, e.toString());
        }
        SimpleDebug.log("LogsActivity onCreate END");
    }
}

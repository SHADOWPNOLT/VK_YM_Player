package com.vkym.player.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.vkym.player.R;
import com.vkym.player.utils.LogCollector;
import java.io.File;
import java.io.FileWriter;

public class LogsActivity extends AppCompatActivity {
    
    private TextView textViewLogs;
    private ScrollView scrollView;
    private Button btnRefresh, btnClear, btnSave;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        
        textViewLogs = findViewById(R.id.textViewLogs);
        scrollView = findViewById(R.id.scrollView);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnClear = findViewById(R.id.btnClear);
        btnSave = findViewById(R.id.btnSave);
        
        refreshLogs();
        
        btnRefresh.setOnClickListener(v -> refreshLogs());
        btnClear.setOnClickListener(v -> {
            LogCollector.clear();
            refreshLogs();
            Toast.makeText(this, "Логи очищены", Toast.LENGTH_SHORT).show();
        });
        btnSave.setOnClickListener(v -> saveLogs());
    }
    
    private void refreshLogs() {
        StringBuilder sb = new StringBuilder();
        for (String log : LogCollector.getLogs()) {
            sb.append(log).append("\n");
        }
        textViewLogs.setText(sb.toString());
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
    
    private void saveLogs() {
        try {
            File dir = new File(getExternalFilesDir(null), "logs");
            if (!dir.exists()) dir.mkdirs();
            
            File file = new File(dir, "log_" + System.currentTimeMillis() + ".txt");
            LogCollector.saveToFile(file);
            
            Toast.makeText(this, "Логи сохранены: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

package com.vkym.player.ui.player;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;

public class FullscreenPlayerActivity extends AppCompatActivity {
    
    private ImageView coverImage;
    private TextView titleText;
    private TextView artistText;
    private TextView currentTimeText;
    private TextView durationText;
    private SeekBar seekBar;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private ImageButton shuffleButton;
    private ImageButton repeatButton;
    
    private ObjectAnimator rotationAnimator;
    private Handler progressHandler = new Handler();
    private boolean isPlaying = true;
    private int currentProgress = 0;
    private int totalDuration = 235000; // 3:55 для теста
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_player);
        
        initViews();
        setupToolbar();
        setupRotationAnimation();
        setupSeekBar();
        setupClickListeners();
        loadTestTrack();
        startProgressUpdate();
    }
    
    private void initViews() {
        coverImage = findViewById(R.id.coverImage);
        titleText = findViewById(R.id.titleText);
        artistText = findViewById(R.id.artistText);
        currentTimeText = findViewById(R.id.currentTimeText);
        durationText = findViewById(R.id.durationText);
        seekBar = findViewById(R.id.seekBar);
        playPauseButton = findViewById(R.id.playPauseButton);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        repeatButton = findViewById(R.id.repeatButton);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void loadTestTrack() {
        titleText.setText("Bohemian Rhapsody");
        artistText.setText("Queen");
        durationText.setText("3:55");
        seekBar.setMax(totalDuration);
        
        // Заглушка для обложки (можно заменить на реальную)
        coverImage.setImageResource(android.R.drawable.ic_menu_gallery);
    }
    
    private void setupRotationAnimation() {
        rotationAnimator = ObjectAnimator.ofFloat(coverImage, "rotation", 0f, 360f);
        rotationAnimator.setDuration(20000);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setRepeatMode(ValueAnimator.RESTART);
        rotationAnimator.setInterpolator(new LinearInterpolator());
    }
    
    private void startProgressUpdate() {
        progressHandler.post(progressRunnable);
    }
    
    private void stopProgressUpdate() {
        progressHandler.removeCallbacks(progressRunnable);
    }
    
    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying && currentProgress < totalDuration) {
                currentProgress += 1000;
                updateProgress();
                progressHandler.postDelayed(this, 1000);
            } else if (currentProgress >= totalDuration) {
                currentProgress = 0;
                updateProgress();
            }
        }
    };
    
    private void updateProgress() {
        seekBar.setProgress(currentProgress);
        long minutes = currentProgress / 60000;
        long seconds = (currentProgress % 60000) / 1000;
        currentTimeText.setText(String.format("%d:%02d", minutes, seconds));
    }
    
    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentProgress = progress;
                    updateProgress();
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private void setupClickListeners() {
        playPauseButton.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            if (isPlaying) {
                playPauseButton.setImageResource(R.drawable.ic_pause);
                rotationAnimator.resume();
                startProgressUpdate();
                Toast.makeText(this, "▶ Воспроизведение", Toast.LENGTH_SHORT).show();
            } else {
                playPauseButton.setImageResource(R.drawable.ic_play);
                rotationAnimator.pause();
                stopProgressUpdate();
                Toast.makeText(this, "⏸ Пауза", Toast.LENGTH_SHORT).show();
            }
        });
        
        nextButton.setOnClickListener(v -> {
            Toast.makeText(this, "⏭ Следующий трек", Toast.LENGTH_SHORT).show();
            currentProgress = 0;
            updateProgress();
        });
        
        prevButton.setOnClickListener(v -> {
            Toast.makeText(this, "⏮ Предыдущий трек", Toast.LENGTH_SHORT).show();
            currentProgress = 0;
            updateProgress();
        });
        
        shuffleButton.setOnClickListener(v -> {
            boolean isShuffled = shuffleButton.getAlpha() == 1.0f;
            shuffleButton.setAlpha(isShuffled ? 0.5f : 1.0f);
            Toast.makeText(this, isShuffled ? "Перемешивание выключено" : "Перемешивание включено", Toast.LENGTH_SHORT).show();
        });
        
        repeatButton.setOnClickListener(v -> {
            float alpha = repeatButton.getAlpha();
            if (alpha < 0.6f) {
                repeatButton.setAlpha(1.0f);
                Toast.makeText(this, "Повтор всех треков", Toast.LENGTH_SHORT).show();
            } else if (alpha > 0.9f) {
                repeatButton.setAlpha(0.3f);
                Toast.makeText(this, "Повтор выключен", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (isPlaying) {
            rotationAnimator.start();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        rotationAnimator.pause();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgressUpdate();
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
    }
}

package com.vkym.player.ui.player;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import com.vkym.player.media.PlaybackService;
import com.vkym.player.ui.viewmodel.SharedPlayerViewModel;
import com.vkym.player.utils.TimeUtils;

public class FullscreenPlayerActivity extends AppCompatActivity {
    
    private RoundedImageView coverImage;
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
    private ImageButton equalizerButton;
    
    private SharedPlayerViewModel viewModel;
    private ObjectAnimator rotationAnimator;
    private Handler progressHandler = new Handler(Looper.getMainLooper());
    private boolean isSeeking = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_player);
        
        initViews();
        setupViewModel();
        setupSeekBar();
        setupClickListeners();
        setupRotationAnimation();
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
        equalizerButton = findViewById(R.id.equalizerButton);
    }
    
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SharedPlayerViewModel.class);
        
        viewModel.getCurrentTrack().observe(this, this::updateTrackInfo);
        viewModel.getIsPlaying().observe(this, isPlaying -> {
            updatePlayPauseButton(isPlaying);
            if (isPlaying != null && isPlaying) {
                rotationAnimator.resume();
                startProgressUpdate();
            } else {
                rotationAnimator.pause();
                stopProgressUpdate();
            }
        });
    }
    
    private void updateTrackInfo(Track track) {
        if (track == null) return;
        
        titleText.setText(track.title);
        artistText.setText(track.artist);
        
        // Загрузка обложки
        if (track.coverUrl != null && !track.coverUrl.isEmpty()) {
            Glide.with(this)
                .load(track.coverUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(coverImage);
        }
        
        durationText.setText(TimeUtils.formatDuration(track.duration));
        seekBar.setMax((int) track.duration);
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
            if (!isSeeking) {
                // В реальном проекте здесь нужно получать позицию из ExoPlayer
                // long currentPosition = player.getCurrentPosition();
                long currentPosition = 0; // временно
                currentTimeText.setText(TimeUtils.formatDuration(currentPosition));
                seekBar.setProgress((int) currentPosition);
            }
            progressHandler.postDelayed(this, 1000);
        }
    };
    
    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTimeText.setText(TimeUtils.formatDuration(progress));
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                // Здесь нужно установить позицию в ExoPlayer
                // player.seekTo(seekBar.getProgress());
            }
        });
    }
    
    private void setupClickListeners() {
        playPauseButton.setOnClickListener(v -> {
            Boolean isPlaying = viewModel.getIsPlaying().getValue();
            if (isPlaying != null && isPlaying) {
                viewModel.setIsPlaying(false);
            } else {
                viewModel.setIsPlaying(true);
            }
        });
        
        nextButton.setOnClickListener(v -> viewModel.nextTrack());
        prevButton.setOnClickListener(v -> viewModel.previousTrack());
        
        shuffleButton.setOnClickListener(v -> {
            Boolean isShuffled = viewModel.getShuffleEnabled().getValue();
            viewModel.setShuffleEnabled(isShuffled == null || !isShuffled);
            updateShuffleButton();
        });
        
        repeatButton.setOnClickListener(v -> {
            Integer mode = viewModel.getRepeatMode().getValue();
            int newMode = (mode == null ? 0 : (mode + 1) % 3);
            viewModel.setRepeatMode(newMode);
            updateRepeatButton();
        });
        
        equalizerButton.setOnClickListener(v -> {
            EqualizerFragment fragment = new EqualizerFragment();
            fragment.show(getSupportFragmentManager(), "equalizer");
        });
    }
    
    private void updatePlayPauseButton(boolean isPlaying) {
        playPauseButton.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }
    
    private void updateShuffleButton() {
        Boolean isShuffled = viewModel.getShuffleEnabled().getValue();
        shuffleButton.setAlpha(isShuffled != null && isShuffled ? 1.0f : 0.5f);
    }
    
    private void updateRepeatButton() {
        Integer mode = viewModel.getRepeatMode().getValue();
        int icon = R.drawable.ic_repeat;
        float alpha = 0.5f;
        
        if (mode != null) {
            switch (mode) {
                case 1:
                    icon = R.drawable.ic_repeat_all;
                    alpha = 1.0f;
                    break;
                case 2:
                    icon = R.drawable.ic_repeat_one;
                    alpha = 1.0f;
                    break;
                default:
                    icon = R.drawable.ic_repeat;
                    alpha = 0.5f;
                    break;
            }
        }
        repeatButton.setImageResource(icon);
        repeatButton.setAlpha(alpha);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Boolean isPlaying = viewModel.getIsPlaying().getValue();
        if (isPlaying != null && isPlaying) {
            rotationAnimator.start();
            startProgressUpdate();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        rotationAnimator.pause();
        stopProgressUpdate();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
        progressHandler.removeCallbacks(progressRunnable);
    }
}

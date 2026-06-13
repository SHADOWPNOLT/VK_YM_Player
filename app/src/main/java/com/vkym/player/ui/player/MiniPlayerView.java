package com.vkym.player.ui.player;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import com.vkym.player.ui.viewmodel.SharedPlayerViewModel;

public class MiniPlayerView extends LinearLayout {
    
    private RoundedImageView coverImage;
    private TextView titleText;
    private TextView artistText;
    private ImageButton playPauseButton;
    private ImageButton closeButton;
    
    private SharedPlayerViewModel viewModel;
    private GestureDetector gestureDetector;
    private OnMiniPlayerClickListener listener;
    
    public interface OnMiniPlayerClickListener {
        void onExpand();
        void onClose();
    }
    
    public MiniPlayerView(Context context) {
        super(context);
        init();
    }
    
    public MiniPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public MiniPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        inflate(getContext(), R.layout.view_mini_player, this);
        
        coverImage = findViewById(R.id.miniCover);
        titleText = findViewById(R.id.miniTitle);
        artistText = findViewById(R.id.miniArtist);
        playPauseButton = findViewById(R.id.miniPlayPause);
        closeButton = findViewById(R.id.miniClose);
        
        // Настройка жестов для свайпа вверх
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
        
        playPauseButton.setOnClickListener(v -> {
            if (viewModel != null) {
                Boolean isPlaying = viewModel.getIsPlaying().getValue();
                if (isPlaying != null && isPlaying) {
                    // Пауза
                    viewModel.setIsPlaying(false);
                } else {
                    // Воспроизведение
                    viewModel.setIsPlaying(true);
                }
            }
        });
        
        closeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClose();
            }
            setVisibility(GONE);
        });
        
        setOnClickListener(v -> {
            if (listener != null) {
                listener.onExpand();
            }
        });
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    
    public void bindViewModel(SharedPlayerViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.viewModel = viewModel;
        
        viewModel.getCurrentTrack().observe(lifecycleOwner, track -> {
            if (track != null) {
                updateTrackInfo(track);
                setVisibility(VISIBLE);
            } else {
                setVisibility(GONE);
            }
        });
        
        viewModel.getIsPlaying().observe(lifecycleOwner, isPlaying -> {
            if (isPlaying != null) {
                updatePlayPauseButton(isPlaying);
            }
        });
    }
    
    private void updateTrackInfo(Track track) {
        titleText.setText(track.title);
        artistText.setText(track.artist);
        
        if (track.coverUrl != null && !track.coverUrl.isEmpty()) {
            Glide.with(getContext())
                .load(track.coverUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(coverImage);
        }
    }
    
    private void updatePlayPauseButton(boolean isPlaying) {
        playPauseButton.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }
    
    public void setOnMiniPlayerClickListener(OnMiniPlayerClickListener listener) {
        this.listener = listener;
    }
    
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        
        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, 
                               float velocityX, float velocityY) {
            float diffY = e2.getY() - e1.getY();
            if (diffY < -SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                // Свайп вверх - развернуть плеер
                if (listener != null) {
                    listener.onExpand();
                }
                return true;
            }
            return false;
        }
    }
}

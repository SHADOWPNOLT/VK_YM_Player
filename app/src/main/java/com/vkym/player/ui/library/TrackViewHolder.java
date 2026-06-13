package com.vkym.player.ui.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import com.vkym.player.utils.TimeUtils;

public class TrackViewHolder extends RecyclerView.ViewHolder {
    
    public RoundedImageView coverImage;
    public TextView titleText;
    public TextView artistText;
    public TextView durationText;
    public ImageButton menuButton;
    public View container;
    
    public TrackViewHolder(@NonNull View itemView) {
        super(itemView);
        coverImage = itemView.findViewById(R.id.coverImage);
        titleText = itemView.findViewById(R.id.titleText);
        artistText = itemView.findViewById(R.id.artistText);
        durationText = itemView.findViewById(R.id.durationText);
        menuButton = itemView.findViewById(R.id.menuButton);
        container = itemView;
    }
    
    public void bind(Track track, TrackAdapter.OnTrackClickListener listener) {
        titleText.setText(track.title);
        artistText.setText(track.artist);
        durationText.setText(TimeUtils.formatDuration(track.duration));
        
        // Загрузка обложки через Glide
        if (track.coverUrl != null && !track.coverUrl.isEmpty()) {
            Glide.with(itemView.getContext())
                .load(track.coverUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(coverImage);
        } else {
            coverImage.setImageResource(R.drawable.ic_placeholder);
        }
        
        container.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackClick(track, getAdapterPosition());
            }
        });
        
        menuButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackMenuClick(track, getAdapterPosition(), v);
            }
        });
    }
}

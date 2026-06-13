package com.vkym.player.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.vkym.player.R;
import com.vkym.player.data.model.Track;
import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    
    private List<Track> tracks = new ArrayList<>();
    private OnTrackClickListener listener;
    
    public interface OnTrackClickListener {
        void onTrackClick(Track track, int position);
        void onTrackMenuClick(Track track, int position, View anchor);
    }
    
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks != null ? tracks : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public List<Track> getTracks() {
        return tracks;
    }
    
    public void setOnTrackClickListener(OnTrackClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.bind(track, listener);
    }
    
    @Override
    public int getItemCount() {
        return tracks.size();
    }
    
    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView titleText;
        TextView artistText;
        TextView durationText;
        ImageButton menuButton;
        
        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.coverImage);
            titleText = itemView.findViewById(R.id.titleText);
            artistText = itemView.findViewById(R.id.artistText);
            durationText = itemView.findViewById(R.id.durationText);
            menuButton = itemView.findViewById(R.id.menuButton);
        }
        
        public void bind(Track track, OnTrackClickListener listener) {
            titleText.setText(track.title);
            artistText.setText(track.artist);
            
            long minutes = track.duration / 60000;
            long seconds = (track.duration % 60000) / 1000;
            durationText.setText(String.format("%d:%02d", minutes, seconds));
            
            itemView.setOnClickListener(v -> {
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
}

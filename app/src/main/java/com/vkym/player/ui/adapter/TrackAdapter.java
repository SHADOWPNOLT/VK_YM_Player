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

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    
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
    
    public void setOnTrackClickListener(OnTrackClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_track, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.titleText.setText(track.title != null ? track.title : "Unknown");
        holder.artistText.setText(track.artist != null ? track.artist : "Unknown Artist");
        
        long minutes = track.duration / 60000;
        long seconds = (track.duration % 60000) / 1000;
        holder.durationText.setText(String.format("%d:%02d", minutes, seconds));
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackClick(track, position);
            }
        });
        
        holder.menuButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackMenuClick(track, position, v);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return tracks.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView titleText;
        TextView artistText;
        TextView durationText;
        ImageButton menuButton;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.coverImage);
            titleText = itemView.findViewById(R.id.titleText);
            artistText = itemView.findViewById(R.id.artistText);
            durationText = itemView.findViewById(R.id.durationText);
            menuButton = itemView.findViewById(R.id.menuButton);
        }
    }
}

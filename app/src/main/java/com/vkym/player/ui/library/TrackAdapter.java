package com.vkym.player.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkym.player.R;
import com.vkym.player.data.model.Track;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackViewHolder> {
    
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
    
    public Track getTrackAt(int position) {
        if (position >= 0 && position < tracks.size()) {
            return tracks.get(position);
        }
        return null;
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
}

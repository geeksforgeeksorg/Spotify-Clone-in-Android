package org.geeksforgeeks.demo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private final ArrayList<TrackModel> trackModels;
    private final Context context;

    public TrackAdapter(ArrayList<TrackModel> trackModels, Context context) {
        this.trackModels = trackModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating layout on below line.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // setting data to text views.
        TrackModel trackRVModal = trackModels.get(position);
        holder.trackNameTV.setText(trackRVModal.getTrackName());
        holder.trackArtistTV.setText(trackRVModal.getTrackArtist());

        // adding click listener for track item view
        holder.itemView.setOnClickListener(v -> {
            String trackUrl = "https://open.spotify.com/track/" + trackRVModal.getId();
            Uri uri = Uri.parse(trackUrl); // missing 'http://' will cause crash
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return trackModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating and initializing variables for text views.
        TextView trackNameTV;
        TextView trackArtistTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackNameTV = itemView.findViewById(R.id.idTVTrackName);
            trackArtistTV = itemView.findViewById(R.id.idTVTrackArtist);
        }
    }
}
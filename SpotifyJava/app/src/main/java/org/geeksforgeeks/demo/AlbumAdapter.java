package org.geeksforgeeks.demo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.geeksforgeeks.demo.databinding.AlbumRvItemBinding;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private final List<AlbumModel> albums;
    private final Context context;

    public AlbumAdapter(List<AlbumModel> albums, Context context) {
        this.albums = albums;
        this.context = context;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AlbumRvItemBinding binding = AlbumRvItemBinding.inflate(inflater, parent, false);
        return new AlbumViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        AlbumModel album = albums.get(position);
        holder.bind(album);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AlbumDetailActivity.class);
            intent.putExtra("id", album.getId());
            intent.putExtra("name", album.getName());
            intent.putExtra("img", album.getImageUrl());
            intent.putExtra("artist", album.getArtistName());
            intent.putExtra("albumUrl", album.getExternal_urls());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final AlbumRvItemBinding binding;

        public AlbumViewHolder(AlbumRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AlbumModel album) {
            Glide.with(context)
                    .load(album.getImageUrl())
                    .into(binding.idIVAlbum);

            binding.idTVAlbumName.setText(album.getName());
            binding.idTVALbumDetails.setText(album.getArtistName());
        }
    }
}

package org.geeksforgeeks.demo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.geeksforgeeks.demo.databinding.AlbumRvItemBinding

class AlbumAdapter(
    private val albums: List<AlbumModel>,
    private val context: Context
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = AlbumRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        holder.bind(album)

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, AlbumDetailActivity::class.java).apply {
                    putExtra("id", album.id)
                    putExtra("name", album.name)
                    putExtra("img", album.imageUrl)
                    putExtra("artist", album.artistName)
                    putExtra("albumUrl", album.external_urls)
                }
            )
        }
    }

    override fun getItemCount() = albums.size

    inner class AlbumViewHolder(private val binding: AlbumRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: AlbumModel) {
            Glide.with(context)
                .load(album.imageUrl)
                .into(binding.idIVAlbum)

            binding.idTVAlbumName.text = album.name
            binding.idTVALbumDetails.text = album.artistName
        }
    }
}
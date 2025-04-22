package org.geeksforgeeks.demo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TrackAdapter(
    private val trackModels: ArrayList<TrackModel>, private val context: Context
) :
    RecyclerView.Adapter<TrackAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating layout on below line.
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.track_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // setting data to text views.
        val trackRVModal = trackModels[position]
        holder.trackNameTV.text = trackRVModal.trackName
        holder.trackArtistTV.text = trackRVModal.trackArtist
        // adding click listener for track item view
        holder.itemView.setOnClickListener {
            val trackUrl = "https://open.spotify.com/track/" + trackRVModal.id
            val uri = Uri.parse(trackUrl) // missing 'http://' will cause crashed
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return trackModels.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // creating and initializing variables for text views.
        internal val trackNameTV: TextView = itemView.findViewById<TextView>(R.id.idTVTrackName)
        val trackArtistTV: TextView = itemView.findViewById<TextView>(R.id.idTVTrackArtist)
    }
}

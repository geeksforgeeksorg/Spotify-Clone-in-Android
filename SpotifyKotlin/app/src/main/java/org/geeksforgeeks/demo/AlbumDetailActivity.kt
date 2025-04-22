package org.geeksforgeeks.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONException

class AlbumDetailActivity : AppCompatActivity() {
    // creating variables on below line.
    private var albumID: String? = ""
    private var albumImgUrl: String? = null
    private var albumName: String? = null
    private var artist: String? = null
    private var albumUrl: String? = null

    private lateinit var albumNameTV: TextView
    private lateinit var artistTV: TextView
    private lateinit var albumIV: ImageView
    private lateinit var playButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // initializing variables on below line.
        setContentView(R.layout.activity_album_detail)
        albumID = intent.getStringExtra("id")
        albumIV = findViewById(R.id.idIVAlbum)
        albumImgUrl = intent.getStringExtra("img")
        albumName = intent.getStringExtra("name")
        artist = intent.getStringExtra("artist")
        albumUrl = intent.getStringExtra("albumUrl")
        Log.e("TAG", "album id is : $albumID")
        albumNameTV = findViewById(R.id.idTVAlbumName)
        playButton = findViewById(R.id.playButton)
        artistTV = findViewById(R.id.idTVArtistName)
        // setting data on below line.
        albumNameTV.text = albumName
        artistTV.text = artist
        // adding click listener for fab on below line.
        playButton.setOnClickListener { // opening album from url on below line.
            val uri = Uri.parse(albumUrl) // missing 'http://' will cause crashed
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        // loading image on below line.
        Glide.with(this).load(albumImgUrl).into(albumIV)
        // getting album tracks on below line.
        getAlbumTracks(albumID)
    }

    private val token: String?
        // method to get access token.
        get() {
            val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            return sh.getString("token", "Not Found")
        }

    // method to get tracks from albums.
    private fun getAlbumTracks(albumID: String?) {
        // on below line creating variable for url
        val url = "https://api.spotify.com/v1/albums/$albumID/tracks"
        // on below line creating list, initializing adapter and setting it to recycler view.
        val trackModels = ArrayList<TrackModel>()
        val trackAdapter = TrackAdapter(trackModels, this)
        val trackRV = findViewById<RecyclerView>(R.id.rvAlbumDetails)
        trackRV.adapter = trackAdapter
        val queue = Volley.newRequestQueue(this@AlbumDetailActivity)
        // on below line making json object request to parse json data.
        val trackObj: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val itemsArray = response.getJSONArray("items")
                    for (i in 0 until itemsArray.length()) {
                        val itemObj = itemsArray.getJSONObject(i)
                        val trackName = itemObj.getString("name")
                        val id = itemObj.getString("id")
                        val trackArtist =
                            itemObj.getJSONArray("artists").getJSONObject(0).getString("name")
                        // on below line adding data to array list.
                        trackModels.add(TrackModel(trackName, trackArtist, id))
                    }
                    trackAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    this@AlbumDetailActivity,
                    "Fail to get Tracks$error", Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                // on below line passing headers.
                val headers = HashMap<String, String>()
                headers["Authorization"] = token!!
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        // on below line adding
        // request to queue.
        queue.add(trackObj)
    }
}

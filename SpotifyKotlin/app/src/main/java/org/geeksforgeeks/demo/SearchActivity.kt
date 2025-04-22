package org.geeksforgeeks.demo

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class SearchActivity : AppCompatActivity() {
    // on below line creating variables
    private var searchQuery: String? = ""
    private lateinit var searchEdt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        // on below line initializing variables.
        searchEdt = findViewById(R.id.idEdtSearch)
        searchQuery = intent.getStringExtra("searchQuery")
        searchEdt.setText(searchQuery)
        // on below line adding action listener
        // for search edit text
        searchEdt.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // on below line calling method to get tracks.
                getTracks(searchEdt.getText().toString())
                return@OnEditorActionListener true
            }
            false
        })
        // on below line getting tracks.
        getTracks(searchQuery)
    }

    private val token: String?
        // method to get token.
        get() {
            val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            return sh.getString("token", "Not Found")
        }


    private fun getTracks(searchQuery: String?) {
        // on below line creating and initializing variables
        // for recycler view,list and adapter.
        val songsRV = findViewById<RecyclerView>(R.id.idRVSongs)
        val trackModels = ArrayList<TrackModel>()
        val trackAdapter = TrackAdapter(trackModels, this)
        songsRV.adapter = trackAdapter
        // on below line creating variable for url.
        val url = "https://api.spotify.com/v1/search?q=$searchQuery&type=track"
        val queue = Volley.newRequestQueue(this@SearchActivity)
        // on below line making json object request
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                try {
                    val trackObj = response.getJSONObject("tracks")
                    val itemsArray = trackObj.getJSONArray("items")
                    for (i in 0 until itemsArray.length()) {
                        val itemObj = itemsArray.getJSONObject(i)
                        val trackName = itemObj.getString("name")
                        val trackArtist =
                            itemObj.getJSONArray("artists").getJSONObject(0).getString("name")
                        val trackID = itemObj.getString("id")
                        // on below line adding data to array list
                        trackModels.add(TrackModel(trackName, trackArtist, trackID))
                    }
                    // on below line notifying adapter
                    trackAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    this@SearchActivity,
                    "Fail to get data : $error", Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                // on below line adding headers.
                val headers = HashMap<String, String>()
                headers["Authorization"] = token!!
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        // adding json object request to queue.
        queue.add(jsonObjectRequest)
    }
}
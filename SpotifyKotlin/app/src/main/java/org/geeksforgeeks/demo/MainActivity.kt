package org.geeksforgeeks.demo

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var albumsRV: RecyclerView
    private lateinit var popularAlbumsRV: RecyclerView
    private lateinit var trendingAlbumsRV: RecyclerView
    private lateinit var searchEdt: EditText
    private var isTokenGenerated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupSearchView()
        generateToken()
    }

    private fun initializeViews() {
        albumsRV = findViewById(R.id.idRVAlbums)
        popularAlbumsRV = findViewById(R.id.idRVPopularAlbums)
        trendingAlbumsRV = findViewById(R.id.idRVTrendingAlbums)
        searchEdt = findViewById(R.id.idEdtSearch)

        // Setup RecyclerViews with horizontal layout
        listOf(albumsRV, popularAlbumsRV, trendingAlbumsRV).forEach { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            recyclerView.setHasFixedSize(true)
        }
    }

    private fun setupSearchView() {
        searchEdt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(searchEdt.text.toString().trim())
                true
            } else {
                false
            }
        }
    }

    private fun searchTracks(searchQuery: String) {
        if (searchQuery.isNotEmpty()) {
            startActivity(
                Intent(this, SearchActivity::class.java).apply {
                    putExtra("searchQuery", searchQuery)
                }
            )
        } else {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateToken() {
        val url = "https://accounts.spotify.com/api/token"
        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val token = JSONObject(response).getString("access_token")
                    getSharedPreferences("MySharedPref", MODE_PRIVATE).edit {
                        putString("token", "Bearer $token")
                        apply()
                    }
                    isTokenGenerated = true
                    // Load data after token is generated
                    loadAllAlbumData()
                } catch (e: JSONException) {
                    showError("Failed to parse token response: ${e.message}")
                }
            },
            { error -> showError("Failed to get token: ${error.message}") }
        ) {
            override fun getHeaders(): Map<String, String> {
                val clientId = "Enter your own client id"
                val clientSecret = "Enter your own client secret"
                val credentials = "$clientId:$clientSecret"
                val auth = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

                return mapOf(
                    "Authorization" to "Basic $auth",
                    "Content-Type" to "application/x-www-form-urlencoded"
                )
            }

            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf("grant_type" to "client_credentials")
            }
        }
        queue.add(request)
    }

    private fun loadAllAlbumData() {
        if (!isTokenGenerated) {
            showError("Token not generated yet")
            return
        }

        // Recommended albums
        loadAlbums(
            recyclerView = albumsRV,
            albumIds = listOf(
                "5Nwsra93UQYJ6xxcjcE10x",
                "0z7bJ6UpjUw8U4TATtc5Ku",
                "36UJ90D0e295TvlU109Xvy",
                "3uuu6u13U0KeVQsZ3CZKK4",
                "45ZIondgVoMB84MQQaUo9T",
                "15CyNDuGY5fsG0Hn9rjnpG",
                "1HeX4SmCFW4EPHQDvHgrVS",
                "6mCDTT1XGTf48p6FkK9qFL"
            )
        )

        // Popular albums
        loadAlbums(
            recyclerView = popularAlbumsRV,
            albumIds = listOf(
                "0sjyZypccO1vyihqaAkdt3",
                "17vZRWjKOX7TmMktjQL2Qx",
                "5Nwsra93UQYJ6xxcjcE10x",
                "2zXKlf81VmDHIMtQe3oD0r",
                "7Gws1vUsWltRs58x8QuYVQ",
                "7uftfPn8f7lwtRLUrEVRYM",
                "7kSY0fqrPep5vcwOb1juye"
            )
        )

        // Trending albums
        loadAlbums(
            recyclerView = trendingAlbumsRV,
            albumIds = listOf(
                "1P4eCx5b11Tfmi4s1GmWmQ",
                "2SsEtiB6yJYn8hRRAmtVda",
                "7hhxms8KCwlQCWffIJpN9b",
                "3umvKIjsD484pa9pCyPK2x",
                "3OHC6XD29wXWADtAOP2geV",
                "3RZxrS2dDZlbsYtMRM89v8",
                "24C47633GRlozws7WBth7t"
            )
        )
    }

    private fun loadAlbums(recyclerView: RecyclerView, albumIds: List<String>) {
        val token = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            .getString("token", "") ?: ""

        if (token.isEmpty()) {
            showError("Authentication token is missing")
            return
        }

        val url = "https://api.spotify.com/v1/albums?ids=${albumIds.joinToString(",")}"
        val queue = Volley.newRequestQueue(this)

        val request = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    parseAlbumResponse(response, recyclerView)
                } catch (e: Exception) {
                    showError("Failed to parse album data: ${e.message}")
                }
            },
            { error ->
                showError("Failed to load albums: ${error.message}")
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                    "Authorization" to token,
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"
                )
            }
        }
        queue.add(request)
    }

    private fun parseAlbumResponse(response: JSONObject, recyclerView: RecyclerView) {
        try {
            val albumArray = response.getJSONArray("albums")
            val albumList = mutableListOf<AlbumModel>()

            for (i in 0 until albumArray.length()) {
                val albumObj = albumArray.getJSONObject(i)
                val artists = albumObj.getJSONArray("artists")
                val images = albumObj.getJSONArray("images")

                albumList.add(AlbumModel(
                    album_type = albumObj.optString("album_type", "album"),
                    artistName = if (artists.length() > 0)
                        artists.getJSONObject(0).optString("name", "Unknown Artist")
                    else "Unknown Artist",
                    external_ids = albumObj.getJSONObject("external_ids").optString("upc", ""),
                    external_urls = albumObj.getJSONObject("external_urls").optString("spotify", ""),
                    href = albumObj.optString("href", ""),
                    id = albumObj.optString("id", ""),
                    imageUrl = if (images.length() > 1)
                        images.getJSONObject(1).optString("url", "")
                    else "",
                    label = albumObj.optString("label", ""),
                    name = albumObj.optString("name", "Unknown Album"),
                    popularity = albumObj.optInt("popularity", 0),
                    release_date = albumObj.optString("release_date", ""),
                    total_tracks = albumObj.optInt("total_tracks", 0),
                    type = albumObj.optString("type", "album")
                ))
            }

            recyclerView.adapter = AlbumAdapter(albumList, this)
        } catch (e: JSONException) {
            showError("JSON parsing error: ${e.message}")
        } catch (e: Exception) {
            showError("Error parsing album data: ${e.message}")
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
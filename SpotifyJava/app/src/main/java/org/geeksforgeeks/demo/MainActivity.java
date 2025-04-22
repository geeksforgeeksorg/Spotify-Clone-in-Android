package org.geeksforgeeks.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView albumsRV, popularAlbumsRV, trendingAlbumsRV;
    private EditText searchEdt;
    private boolean isTokenGenerated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();   // Link UI elements with code
        setupSearchView();   // Set listener for the search action
        generateToken();     // Get Spotify API token
    }

    private void initializeViews() {
        albumsRV = findViewById(R.id.idRVAlbums);
        popularAlbumsRV = findViewById(R.id.idRVPopularAlbums);
        trendingAlbumsRV = findViewById(R.id.idRVTrendingAlbums);
        searchEdt = findViewById(R.id.idEdtSearch);

        // Set horizontal layout for each RecyclerView
        RecyclerView[] recyclerViews = {albumsRV, popularAlbumsRV, trendingAlbumsRV};
        for (RecyclerView rv : recyclerViews) {
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rv.setHasFixedSize(true);
        }
    }

    private void setupSearchView() {
        searchEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(searchEdt.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void searchTracks(String searchQuery) {
        if (!searchQuery.isEmpty()) {
            // Navigate to SearchActivity with query
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("searchQuery", searchQuery);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateToken() {
        String url = "https://accounts.spotify.com/api/token";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String token = jsonObject.getString("access_token");

                        // Save token in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        sharedPreferences.edit().putString("token", "Bearer " + token).apply();

                        isTokenGenerated = true;
                        loadAllAlbumData(); // Load albums after token is ready
                    } catch (JSONException e) {
                        showError("Failed to parse token response: " + e.getMessage());
                    }
                },
                error -> showError("Failed to get token: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                String clientId = "Enter your own client id";
                String clientSecret = "Enter your own client secret";
                String credentials = clientId + ":" + clientSecret;
                String auth = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + auth);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "client_credentials");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void loadAllAlbumData() {
        if (!isTokenGenerated) {
            showError("Token not generated yet");
            return;
        }

        // Load Recommended Albums
        loadAlbums(albumsRV, List.of(
                "5Nwsra93UQYJ6xxcjcE10x", "0z7bJ6UpjUw8U4TATtc5Ku", "36UJ90D0e295TvlU109Xvy",
                "3uuu6u13U0KeVQsZ3CZKK4", "45ZIondgVoMB84MQQaUo9T", "15CyNDuGY5fsG0Hn9rjnpG",
                "1HeX4SmCFW4EPHQDvHgrVS", "6mCDTT1XGTf48p6FkK9qFL"
        ));

        // Load Popular Albums
        loadAlbums(popularAlbumsRV, List.of(
                "0sjyZypccO1vyihqaAkdt3", "17vZRWjKOX7TmMktjQL2Qx", "5Nwsra93UQYJ6xxcjcE10x",
                "2zXKlf81VmDHIMtQe3oD0r", "7Gws1vUsWltRs58x8QuYVQ", "7uftfPn8f7lwtRLUrEVRYM",
                "7kSY0fqrPep5vcwOb1juye"
        ));

        // Load Trending Albums
        loadAlbums(trendingAlbumsRV, List.of(
                "1P4eCx5b11Tfmi4s1GmWmQ", "2SsEtiB6yJYn8hRRAmtVda", "7hhxms8KCwlQCWffIJpN9b",
                "3umvKIjsD484pa9pCyPK2x", "3OHC6XD29wXWADtAOP2geV", "3RZxrS2dDZlbsYtMRM89v8",
                "24C47633GRlozws7WBth7t"
        ));
    }

    private void loadAlbums(RecyclerView recyclerView, List<String> albumIds) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        if (token.isEmpty()) {
            showError("Authentication token is missing");
            return;
        }

        String url = "https://api.spotify.com/v1/albums?ids=" + String.join(",", albumIds);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        parseAlbumResponse(response, recyclerView);
                    } catch (Exception e) {
                        showError("Failed to parse album data: " + e.getMessage());
                    }
                },
                error -> showError("Failed to load albums: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void parseAlbumResponse(JSONObject response, RecyclerView recyclerView) {
        try {
            JSONArray albumArray = response.getJSONArray("albums");
            List<AlbumModel> albumList = new ArrayList<>();

            for (int i = 0; i < albumArray.length(); i++) {
                JSONObject albumObj = albumArray.getJSONObject(i);
                JSONArray artists = albumObj.getJSONArray("artists");
                JSONArray images = albumObj.getJSONArray("images");

                String artistName = (artists.length() > 0)
                        ? artists.getJSONObject(0).optString("name", "Unknown Artist")
                        : "Unknown Artist";

                String imageUrl = (images.length() > 1)
                        ? images.getJSONObject(1).optString("url", "")
                        : "";

                albumList.add(new AlbumModel(
                        albumObj.optString("album_type", "album"),
                        artistName,
                        albumObj.getJSONObject("external_ids").optString("upc", ""),
                        albumObj.getJSONObject("external_urls").optString("spotify", ""),
                        albumObj.optString("href", ""),
                        albumObj.optString("id", ""),
                        imageUrl,
                        albumObj.optString("label", ""),
                        albumObj.optString("name", "Unknown Album"),
                        albumObj.optInt("popularity", 0),
                        albumObj.optString("release_date", ""),
                        albumObj.optInt("total_tracks", 0),
                        albumObj.optString("type", "album")
                ));
            }

            recyclerView.setAdapter(new AlbumAdapter(albumList, this));

        } catch (JSONException e) {
            showError("JSON parsing error: " + e.getMessage());
        } catch (Exception e) {
            showError("Error parsing album data: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
package org.geeksforgeeks.demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    // on below line creating variables
    private String searchQuery = "";
    private EditText searchEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // on below line initializing variables.
        searchEdt = findViewById(R.id.idEdtSearch);
        searchQuery = getIntent().getStringExtra("searchQuery");
        searchEdt.setText(searchQuery);

        // on below line adding action listener
        // for search edit text
        searchEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // on below line calling method to get tracks.
                getTracks(searchEdt.getText().toString());
                return true;
            }
            return false;
        });

        // on below line getting tracks.
        getTracks(searchQuery);
    }

    // method to get token.
    private String getToken() {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sh.getString("token", "Not Found");
    }

    private void getTracks(String searchQuery) {
        // on below line creating and initializing variables
        // for recycler view, list and adapter.
        RecyclerView songsRV = findViewById(R.id.idRVSongs);
        ArrayList<TrackModel> trackModels = new ArrayList<>();
        TrackAdapter trackAdapter = new TrackAdapter(trackModels, this);
        songsRV.setAdapter(trackAdapter);

        // on below line creating variable for url.
        String url = "https://api.spotify.com/v1/search?q=" + searchQuery + "&type=track";

        // on below line making json object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject trackObj = response.getJSONObject("tracks");
                        JSONArray itemsArray = trackObj.getJSONArray("items");
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObj = itemsArray.getJSONObject(i);
                            String trackName = itemObj.getString("name");
                            String trackArtist = itemObj.getJSONArray("artists").getJSONObject(0).getString("name");
                            String trackID = itemObj.getString("id");

                            // on below line adding data to array list
                            trackModels.add(new TrackModel(trackName, trackArtist, trackID));
                        }

                        // on below line notifying adapter
                        trackAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(
                        SearchActivity.this,
                        "Fail to get data : " + error, Toast.LENGTH_SHORT
                ).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // on below line adding headers.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // adding json object request to queue.
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}
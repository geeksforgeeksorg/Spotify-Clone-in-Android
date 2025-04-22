package org.geeksforgeeks.demo;

public class TrackModel {
    private String trackName;
    private String trackArtist;
    private String id;

    // Constructor
    public TrackModel(String trackName, String trackArtist, String id) {
        this.trackName = trackName;
        this.trackArtist = trackArtist;
        this.id = id;
    }

    // Getters
    public String getTrackName() {
        return trackName;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public String getId() {
        return id;
    }

    // Setters
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }

    public void setId(String id) {
        this.id = id;
    }
}
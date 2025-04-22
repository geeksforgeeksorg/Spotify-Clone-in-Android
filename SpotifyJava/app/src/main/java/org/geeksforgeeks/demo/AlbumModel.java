package org.geeksforgeeks.demo;

public class AlbumModel {
    private String album_type;
    private String artistName;
    private String external_ids;
    private String external_urls;
    private String href;
    private String id;
    private String imageUrl;
    private String label;
    private String name;
    private int popularity;
    private String release_date;
    private int total_tracks;
    private String type;

    public AlbumModel(String album_type, String artistName, String external_ids, String external_urls,
                      String href, String id, String imageUrl, String label, String name,
                      int popularity, String release_date, int total_tracks, String type) {
        this.album_type = album_type;
        this.artistName = artistName;
        this.external_ids = external_ids;
        this.external_urls = external_urls;
        this.href = href;
        this.id = id;
        this.imageUrl = imageUrl;
        this.label = label;
        this.name = name;
        this.popularity = popularity;
        this.release_date = release_date;
        this.total_tracks = total_tracks;
        this.type = type;
    }

    // Add getters (and setters if needed)

    public String getAlbum_type() { return album_type; }
    public String getArtistName() { return artistName; }
    public String getExternal_ids() { return external_ids; }
    public String getExternal_urls() { return external_urls; }
    public String getHref() { return href; }
    public String getId() { return id; }
    public String getImageUrl() { return imageUrl; }
    public String getLabel() { return label; }
    public String getName() { return name; }
    public int getPopularity() { return popularity; }
    public String getRelease_date() { return release_date; }
    public int getTotal_tracks() { return total_tracks; }
    public String getType() { return type; }
}

package com.sylphonia.moodjournalai;


public class Track {
    private final String name;
    private final String artist;
    private final String imageUrl;
    private final String previewUrl;

    public Track(String name, String artist, String imageUrl, String previewUrl) {
        this.name = name;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
    }

    public String getName() { return name; }
    public String getArtist() { return artist; }
    public String getImageUrl() { return imageUrl; }
    public String getPreviewUrl() { return previewUrl; }
}

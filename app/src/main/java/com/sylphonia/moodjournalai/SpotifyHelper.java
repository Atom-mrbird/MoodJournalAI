package com.sylphonia.moodjournalai;


import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SpotifyHelper {
    private static final String TAG = "SpotifyHelper";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String SEARCH_URL = "https://api.spotify.com/v1/search?q=%s&type=track&limit=5";

    private final OkHttpClient client = new OkHttpClient();
    private String accessToken;

    // 1️⃣ Access Token al
    public void fetchAccessToken() throws IOException {
        String credentials = Credentials.basic(BuildConfig.SPOTIFY_CLIENT_ID, BuildConfig.SPOTIFY_CLIENT_SECRET);
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url(TOKEN_URL)
                .addHeader("Authorization", credentials)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful() && response.body() != null) {
            JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
            accessToken = json.get("access_token").getAsString();
        } else {
            Log.e(TAG, "Token alınamadı: " + response);
        }
    }

    // 2️⃣ Duyguya göre öneri getir
    public List<Track> getTracksByMood(String mood) throws IOException {
        if (accessToken == null) fetchAccessToken();
        String[] happyQueries = {"happy", "joyful", "cheerful", "uplifting"};
        String query = happyQueries[new Random().nextInt(happyQueries.length)];
        int randomOffset = new Random().nextInt(50); // 0–49 arası rastgele sayı
        String url = "https://api.spotify.com/v1/search?q=" + query + "&type=track&limit=5&offset=" + randomOffset;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        Response response = client.newCall(request).execute();
        List<Track> results = new ArrayList<>();

        if (response.isSuccessful() && response.body() != null) {
            JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
            JsonArray tracks = json.getAsJsonObject("tracks").getAsJsonArray("items");

            for (int i = 0; i < tracks.size(); i++) {
                JsonObject track = tracks.get(i).getAsJsonObject();
                String name = track.get("name").getAsString();
                String artist = track.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString();
                String imageUrl = track.getAsJsonObject("album")
                        .getAsJsonArray("images").get(0).getAsJsonObject().get("url").getAsString();
                String previewUrl = track.has("preview_url") && !track.get("preview_url").isJsonNull()
                        ? track.get("preview_url").getAsString() : null;

                results.add(new Track(name, artist, imageUrl, previewUrl));
            }
        } else {
            Log.e(TAG, "Spotify API hatası: " + response.code());
        }
        return results;
    }
}

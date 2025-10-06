package com.sylphonia.moodjournalai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private EditText etMood;
    private Button btnAnalyze;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMood = findViewById(R.id.etMood);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAnalyze.setOnClickListener(v -> {
            String mood = etMood.getText().toString().trim();
            if (mood.isEmpty()) {
                Toast.makeText(this, "Bir duygu gir (ör: happy, sad, angry)", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    SpotifyHelper spotify = new SpotifyHelper();
                    List<Track> songs = spotify.getTracksByMood(mood);
                    runOnUiThread(() -> {
                        recyclerView.setAdapter(new TrackAdapter(this, songs));
                        progressBar.setVisibility(View.GONE);
                    });
                } catch (IOException e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Spotify API hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }
}

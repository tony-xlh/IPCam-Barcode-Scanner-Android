package com.tonyxlh.ipcambarcodescanner;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.ui.PlayerView;
import androidx.media3.common.MediaItem;

public class MainActivity extends AppCompatActivity {
    private PlayerView playerView;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        playerView = findViewById(R.id.playerView);
        MediaSource mediaSource =
                new RtspMediaSource.Factory()
                   .createMediaSource(MediaItem.fromUri(Uri.parse("rtsp://192.168.8.65:8554/mystream")));
        ExoPlayer player = new ExoPlayer.Builder(getApplicationContext()).build();
        player.setVolume(0);
        playerView.setPlayer(player);
        // Set the media source to be played.
        player.setMediaSource(mediaSource);
        // Prepare the player.
        player.prepare();
        player.setPlayWhenReady(true);
    }
}
package com.tonyxlh.ipcambarcodescanner;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.Format;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.video.VideoFrameMetadataListener;
import androidx.media3.ui.PlayerView;
import androidx.media3.common.MediaItem;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private PlayerView playerView;
    protected Uri uri = Uri.parse("rtsp://192.168.8.65:8554/mystream");
    private boolean decoding = false;
    private boolean isVideoPlaying = false;
    private Timer timer = null;
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
                   .createMediaSource(MediaItem.fromUri(uri));
        ExoPlayer player = new ExoPlayer.Builder(getApplicationContext()).build();
        player.setVolume(0);
        playerView.setPlayer(player);
        // Set the media source to be played.
        player.setMediaSource(mediaSource);
        player.addListener(
            new Player.Listener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    isVideoPlaying = isPlaying;
                }
            });
        player.setPlayWhenReady(true);
        // Prepare the player.
        player.prepare();
        startDecoding();
    }


    private void startDecoding(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                takePhotoAndDecode();
            }
        };
        timer = new Timer();
        timer.schedule(task, 1000,100);
    }

    private void stopDecoding(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @SuppressLint("NewApi")
    private void takePhotoAndDecode() {
        if (decoding == false && isVideoPlaying) {
            decoding = true;
            SurfaceView surfaceView = (SurfaceView) playerView.getVideoSurfaceView();
            // Create a bitmap the size of the scene view.
            final Bitmap bitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(),
                    Bitmap.Config.ARGB_8888);
            // Create a handler thread to offload the processing of the image.
            final HandlerThread handlerThread = new HandlerThread("PixelCopier");
            handlerThread.start();
            // Make the request to copy.
            PixelCopy.request(surfaceView, bitmap, (copyResult) -> {
                Log.d("DBR","copyresult:"+copyResult);
                if (copyResult == PixelCopy.SUCCESS) {
                    Log.d("DBR",bitmap.getWidth()+"x"+bitmap.getHeight());
                }
                decoding = false;
                handlerThread.quitSafely();
            }, new Handler(handlerThread.getLooper()));
        }
    }
}
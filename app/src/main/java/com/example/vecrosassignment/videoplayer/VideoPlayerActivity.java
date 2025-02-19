package com.example.vecrosassignment.videoplayer;

import android.app.AlertDialog;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.LoadControl;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.ui.PlayerView;

import com.example.vecrosassignment.MainActivity;
import com.example.vecrosassignment.R;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.snackbar.Snackbar;

public class VideoPlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private Button btnMute, btnPlayPause, btnStop;
    private ImageView pipButton;
    private Handler latencyHandler = new Handler(Looper.getMainLooper());
    private static final long LATENCY_CHECK_INTERVAL = 2000;

    private boolean isPlaying = true;
    private boolean isMuted = false;
    private static final long MAX_LATENCY_THRESHOLD = 5000;
    private String rtspUrl;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.player_view);
        btnMute = findViewById(R.id.btnMute);
        btnPlayPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        pipButton = findViewById(R.id.pip);  // Ensure your ImageView has this ID in XML

        rtspUrl = getIntent().getStringExtra("RTSP_URL");

        if (rtspUrl == null || rtspUrl.isEmpty()) {
            showError("Invalid RTSP URL. Kindly check your URL.");
            return;
        }

        if (!isInternetAvailable()) {
            showError("Kindly check your internet connection.");
            return;
        }

        initializePlayer(rtspUrl);

        // Play button
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnStop.setOnClickListener(v -> stopPlayer());
        btnMute.setOnClickListener(v -> toggleMute());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pipButton.setOnClickListener(v -> enterPipMode());
        }

        // Stop button
        btnStop.setOnClickListener(v -> {
            player.stop();
            finish();
        });

        // PiP button click event
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pipButton.setOnClickListener(v -> enterPipMode());
        }
    }
    private void togglePlayPause() {
        if (player != null) {
            if (isPlaying) {
                player.pause();
                btnPlayPause.setText("Play");
            } else {
                player.play();
                btnPlayPause.setText("Pause");
            }
            isPlaying = !isPlaying;
        }
    }

    private void toggleMute() {
        if (player != null) {
            if (isMuted) {
                player.setVolume(1f);
                btnMute.setText("Mute");
            } else {
                player.setVolume(0f);
                btnMute.setText("Unmute");
            }
            isMuted = !isMuted;
        }
    }
    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer(String rtspUrl) {
        try {
            LoadControl loadControl = new DefaultLoadControl.Builder()
                    .setBufferDurationsMs(500, 1000, 500, 500)
                    .build();

            player = new ExoPlayer.Builder(this).setLoadControl(loadControl).build();
            playerView.setPlayer(player);
            playerView.setUseController(false);

            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(Uri.parse(rtspUrl))
                    .build();

            RtspMediaSource mediaSource = new RtspMediaSource.Factory()
                    .createMediaSource(mediaItem);

            player.setMediaSource(mediaSource);
            player.prepare();
            player.play();

            // Add a listener to capture errors during playback
            player.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(PlaybackException error) {
//                    Player.Listener.super.onPlayerError(error);
                    showRtspError();
                }
            });

            startLatencyCheck();
        } catch (Exception e) {
            showRtspError();
        }
    }

    private void showRtspError() {
        new AlertDialog.Builder(this)
                .setTitle("RTSP Error")
                .setMessage("Error fetching RTSP. Try revalidating your link.")
                .setPositiveButton("OK", (dialog, which) -> navigateToHomePage())
                .setCancelable(false)
                .show();
    }
    private void navigateToHomePage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void startLatencyCheck() {
        latencyHandler.postDelayed(() -> {
            if (player != null) {
                long latency = player.getBufferedPosition() - player.getCurrentPosition();
                if (latency > MAX_LATENCY_THRESHOLD) {
                    showLatencyWarning();
                }
            }
            latencyHandler.postDelayed(this::startLatencyCheck, LATENCY_CHECK_INTERVAL);
        }, LATENCY_CHECK_INTERVAL);
    }

    private void showLatencyWarning() {
        Snackbar snackbar = Snackbar.make(playerView, "You're viewing old scenes", Snackbar.LENGTH_INDEFINITE)
                .setAction("Refresh", v -> restartStream());

        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarView.setLayoutParams(params);

        snackbar.show();

    }
    private void stopPlayer() {
        if (player != null) {
            player.stop();
            finish();
        }
    }
    private void restartStream() {
        if (player != null) {
            player.stop();
            player.prepare();
            player.play();
        }
    }


    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPipMode() {
        PictureInPictureParams params = new PictureInPictureParams.Builder()
                .setAspectRatio(new Rational(playerView.getWidth(), playerView.getHeight()))
                .build();
        enterPictureInPictureMode(params);

        // Hide controls when entering PiP mode
        btnPlayPause.setVisibility(Button.GONE);
        btnMute.setVisibility(Button.GONE);
        btnStop.setVisibility(Button.GONE);
        pipButton.setVisibility(ImageView.GONE);
    }

    @Override
    protected void onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPipMode();
        }
        super.onUserLeaveHint();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        }

        if (!isInPictureInPictureMode) {
            // Restore controls when exiting PiP mode
            btnPlayPause.setVisibility(Button.VISIBLE);
            btnMute.setVisibility(Button.VISIBLE);
            btnStop.setVisibility(Button.VISIBLE);
            pipButton.setVisibility(ImageView.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}

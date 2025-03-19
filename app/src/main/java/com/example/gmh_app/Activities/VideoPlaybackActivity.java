package com.example.gmh_app.Activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.gmh_app.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoPlaybackActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_URI = "EXTRA_VIDEO_URI";
    public static final int REQUEST_CODE_VIDEO_PLAYBACK = 1;
    public static final int REQUEST_CODE_QUESTION = 2;

    ProgressBar progressBar;
    ImageView bt_fullscreen;
    SimpleExoPlayer simpleExoPlayer;

    boolean isFullScreen = false;
    LinearLayout sec_mid, sec_bottom;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_video_playback);

        PlayerView playerView = findViewById(R.id.player);
        progressBar = findViewById(R.id.progress_bar);
        bt_fullscreen = findViewById(R.id.bt_fullscreen);

        bt_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFullScreen) {
                    bt_fullscreen.setImageDrawable(ContextCompat.getDrawable(VideoPlaybackActivity.this, R.drawable.baseline_fullscreen_exit_24));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    isFullScreen = true;
                } else {
                    bt_fullscreen.setImageDrawable(ContextCompat.getDrawable(VideoPlaybackActivity.this, R.drawable.baseline_fullscreen_24));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    isFullScreen = false;
                }
            }
        });

        simpleExoPlayer = new SimpleExoPlayer.Builder(this)
                .setSeekBackIncrementMs(30000)
                .setSeekForwardIncrementMs(30000)
                .build();
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);

        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    // Start playback automatically once the video is ready
                    simpleExoPlayer.play();
                } else if (playbackState == Player.STATE_ENDED) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        String videoUriString = getIntent().getStringExtra(EXTRA_VIDEO_URI);
        if (videoUriString != null) {
            Uri videoUri = Uri.parse(videoUriString);
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            simpleExoPlayer.setMediaItem(mediaItem);
            simpleExoPlayer.prepare();  // Prepare the player
        }
    }

    private void lockScreen(boolean lock) {
        sec_mid = findViewById(R.id.sec_controlvid1);
        sec_bottom = findViewById(R.id.sec_controlvid2);
        if (lock) {
            sec_mid.setVisibility(View.INVISIBLE);
            sec_bottom.setVisibility(View.INVISIBLE);
        } else {
            sec_mid.setVisibility(View.VISIBLE);
            sec_bottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        simpleExoPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        navigateToTopicsActivity();
//    }
//
//    private void navigateToTopicsActivity() {
//        Intent intent = new Intent(this, TopicsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
}

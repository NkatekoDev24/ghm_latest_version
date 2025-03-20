package com.example.gmh_app.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.gmh_app.Models.VideoModel;
import com.example.gmh_app.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import java.util.ArrayList;

public class VideoPlaybackActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_LIST = "EXTRA_VIDEO_LIST";
    public static final String EXTRA_VIDEO_INDEX = "EXTRA_VIDEO_INDEX";
    public static final int REQUEST_CODE_VIDEO_PLAYBACK = 1;
    public static final int REQUEST_CODE_QUESTION = 2;

    private ArrayList<VideoModel> videoList;
    private int currentVideoIndex;

    private SimpleExoPlayer simpleExoPlayer;
    private ProgressBar progressBar;
    private ImageView exoSkipForward, exoSkipBack, exoPlay, exoPause, exoReplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playback);

        PlayerView playerView = findViewById(R.id.player);
        progressBar = findViewById(R.id.progress_bar);
        exoSkipForward = findViewById(R.id.exo_skip_forward);
        exoSkipBack = findViewById(R.id.exo_skip_back);
        exoPlay = findViewById(R.id.exo_play);
        exoPause = findViewById(R.id.exo_pause);
        exoReplay = findViewById(R.id.exo_replay);

        // Hide replay button initially
        exoReplay.setVisibility(View.GONE);

        videoList = getIntent().getParcelableArrayListExtra(EXTRA_VIDEO_LIST);
        currentVideoIndex = getIntent().getIntExtra(EXTRA_VIDEO_INDEX, 0);

        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);

        loadVideo();

        exoSkipForward.setOnClickListener(v -> skipToNextVideo());
        exoSkipBack.setOnClickListener(v -> skipToPreviousVideo());

        // Replay button click listener to restart video
        exoReplay.setOnClickListener(v -> replayVideo());

        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }

                if (playbackState == Player.STATE_ENDED) {
//                    showReplayButton();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void loadVideo() {
        if (videoList == null || videoList.isEmpty() || currentVideoIndex < 0 || currentVideoIndex >= videoList.size()) {
            setResult(RESULT_OK);
            finish();
            return;
        }

        VideoModel currentVideo = videoList.get(currentVideoIndex);
        if (currentVideo == null || currentVideo.getVideoUri() == null || currentVideo.getVideoUri().isEmpty()) {
            Toast.makeText(this, "Invalid video URL", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
            return;
        }

        Uri videoUri = Uri.parse(currentVideo.getVideoUri());
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        simpleExoPlayer.play();

        // Ensure play/pause buttons are visible and replay is hidden
        exoPlay.setVisibility(View.VISIBLE);
        exoPause.setVisibility(View.VISIBLE);
        exoReplay.setVisibility(View.GONE);
    }

    private void showReplayButton() {
        exoPlay.setVisibility(View.GONE);
        exoPause.setVisibility(View.GONE);
        exoReplay.setVisibility(View.VISIBLE);
    }

    private void replayVideo() {
        simpleExoPlayer.seekTo(0); // Restart video
        simpleExoPlayer.play();

        // Restore play/pause buttons
        exoPlay.setVisibility(View.VISIBLE);
        exoPause.setVisibility(View.VISIBLE);
        exoReplay.setVisibility(View.GONE);
    }

    private void skipToNextVideo() {
        if (currentVideoIndex < videoList.size() - 1) {
            currentVideoIndex += 3;
            loadVideo();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void skipToPreviousVideo() {
        if (currentVideoIndex > 0) {
            currentVideoIndex -= 3;
            loadVideo();
        }else if(currentVideoIndex == 1){
            exoSkipBack.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }
}

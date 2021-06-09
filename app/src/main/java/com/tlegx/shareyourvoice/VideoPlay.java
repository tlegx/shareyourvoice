package com.tlegx.shareyourvoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlay extends AppCompatActivity {

    //Variables
    private VideoView videoPlay;
    private TextView outputFileName;
    private ImageButton playButton;
    private ImageButton pauseButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        //Ignore Android 10+ night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Launches this activity and get the video file name from main activity
        Intent intent = getIntent();
        String vidfilename = intent.getExtras().getString("toActivity2");
        Log.d("vidfilename", vidfilename);

        //Initialize components
        videoPlay = findViewById(R.id.videoView);
        outputFileName = findViewById(R.id.outputFileName);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);

        //Prepare layout
        playButton.setVisibility(View.INVISIBLE);
        playButton.setEnabled(false);
        pauseButton.setVisibility(View.VISIBLE);
        pauseButton.setEnabled(true);
        outputFileName.setText("Output file:\n/storage/emulated/0/syv_video_" + vidfilename + ".avi");

        //Play video once activity is launched
        videoPlay.setVideoPath("/storage/emulated/0/syv_video_" + vidfilename + ".avi");
        videoPlay.start();

        //Setting up onClickListener
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPlay.start();
                playButton.setVisibility(View.INVISIBLE);
                playButton.setEnabled(false);
                pauseButton.setVisibility(View.VISIBLE);
                pauseButton.setEnabled(true);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPlay.pause();
                playButton.setVisibility(View.VISIBLE);
                playButton.setEnabled(true);
                pauseButton.setVisibility(View.INVISIBLE);
                pauseButton.setEnabled(false);
            }
        });

        //Handles when video completed playing
        videoPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoPlay.pause();
                playButton.setVisibility(View.VISIBLE);
                playButton.setEnabled(true);
                pauseButton.setVisibility(View.INVISIBLE);
                pauseButton.setEnabled(false);
            }
        });
    }
}
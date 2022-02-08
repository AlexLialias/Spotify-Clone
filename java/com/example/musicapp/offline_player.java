package com.example.musicapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class offline_player extends AppCompatActivity {

    MediaPlayer song;
    int totalTime;
    static int state = 0;
    SeekBar position;
    SeekBar volume;
    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;
    boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_player);

        song = new MediaPlayer();
        int file_position = getIntent().getIntExtra("position",0);




        try{

            //FileInputStream fis = getApplicationContext().openFileInput("Night in nice");
            File dir = new File(getApplicationContext().getFilesDir()+"/songs");
            File[] fileList = dir.listFiles();
            song.setDataSource(getApplicationContext().getFilesDir()+"/songs/"+fileList[file_position].getName());
            song.prepare();
            //song.start();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        song.setLooping(true);
        song.seekTo(0);
        song.setVolume(0.5f, 0.5f);
        totalTime = song.getDuration();
        elapsedTimeLabel = this.findViewById(R.id.seconds_passed);
        remainingTimeLabel = this.findViewById(R.id.duration);


        //position changes
        position = (SeekBar) findViewById(R.id.position);
        position.setMax(totalTime);
        position.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    song.seekTo(progress);
                    position.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //volume changes
        volume = (SeekBar) findViewById(R.id.volume);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                song.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        active = true;


        //update timers
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (song != null && active) {
                    try {
                        android.os.Message msg = new android.os.Message();
                        msg.what = song.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();



    }

        private Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                int currentPosition = msg.what;
                position.setProgress(currentPosition);

                String elapsedTime = translatetime(currentPosition);
                elapsedTimeLabel.setText(elapsedTime);

                String remainingTime = translatetime(totalTime);
                remainingTimeLabel.setText(remainingTime);
            }
        };

        public String translatetime(int time){
            String label = "";
            int min = time/1000/60;
            int sec = time/1000%60;

            label =  min +":";
            if(sec<10) label+="0";
            label+=sec;

            return label;
        }


    @Override
    protected void onPause() {
        super.onPause();
        active=false;
        if(song!=null){
            song.release();
        }
        state--;
       // onDestroy();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void PlayMusic(View view) {
            Button play = this.findViewById(R.id.play);
            if(state==0) {
                song.start();
                play.setBackgroundResource(R.drawable.pause);
                state++;
            } else{
                song.pause();
                play.setBackgroundResource(R.drawable.play);
                state--;
            }

        }

}

package com.example.musicapp;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class Player extends AppCompatActivity {

    MediaPlayer mediaPlayer = new MediaPlayer();
    MediaPlayer mediaPlayer2 = new MediaPlayer();
    int totalTime;
    static int state = 1;
    SeekBar position;
    int current =1;
    SeekBar volume;
    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;
    boolean active;
    int m;
    int j;
    private ConsumerImpl c = new ConsumerImpl();
    updateChunks uc = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //init
        c.init();
        uc = new updateChunks();
        m = 0;
        j=0;

        uc.execute();




        //volume changes
        volume = (SeekBar)findViewById(R.id.volume);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress/100f;
                mediaPlayer.setVolume(volume,volume);
                mediaPlayer2.setVolume(volume,volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }

    public void updatePlayer(File file) throws IOException {
        if(mediaPlayer.isPlaying()) return;
        mediaPlayer.reset();
        FileInputStream fis = new FileInputStream(file);
        mediaPlayer.setDataSource(fis.getFD());
        mediaPlayer.prepare();
        mediaPlayer.start();
        m++;
        current=1;
    }

    public void updatePlayer2(File file) throws IOException {
        if(mediaPlayer2.isPlaying()) return;
        mediaPlayer2.reset();
        FileInputStream fis = new FileInputStream(file);
        mediaPlayer2.setDataSource(fis.getFD());
        mediaPlayer2.prepare();
        mediaPlayer2.start();
        m++;
        System.out.println(mediaPlayer.getDuration());
        current=2;
    }

    public void SaveSong(View view) {


        String filename = c.songRequest;
        try{
            File dir  = new File(getApplicationContext().getFilesDir()+"/songs");
            if(!dir.exists()){
                dir.mkdir();
            }
            FileOutputStream fileOutputStream = null;
            File file = new File(getApplicationContext().getFilesDir()+"/songs",filename);
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            fileOutputStream= new FileOutputStream(file);

            //test
            byte[] arr = new byte[0];
            int k=0;
            while(k<c.glsize){
                if(c.chunk.get(k)!=null){


                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(arr);
                    outputStream.write(c.chunk.get(k));

                    arr =outputStream.toByteArray();

                 k++;
                }
            }
            fileOutputStream.write(arr);
            //FileOutputStream fileOutputStream = openFileOutput(filename,MODE_PRIVATE);
            //fileOutputStream.write(c.chunk.get(0));
            fileOutputStream.close();
            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


     public class updateChunks extends AsyncTask<String,File,FileInputStream>{

        @Override
        protected FileInputStream doInBackground(String... strings) {


            try {
                // create temp file that will hold byte array
                File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
                tempMp3.deleteOnExit();
                FileOutputStream fos = new FileOutputStream(tempMp3);
                fos.write(c.chunk.get(m));
                fos.close();
                publishProgress(tempMp3);

            } catch (IOException ex) {
                String s = ex.toString();
                ex.printStackTrace();
            }

            while (m < c.glsize) {
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration() - 300 && j == 0) {
                        j = 1;
                        try {
                            // create temp file that will hold byte array
                            File tempMp2 = File.createTempFile("kurchina", "mp3", getCacheDir());
                            tempMp2.deleteOnExit();
                            FileOutputStream fos = new FileOutputStream(tempMp2);
                            fos.write(c.chunk.get(m));
                            fos.close();
                            publishProgress(tempMp2);

                        } catch (IOException ex) {
                            String s = ex.toString();
                            ex.printStackTrace();
                        }
                    }
                    if (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration() - 300) {
                        j = 0;
                    }
                } else if (mediaPlayer2.isPlaying()) {
                    if (mediaPlayer2.getCurrentPosition() >= mediaPlayer2.getDuration() - 300 && j == 0) {
                        j = 1;
                        try {
                            // create temp file that will hold byte array
                            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
                            tempMp3.deleteOnExit();
                            FileOutputStream fos = new FileOutputStream(tempMp3);
                            fos.write(c.chunk.get(m));
                            fos.close();
                            publishProgress(tempMp3);

                        } catch (IOException ex) {
                            String s = ex.toString();
                            ex.printStackTrace();
                        }
                    }
                    if (mediaPlayer2.getCurrentPosition() < mediaPlayer2.getDuration() - 300) {
                        j = 0;
                    }
                }

            }





            return null;




        }
        @Override
        protected void onProgressUpdate(File... values) {
            super.onProgressUpdate(values);

            File file = values[0];
            if(m%2==0) {
                try {
                    updatePlayer(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    updatePlayer2(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


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
    protected void onPause(){
        super.onPause();
        active=false;
        c.ResetChunks();
        if(uc!=null)
            uc.cancel(true);
        if(mediaPlayer != null){
            mediaPlayer.release();
        }

        if(mediaPlayer2 != null){
            mediaPlayer2.release();
        }
        state--;
    }

    @Override
    protected void onResume() {
        super.onResume();
        active=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        c.ResetChunks();
        this.finish();
    }

    public void PlayMusic(View view) {
        Button play = this.findViewById(R.id.play);

        if(state==0) {
            if(current==1){
                mediaPlayer.start();
            }else{
                mediaPlayer2.start();
            }
            play.setBackgroundResource(R.drawable.pause);
            state++;
        } else {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }else {
                mediaPlayer2.pause();
            }
            play.setBackgroundResource(R.drawable.play);
            state--;
        }


    }
}

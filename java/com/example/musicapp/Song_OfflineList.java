package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class Song_OfflineList extends AppCompatActivity {

    ListView songListView;
    ArtistAdapter songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song__offline_list);

        songListView = findViewById(R.id.songOfflinelist);
        ArrayList<String> songslist = new ArrayList();
        File dir = new File(getApplicationContext().getFilesDir()+"/songs");
        File[] fileList = dir.listFiles();
        if(fileList!=null) {
            for (int i = 0; i < fileList.length; i++) {
                songslist.add(fileList[i].getName());
            }
            songs = new ArtistAdapter(getApplicationContext(),songslist);
            songListView.setAdapter(songs);
            songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent mi = new Intent(Song_OfflineList.this,offline_player.class);
                    mi.putExtra("position",position);
                    startActivity(mi);
                }
            });

        }else{
            songslist.add("Empty!");

            songs = new ArtistAdapter(getApplicationContext(),songslist);
            songListView.setAdapter(songs);
        }



    }
}

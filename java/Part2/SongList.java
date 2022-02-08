package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class SongList extends AppCompatActivity {
    ListView songListView;
    ArtistAdapter songs;
    ConsumerImpl c = new ConsumerImpl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);


        c.init();

        songListView = findViewById(R.id.songlist);
        ArrayList<String> songslist = c.SongList;


        songs = new ArtistAdapter(getApplicationContext(),songslist);
        songListView.setAdapter(songs);
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mi = new Intent(SongList.this,Player.class);
                c.songRequest=songs.getItem(position);
                startActivity(mi);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        //c.ResetSongs();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        c.ResetSongs();
        this.finish();
    }
}

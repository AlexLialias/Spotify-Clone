package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class StoredSongs extends AppCompatActivity {

    ListView songListView;
    ArtistAdapter songs;
    Request r = new Request();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stored_songs);




        songListView = findViewById(R.id.savedsongs);
        //ArrayList<String> songslist = r.getSongs();
        ArrayList<String> songslist = new ArrayList();
        songs = new ArtistAdapter(getApplicationContext(),songslist);
        songListView.setAdapter(songs);
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                r.setRequestedSong(position);

                Intent mi = new Intent(StoredSongs.this,Player.class);
                startActivity(mi);


            }
        });
    }
}

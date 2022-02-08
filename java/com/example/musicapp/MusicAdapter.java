package com.example.musicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MusicAdapter extends ArrayAdapter<ArtistName> {
    private Context context;
    private List<ArtistName> songs = new ArrayList<>();

    public MusicAdapter(@NonNull Context context,ArrayList<ArtistName> songs){
        super(context,0,songs);
        this.context=context;
        this.songs=songs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        View list = convertView;
        if(list==null)
            list = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        ArtistName current = songs.get(position);
       // View photo = list.findViewById(R.id.photo);
        //photo.setBackgroundResource(MusicFile.get);

        TextView name = (TextView) list.findViewById(R.id.Name);
        //name.setText("Τιτλος:" + (current.getTrackName()));

        TextView genre = (TextView) list.findViewById(R.id.Songs);
        //genre.setText("Τιτλος:" + (current.getGenre()));

        TextView album = (TextView) list.findViewById(R.id.Latest);
        //album.setText("Τιτλος:" + (current.getAlbumInfo()));

        return list;
    }
}

package com.example.musicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ArtistAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> artirsts = new ArrayList<>();

    public ArtistAdapter(@NonNull Context context,ArrayList<String> artirsts){
        super(context,0,artirsts);
        this.context=context;
        this.artirsts=artirsts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        View list = convertView;
        if(list==null)
            list = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        String current = artirsts.get(position);
        // View photo = list.findViewById(R.id.photo);
        //photo.setBackgroundResource(MusicFile.get);

        TextView name = (TextView) list.findViewById(R.id.Name);
        name.setText(current);



        return list;
    }
}
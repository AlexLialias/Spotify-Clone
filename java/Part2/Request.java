package com.example.musicapp;

import java.util.ArrayList;

public class Request {
    public static String request;
    private static ArrayList<String> Artist = new ArrayList();
    private static ArrayList<String> Songs = new ArrayList();
    private static int position;
    private static int size;
    public  static ArrayList<byte[]> chunk;

    public Request(){

    }



    public static void setRequest(String request){Request.request=request;}
    public  String getRequest() {
        return request;
    }
    public static void setArtistL(ArrayList<String> list)
    {
        for(int i =0;i<list.size();i++) {
            Artist.add(list.get(i));
        }
    }
    public static void setSongs(ArrayList<String> songs){Songs=songs;}
    public  ArrayList<String> getArtistL(){return Artist;}
    public  ArrayList<String> getSongs(){return Songs;}

    public static String getRequestedSong() {
        return Songs.get(position);
    }

    public static void setRequestedSong(int position) {
        Request.position = position;
    }

    public static void setChunk(byte[] dummy){
        chunk.add(dummy);
    }

    public static ArrayList<byte[]> getChunk(){
        return chunk;
    }

    public int getSize() {
        return size;
    }

    public static void setSize(int size) {
        Request.size = size;
    }
}

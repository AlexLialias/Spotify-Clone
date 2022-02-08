package com.example.musicapp;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

public class ConsumerImpl{
    private static Socket requestSocket = null;
    private static ObjectOutputStream out = null;
    private static ObjectInputStream in = null;

    public static List<BrokerImpl> brokers = new ArrayList<>();
    public static Request r = new Request();
    private static int hashBroker2;
    private static int hashBroker3;

    public static String tosend;
    public static ArrayList<String> Artist = new ArrayList();
    public static ArrayList<String> SongList = new ArrayList();
    public static String songRequest ;
    public  static ArrayList<byte[]> chunk = new ArrayList<byte[]>();
    public static int glsize;
    public ConsumerImpl() {}



    /**Connects to Broker 1 and receives information about broker2 and Broker 3, then calls register()*/
    public static void init() {
        try {
            Message success;
            Message request;
            requestSocket = new Socket("192.168.1.7", 6000);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            for(int i=0; i<3; i++){
                request = (Message)in.readObject();
                brokers.add(new BrokerImpl(request.getIP(),request.getPort(),request.getID()));
                success = new Message("Broker: " +brokers.get(0).id + " received");
                out.writeObject(success);
            }

            hashBroker2 = ((Message)in.readObject()).getNumbers();
            hashBroker3 = ((Message)in.readObject()).getNumbers();

            System.out.println("Consumer connected to broker");
            disconnect(in, out);

            register();
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
        }
    }

    /**Asks the user for the artist name he wants, compares the hashes of the name and each of the brokers(IP+Port) hash, then corrects to the correct one*/
    public static void register() {
        Scanner scn = new Scanner(System.in);
        System.out.print("Enter Artist Name or type Exit: ");
        //tosend = r.getRequest();
        while (tosend==null){
            //tosend = r.request;

        }
        System.out.print(tosend);
        if(tosend.equalsIgnoreCase("exit")){
            System.out.println("Goodbye!");
            exit(0);
        }

        BrokerImpl b2 = brokers.get(1);
        BrokerImpl b3 = brokers.get(2);

        //now we have to connect with the correct broker

        if (getMd5(tosend) < hashBroker2%100)
        {
            connect(b2, tosend);
        }else {
            connect(b3, tosend);
        }

    }

    /**Performs all the actions required to provide the user the song he wants*/
    public static void connect(BrokerImpl b, String artistName) {
        try{

            requestSocket = new Socket("192.168.1.7", b.port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            Scanner scn = new Scanner(System.in);
            Message msg = new Message(artistName);
            out.writeObject(msg);

            ArrayList<PublisherImpl> pub;
            pub = ((Message)in.readObject()).getPub();
            System.out.println("Number of available artists: "+pub.size()+artistName);

            String accepted = ((Message) in.readObject()).getMessage();
            msg = new Message("Artist availability received");
            out.writeObject(msg);

            boolean check = false;

            for(int i=0; i<pub.size(); i++){

                for (int j=0;j<pub.get(i).availableArtists.size();j++){
                    if(artistName.equals(pub.get(i).availableArtists.get(j)))
                        check=true;
                }
            }
            if(check){
                Artist.add(artistName);
            }else{
                for(int i=0; i<pub.size(); i++){
                    for (int j=0;j<pub.get(i).availableArtists.size();j++){
                        Artist.add(pub.get(i).availableArtists.get(j));
                    }
                }
            }

            if(accepted.equalsIgnoreCase("exists")) {
                System.out.println("Artist "+ accepted + "\nList of available songs: ");
                List<String> songNames;
                songNames = ((Message) in.readObject()).getSongnames();
                System.out.println(songNames);
                for(int i = 0;i<songNames.size();i++){
                    SongList.add(songNames.get(i));
                }

                boolean flag = false;

                    System.out.print("Request a song: ");

                    if(songNames.contains(songRequest)){
                        msg = new Message(songRequest);
                        out.writeObject(msg);                   //sends the name of the song the user requested
                        int size = ((Message) in.readObject()).getNumbers();
                        glsize=size;
                        for (int i = 0; i<size; i++){
                            //try (FileOutputStream stream = new FileOutputStream("musicDownloads\\" + songRequest + "_" + i + ".mp3")) {
                                Message info = ((Message) in.readObject());
                                //stream.write(info.getSongChunk());
                                 chunk.add(info.getSongChunk());
                           /* } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                        }
                       /* MusicFile search = new MusicFile(new File("musicDownloads\\" + songRequest + "_0.mp3"));
                        for(int i = 0; i< size; i++){
                            try {
                                Mp3File mp3File = new Mp3File("musicDownloads\\" + songRequest + "_" + i + ".mp3");
                                ID3v2 id3v2Tag = new ID3v24Tag(); mp3File.setId3v2Tag(id3v2Tag);
                                id3v2Tag.setArtist(search.getArtistName());
                                id3v2Tag.setTitle(search.getTrackName());
                                id3v2Tag.setAlbum(search.getAlbumInfo());
                                mp3File.save("musicDownloads\\" + songRequest + "-" + i + ".mp3");
                            } catch (UnsupportedTagException | InvalidDataException | NotSupportedException e) {
                                e.printStackTrace();
                            }
                            File file = new File("musicDownloads\\" + songRequest + "_" + i + ".mp3");
                            file.delete();
                        }*/
                    }else{
                        System.out.println("Unknown song name. Please try again");
                    }

            }else{

            }
            //register();
        } catch (UnknownHostException unknownHost) {
           System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
        }
    }

    /**Closes the connection*/
    private static void disconnect(ObjectInputStream in,ObjectOutputStream out) {
        try {
            requestSocket.close();
            in.close();
            out.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static void writeWholeSong(String songRequest, int size) throws IOException {
        FileOutputStream stream = new FileOutputStream("C:\\Sxoli\\Katanemimena\\SpotifyAPP\\src\\com\\example\\musicDownloads\\" + songRequest + ".mp3");
        byte[] arr = new byte[0];
        for (int i = 0; i<size; i++){
            try {
                Message info = ((Message) in.readObject());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write( arr );
                outputStream.write( info.getSongChunk() );

                arr = outputStream.toByteArray( );
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        stream.write(arr);
        stream.flush();
    }

    public static void main(String[] args) {
        init();
    }

    private static String getSystemIP()
    {
        String current_ip = null;
        try(final DatagramSocket socket = new DatagramSocket())
        {
            socket.connect(InetAddress.getByName("1.1.1.1"), 10002);
            current_ip = socket.getLocalAddress().getHostAddress();
        }
        catch (SocketException | UnknownHostException e)
        {
            e.printStackTrace();
        }
        return current_ip;
    }

    public static int getMd5(String input)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32)
            {
                hashtext = "0" + hashtext;
            }
            int md5Dec = Integer.parseInt(hashtext.substring(0, 5), 16)%100;
            return md5Dec;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ResetChunks(){
        chunk.clear();
    }

    public static void ResetSongs(){
        SongList.clear();
        songRequest=null;
    }

    public static void ResetArtists(){
        Artist.clear();
        tosend = null;
    }
}

package com.funstudio.propermusicplayer.models;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private ArrayList<Song> albumSongs;
    private  String albumName;

    public Album(String name, ArrayList<Song> songs)
    {
        albumName = name;
        albumSongs = songs;

    }

    public  String getAlbumName()
    {
        return albumName;
    }

    public void setAlbumName(String name)
    {
        albumName  = name;
    }

    public  void setSongsInAlbum(ArrayList<Song> songs)
    {
        albumSongs = songs;
    }

    public  ArrayList<Song> getSongsInAlbum()
    {
        return  albumSongs;
    }
}

package com.funstudio.propermusicplayer.models;

import android.graphics.Bitmap;

public class Song {


    private  String songTitle;

    private   long duration;

    private    String albumName;
    private  String artistName;
    private long id;
    private  String path;
    private int trackNumber;


    public Song() {
        this.id = -1;
        this.path = "";
        songTitle = "";
        this.artistName = "";
        this.albumName = "";
        this.duration = -1;
        trackNumber = -1;

    }

    public Song(long _id, String _title, String _artistName, String _albumName, long duration,String path) {
        this.id = _id;
        songTitle = _title;
        this.artistName = _artistName;
        this.albumName = _albumName;
        this.duration = duration;

        this.path = path;
    }
    public String getAlbum() {
        return albumName;
    }

    public void setAlbum(String album) {
        this.albumName = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }
    public String getTitle() {
        return songTitle;
    }

    public void setTitle(String title) {
        songTitle= title;
    }

    public String getArtist() {
        return artistName;
    }

    public void setArtist(String artist) {
        artistName = artist;
    }
}

package com.funstudio.propermusicplayer.comparators;


import com.funstudio.propermusicplayer.models.Song;

import java.util.Comparator;

public class SongComparator implements Comparator<Song> {

    @Override
    public int compare(Song song1, Song song2) {
        return song1.getTitle().toString().compareTo(song2.getTitle().toString());
    }
}

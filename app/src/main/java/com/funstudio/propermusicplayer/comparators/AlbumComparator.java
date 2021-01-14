package com.funstudio.propermusicplayer.comparators;

import com.funstudio.propermusicplayer.models.Album;

import java.util.Comparator;

public class AlbumComparator implements Comparator<Album>
{

    @Override
    public int compare(Album album1, Album album2) {
        return album1.getAlbumName().toString().compareTo(album2.getAlbumName().toString());
    }
}

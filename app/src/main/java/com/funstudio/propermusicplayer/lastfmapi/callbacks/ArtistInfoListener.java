
package com.funstudio.propermusicplayer.lastfmapi.callbacks;

import com.funstudio.propermusicplayer.lastfmapi.models.LastfmArtist;

public interface ArtistInfoListener {

    void artistInfoSucess(LastfmArtist artist);

    void artistInfoFailed();

}

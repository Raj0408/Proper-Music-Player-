package com.funstudio.propermusicplayer.fragments;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.funstudio.propermusicplayer.interfaces.MusicStateListener;
import com.funstudio.propermusicplayer.view.PlayPauseButton;
import com.funstudio.propermusicplayer.view.PlayPauseDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.steamcrafted.materialiconlib.MaterialIconView;

public class BaseNowplayingFragment extends Fragment  implements MusicStateListener {

    private MaterialIconView previous, next;
    private PlayPauseButton mPlayPause;
    private PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    private FloatingActionButton playPauseFloating;
    private View playPauseWrapper;

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {

    }
}

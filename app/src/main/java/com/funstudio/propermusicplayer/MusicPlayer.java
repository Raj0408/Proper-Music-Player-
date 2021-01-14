package com.funstudio.propermusicplayer;

import android.os.RemoteException;

public class MusicPlayer {

public static  int Queuepos;
    public static int getQueuePosition() {

        return Queuepos;
    }

    public static void setQueuePosition(final int position) {
      Queuepos = position;
        }



    }



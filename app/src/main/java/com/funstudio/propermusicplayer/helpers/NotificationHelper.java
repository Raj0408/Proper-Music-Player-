package com.funstudio.propermusicplayer.helpers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public final class NotificationHelper {
    public static final int INTENT_VIEW_ID = 0x7f0f0200;
    public static final String SEEK_COUNT_EXTRA = "new_queue_position";
    public static final String TRACK_INFO_EXTRA = "track_data";
    public static final String INTENT_ACTION = "SWITCH_TRACK";
    public static final String REPLY_INTENT_EXTRA = "reply";
    public static final String CURRENT_PLAYING_POSITION_EXTRA = "current_queue_position";

    private NotificationHelper() {
    }

    public static boolean isSupported(Notification notification) {
        try {
            notification.bigContentView.setIntent(INTENT_VIEW_ID, "resolveIntent", new Intent());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static void insertToNotification(Notification notification, ArrayList<Bundle> list, Context musicPlaybackService, int currentQueuePosition)  {
        try {
            Intent data = new Intent();
            data.putParcelableArrayListExtra(TRACK_INFO_EXTRA, list);
            Intent reply = new Intent(INTENT_ACTION).setClass(musicPlaybackService, musicPlaybackService.getClass());
            data.putExtra(REPLY_INTENT_EXTRA, PendingIntent.getService(musicPlaybackService, 0, reply, PendingIntent.FLAG_UPDATE_CURRENT));
            data.putExtra(CURRENT_PLAYING_POSITION_EXTRA, currentQueuePosition);
            notification.bigContentView.setIntent(INTENT_VIEW_ID, "resolveIntent", data);
        } catch (Throwable t) {

        }
    }

    /**
     * @param intent intent supplied to {@link android.app.Service#onStartCommand(Intent, int, int)}
     * @return count the music play should switch to.
     * <p/>
     * <p>Result is:
     * <table>
     * <tr>
     * <td>0</td>
     * <td>if it shouldn't switch at all</td>
     * </tr>
     * <tr>
     * <td><0</td>
     * <td>if it should switch backwards</td>
     * </tr>
     * <tr>
     * <td>>0</td>
     * <td>if it should switch forwards</td>
     * </tr>
     * </table>
     */
    public static int getPosition(Intent intent) {
        if (checkIntent(intent)) {
            return intent.getIntExtra(SEEK_COUNT_EXTRA, 0);
        } else {
            return 0;
        }
    }

    /**
     * @param intent intent supplied to {@link android.app.Service#onStartCommand(Intent, int, int)}
     * @return true if the intent was a track switching action and should be handled
     */
    public static boolean checkIntent(Intent intent) {
        return intent != null && intent.getAction() != null && intent.getAction().equals(INTENT_ACTION);
    }
}

package  com.funstudio.propermusicplayer.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.funstudio.propermusicplayer.helpers.MediaButtonIntentReceiver;
import com.funstudio.propermusicplayer.interfaces.IProperService;
import com.funstudio.propermusicplayer.interfaces.MusicPlaybackTrack;
import com.funstudio.propermusicplayer.models.Song;
import com.funstudio.propermusicplayer.utils.TimberUtils;
import com.funstudio.propermusicplayer.utils.TimberUtils.IdType;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

public class MusicService extends Service implements  IProperService
{

    private WakeLock mWakeLock;

    public static boolean isSongPlaying ;
    public static final String PLAYSTATE_CHANGED = "com.naman14.timber.playstatechanged";
    public static final String POSITION_CHANGED = "com.naman14.timber.positionchanged";
    public static final String META_CHANGED = "com.naman14.timber.metachanged";
    public static final String QUEUE_CHANGED = "com.naman14.timber.queuechanged";
    public static final String PLAYLIST_CHANGED = "com.naman14.timber.playlistchanged";
    public static final String REPEATMODE_CHANGED = "com.naman14.timber.repeatmodechanged";
    public static final String SHUFFLEMODE_CHANGED = "com.naman14.timber.shufflemodechanged";
    public static final String TRACK_ERROR = "com.naman14.timber.trackerror";
    public static final String TIMBER_PACKAGE_NAME = "com.naman14.timber";
    public static final String MUSIC_PACKAGE_NAME = "com.android.music";
    public static final String SERVICECMD = "com.naman14.timber.musicservicecommand";
    public static final String TOGGLEPAUSE_ACTION = "com.naman14.timber.togglepause";
    public static final String PAUSE_ACTION = "com.naman14.timber.pause";
    public static final String STOP_ACTION = "com.naman14.timber.stop";
    public static final String PREVIOUS_ACTION = "com.naman14.timber.previous";
    public static final String PREVIOUS_FORCE_ACTION = "com.naman14.timber.previous.force";
    public static final String NEXT_ACTION = "fcom.naman14.timber.next";
    public static final String REPEAT_ACTION = "com.naman14.timber.repeat";
    public static final String SHUFFLE_ACTION = "com.naman14.timber.shuffle";
    public static final String FROM_MEDIA_BUTTON = "frommediabutton";
    public static final String REFRESH = "com.naman14.timber.refresh";
    public static final String UPDATE_LOCKSCREEN = "com.naman14.timber.updatelockscreen";
    public static final int NEXT = 2;
    public static final int LAST = 3;
    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;
    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;
    public static final int MAX_HISTORY_SIZE = 1000;
    private static final String TAG = "MusicPlaybackService";
    private static final boolean D = false;
    private static final String SHUTDOWN = "com.naman14.timber.shutdown";
    private static final int IDCOLIDX = 0;
    private static final int TRACK_ENDED = 1;
    private static final int TRACK_WENT_TO_NEXT = 2;
    private static final int RELEASE_WAKELOCK = 3;
    private static final int SERVER_DIED = 4;
    private static final int FOCUSCHANGE = 5;
    private static final int FADEDOWN = 6;
    private static final int FADEUP = 7;
    private static final int IDLE_DELAY = 5 * 60 * 1000;
    private static final long REWIND_INSTEAD_PREVIOUS_THRESHOLD = 3000;
    private Cursor mCursor;
    private Cursor mAlbumCursor;
    private AudioManager mAudioManager;
    private SharedPreferences mPreferences;
    private boolean mServiceInUse = false;
    private boolean mIsSupposedToBePlaying = false;
    private long mLastPlayedTime;
    //private int mNotifyMode = NOTIFY_MODE_NONE;
    private long mNotificationPostTime = 0;
    private boolean mQueueIsSaveable = true;
    private boolean mPausedByTransientLossOfFocus = false;
    private boolean mShowAlbumArtOnLockscreen;

    private int mCardId;

    private int mPlayPos = -1;

    private int mNextPlayPos = -1;

    private int mOpenFailedCounter = 0;

    private int mMediaMountedCount = 0;

    private int mShuffleMode = SHUFFLE_NONE;

    private int mRepeatMode = REPEAT_NONE;

    private int mServiceStartId = -1;

    private ArrayList<MusicPlaybackTrack> mPlaylist = new ArrayList<MusicPlaybackTrack>(100);

    private long[] mAutoShuffleList = null;

    private MusicPlayerHandler mPlayerHandler;
    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(final int focusChange) {
            mPlayerHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };

    private static final String[] PROJECTION = new String[]{
            "audio._id AS _id", MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
    };
    private static final String[] ALBUM_PROJECTION = new String[]{
            MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.LAST_YEAR
    };

    private static LinkedList<Integer> mHistory = new LinkedList<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (D) Log.d(TAG, "Service bound, intent = " + intent);
        //cancelShutdown();
        return null;
    }

    @Override
    public void openFile(String path) throws RemoteException {

    }

    @Override
    public void open(long[] list, int position, long sourceId, int sourceType) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void play() {

    }

    public void prev(boolean forcePrevious) {
        synchronized (this) {
            boolean goPrevious = getRepeatMode() != REPEAT_CURRENT &&
                    (position() < REWIND_INSTEAD_PREVIOUS_THRESHOLD || forcePrevious);

            if (goPrevious) {
                if (D) Log.d(TAG, "Going to previous track");
                int pos = getPreviousPlayPosition(true);

                if (pos < 0) {
                    return;
                }
                mNextPlayPos = mPlayPos;
                mPlayPos = pos;
                stop(false);
                openCurrent();
                play(false);
                notifyChange(META_CHANGED);
            } else {
                if (D) Log.d(TAG, "Going to beginning of track");
                seek(0);
                play(false);
            }
        }
    }


    private void stop(final boolean goToIdle) {
        if (D) Log.d(TAG, "Stopping playback, goToIdle = " + goToIdle);
        long duration = this.duration();
        long position = this.position();
        if (duration > 30000 && (position >= duration / 2) || position > 240000) {
          //  scrobble();
        }
/*
        if (mPlayer.isInitialized()) {
            mPlayer.stop();
        }*/
       /* mFileToPlay = null;
        closeCursor();
        if (goToIdle) {
            setIsSupposedToBePlaying(false, false);
        } else {
            if (TimberUtils.isLollipop())
                stopForeground(false);
            else stopForeground(true);
        }*/
    }
    @Override
    public void next() {

    }

    @Override
    public void enqueue(long[] list, int action, long sourceId, int sourceType) {

    }

    @Override
    public void setQueuePosition(int index) {

    }

    @Override
    public void setShuffleMode(int shufflemode) {

    }

    @Override
    public void setRepeatMode(int repeatmode) {

    }


    public int getPreviousPlayPosition(boolean removeFromHistory) {
        synchronized (this) {
            if (mShuffleMode == SHUFFLE_NORMAL) {

                final int histsize = mHistory.size();
                if (histsize == 0) {
                    return -1;
                }
                final Integer pos = mHistory.get(histsize - 1);
                if (removeFromHistory) {
                    mHistory.remove(histsize - 1);
                }
                return pos.intValue();
            } else {
                if (mPlayPos > 0) {
                    return mPlayPos - 1;
                } else {
                    return mPlaylist.size() - 1;
                }
            }
        }
    }

    private void notifyChange(final String what) {
        if (D) Log.d(TAG, "notifyChange: what = " + what);

        // Update the lockscreen controls
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // updateMediaSession(what);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //updateRemoteControlClient(what);
        }
        if (what.equals(POSITION_CHANGED)) {
            return;
        }

        final Intent intent = new Intent(what);
        intent.putExtra("id", getAudioId());
        intent.putExtra("artist", getArtistName());
        intent.putExtra("album", getAlbumName());
        intent.putExtra("albumid", getAlbumId());
        intent.putExtra("track", getTrackName());
        intent.putExtra("playing", isPlaying());

        sendStickyBroadcast(intent);

        final Intent musicIntent = new Intent(intent);
        musicIntent.setAction(what.replace(TIMBER_PACKAGE_NAME, MUSIC_PACKAGE_NAME));
        sendStickyBroadcast(musicIntent);

        if (what.equals(META_CHANGED)) {

           // mRecentStore.addSongId(getAudioId());
          //  mSongPlayCount.bumpSongCount(getAudioId());

        } else if (what.equals(QUEUE_CHANGED)) {
            //saveQueue(true);
            if (isPlaying()) {

                if (mNextPlayPos >= 0 && mNextPlayPos < mPlaylist.size()
                        && getShuffleMode() != SHUFFLE_NONE) {
                   // setNextTrack(mNextPlayPos);
                } else {
                  //  setNextTrack();
                }
            }
        } else {
           // saveQueue(false);
        }

        if (what.equals(PLAYSTATE_CHANGED)) {
           // updateNotification();
        }

    }
    private void openCurrent() {
        //openCurrentAndMaybeNext(false);
    }

    public void moveQueueItem(int index1, int index2) {
        synchronized (this) {
            if (index1 >= mPlaylist.size()) {
                index1 = mPlaylist.size() - 1;
            }
            if (index2 >= mPlaylist.size()) {
                index2 = mPlaylist.size() - 1;
            }

            if (index1 == index2) {
                return;
            }

            final MusicPlaybackTrack track = mPlaylist.remove(index1);
            if (index1 < index2) {
                mPlaylist.add(index2, track);
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index1 && mPlayPos <= index2) {
                    mPlayPos--;
                }
            } else if (index2 < index1) {
                mPlaylist.add(index2, track);
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index2 && mPlayPos <= index1) {
                    mPlayPos++;
                }
            }
           // notifyChange(QUEUE_CHANGED);
        }
    }

    @Override
    public void refresh() {

    }

    public void play(boolean createNewNextTrack) {
        int status = mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (D) Log.d(TAG, "Starting playback: audio focus request status = " + status);

        if (status != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }

        final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(intent);

        mAudioManager.registerMediaButtonEventReceiver(new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName()));
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mSession.setActive(true);

        if (createNewNextTrack) {
            setNextTrack();
        } else {
            setNextTrack(mNextPlayPos);
        }

        if (mPlayer.isInitialized()) {
            final long duration = mPlayer.duration();
            if (mRepeatMode != REPEAT_CURRENT && duration > 2000
                    && mPlayer.position() >= duration - 2000) {
                gotoNext(true);
            }

            mPlayer.start();
            mPlayerHandler.removeMessages(FADEDOWN);
            mPlayerHandler.sendEmptyMessage(FADEUP);

            setIsSupposedToBePlaying(true, true);

            cancelShutdown();
            updateNotification();
            notifyChange(META_CHANGED);
        } else if (mPlaylist.size() <= 0) {
            setShuffleMode(SHUFFLE_AUTO);
        }*/
    }

    public void enqueue(final long[] list, final int action, long sourceId, IdType sourceType) {
        synchronized (this) {
            if (action == NEXT && mPlayPos + 1 < mPlaylist.size()) {
                //addToPlayList(list, mPlayPos + 1, sourceId, sourceType);
                mNextPlayPos = mPlayPos + 1;
               // notifyChange(QUEUE_CHANGED);
            } else {
              //  addToPlayList(list, Integer.MAX_VALUE, sourceId, sourceType);
               // notifyChange(QUEUE_CHANGED);
            }

            if (mPlayPos < 0) {
                mPlayPos = 0;
               // openCurrentAndNext();
                play();
             //   notifyChange(META_CHANGED);
            }
        }
    }
    public void playlistChanged() {
        //notifyChange(PLAYLIST_CHANGED);
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public long[] getQueue() {
        return new long[0];
    }

    @Override
    public long getQueueItemAtPosition(int position) {
        return 0;
    }

    @Override
    public int getQueueSize() {
        return 0;
    }

    @Override
    public int getQueuePosition() {
        return 0;
    }

    @Override
    public int getQueueHistoryPosition(int position) {
        return 0;
    }

    @Override
    public int getQueueHistorySize() {
        return 0;
    }

    @Override
    public int[] getQueueHistoryList() {
        return new int[0];
    }

    @Override
    public long duration() {
        return 0;
    }

    @Override
    public long position() {
        return 0;
    }

    @Override
    public long seek(long pos) {
        return 0;
    }

    @Override
    public void seekRelative(long deltaInMs) {

    }

    @Override
    public long getAudioId() {
        return 0;
    }

    @Override
    public MusicPlaybackTrack getCurrentTrack() {
        return null;
    }

    @Override
    public MusicPlaybackTrack getTrack(int index) {
        return null;
    }

    @Override
    public long getNextAudioId() {
        return 0;
    }

    @Override
    public long getPreviousAudioId() {
        return 0;
    }

    @Override
    public long getArtistId() {
        return 0;
    }

    @Override
    public long getAlbumId() {
        return 0;
    }

    @Override
    public String getArtistName() {
        return null;
    }

    @Override
    public String getTrackName() {
        return null;
    }

    @Override
    public String getAlbumName() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public int getShuffleMode() {
        return 0;
    }

    @Override
    public int removeTracks(int first, int last) {
        return 0;
    }

    @Override
    public int removeTrack(long id) {
        return 0;
    }

    @Override
    public boolean removeTrackAtPosition(long id, int position) {
        return false;
    }

    @Override
    public int getRepeatMode() {
        return 0;
    }

    @Override
    public int getMediaMountedCount() {
        return 0;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public interface TrackErrorExtra {

        String TRACK_NAME = "trackname";
    }

    private static final class TrackErrorInfo {
        public long mId;
        public String mTrackName;

        public TrackErrorInfo(long id, String trackName) {
            mId = id;
            mTrackName = trackName;
        }
    }

    private void cycleShuffle() {
        if (mShuffleMode == SHUFFLE_NONE) {
            setShuffleMode(SHUFFLE_NORMAL);
//            if (mRepeatMode == REPEAT_CURRENT) {
//                setRepeatMode(REPEAT_ALL);
//            }
        } else if (mShuffleMode == SHUFFLE_NORMAL || mShuffleMode == SHUFFLE_AUTO) {
            setShuffleMode(SHUFFLE_NONE);
        }
    }
    private static final class ServiceStub implements IProperService {

        private final WeakReference<MusicService> mService;

        private ServiceStub(final MusicService service) {
            mService = new WeakReference<MusicService>(service);
        }


        @Override
        public void openFile(final String path) throws RemoteException {
            mService.get().openFile(path);
        }

        @Override
        public void open(final long[] list, final int position, long sourceId, int sourceType)
               {
           // mService.get().open(list, position, sourceId, IdType.getTypeById(sourceType));
        }

        @Override
        public void stop(){
            mService.get().stop();
        }

        @Override
        public void pause() {
            mService.get().pause();
        }


        @Override
        public void play() {
            mService.get().play();
        }

        @Override
        public void prev(boolean forcePrevious)  {
            mService.get().prev(forcePrevious);
        }

        @Override
        public void next()  {
          //  mService.get().gotoNext(true);
        }

        @Override
        public void enqueue(final long[] list, final int action, long sourceId, int sourceType)
               {
            mService.get().enqueue(list, action, sourceId,IdType.getTypeById(sourceType));
        }

        @Override
        public void moveQueueItem(final int from, final int to) {
            mService.get().moveQueueItem(from, to);
        }

        @Override
        public void refresh()  {
            mService.get().refresh();
        }

        @Override
        public void playlistChanged(){
            mService.get().playlistChanged();
        }

        @Override
        public boolean isPlaying() {
            return mService.get().isPlaying();
        }

        @Override
        public long[] getQueue() {
            return mService.get().getQueue();
        }

        @Override
        public long getQueueItemAtPosition(int position)  {
            return mService.get().getQueueItemAtPosition(position);
        }

        @Override
        public int getQueueSize() {
            return mService.get().getQueueSize();
        }

        @Override
        public int getQueueHistoryPosition(int position)  {
            return mService.get().getQueueHistoryPosition(position);
        }

        @Override
        public int getQueueHistorySize() {
            return mService.get().getQueueHistorySize();
        }

        @Override
        public int[] getQueueHistoryList()  {
            return mService.get().getQueueHistoryList();
        }

        @Override
        public long duration(){
            return mService.get().duration();
        }

        @Override
        public long position()  {
            return mService.get().position();
        }

        @Override
        public long seek(final long position)  {
            return mService.get().seek(position);
        }

        @Override
        public void seekRelative(final long deltaInMs)  {
            mService.get().seekRelative(deltaInMs);
        }

        @Override
        public long getAudioId()  {
            return mService.get().getAudioId();
        }

        @Override
        public MusicPlaybackTrack getCurrentTrack() {
            return mService.get().getCurrentTrack();
        }

        @Override
        public MusicPlaybackTrack getTrack(int index)  {
            return mService.get().getTrack(index);
        }

        @Override
        public long getNextAudioId() {
            return mService.get().getNextAudioId();
        }

        @Override
        public long getPreviousAudioId() {
            return mService.get().getPreviousAudioId();
        }

        @Override
        public long getArtistId() {
            return mService.get().getArtistId();
        }

        @Override
        public long getAlbumId()  {
            return mService.get().getAlbumId();
        }

        @Override
        public String getArtistName() {
            return mService.get().getArtistName();
        }

        @Override
        public String getTrackName()  {
            return mService.get().getTrackName();
        }

        @Override
        public String getAlbumName()  {
            return mService.get().getAlbumName();
        }

        @Override
        public String getPath()  {
            return mService.get().getPath();
        }

        @Override
        public int getQueuePosition() {
            return mService.get().getQueuePosition();
        }

        @Override
        public void setQueuePosition(final int index)  {
            mService.get().setQueuePosition(index);
        }

        @Override
        public int getShuffleMode()  {
            return mService.get().getShuffleMode();
        }

        @Override
        public void setShuffleMode(final int shufflemode) {
            mService.get().setShuffleMode(shufflemode);
        }

        @Override
        public int getRepeatMode()  {
            return mService.get().getRepeatMode();
        }

        @Override
        public void setRepeatMode(final int repeatmode)  {
            mService.get().setRepeatMode(repeatmode);
        }

        @Override
        public int removeTracks(final int first, final int last) {
            return mService.get().removeTracks(first, last);
        }


        @Override
        public int removeTrack(final long id)  {
            return mService.get().removeTrack(id);
        }


        @Override
        public boolean removeTrackAtPosition(final long id, final int position)
                {
            return mService.get().removeTrackAtPosition(id, position);
        }


        @Override
        public int getMediaMountedCount() {
            return mService.get().getMediaMountedCount();
        }


        @Override
        public int getAudioSessionId() {
            return mService.get().getAudioSessionId();
        }

    }

    private static final class MultiPlayer implements MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener {

        private final WeakReference<MusicService> mService;

        private MediaPlayer mCurrentMediaPlayer = new MediaPlayer();

        private MediaPlayer mNextMediaPlayer;

        private Handler mHandler;

        private boolean mIsInitialized = false;

        private String mNextMediaPath;


        public MultiPlayer(final MusicService service) {
            mService = new WeakReference<MusicService>(service);
            mCurrentMediaPlayer.setWakeMode( mService.get(), PowerManager.PARTIAL_WAKE_LOCK);

        }


        public void setDataSource(final String path) {
            try {
                mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
                if (mIsInitialized) {
                    setNextDataSource(null);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }


        private boolean setDataSourceImpl(final MediaPlayer player, final String path) {
            try {
                player.reset();
                player.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
                    player.setDataSource(mService.get(), Uri.parse(path));
                } else {
                    player.setDataSource(path);
                }
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);

                player.prepare();
            } catch (final IOException | IllegalArgumentException todo) {

                return false;
            }
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            return true;
        }


        public void setNextDataSource(final String path) {
            mNextMediaPath = null;
            try {
                mCurrentMediaPlayer.setNextMediaPlayer(null);
            } catch (IllegalArgumentException e) {
                Log.i(TAG, "Next media player is current one, continuing");
            } catch (IllegalStateException e) {
                Log.e(TAG, "Media player not initialized!");
                return;
            }
            if (mNextMediaPlayer != null) {
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
            if (path == null) {
                return;
            }
            mNextMediaPlayer = new MediaPlayer();
            mNextMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
            mNextMediaPlayer.setAudioSessionId(getAudioSessionId());
            try {
                if (setDataSourceImpl(mNextMediaPlayer, path)) {
                    mNextMediaPath = path;
                    mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
                } else {
                    if (mNextMediaPlayer != null) {
                        mNextMediaPlayer.release();
                        mNextMediaPlayer = null;
                    }
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }


        public void setHandler(final Handler handler) {
            mHandler = handler;
        }


        public boolean isInitialized() {
            return mIsInitialized;
        }


        public void start() {
            mCurrentMediaPlayer.start();
        }


        public void stop() {
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
        }


        public void release() {
            mCurrentMediaPlayer.release();
        }


        public void pause() {
            mCurrentMediaPlayer.pause();
        }


        public long duration() {
            return mCurrentMediaPlayer.getDuration();
        }


        public long position() {
            return mCurrentMediaPlayer.getCurrentPosition();
        }


        public long seek(final long whereto) {
            mCurrentMediaPlayer.seekTo((int) whereto);
            return whereto;
        }


        public void setVolume(final float vol) {
            try {
                mCurrentMediaPlayer.setVolume(vol, vol);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        public int getAudioSessionId() {
            return mCurrentMediaPlayer.getAudioSessionId();
        }

        public void setAudioSessionId(final int sessionId) {
            mCurrentMediaPlayer.setAudioSessionId(sessionId);
        }

        @Override
        public boolean onError(final MediaPlayer mp, final int what, final int extra) {
            Log.w(TAG, "Music Server Error what: " + what + " extra: " + extra);
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    final MusicService service = mService.get();
                    final TrackErrorInfo errorInfo = new TrackErrorInfo(service.getAudioId(),
                            service.getTrackName());

                    mIsInitialized = false;
                    mCurrentMediaPlayer.release();
                    mCurrentMediaPlayer = new MediaPlayer();
                    mCurrentMediaPlayer.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);
                    Message msg = mHandler.obtainMessage(SERVER_DIED, errorInfo);
                    mHandler.sendMessageDelayed(msg, 2000);
                    return true;
                default:
                    break;
            }
            return false;
        }


        @Override
        public void onCompletion(final MediaPlayer mp) {
            if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = mNextMediaPlayer;
                mNextMediaPath = null;
                mNextMediaPlayer = null;
                mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
            } else {
                mService.get().mWakeLock.acquire(30000);
                mHandler.sendEmptyMessage(TRACK_ENDED);
                mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
            }
        }
    }



    private static final class Shuffler {

    private final LinkedList<Integer> mHistoryOfNumbers = new LinkedList<Integer>();

    private final TreeSet<Integer> mPreviousNumbers = new TreeSet<Integer>();

    private final Random mRandom = new Random();

    private int mPrevious;


    public Shuffler() {
        super();
    }


    public int nextInt(final int interval) {
        int next;
        do {
            next = mRandom.nextInt(interval);
        } while (next == mPrevious && interval > 1
                && !mPreviousNumbers.contains(Integer.valueOf(next)));
        mPrevious = next;
        mHistoryOfNumbers.add(mPrevious);
        mPreviousNumbers.add(mPrevious);
        cleanUpHistory();
        return next;
    }


    private void cleanUpHistory() {
        if (!mHistoryOfNumbers.isEmpty() && mHistoryOfNumbers.size() >= MAX_HISTORY_SIZE) {
            for (int i = 0; i < Math.max(1, MAX_HISTORY_SIZE / 2); i++) {
                mPreviousNumbers.remove(mHistoryOfNumbers.removeFirst());
            }
        }
    }
}

    private static final class MusicPlayerHandler extends Handler {
        private final WeakReference<MusicService> mService;
        private float mCurrentVolume = 1.0f;


        public MusicPlayerHandler(final MusicService service, final Looper looper) {
            super(looper);
            mService = new WeakReference<MusicService>(service);
        }


        @Override
        public void handleMessage(final Message msg) {
            final MusicService service = mService.get();
            if (service == null) {
                return;
            }

            synchronized (service) {
                switch (msg.what) {
                    case FADEDOWN:
                        mCurrentVolume -= .05f;
                        if (mCurrentVolume > .2f) {
                            sendEmptyMessageDelayed(FADEDOWN, 10);
                        } else {
                            mCurrentVolume = .2f;
                        }
                      //  service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case FADEUP:
                        mCurrentVolume += .01f;
                        if (mCurrentVolume < 1.0f) {
                            sendEmptyMessageDelayed(FADEUP, 10);
                        } else {
                            mCurrentVolume = 1.0f;
                        }
                      //  service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case SERVER_DIED:
                        if (service.isPlaying()) {
                            final TrackErrorInfo info = (TrackErrorInfo) msg.obj;
                           // service.sendErrorMessage(info.mTrackName);


                            service.removeTrack(info.mId);
                        } else {
                           // service.openCurrentAndNext();
                        }
                        break;
                    case TRACK_WENT_TO_NEXT:
                       // mService.get().scrobble();
                       // service.setAndRecordPlayPos(service.mNextPlayPos);
                       // service.setNextTrack();
                        if (service.mCursor != null) {
                            service.mCursor.close();
                            service.mCursor = null;
                        }
                        //service.updateCursor(service.mPlaylist.get(service.mPlayPos).mId);
                        service.notifyChange(META_CHANGED);
                        //service.updateNotification();
                        break;
                    case TRACK_ENDED:
                        if (service.mRepeatMode == REPEAT_CURRENT) {
                            service.seek(0);
                            service.play();
                        } else {
                           // service.gotoNext(false);
                        }
                        break;
                    case RELEASE_WAKELOCK:
                        service.mWakeLock.release();
                        break;
                    case FOCUSCHANGE:
                        if (D) Log.d(TAG, "Received audio focus change event " + msg.arg1);
                        switch (msg.arg1) {
                            case AudioManager.AUDIOFOCUS_LOSS:
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                if (service.isPlaying()) {
                                    service.mPausedByTransientLossOfFocus =
                                            msg.arg1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
                                }
                                service.pause();
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                removeMessages(FADEUP);
                                sendEmptyMessage(FADEDOWN);
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (!service.isPlaying()
                                        && service.mPausedByTransientLossOfFocus) {
                                    service.mPausedByTransientLossOfFocus = false;
                                    mCurrentVolume = 0f;
                                   // service.mPlayer.setVolume(mCurrentVolume);
                                    service.play();
                                } else {
                                    removeMessages(FADEDOWN);
                                    sendEmptyMessage(FADEUP);
                                }
                                break;
                            default:
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

}


package com.funstudio.propermusicplayer.activities;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.RenderScript;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.funstudio.propermusicplayer.MusicPlayer;
import com.funstudio.propermusicplayer.R;
import com.funstudio.propermusicplayer.adapters.SlidingQueueAdapter;
import com.funstudio.propermusicplayer.adapters.SongsAdapter;
import com.funstudio.propermusicplayer.loaders.ImageLoader;
import com.funstudio.propermusicplayer.models.Song;
import com.funstudio.propermusicplayer.view.CircularSeekBar;
import com.funstudio.propermusicplayer.view.PlayPauseButton;
import com.funstudio.propermusicplayer.view.PlayPauseDrawable;
import com.funstudio.propermusicplayer.view.TimelyView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Target;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class PlayerActivity extends AppCompatActivity {


    ImageView mBlurredArt;
    RecyclerView horizontalRecyclerview;
    SlidingQueueAdapter horizontalAdapter;

    public static  PlayerActivity playerInstance;
    int songPosition;
    private TextView songName,artistName;
    private static TextView duration,totalDuration;

    private ImageView playerAlbumArt;
    protected int value;
    protected  boolean place;
    private static  ImageView play_pause,prev,next;
    public SeekBar seekBar ;
    private boolean duetoplaypause = false;

    public ImageView albumart, shuffle, repeat;


    protected LinearLayout info_layout,player_controls;

    private static MediaPlayer MP;

    private PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    private MaterialIconView previous, next1;

    ImageLoader imageLoader;
    public  Song selectedSong;
    private TextView songtitle, songartist;
    private CircularSeekBar mCircularProgress;
    private int overflowcounter = 0;
public  boolean actPaused;

    private TimelyView timelyView11, timelyView12, timelyView13, timelyView14, timelyView15;
    private TextView hourColon;
    private int[] timeArr = new int[]{0, 0, 0, 0, 0};
    private Handler mElapsedTimeHandler;
    public RecyclerView recyclerView;
    private FloatingActionButton playPauseFloating;
    Context context;
     List<Song> songs;
    boolean isSongPlaying = false;

@Override
    protected  void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);
    imageLoader = new ImageLoader(this);


         Intent getStuff = getIntent();
         Bundle bundle = getStuff.getExtras();
         assert bundle != null;
         songPosition = bundle.getInt("index");
songs = SongsAdapter.mSongs;
selectedSong =songs.get(songPosition);

    initPlayer(songPosition);
setTheSongDetails();

mBlurredArt = findViewById(R.id.album_art_blurred);
horizontalRecyclerview = findViewById(R.id.queue_recyclerview_horizontal);

    setupHorizontalQueue();


      /*  playerInstance = this;







            Buttons();*/
}

    private void  setupHorizontalQueue() {
        horizontalRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        horizontalAdapter = new SlidingQueueAdapter(this, songs);
        horizontalRecyclerview.setAdapter(horizontalAdapter);
        horizontalRecyclerview.scrollToPosition(songPosition);

    }


    private class  setBlurredAlbumArt extends AsyncTask<Bitmap,Void,Drawable>
    {

        @Override
        protected Drawable doInBackground(Bitmap... loadedImage) {
            Drawable drawable = null;
            try{
                drawable = createBlurredImageFromBitmap(loadedImage[0],getApplicationContext(),6);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return  drawable;
        }

        @Override
        protected  void onPostExecute(Drawable result)
        {
            if (result != null) {
                if (mBlurredArt.getDrawable() != null) {
                    final TransitionDrawable td =
                            new TransitionDrawable(new Drawable[]{
                                    mBlurredArt.getDrawable(),
                                    result
                            });
                    mBlurredArt.setImageDrawable(td);
                    td.startTransition(200);

                } else {
                    mBlurredArt.setImageDrawable(result);
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    public static Drawable createBlurredImageFromBitmap(Bitmap bitmap, Context context, int inSampleSize) {

        RenderScript rs = RenderScript.create(context);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);

        final android.renderscript.Allocation input = android.renderscript.Allocation.createFromBitmap(rs, blurTemplate);
        final android.renderscript.Allocation output = android.renderscript.Allocation.createTyped(rs, input.getType());
        final android.renderscript.ScriptIntrinsicBlur script = android.renderscript.ScriptIntrinsicBlur.create(rs, android.renderscript.Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);

        return new BitmapDrawable(context.getResources(), blurTemplate);
    }
    public void setTheSongDetails()
{
    albumart = findViewById(R.id.album_art);
    shuffle =findViewById(R.id.shuffle);
    repeat = findViewById(R.id.repeat);
    next1 = findViewById(R.id.next);
    previous = findViewById(R.id.previous);
    playPauseFloating = findViewById(R.id.playpausefloating);



    songtitle = findViewById(R.id.song_title);
    songartist = findViewById(R.id.song_artist);


    timelyView11 = findViewById(R.id.timelyView11);
    timelyView12 = findViewById(R.id.timelyView12);
    timelyView13 =findViewById(R.id.timelyView13);
    timelyView14 = findViewById(R.id.timelyView14);
    timelyView15 = findViewById(R.id.timelyView15);
    hourColon = findViewById(R.id.hour_colon);


    mCircularProgress =findViewById(R.id.song_progress_circular);

   songtitle.setSelected(true);

    Toolbar toolbar = findViewById(R.id.toolbar);
    if(toolbar != null)
    {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");


    }

if(playPauseFloating != null)
{
    //
    playPauseFloating.setImageDrawable(playPauseDrawable);
    if(isSongPlaying)
    {
        playPauseDrawable.transformToPause(false);

    }else
    {
        playPauseDrawable.transformToPlay(false);
    }
}

if(mCircularProgress != null)
{
    mCircularProgress.setCircleProgressColor(android.R.color.white);
    mCircularProgress.setPointerColor(android.R.color.white);
    mCircularProgress.setPointerHaloColor(android.R.color.white);
}


    if (timelyView11 != null) {
        String time = makeShortTimeString(MP.getCurrentPosition()/ 1000);
        if (time.length() < 5) {
            timelyView11.setVisibility(View.GONE);
            timelyView12.setVisibility(View.GONE);
            hourColon.setVisibility(View.GONE);

            changeDigit(timelyView13, time.charAt(0) - '0');
            changeDigit(timelyView14, time.charAt(2) - '0');
            changeDigit(timelyView15, time.charAt(3) - '0');

        } else if (time.length() == 5) {
            timelyView12.setVisibility(View.VISIBLE);
            changeDigit(timelyView12, time.charAt(0) - '0');
            changeDigit(timelyView13, time.charAt(1) - '0');
            changeDigit(timelyView14, time.charAt(3) - '0');
            changeDigit(timelyView15, time.charAt(4) - '0');
        } else {
            timelyView11.setVisibility(View.VISIBLE);
            hourColon.setVisibility(View.VISIBLE);
            changeDigit(timelyView11, time.charAt(0) - '0');
            changeDigit(timelyView12, time.charAt(2) - '0');
            changeDigit(timelyView13, time.charAt(3) - '0');
            changeDigit(timelyView14, time.charAt(5) - '0');
            changeDigit(timelyView15, time.charAt(6) - '0');
        }
    }

setSongDetails();
}


private void setSongDetails()
{
    updateSongDetails();

   setSeekBarListener();

   next1.setOnClickListener(
           new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Handler handler = new Handler();
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           next();
                       }
                   },200);
               }
           }
   );
    previous.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   previous();
                    notifyPlayingDrawableChange();
                }
            }, 200);

        }
    });

    playPauseFloating.setOnClickListener(mFLoatingButtonListener);
}

public void notifyPlayingDrawableChange()
{

}

private final View.OnClickListener mFLoatingButtonListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        duetoplaypause = true;
        if(selectedSong == null)
        {

            Toast.makeText(getApplicationContext(), "No current track selected", Toast.LENGTH_SHORT).show();
        }else
        {
            playPauseDrawable.transformToPause(true);
            playPauseDrawable.transformToPlay(true);

            Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
play();
                        }
                    }, 250);

        }
    }
};
public  void previous()
{
    previous.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(songPosition == 0)
            {
                songPosition = songs.size()-1;
            }else
            {
                songPosition--;

            }
            initPlayer(songPosition);
            updateSongDetails();
        }
    });
}
public  void next()
{
    songPosition++;
    initPlayer(songPosition);
updateSongDetails();
}


public  void clickedsong( int songpos)
{
    initPlayer(songpos);
    updateSongDetails();
}

private  void setSeekBarListener()
{
    if(mCircularProgress != null)
    {
        mCircularProgress.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    int position = progress;
                    if (position < 0) {
                        position = 0;
                    }
                    MP.seekTo(position);
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
    }
}


public  void updateSongDetails()
{
    selectedSong =songs.get(songPosition);
    String songname = selectedSong.getTitle();
    String songArtist = selectedSong.getArtist();

    if(!duetoplaypause) {
        if (albumart != null) {
imageLoader.DisplayImage(selectedSong.getPath(), albumart);


        }
        doAlbumArtStuff(imageLoader.getBitmaps(selectedSong.getPath()));

        if (songtitle != null && songname != null) {
            songtitle.setText(selectedSong.getTitle());
            if (songname.length() <= 23) {
                songtitle.setTextSize(25);
            } else if (songname.length() >= 30) {
                songtitle.setTextSize(18);
            } else {
                songtitle.setTextSize(18 + (songname.length() - 24));
            }

        }
        songartist.setText(songArtist);

    }
    duetoplaypause = false;

    updatePlayPauseFloatingButton();

mCircularProgress.setMax((int)MP.getDuration());
if(mUpdateCircularProgress != null)
{
    mCircularProgress.removeCallbacks(mUpdateCircularProgress);

}
mCircularProgress.postDelayed(mUpdateCircularProgress, 10);
mElapsedTimeHandler = new Handler();
mElapsedTimeHandler.postDelayed(mUpdateElapsedTime,600);

}


public  void doAlbumArtStuff(Bitmap loadedImage)
{
    setBlurredAlbumArt blurredAlbumArt = new setBlurredAlbumArt();
    blurredAlbumArt.execute(loadedImage);
}

public  Runnable mUpdateCircularProgress = new Runnable() {
    @Override
    public void run() {
        long position = MP.getCurrentPosition();
        if(mCircularProgress != null)
        {
            mCircularProgress.setProgress((int)position);

        }
        overflowcounter --;
        int delay= 250;
        if(overflowcounter <0 && !actPaused)
        {
            overflowcounter++;
            mCircularProgress.postDelayed(mUpdateCircularProgress,delay);
        }

    }
};

public  Runnable mUpdateElapsedTime = new Runnable() {
    @Override
    public void run() {
        String time = makeShortTimeString(MP.getCurrentPosition()/1000);
        if(time.length() < 5)
        {
            timelyView11.setVisibility(View.GONE);
            timelyView12.setVisibility(View.GONE);
            hourColon.setVisibility(View.GONE);
            tv13(time.charAt(0) - '0');
            tv14(time.charAt(2) - '0');
            tv15(time.charAt(3) - '0');
        } else if (time.length() == 5) {
            timelyView12.setVisibility(View.VISIBLE);
            tv12(time.charAt(0) - '0');
            tv13(time.charAt(1) - '0');
            tv14(time.charAt(3) - '0');
            tv15(time.charAt(4) - '0');
        } else {
            timelyView11.setVisibility(View.VISIBLE);
            hourColon.setVisibility(View.VISIBLE);
            tv11(time.charAt(0) - '0');
            tv12(time.charAt(2) - '0');
            tv13(time.charAt(3) - '0');
            tv14(time.charAt(5) - '0');
            tv15(time.charAt(6) - '0');
        }
        mElapsedTimeHandler.postDelayed(this, 600);
    }
};
    public void updatePlayPauseFloatingButton() {
        if (!MP.isPlaying()) {
            playPauseDrawable.transformToPause(false);
        } else {
            playPauseDrawable.transformToPlay(false);

        }
    }
    public void changeDigit(TimelyView tv, int end) {
        ObjectAnimator obja = tv.animate(end);
        obja.setDuration(400);
        obja.start();
    }

    public void changeDigit(TimelyView tv, int start, int end) {
        try {
            ObjectAnimator obja = tv.animate(start, end);
            obja.setDuration(400);
            obja.start();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
    }

    public void tv11(int a) {
        if (a != timeArr[0]) {
            changeDigit(timelyView11, timeArr[0], a);
            timeArr[0] = a;
        }
    }

    public void tv12(int a) {
        if (a != timeArr[1]) {
            changeDigit(timelyView12, timeArr[1], a);
            timeArr[1] = a;
        }
    }

    public void tv13(int a) {
        if (a != timeArr[2]) {
            changeDigit(timelyView13, timeArr[2], a);
            timeArr[2] = a;
        }
    }

    public void tv14(int a) {
        if (a != timeArr[3]) {
            changeDigit(timelyView14, timeArr[3], a);
            timeArr[3] = a;
        }
    }

    public void tv15(int a) {
        if (a != timeArr[4]) {
            changeDigit(timelyView15, timeArr[4], a);
            timeArr[4] = a;
        }
    }
    public  final String makeShortTimeString(long secs )
    {
        long hours, mins;

        hours = secs/3600;
        secs %= 3600;
        mins = secs/60;
        secs %= 60;

        final  String durationFormat = this.getResources().getString(hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
return String.format(durationFormat,hours,mins,secs);
    }
public  void initPlayer(final int position)
{
    isSongPlaying = true;
    if(MP != null && MP.isPlaying())
    {
        MP.reset();
    }


try {


    MP = MediaPlayer.create(getApplicationContext(), Uri.parse(songs.get(position).getPath()));

    MP.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {

       MP.start();
        }
    });

    MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                   @Override
                                   public void onCompletion(MediaPlayer mp) {
                                       int curSongPostition = position;
                                       curSongPostition = (curSongPostition + 1) % (songs.size());
initPlayer(curSongPostition);
                                   }
                               }

    );
}catch (Exception ex)
{
    ex.printStackTrace();
}


}


public  void setData(int position)
{
    String name = songs.get(position).getTitle();
    String artist = songs.get(position).getArtist();
    songName.setText(name);
    artistName.setText(artist);

}





public  void play()
{
    if(MP != null && !MP.isPlaying())
    {
        isSongPlaying = true;
        MP.start();


    }
    else
    {
        pause();
    }
}

public  void pause()
{
    if(MP.isPlaying())
    {
        isSongPlaying = false;
        MP.pause();

    }
}
public  int getPosition()
{
    return songPosition;
}


@Override
public  void onPause()
{
    super.onPause();
    actPaused = true;
}

@Override
    public  void onResume()
{
    super.onResume();
    actPaused = false;
    if(mCircularProgress != null)
    {
        mCircularProgress.postDelayed(mUpdateCircularProgress, 10);
    }
}


}

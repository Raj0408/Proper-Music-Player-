package com.funstudio.propermusicplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import com.funstudio.propermusicplayer.activities.PlayerActivity;
import com.funstudio.propermusicplayer.comparators.AlbumComparator;
import com.funstudio.propermusicplayer.comparators.SongComparator;
import com.funstudio.propermusicplayer.fragments.AlbumSongsFragment;
import com.funstudio.propermusicplayer.fragments.AlbumsFragment;
import com.funstudio.propermusicplayer.fragments.MainFragment;
import com.funstudio.propermusicplayer.fragments.SongsFragment;
import com.funstudio.propermusicplayer.models.Album;
import com.funstudio.propermusicplayer.models.Song;
import com.funstudio.propermusicplayer.permissions.Nammu;
import com.funstudio.propermusicplayer.permissions.PermissionCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SongsFragment.onSongClickListener
, AlbumsFragment.onAlbumClickListener,
AlbumSongsFragment.callbackListenerForAlbum{
    public static ArrayList<Song> localsongs = new ArrayList<>();
    public static List<Album> albums = new ArrayList<>();

    public static ArrayList<ArrayList<Song>>arrayList = new ArrayList<>();


    public  static Album albumPosition;

    public  static  int SelectedSong;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Nammu.init(this);

checkPermissionAndThenLoad();

    }

    private  final PermissionCallback permissionReadStorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            }

        @Override
        public void permissionRefused() {
            finish();

        }


    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionAndThenLoad() {
        //check for permission
        if (Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            getSongs();

            MainFragment mainFragment = new MainFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction().add(R.id.container,mainFragment).commit();



        } else {
            if (Nammu.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Nammu.askForPermission(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},permissionReadStorageCallback);

            } else {
                Nammu.askForPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadStorageCallback);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public  void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults)
    {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    public  void getSongs()
    {
        localsongs.clear();
        albums.clear();

        ContentResolver musicResolver = this.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, MediaStore.MediaColumns.DATE_ADDED + " DESC");
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int songName = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songID = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songArtist = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int songPath = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songDuration = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);


            do {
                long id = musicCursor.getLong(songID);
                String name = musicCursor.getString(songName);
                String artist = musicCursor.getString(songArtist);
                String album = musicCursor.getString(songAlbum);
                String path = musicCursor.getString(songPath);
                long duration = musicCursor.getLong(songDuration);

                if (duration > 10000) {
                    Song aSong = new Song(id, name, artist, album, duration, path);
                    localsongs.add(aSong);


                    int pos;
                    if(album != null)
                    {
                        pos = checkAlbums(album);
                        if(pos != -1)
                        {
                            albums.get(pos).getSongsInAlbum().add(aSong);
                        }else {
                            ArrayList<Song> aSong2 = new ArrayList<>();
                            aSong2.add(aSong);
                            Album ab = new Album(album, aSong2);
                            albums.add(ab);
                        }
                    }
                }
            } while (musicCursor.moveToNext());


        }
        if (musicCursor != null)
            musicCursor.close();

        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        try {
            if(localsongs.size()>0)
            {
                Collections.sort(localsongs,new SongComparator());

            }

            if(albums.size() >0)
            {
                Collections.sort(albums,new AlbumComparator());
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }



    public int checkAlbums(String album)
    {
        for(int i = 0; i < albums.size(); i++)
        {
            Album album1 = albums.get(i);
            if(album1.getAlbumName().equals(album))
            {
                return i;
            }
        }
        return -1;
    }



    @Override
    public void onSongClicked(int songPosition) {

        if(songPosition>=0)
        {
            Intent intent = new Intent(this, PlayerActivity.class).putExtra("index", songPosition);
            startActivity(intent);
        }

    }

    @Override
    public void onAlbumClicked() {
        AlbumSongsFragment albumSongsFragment = new AlbumSongsFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, albumSongsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onAlbumSongClicked(int position) {
        if(position>=0)
        {
            Intent intent = new Intent(this, PlayerActivity.class).putExtra("index", position);
            startActivity(intent);
        }
    }
}
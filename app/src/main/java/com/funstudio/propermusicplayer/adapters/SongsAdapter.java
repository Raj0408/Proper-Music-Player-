package com.funstudio.propermusicplayer.adapters;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.funstudio.propermusicplayer.R;
import com.funstudio.propermusicplayer.interfaces.OnClickListener;
import com.funstudio.propermusicplayer.loaders.ImageLoader;
import com.funstudio.propermusicplayer.models.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsListViewHolder> {

private Context ctx;
public static ArrayList<Song> mSongs;
protected OnClickListener onClickListener;
ImageLoader imageLoader;



    public  SongsAdapter(@NonNull Context activity,ArrayList<Song> Songs)
    {
ctx = activity;
imageLoader = new ImageLoader(ctx);
mSongs = Songs;

    }




  public  Song getSongs(int position)
  {
      return mSongs.get(position);
  }

    @NonNull
    @Override
    public SongsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent,false);
        return new SongsListViewHolder(view,onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsListViewHolder holder, int position) {
 Song song = mSongs.get(position);
 holder.songName.setText(song.getTitle());
 holder.artistName.setText(song.getArtist());
      imageLoader.DisplayImage(song.getPath(), holder.songImage);
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public  static class SongsListViewHolder extends  RecyclerView.ViewHolder
    {
        public ImageView songImage;
        public TextView songName;
        public  TextView artistName;



        public SongsListViewHolder(@NonNull View itemView,OnClickListener onClickListen) {
            super(itemView);
            songImage = itemView.findViewById(R.id.songalbumart);
            songName = itemView.findViewById(R.id.songname);
            artistName = itemView.findViewById(R.id.artistname);

        }



    }

    public void updateDataSet(ArrayList<Song> arraylist) {
        mSongs = arraylist;

    }

    public  Song getSong(int position)
    {
        return mSongs.get(position);
    }

}

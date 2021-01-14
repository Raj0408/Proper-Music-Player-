package com.funstudio.propermusicplayer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.funstudio.propermusicplayer.R;

import com.funstudio.propermusicplayer.loaders.ImageLoader;
import com.funstudio.propermusicplayer.models.Album;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder> {
List<Album> albums;
ImageLoader loader;
Context ctx;



public  AlbumsAdapter (List<Album> albumList, Context ctx)
{
    albums = albumList;
    this.ctx = ctx;
    loader = new ImageLoader(ctx);

}
    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item,parent,false);
AlbumViewHolder albumViewHolder = new AlbumViewHolder(view);
    return albumViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
Album album = albums.get(position);
holder.title.setText(album.getAlbumName());
if(album.getSongsInAlbum().size()>1)
    holder.artist.setText(album.getSongsInAlbum().size()+" Songs");
else
    holder.artist.setText(album.getSongsInAlbum().size()+ " Song");
        holder.title.setTextColor(Color.parseColor("#DDDDDD"));
        holder.artist.setTextColor(Color.parseColor("#BBBBBB"));
        loader.DisplayImage(album.getSongsInAlbum().get(0).getPath(), holder.albumArt);

    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public  class  AlbumViewHolder extends RecyclerView.ViewHolder
    {
        ImageView albumArt;
        TextView title,artist;
        RelativeLayout bottomHolder;


                public AlbumViewHolder(View view)
                {
                    super(view);
                    albumArt = view.findViewById(R.id.album_frag_art);
                    title = view.findViewById(R.id.item_title_album);
                    artist=view.findViewById(R.id.item_artist_album);
                    bottomHolder = view.findViewById(R.id.bottomHolder);

                }

            }
}

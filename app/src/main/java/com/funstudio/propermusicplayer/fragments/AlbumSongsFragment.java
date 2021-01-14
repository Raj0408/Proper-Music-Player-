package com.funstudio.propermusicplayer.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funstudio.propermusicplayer.MainActivity;
import com.funstudio.propermusicplayer.R;
import com.funstudio.propermusicplayer.adapters.SongsAdapter;
import com.funstudio.propermusicplayer.clickitemtouchlistener.ClickItemTouchListener;
import com.funstudio.propermusicplayer.loaders.ImageLoader;


public class AlbumSongsFragment extends Fragment {
    SongsAdapter adapter;

    ImageLoader imageLoader;

    ImageView backbtn,backDrop;

    RecyclerView recyclerView;

    Context context;





    TextView albumTitle,numberOfSongsInAlbum;

    MainActivity activity;
    LinearLayoutManager mLayoutManager2;

    callbackListenerForAlbum albumCallback;

    public AlbumSongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album_songs, container, false);


    }





    public interface  callbackListenerForAlbum
    {
        void onAlbumSongClicked(int position);


    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        activity = (MainActivity)context;

        try {


        imageLoader = new ImageLoader(context);
        albumCallback = (callbackListenerForAlbum)context;

        }catch (ClassCastException ex)
        {
            throw  new ClassCastException(context.toString()
            + "must implement callbackListenerForAlbum");

        }
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       backbtn = view.findViewById(R.id.view_album_back_btn);


        backDrop = view.findViewById(R.id.album_backdrop);


       imageLoader.DisplayImage(MainActivity.albumPosition.getSongsInAlbum().get(0).getPath(), backDrop);



albumTitle = view.findViewById(R.id.album_title);
albumTitle.setText(MainActivity.albumPosition.getAlbumName());

numberOfSongsInAlbum = view.findViewById(R.id.album_tracks_text);


int tmp = MainActivity.albumPosition.getSongsInAlbum().size();
String nrSongs;
if(tmp ==1)
{
    nrSongs ="1 Song";
}else
{
    nrSongs = tmp+ "Songs";
}

numberOfSongsInAlbum.setText(nrSongs);

recyclerView = view.findViewById(R.id.album_songs_recycler);



adapter = new SongsAdapter(context, MainActivity.albumPosition.getSongsInAlbum());
        mLayoutManager2 = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager2);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

recyclerView.addOnItemTouchListener(new ClickItemTouchListener(recyclerView) {
    @Override
    public boolean onClick(RecyclerView parent, View view, int position, long id) {

        albumCallback.onAlbumSongClicked(position);
        return true;
    }

    @Override
    public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
});
    }


}
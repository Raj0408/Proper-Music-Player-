package com.funstudio.propermusicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.funstudio.propermusicplayer.MainActivity;
import com.funstudio.propermusicplayer.R;
import com.funstudio.propermusicplayer.adapters.AlbumsAdapter;
import com.funstudio.propermusicplayer.clickitemtouchlistener.ClickItemTouchListener;
import com.google.android.material.snackbar.Snackbar;

public class AlbumsFragment extends Fragment   {

    public AlbumsAdapter adapter;

    View bottomMarginLayout;

    GridLayoutManager gridLayoutManager;

    public onAlbumClickListener callback;



    public RecyclerView recyclerView;




    public  interface  onAlbumClickListener
    {
        public  void onAlbumClicked();

    }

    public  AlbumsFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
   recyclerView = view.findViewById(R.id.albums_recyclerview);
   adapter = new AlbumsAdapter(MainActivity.albums,getContext());
   gridLayoutManager = new GridLayoutManager(getContext(), 2);
   recyclerView.setLayoutManager(gridLayoutManager);
   recyclerView.setItemAnimator(new DefaultItemAnimator());
   recyclerView.setAdapter(adapter);

   recyclerView.addOnItemTouchListener(new ClickItemTouchListener(recyclerView) {
       @Override
       public boolean onClick(RecyclerView parent, View view, int position, long id) {
           if(position >=0)
           {
MainActivity.albumPosition = MainActivity.albums.get(position);
               callback.onAlbumClicked();
           }
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

        @Override
    public  void onAttach(Context context)
    {
        super.onAttach(context);
        try {
            callback = (onAlbumClickListener)context;

        }catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()+"must implement onAlbumClickListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        gridLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    public  void updateAdapter()
    {
        if(adapter != null)
        {
            adapter.notifyDataSetChanged();
        }
    }

}

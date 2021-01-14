package com.funstudio.propermusicplayer.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.funstudio.propermusicplayer.MainActivity;
import com.funstudio.propermusicplayer.R;
import com.funstudio.propermusicplayer.adapters.SongsAdapter;
import com.funstudio.propermusicplayer.clickitemtouchlistener.ClickItemTouchListener;

import java.util.Objects;


public class SongsFragment extends Fragment {

    onSongClickListener callBack;
    SongsAdapter adapter;
    RecyclerView songsrecyclerView;
    LinearLayoutManager mLayoutManager2;
    Context ctx;



    public SongsFragment() {
        // Required empty public constructor
    }

    public  interface  onSongClickListener
    {
        public  void onSongClicked(int songPosition);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callBack = (onSongClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        ctx = context;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songsrecyclerView = view.findViewById(R.id.SongsList);
        songrecycler();




    }

    public void songrecycler()
    {

        adapter = new SongsAdapter(Objects.requireNonNull(getContext()), MainActivity.localsongs);
        mLayoutManager2 = new LinearLayoutManager(getContext());
        songsrecyclerView.setLayoutManager(mLayoutManager2);
        songsrecyclerView.setItemAnimator(new DefaultItemAnimator());
        songsrecyclerView.setAdapter(adapter);

        songsrecyclerView.addOnItemTouchListener(new ClickItemTouchListener(songsrecyclerView) {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                if(position >=0)
                {

                    callBack.onSongClicked(position);
                }
                return true;
            }


            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return false;
            }
        });

    }
}
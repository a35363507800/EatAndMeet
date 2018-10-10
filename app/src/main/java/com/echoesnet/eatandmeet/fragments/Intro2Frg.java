package com.echoesnet.eatandmeet.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Intro2Frg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Intro2Frg extends Fragment
{
    public Intro2Frg()
    {
        // Required empty public constructor
    }

    public static Intro2Frg newInstance()
    {
        return new Intro2Frg();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
       return inflater.inflate(R.layout.frg_intro2, container, false);
    }

}

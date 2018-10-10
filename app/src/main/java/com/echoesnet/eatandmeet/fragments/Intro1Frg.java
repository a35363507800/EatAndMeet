package com.echoesnet.eatandmeet.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.R;


import butterknife.ButterKnife;

public class Intro1Frg extends Fragment
{
    public Intro1Frg()
    {
        // Required empty public constructor
    }

    public static Intro1Frg newInstance()
    {
        return new Intro1Frg();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.frg_intro1, container, false);
    }

}

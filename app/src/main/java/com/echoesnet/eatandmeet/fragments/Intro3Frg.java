package com.echoesnet.eatandmeet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.echoesnet.eatandmeet.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFrgIntro3InteractListener} interface
 * to handle interaction events.
 * Use the {@link Intro3Frg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Intro3Frg extends Fragment
{
    //为了演示fragment和包含它的activity互相通信的原始方法，此处做一演示，其他地方推介使用EventBus
    private static final String ARG_PAGE = "page";
    private static final String ARG_TITLE = "title";
    Unbinder unbinder;

    //    private String title;
//    private int page;
    private OnFrgIntro3InteractListener mListener;

    public Intro3Frg()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param page  Parameter 1.
     * @param title Parameter 2.
     * @return A new instance of fragment Intro3Frg.
     */
    // TODO: Rename and change types and number of parameters
    public static Intro3Frg newInstance(int page, String title)
    {
        Intro3Frg fragment = new Intro3Frg();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE,"default");
            page = getArguments().getInt(ARG_PAGE,0);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_intro3, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_start_app})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_start_app:
                if (mListener != null)
                {
                    mListener.onStartButtonClick(view);
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFrgIntro3InteractListener)
        {
            mListener = (OnFrgIntro3InteractListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFrgIntro3InteractListener
    {
        void onStartButtonClick(View v);
    }
}

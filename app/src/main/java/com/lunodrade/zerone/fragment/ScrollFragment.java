package com.lunodrade.zerone.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lunodrade.zerone.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScrollFragment extends Fragment {


    public ScrollFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_scroll, container, false);



        return rootView;
    }

}

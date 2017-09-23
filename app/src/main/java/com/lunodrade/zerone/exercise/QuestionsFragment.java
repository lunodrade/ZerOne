package com.lunodrade.zerone.exercise;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lunodrade.zerone.R;

/**
 * Created by lunodrade on 22/09/2017.
 */

public class QuestionsFragment extends Fragment {

    // Required empty public constructor
    public QuestionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questions, container, false);
        return view;
    }
}

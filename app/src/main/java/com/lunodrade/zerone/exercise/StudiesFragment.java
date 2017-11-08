package com.lunodrade.zerone.exercise;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.lunodrade.zerone.ExerciseActivity;
import com.lunodrade.zerone.R;


public class StudiesFragment extends Fragment {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  Variáveis
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/
    private ExerciseActivity mActivity;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  Métodos da classe
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/
    public StudiesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_studies, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = (ExerciseActivity) getActivity();
        //
        loadWebviewContent(view);
    }

    private void loadWebviewContent(View view) {
        String file = mActivity.mBookCode;

        WebView webViewHeroes = (WebView) view.findViewById(R.id.WebViewHeroes);
        webViewHeroes.setBackgroundColor(Color.TRANSPARENT);

        webViewHeroes.getSettings().setLoadWithOverviewMode(true);
        webViewHeroes.getSettings().setUseWideViewPort(true);
        webViewHeroes.getSettings().setJavaScriptEnabled(true);
        webViewHeroes.loadUrl("file:///android_asset/" + file + ".html");
    }
}

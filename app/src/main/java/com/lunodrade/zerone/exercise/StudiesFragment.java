package com.lunodrade.zerone.exercise;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.lunodrade.zerone.R;

/**
 * Created by lunodrade on 22/09/2017.
 */

public class StudiesFragment extends Fragment {

    // Required empty public constructor
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

        // Get your HTML
        //String yourHTML = "<p>Some <b>html</b> you have somewhere</p>";
        // Get a handle on your webview
        WebView webViewHeroes = (WebView) view.findViewById(R.id.WebViewHeroes);
        // Populate webview with your html
        //webViewHeroes.loadData(yourHTML, "text/html", null);
        // Populate webview with your html
        webViewHeroes.setBackgroundColor(Color.TRANSPARENT);
        webViewHeroes.loadUrl("file:///android_asset/hola.html");

        return view;
    }

}

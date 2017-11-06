package com.lunodrade.zerone;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lunodrade.zerone.ranking.RankingAdapter;

import java.util.ArrayList;

public class RankingViewer extends AppCompatActivity {

    RecyclerView mRecyclerView;
    String[] spacecrafts = {"Juno","Hubble","Casini","WMAP","Spitzer","Pioneer","Columbia","Challenger","Apollo","Curiosity",
            "Juno","Hubble","Casini","WMAP","Spitzer","Pioneer","Columbia","Challenger","Apollo","Curiosity"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_viewer);

        mRecyclerView = (RecyclerView) findViewById(R.id.ranking_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //ADAPTER
        RankingAdapter adapter = new RankingAdapter(this, spacecrafts);
        mRecyclerView.setAdapter(adapter);
    }
}

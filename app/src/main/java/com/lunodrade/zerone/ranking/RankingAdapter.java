package com.lunodrade.zerone.ranking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lunodrade.zerone.R;

/**
 * Created by lunodrade on 04/11/2017.
 */

public class RankingAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Context c;
    String[] spacecrafts;
    public RankingAdapter(Context c, String[] spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.main_line_view, parent, false);
        return new MyViewHolder(v);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //BIND DATA
        holder.nameTxt.setText(spacecrafts[position]);
    }
    @Override
    public int getItemCount() {
        return spacecrafts.length;
    }
}

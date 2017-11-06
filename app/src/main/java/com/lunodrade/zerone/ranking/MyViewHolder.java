package com.lunodrade.zerone.ranking;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lunodrade.zerone.R;

/**
 * Created by lunodrade on 04/11/2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView nameTxt;
    public MyViewHolder(View itemView) {
        super(itemView);
        nameTxt = (TextView) itemView.findViewById(R.id.nameTxt);
    }
}
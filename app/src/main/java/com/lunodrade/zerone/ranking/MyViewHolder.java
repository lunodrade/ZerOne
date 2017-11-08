package com.lunodrade.zerone.ranking;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lunodrade.zerone.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView userPosition;
    TextView userName;
    TextView userXp;
    CircleImageView userPhoto;

    public MyViewHolder(View itemView) {
        super(itemView);
        userPosition = (TextView) itemView.findViewById(R.id.ranking_holder_position);
        userName = (TextView) itemView.findViewById(R.id.ranking_holder_name);
        userXp = (TextView) itemView.findViewById(R.id.ranking_holder_xp);
        userPhoto = (CircleImageView) itemView.findViewById(R.id.ranking_holder_photo);
    }
}
package com.lunodrade.zerone.achievement;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunodrade.zerone.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class AchieViewerHolder extends RecyclerView.ViewHolder {

    ImageView achievIcon;
    TextView achievName;
    TextView achievDescription;
    TextView achievXp;
    TextView achievCount;

    public AchieViewerHolder(View itemView) {
        super(itemView);

        achievIcon = (ImageView) itemView.findViewById(R.id.achievement_holder_icon);
        achievName = (TextView) itemView.findViewById(R.id.achievement_holder_title);
        achievDescription = (TextView) itemView.findViewById(R.id.achievement_holder_content);
        achievXp = (TextView) itemView.findViewById(R.id.achievement_holder_xp);
        //count
    }
}

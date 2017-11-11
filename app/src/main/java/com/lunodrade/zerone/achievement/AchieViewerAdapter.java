package com.lunodrade.zerone.achievement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lunodrade.zerone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AchieViewerAdapter extends RecyclerView.Adapter<AchieViewerHolder> {

    Context mContext;
    ArrayList<AchieViewerEntry> mAchievements = new ArrayList<>();

    public AchieViewerAdapter(Context c, ArrayList<AchieViewerEntry> achievements) {
        this.mContext = c;
        this.mAchievements = achievements;
    }

    @Override
    public AchieViewerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_achievement, parent, false);
        return new AchieViewerHolder(v);
    }

    @Override
    public void onBindViewHolder(AchieViewerHolder holder, int position) {
        //BIND DATA

        AchieViewerEntry item = mAchievements.get(position);

        holder.achievIcon.setImageDrawable(item.achievResourceIcon);
        holder.achievName.setText(item.getName());
        holder.achievDescription.setText(item.getDescription());
        holder.achievXp.setText(""+item.getXp()+" XP");
    }

    @Override
    public int getItemCount() {
        return mAchievements.size();
    }
}

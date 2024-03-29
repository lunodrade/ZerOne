package com.lunodrade.zerone.ranking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lunodrade.zerone.R;

import java.util.ArrayList;


public class RankingAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Context mContext;
    ArrayList<RankingEntry> mMembers = new ArrayList<>();

    public RankingAdapter(Context c, ArrayList<RankingEntry> members) {
        this.mContext = c;
        this.mMembers = members;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_ranking, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //BIND DATA
        RankingEntry item = mMembers.get(position);
        holder.userName.setText(item.getName());
        holder.userXp.setText("" + item.getXp() + " pontos");

        if (item.getPhoto() != null ) {
            Glide.with(mContext)
                    .load(item.getPhoto())
                    .into(holder.userPhoto);
        }

        holder.userPosition.setText("" + item.getPosition() + "º");

    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }
}


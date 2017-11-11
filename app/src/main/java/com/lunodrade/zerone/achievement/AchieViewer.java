package com.lunodrade.zerone.achievement;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lunodrade.zerone.R;
import com.lunodrade.zerone.models.User;
import com.lunodrade.zerone.ranking.RankingEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AchieViewer extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    RecyclerView mRecyclerView;
    AchieViewerAdapter mAdapter;

    User mUser;
    ArrayList<AchieViewerEntry> mAchievements = new ArrayList<AchieViewerEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achie_viewer);

        this.getSupportActionBar().setTitle("Achievements");

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        loadUserInfo(mFirebaseUser.getUid());

        mRecyclerView = (RecyclerView) findViewById(R.id.achievement_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //ADAPTER
        mAdapter = new AchieViewerAdapter(this, mAchievements);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void loadUserInfo(String uid) {
        mDatabase.child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                if (mUser != null) {
                    insertAchievements(mUser.achievements);
                    updateInterface();
                } else {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void insertAchievements(Map<String, Integer> achievements) {
        for(Map.Entry<String, Integer> entry : achievements.entrySet()) {
            String achiev = entry.getKey();
            Integer count = entry.getValue();

            AchievementControl data = new AchievementControl(mUser, getApplicationContext());

            String achievName = data.getTitle(achiev);
            String achievDescription = data.getTitle(achiev+"_content");
            Integer achievXp = data.getXp(achiev);
            Integer achievCount = count;
            Drawable achievResourceIcon = data.getIcon(achiev);

            AchieViewerEntry temp = new AchieViewerEntry(achievName, achievDescription, achievXp,
                                                         achievCount, achievResourceIcon);
            mAchievements.add(temp);
        }
    }

    private void updateInterface() {
        mAdapter.notifyDataSetChanged();
    }
}

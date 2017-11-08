package com.lunodrade.zerone;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lunodrade.zerone.models.Room;
import com.lunodrade.zerone.models.User;
import com.lunodrade.zerone.ranking.RankingAdapter;
import com.lunodrade.zerone.ranking.RankingEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingViewer extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    RecyclerView mRecyclerView;
    RankingAdapter mAdapter;
    ArrayList<RankingEntry> mMembers = new ArrayList<RankingEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_viewer);

        this.getSupportActionBar().setTitle("Ranking");

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        Bundle extras = getIntent().getExtras();
        checkExtras(extras);

        synchronizeWithUserInfo(0);

        mRecyclerView = (RecyclerView) findViewById(R.id.ranking_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //ADAPTER
        mAdapter = new RankingAdapter(this, mMembers);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void checkExtras(Bundle extras) {
        Log.d("RankingViewer", "checkExtras: " + (extras != null));
        if (extras != null) {
            HashMap<String, Integer> tempoMembers = (HashMap) extras.getSerializable("members");
            Log.d("RankingViewer", "checkExtras: " + tempoMembers);

            for(Map.Entry<String, Integer> entry : tempoMembers.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();

                RankingEntry temp = new RankingEntry(key, value);

                Log.d("RankingAdapter", "RankingAdapter: key: " + key + " | value: " + value);
                mMembers.add(temp);
            }

            sortListReverse(mMembers);

            // Colocar as posições em cada entrada
            int virtualValue = -1;
            int virtualPosition = 1;
            int realPosition = 1;

            for(RankingEntry entry : mMembers) {

                if (virtualValue == -1)
                    virtualValue = entry.getXp();

                if (entry.getXp() < virtualValue) {
                    virtualValue = entry.getXp();
                    virtualPosition = realPosition;
                }

                entry.setPosition(virtualPosition);
                realPosition++;
            }
        }
    }

    private void synchronizeWithUserInfo(int index) {
        Log.d("RankingViewer", "synchronizeWithUserInfo. Index: " + index);
        if (index < mMembers.size()) {
            RankingEntry item = mMembers.get(index);
            loadUserInfo(item.getUid(), index);
        } else {
            updateInterface();
        }
    }

    private void loadUserInfo(String uid, final int index) {
        mDatabase.child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User tempUser = dataSnapshot.getValue(User.class);
                if (tempUser != null) {
                    mMembers.get(index).setName(tempUser.name);
                    mMembers.get(index).setPhoto(tempUser.profilePhoto);
                }
                synchronizeWithUserInfo(index + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void updateInterface() {
        Log.d("RankingViewer", "updateInterface: " + mMembers);

        mAdapter.notifyDataSetChanged();
    }

    private void sortList(List<RankingEntry> list) {
        Collections.sort(list, new Comparator<RankingEntry>() {
            public int compare(RankingEntry ideaVal1, RankingEntry ideaVal2) {
                // avoiding NullPointerException in case name is null
                Long idea1 = new Long(ideaVal1.getXp());
                Long idea2 = new Long(ideaVal2.getXp());
                return idea1.compareTo(idea2);
            }
        });
    }

    private void sortListReverse(List<RankingEntry> list) {
        Collections.sort(list, new Comparator<RankingEntry>() {
            public int compare(RankingEntry ideaVal1, RankingEntry ideaVal2) {
                // avoiding NullPointerException in case name is null
                Long idea1 = new Long(ideaVal1.getXp());
                Long idea2 = new Long(ideaVal2.getXp());
                return idea2.compareTo(idea1);
            }
        });
    }
}

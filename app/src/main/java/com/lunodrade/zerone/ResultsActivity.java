package com.lunodrade.zerone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lunodrade.zerone.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ResultsActivity extends Activity {

    @BindView(R.id.results_bookname)
    TextView mBookName;
    @BindView(R.id.results_correctnumber)
    TextView mCorrectNumber;
    @BindView(R.id.results_wrongnumber)
    TextView mWrongNumber;
    @BindView(R.id.results_user_xp)
    TextView mUserXP;
    @BindView(R.id.results_xpreceived)
    TextView mXpReceived;
    @BindView(R.id.results_room_points)
    TextView mXpRoom;

    @BindView(R.id.results_block_loading)
    View mLoadingBlock;
    @BindView(R.id.results_block_fail)
    View mFailBlock;
    @BindView(R.id.results_block_sucess)
    View mSucessBlock;
    @BindView(R.id.results_block_sucess_room)
    View mSucessBlockRoom;

    private String mBookResult;
    private int mBookLevel;
    private int mBookPoints;
    private int mPomodoroSeconds;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private User mUserClass;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ButterKnife.bind(this);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        Bundle extras = getIntent().getExtras();
        checkExtras(extras);
    }

    private void checkExtras(Bundle extras) {
        Log.d("ResultsActivity", "checkExtras: existe extras? " + (extras != null));
        if (extras != null) {
            Log.d("ResultsActivity", "checkExtras: chamando leitura de extras = " + extras.getString("booktitle") );

            mBookName.setText(extras.getString("booktitle"));
            mCorrectNumber.setText(extras.getString("correct"));
            mWrongNumber.setText(extras.getString("wrong"));

            mBookResult = extras.getString("result");
            mBookLevel = extras.getInt("booklevel");
            mBookPoints = extras.getInt("bookpoints");

            mPomodoroSeconds = extras.getInt("pomodoro");

            Log.d("ResultsActivity", "checkExtras: " + mBookLevel + " | " + mBookPoints + " | " + mPomodoroSeconds);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mUserClass != null) {
            updateInterface(mUserClass);
        } else {
            loadDatabase();
        }
    }

    private void loadDatabase() {
        // Attach a listener to read the data at our posts reference
        String uid = mFirebaseUser.getUid();
        mDatabase.child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUserClass = user;
                updateInterface(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void updateInterface(User user) {

        if (mBookResult.equals("sucess")) {
            int discount = (mBookLevel - user.getLvlForXP()) * 10;
            double prepoints = (mBookPoints + (mBookPoints / 100.0 * discount));
            long points = prepoints > 1 ? Math.round(prepoints) : 1;
            int received = ((int) points);

            mUserClass.xp += received;

            mUserXP.setText(""+user.xp);
            mXpReceived.setText(""+received);

            //Bloco de atualização do Firebase
            String uid = mFirebaseUser.getUid();
            Map<String, Object> childUpdates = new HashMap<>();

            if (user.activeRoomName != null) {
                user.activeRoomXp += received;
                String urlRanking = "/rooms/" + user.activeRoomId + "/members/" + uid;
                childUpdates.put(urlRanking, user.activeRoomXp);

                mXpRoom.setText(""+user.activeRoomXp + "pts");
                mSucessBlockRoom.setVisibility(View.VISIBLE);
            }

            childUpdates.put("/users/" + uid, mUserClass);
            mDatabase.updateChildren(childUpdates);

            mSucessBlock.setVisibility(View.VISIBLE);
        } else {
            mFailBlock.setVisibility(View.VISIBLE);
        }

        //Mostrar informações
        mLoadingBlock.setVisibility(View.GONE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    @OnClick ({R.id.results_fail_continue, R.id.results_sucess_continue})
    public void continueButton(Button button) {
        Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (mUserClass != null)
            intent.putExtra("user", mUserClass);

        startActivity(intent);
        finish();
    }
}

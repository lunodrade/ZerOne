package com.lunodrade.zerone;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lunodrade.zerone.models.User;

public class SplashActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ZerOne_SplashActivity";

    private User mUserClass;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if ( mFirebaseUser != null ) {
            loadDatabase();
        } else {
            openMainActivity(false);
        }
    }

    private void openMainActivity(boolean logged) {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

        if (logged)
            intent.putExtra("user", mUserClass);

        startActivity(intent);
        finish();
    }

    private void loadDatabase() {
        // Attach a listener to read the data at our posts reference
        String uid = mFirebaseUser.getUid();
        mDatabase.child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUserClass = user;
                openMainActivity(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}

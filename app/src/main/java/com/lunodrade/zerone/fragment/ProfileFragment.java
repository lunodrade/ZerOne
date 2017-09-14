package com.lunodrade.zerone.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lunodrade.zerone.MainActivity;
import com.lunodrade.zerone.R;
import com.lunodrade.zerone.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    /*
    @BindView(R.id.profile_user_photo) CircleImageView mPhotoImage;
    @BindView(R.id.profile_user_name) TextView mNameView;
    @BindView(R.id.profile_user_level) TextView mLevelView;
    @BindView(R.id.profile_user_score) TextView mScoreView;
    @BindView(R.id.profile_user_room) TextView mRoomView;
    */

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private ValueEventListener mPostListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        MainActivity activity = (MainActivity) getActivity();
        User user = activity.getUserClass();
        if (user != null) {
            updateInterface(user);
        }

        loadDatabase();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.v("a", "TESSSSSSSSSSSSSSSSS");

        //String uid = mFirebaseUser.getUid();
        //mDatabase.child("users/" + uid).removeEventListener(mPostListener);
    }

    private void loadDatabase() {
        // Attach a listener to read the data at our posts reference
        String uid = mFirebaseUser.getUid();

        mPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                updateInterface(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        };

        mDatabase.child("users/" + uid).addValueEventListener(mPostListener);
    }

    private void updateInterface(User user) {

        if (getActivity() == null)
            return;

        /*
        if (user.profilePhoto != null ) {
            Glide.with(this)
                    .load(user.profilePhoto)
                    .into(mPhotoImage);
        }

        mNameView.setText(user.name);

        mScoreView.setText( ""+user.xp );
        mLevelView.setText( ""+ getLvlForXP(user.xp) );

        if(user.activeRoomId != null) {
            mRoomView.setText(user.activeRoomName);
        } else {
            mRoomView.setText("Sem sala, entre em uma ;)");
        }
        */
    }

    public static int getLvlForXP(int xp) {
        if (xp <= 255) {
            return xp  / 17;
        }else if (xp > 272 && xp < 887) {
            return (int) ((Math.sqrt(24 * xp - 5159) + 59) / 6);
        }else if (xp > 825) {
            return (int) ((Math.sqrt(56 * xp - 32511) + 303) / 14);
        }
        return 0;
    }


}

package com.lunodrade.zerone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lunodrade.zerone.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.ui.ExtraConstants.EXTRA_IDP_RESPONSE;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ZerOne_LoginActivity";

    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final int RC_SIGN_IN = 100;

    private boolean mWriteUser = false;

    private String loginStatus;
    private User mUserClass;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @BindView(android.R.id.content) View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        Log.v(LOG_TAG, "\n\n-----------------\n\n");

        /*
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loginStatus = extras.getString("loginStatus");
        }
        */


        /*
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if ( mFirebaseUser != null ) {
            Log.v(LOG_TAG, "onCreate == " + mFirebaseUser);
            onAuthSuccess(false);
        }
        */
        //AuthUI.getInstance().signOut(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserClass = (User) extras.getSerializable("user");
        }

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if ( mFirebaseUser != null ) {

            Log.v(LOG_TAG, "Check == " + mWriteUser);

            onAuthSuccess(false);

        } else {
            showSignInScreen();
            Log.v(LOG_TAG, "Chamando showSignIn" );
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    private void onAuthSuccess(boolean writeUser) {
        //
        if (writeUser)
            writeNewUser();

        Log.v(LOG_TAG, "Chamando MainActivity");
        // ir para MainActivity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        if (mUserClass != null)
            intent.putExtra("user", mUserClass);

        startActivity(intent);
        finish();
    }

    private void writeNewUser() {
        String userUid = mFirebaseUser.getUid();
        User user = new User(mFirebaseUser);

        Log.v(LOG_TAG, "Escrevendo usuário com ID: " + userUid);

        mDatabase.child("users").child(userUid).setValue(user);
    }

    private void onAuthSuccessWithCheck() {

        Log.v(LOG_TAG, "Checando se dados existem");

        DatabaseReference users = mDatabase.child("users");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String userUid = mFirebaseUser.getUid();
                //Testa se o usuário já existe
                if (snapshot.child(userUid).exists()) {
                    onAuthSuccess(false);
                    Log.v(LOG_TAG, "Usuário " + userUid + " já existia.");
                } else {
                    onAuthSuccess(true);
                    mWriteUser = true;
                    Log.v(LOG_TAG, "Usuário " + userUid + " ainda NÃO existia.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void showSignInScreen() {
        Log.v(LOG_TAG, "Abrindo showSignIn" );
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.mipmap.logo)
                        .setProviders(getSelectedProviders())
                        .setTosUrl(GOOGLE_TOS_URL)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    @MainThread
    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();

        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

        selectedProviders.add(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                        .setPermissions(getGooglePermissions())
                        .build());


        return selectedProviders;
    }

    @MainThread
    private List<String> getGooglePermissions() {
        List<String> result = new ArrayList<>();
        //result.add(Scopes.DRIVE_FILE);
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(LOG_TAG, "handleSignInResponse code: " + requestCode);

        //
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }
        //
        showSnackbar(R.string.unknown_response);
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        //
        if (resultCode == RESULT_OK) {
            Log.v(LOG_TAG, "Logou pelo handleSignIn.");
            onAuthSuccessWithCheck();

            mWriteUser = true;

            return;
        }

        if (resultCode == RESULT_CANCELED) {
            showSnackbar(R.string.sign_in_cancelled);
            return;
        }

        //if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
        //    showSnackbar(R.string.no_internet_connection);
        //    return;
        //}

        showSnackbar(R.string.unknown_sign_in_response);
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    public List<String> getFacebookPermissions() {
        List<String> result = new ArrayList<>();
        //result.add(Scopes.DRIVE_FILE);
        return result;
    }
}
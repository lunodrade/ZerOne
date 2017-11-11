package com.lunodrade.zerone;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lunodrade.zerone.achievement.AchievementControl;
import com.lunodrade.zerone.fragment.HomeFragment;
import com.lunodrade.zerone.fragment.ProfileFragment;
import com.lunodrade.zerone.fragment.RoomsFragment;
import com.lunodrade.zerone.models.User;
import com.lunodrade.zerone.walkthrough.WalkthroughActivity;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Fragment fragment;
    private FragmentManager fragmentManager;

    private User mUserClass;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private boolean mHasNavBar;
    private int mBottomBarPixelsSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Início");


        SharedPreferences settings = getSharedPreferences("prefs", 0);
        boolean firstRun = settings.getBoolean("firstRun", false);

        if (firstRun == false) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstRun", true);
            editor.commit();

            Intent intent = new Intent(this, WalkthroughActivity.class);
            startActivity(intent);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("bottom_bar_height_minus14", "dimen", "com.lunodrade.zerone");
        if (resourceId > 0) {
            mBottomBarPixelsSize = resources.getDimensionPixelSize(resourceId);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                fragment = new HomeFragment();

                if (tabId == R.id.tab_home) {
                    toolbar.setTitle("Início");
                    fragment = new HomeFragment();
                } else if (tabId == R.id.tab_rooms) {
                    toolbar.setTitle("Salas");
                    fragment = new RoomsFragment();
                } else if (tabId == R.id.tab_profile) {
                    toolbar.setTitle("");
                    fragment = new ProfileFragment();
                }

                Log.v("AAAA", "voltou top");
                NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.myScrollingContent);
                nestedScrollView.fullScroll(View.FOCUS_UP);

                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.replace(R.id.contentContainer, fragment);
                transaction.commit();
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_rooms) {
                }
                Log.v("AAAA", "voltou top");

                NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.myScrollingContent);
                nestedScrollView.fullScroll(View.FOCUS_UP);
            }
        });

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mUserClass = (User) extras.getSerializable("user");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        mHasNavBar = hasNavBar(this);

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
        View navView = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);

        CircleImageView mPhotoImage = (CircleImageView)  navView.findViewById(R.id.navHeaderUserImage);
        if (user.profilePhoto != null ) {
            Glide.with(this)
                    .load(user.profilePhoto)
                    .into(mPhotoImage);
        }

        TextView mNameView = (TextView)  navView.findViewById(R.id.navHeaderUserName);
        mNameView.setText(user.name);

        TextView mEmailView = (TextView)  navView.findViewById(R.id.navHeaderUserEmail);
        mEmailView.setText(user.email);
    }

    public User getUserClass() {
        return mUserClass;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fragment_home_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_walkthrough) {
            Intent intent = new Intent(this, WalkthroughActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showSnackbar(@StringRes int errorMessageRes) {
        View mRootView = this.findViewById(R.id.placeSnackBar);

        int value = 0;
        if (mHasNavBar)
            value = mBottomBarPixelsSize;

        Snackbar snackbar = Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG);
        final View snackBarView = snackbar.getView();
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackBarView.getLayoutParams();

        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin,
                params.bottomMargin - value);

        snackBarView.setLayoutParams(params);
        snackbar.show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean hasNavBar(Context context) {
        Point realSize = new Point();
        Point screenSize = new Point();
        boolean hasNavBar = false;
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        realSize.x = metrics.widthPixels;
        realSize.y = metrics.heightPixels;
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        if (realSize.y != screenSize.y) {
            int difference = realSize.y - screenSize.y;
            int navBarHeight = 0;
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navBarHeight = resources.getDimensionPixelSize(resourceId);
            }
            if (navBarHeight != 0) {
                if (difference == navBarHeight) {
                    hasNavBar = true;
                }
            }

        }
        return hasNavBar;

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

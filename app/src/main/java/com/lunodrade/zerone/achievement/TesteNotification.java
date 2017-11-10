package com.lunodrade.zerone.achievement;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;

import com.lunodrade.zerone.R;
import com.lunodrade.zerone.models.User;

import java.io.InputStream;

public class TesteNotification extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste_notification);

        User mUserClass = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserClass = (User) extras.getSerializable("user");
        }




        // -----------------------------------------------
        checkPermission();

        AchievementControl control = new AchievementControl(mUserClass, getApplicationContext());
        control.unlock("fully_approved");
        // -----------------------------------------------


    }




    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("TesteNotification", "onActivityResult: GAMIFICATION - " + (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE));

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();

            }
            else
            {
                //do as per your logic
                //TODO: usuário permitiu
            }
        }
    }

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
}

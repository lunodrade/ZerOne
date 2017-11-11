package com.lunodrade.zerone.achievement;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lunodrade.zerone.R;
import com.lunodrade.zerone.models.User;

import java.util.HashMap;
import java.util.Map;

////////////////////////////////////////////////////////////////////////////////////////////////
//
//
//
//////////////////////////////////////////////////////////////////////////////////////////////*/

public class AchievementControl {

    AchievementUnlocked mAchievementUnlocked;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    User mUser = null;

    Context mApplicationContext;
    Resources mResources;
    String mPackageName;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    public AchievementControl(User user, Context context) {
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUser = user;
        Log.d("AchievementControl", "Lista de achievements: " + mUser.achievements);

        mApplicationContext = context;
        mResources = mApplicationContext.getResources();
        mPackageName = mApplicationContext.getPackageName();

        mAchievementUnlocked = new AchievementUnlocked(mApplicationContext);
        mAchievementUnlocked.setRounded(false).setLarge(false).setTopAligned(true).setReadingDelay(4000);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    public void unlock(String achiev, Boolean unique) {
        //Testa se é um achievement de uma só vez
        if (unique) {
            //Testa se o usuário já ganhou esse achievement
            if (mUser.achievements.containsKey(achiev)) {
                Log.d("AchievementControl", "Já possuía o achievement, e ele é único.");
                return;
            }
        }
        //Caso não seja único, ou ainda não possua, chama a função padrão
        unlock(achiev);
    }

    public void unlock(String achiev) {
        //Mostrar a notificação ao usuário
        showNotification(achiev);
        //Adicionar os dados na classe User
        addAchievementInUser(achiev);
        //Gravar os dados no servidor online do Firebase
        saveUserInFirebase();
    }

    private void showNotification(String achiev) {
        AchievementData data = new AchievementData();

        String fullTitle = getTitle(achiev)+ "     (+" + getXp(achiev) + " XP)";
        data.setTitle(fullTitle);
        data.setSubtitle(getTitle(achiev+"_content"));
        data.setIcon(getIcon(achiev));
        data.setTextColor(Color.WHITE);
        data.setIconBackgroundColor(Color.WHITE);
        data.setBackgroundColor(Color.BLACK);

        mAchievementUnlocked.show(data);
    }

    private void addAchievementInUser(String achiev) {
        if (mUser.achievements.containsKey(achiev)) {
            int value = mUser.achievements.get(achiev);
            mUser.achievements.put(achiev, value+1);
        } else {
            mUser.achievements.put(achiev, 1);
        }

        mUser.xp += getXp(achiev);
    }

    private void saveUserInFirebase() {
        /*
        Log.d("AchievementControl", "Achievements que possui: " + mUser.achievements);

        Map<String, Object> childUpdates = new HashMap<>();
        String uid = mFirebaseUser.getUid();

        String urlUserAchievement = "/users/" + uid + "/achievements/";

        childUpdates.put(urlUserAchievement, mUser.achievements);
        mDatabase.updateChildren(childUpdates);
        */
        Log.d("AchievementControl", "Achievements que possui: " + mUser.achievements);

        Map<String, Object> childUpdates = new HashMap<>();
        String uid = mFirebaseUser.getUid();

        String urlUserAchievement = "/users/" + uid;

        childUpdates.put(urlUserAchievement, mUser);
        mDatabase.updateChildren(childUpdates);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    public String getTitle(String achiev) {
        Log.d("AchievementControl", "ACHIEV NAME = " + achiev);
        int resourceID = mResources.getIdentifier("achievement_"+achiev, "string", mPackageName);
        String resourceString = mResources.getString(resourceID);

        return resourceString;
    }

    public Drawable getIcon(String achiev) {
        int resID = mResources.getIdentifier("achievement_"+achiev, "drawable", mPackageName);
        Drawable resourceIcon = ContextCompat.getDrawable(mApplicationContext, resID);

        return resourceIcon;
    }

    public Integer getXp(String achiev) {
        Log.d("AchievementControl", "AchievName XP → " + "achievement_"+achiev+"_xp");
        int resourceID = mResources.getIdentifier("achievement_"+achiev+"_xp", "string", mPackageName);
        String resourceString = mResources.getString(resourceID);

        return Integer.parseInt(resourceString);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

}

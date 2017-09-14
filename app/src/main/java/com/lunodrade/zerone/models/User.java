package com.lunodrade.zerone.models;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User implements Serializable {

    public String name;
    public String email;
    public String profilePhoto;
    public int xp = 0;
    public String activeRoomName;
    public String activeRoomId;
    public Map<String, String> roomsCreatedByMe = new HashMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(FirebaseUser firebaseUser) {
        //Pega os valores defaults do Firebase User
        String displayName = firebaseUser.getDisplayName();
        String displayEmail = firebaseUser.getEmail();
        Uri profileUri = firebaseUser.getPhotoUrl();

        //Dependendo do Provider, algum dos valores acima podem ser nulos
        //então vamos procurar por valores para substituílos
        for (UserInfo userInfo : firebaseUser.getProviderData()) {
            if (displayName == null && userInfo.getDisplayName() != null) {
                displayName = userInfo.getDisplayName();
            }
            if (profileUri == null && userInfo.getPhotoUrl() != null) {
                profileUri = userInfo.getPhotoUrl();
            }
        }

        //Coloca agora como valores finais
        this.name = displayName;
        this.email = displayEmail;
        this.profilePhoto = profileUri.toString();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("email", email);
        result.put("profilePhoto", profilePhoto);
        result.put("xp", xp);
        result.put("activeRoomName", activeRoomName);
        result.put("activeRoomId", activeRoomId);
        result.put("roomsCreatedByMe", roomsCreatedByMe);

        return result;
    }

}
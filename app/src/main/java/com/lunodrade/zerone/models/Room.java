package com.lunodrade.zerone.models;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Room {

    public String name;
    public String password;
    public Map<String, Integer> members = new HashMap<>();

    public Room() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Room(FirebaseUser firebaseUser) {
        Log.v("ROOM", "entrou no fragment_rooms_options");
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("password", password);
        result.put("members", members);

        return result;
    }
}


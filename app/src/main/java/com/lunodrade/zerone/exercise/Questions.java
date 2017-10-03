package com.lunodrade.zerone.exercise;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Questions {

    public Map<String, Object> book = new HashMap<>();

    public Questions() {    }

    public Map<Integer, LinkedTreeMap> getQuestions() {

        HashMap<Integer, LinkedTreeMap> result = new HashMap<>();

        for(Map.Entry<String, Object> entry : book.entrySet()) {
            String key = entry.getKey();
            ArrayList<Object> value = (ArrayList) entry.getValue();

            for(Object question : value) {
                Log.d("Questions", "getProducts: QUESTÃO " + question);

                LinkedTreeMap obj = (LinkedTreeMap) question;
                Integer num = Integer.parseInt(obj.get("id").toString());

                result.put(num, obj);
            }
        }

        return result;
    }




}

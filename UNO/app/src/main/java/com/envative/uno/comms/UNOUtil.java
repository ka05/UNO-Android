package com.envative.uno.comms;

import android.content.Context;
import android.content.SharedPreferences;

import com.envative.emoba.services.EMWebService;
import com.envative.uno.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by clay on 5/29/16.
 */
public class UNOUtil {

    private static UNOUtil instance;
    private Context context;
    private final SharedPreferences sharedPref;

    private UNOUtil(Context context){
        this.context  = context;
        sharedPref = context.getSharedPreferences("UNOPref", Context.MODE_PRIVATE);
    }

    public static UNOUtil get(Context context) {
        if (instance == null) {
            instance = new UNOUtil(context.getApplicationContext());
        }

        instance.context = context;
        return instance;
    }

    public Boolean checkLoggedIn(){
        boolean loggedIn = sharedPref.getBoolean("loggedIn", false);
//        Log.d("logged in::",loggedIn + "");
        EMWebService.loggedIn = loggedIn;

        if(loggedIn){
            getUser();
        }

        return loggedIn;
    }


    public void setLoggedIn(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("loggedIn", true);
        editor.apply();
        EMWebService.loggedIn = true;

        saveUser();
    }

    private void saveUser() {
        if(UNOAppState.currUser != null){
            SharedPreferences.Editor prefsEditor = sharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(UNOAppState.currUser);
            prefsEditor.putString("user", json);
            prefsEditor.apply();
        }else{
//            Log.d("ERROR: ","user obj is null");
        }
    }

    public void getUser() {
        String userInfoJsonString = sharedPref.getString("user", null);
        if(userInfoJsonString != null){
            UNOAppState.currUser = new Gson().fromJson(userInfoJsonString, User.class);
        }else{
//            Log.d("Error: ", "User info not saved");
        }
    }

    public void setLoggedOut(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("loggedIn");
        editor.remove("user");
        editor.remove("sessionToken");
        editor.remove("lastViewedProjectId");
        editor.apply();

//        UNOAppState.getInstance().initialized = false; // for coming in from notification
        UNOAppState.getInstance().setLoggedOut();

//        updateNotificationCount(0);
        EMWebService.loggedIn = false;
    }

    public static ArrayList<String> buildUsersChallengedArray(JsonArray usersChallenged){
        ArrayList<String> usernames = new ArrayList<>();

        for(JsonElement user : usersChallenged){
            usernames.add(user.getAsJsonObject().get("username").getAsString());
        }

        return usernames;
    }

    public void savePhoto(File photoFile) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("profilePhoto", photoFile.getPath());
        editor.apply();
    }

    public String getProfilePhoto(){
        String userInfoJsonString = sharedPref.getString("profilePhoto", null);
        if(userInfoJsonString != null){
            return userInfoJsonString;
        }else{
            return "";
        }
    }
}

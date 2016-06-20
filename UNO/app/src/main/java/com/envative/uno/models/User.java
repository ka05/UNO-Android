package com.envative.uno.models;

import android.content.Context;
import android.util.Log;

import com.envative.emoba.delegates.Callback;
import com.envative.uno.comms.UNOUtil;
import com.google.gson.JsonObject;

import java.io.File;

/**
 * Created by clay on 5/30/16.
 */
public class User {

    public String id;
    public String email;
    public String username;
    public String socketId;
    public String token;
    public String profileImgUrl = "";
    public String profileImgPath = "";
    public int winCount;
    public boolean online;
    public boolean inAGame;

    public User(JsonObject user, Context context){
        this.id = user.get("id").getAsString();
        this.email = user.get("email").getAsString();
        this.profileImgUrl = user.get("profileImg").getAsString();
        this.username = user.get("username").getAsString();
        this.socketId = user.get("socketId").getAsString();
        this.token = (!user.get("token").isJsonNull()) ? user.get("token").getAsString() : "";
        this.winCount = user.get("winCount").getAsInt();
        this.online = user.get("online").getAsBoolean();
        this.inAGame = user.get("inAGame").getAsBoolean();

    }

    public void handleSaveProfileImage(Context context, final Callback callback) {
        // image filename will always be coming from server :. must convert to current filename

        // if the path value was not set yet
        if(this.profileImgPath != null){
            if(!this.profileImgPath.equals("")){
                Log.d("handleSaveProfileImage", "all set");
                return;
            }
        }

        if(!this.profileImgUrl.equals("")){

            String profileImgFilePath = UNOUtil.get(context).baseImageDirectory + this.profileImgUrl.replace("/media/users/", "");
            File profileImgFile = new File(profileImgFilePath);
            Log.d("handleSaveProfileImage", profileImgFile.getPath());
            // if the profile image doesnt exist save it
            if(!profileImgFile.exists()){
                Log.d("profileImageExists", "false");
                UNOUtil.get(context).saveImage(context, this.profileImgUrl, new Callback() {
                    @Override
                    public void callback(Object object) {
                        profileImgPath = (String)object;
                        callback.callback(null);
                    }
                });
            }else{
                Log.d("profileImageExists", "true");
                if(profileImgFilePath != null){
                    this.profileImgPath = profileImgFilePath;
                }
                callback.callback(null);
            }
        }else{

            callback.callback(null);
        }
    }
}

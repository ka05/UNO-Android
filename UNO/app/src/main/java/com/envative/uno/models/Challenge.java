package com.envative.uno.models;

import android.text.TextUtils;

import com.envative.uno.comms.UNOAppState;
import com.envative.uno.comms.UNOUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by clay on 5/30/16.
 */
public class Challenge {

    public String id = "";
    public String challenger = "";
    public ArrayList<String> usersChallenged = new ArrayList<>();
    public String timestamp = "";
    public String status = "";
    public String displayText = "";


    public Challenge(JsonObject challengeObj){
        this.id = challengeObj.get("id").getAsString();
        this.challenger = challengeObj.get("challenger").getAsJsonObject().get("username").getAsString();
        this.usersChallenged = UNOUtil.buildUsersChallengedArray(challengeObj.get("usersChallenged").getAsJsonArray());
        this.timestamp = challengeObj.get("timestamp").getAsString();
        this.status = challengeObj.get("status").getAsString();

        if(challenger.equals(UNOAppState.currUser.username)){
            displayText = "You challenged " + TextUtils.join(", ", usersChallenged) + " (" + status + ")";
        }else{
            displayText = "Challenge from " + challenger + " (" + status + ")";
        }
    }
}

package com.envative.uno.models;

import android.util.Log;

import com.envative.uno.comms.UNOAppState;
import com.envative.uno.comms.UNOUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by clay on 6/2/16.
 */
public class Player {

    public String username = "";
    public String id = "";
    public String profileImgUrl = "";
    public String profileImgPath = "";
    public boolean inGame = false;
    public int cardCount = 0;
    public boolean calledUno = false;
    public boolean isMyTurn = false;
    public ArrayList<Card> hand = new ArrayList<>();

    public Player(JsonObject playerObj){
        if(playerObj != null){
            Log.d("UNO Game Player", " playerObj not null");
            username = playerObj.get("username").getAsString();
            id = playerObj.get("id").getAsString();

            if(playerObj.get("profileImg") != null){
                profileImgUrl = UNOAppState.devURL + File.separator + playerObj.get("profileImg").getAsString();
                profileImgPath = buildProfileImgPath(playerObj.get("profileImg").getAsString());
            }

            inGame = playerObj.get("inGame").getAsBoolean();
            calledUno = playerObj.get("calledUno").getAsBoolean();
            isMyTurn = playerObj.get("isMyTurn").getAsBoolean();

            if(playerObj.getAsJsonArray("hand") != null){
                JsonArray handCards = playerObj.getAsJsonArray("hand");

                hand.clear();
                for(JsonElement card : handCards){
                    hand.add( new Card( card.getAsJsonObject() ) );
                }
            }

            if(playerObj.get("cardCount") != null) {
                cardCount = playerObj.get("cardCount").getAsInt();
            }else{
                cardCount = hand.size();
            }
        }else{
            Log.d("UNO Game Player", " playerObj is null");
        }
    }

    public String buildProfileImgPath(String imageUrl){
        String imagePath = imageUrl.replace("/media/users/" ,"");
        return UNOUtil.baseImageDirectory + imagePath;
    }
}

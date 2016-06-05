package com.envative.uno.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by clay on 6/2/16.
 */
public class Player {

    public String username = "";
    public String id = "";
    public boolean inGame = false;
    public String cardCount = "";
    public boolean calledUno = false;
    public boolean isMyTurn = false;
    public ArrayList<Card> hand = new ArrayList<>();

    public Player(JsonObject playerObj){

        username = playerObj.get("username").getAsString();
        id = playerObj.get("id").getAsString();
        inGame = playerObj.get("inGame").getAsBoolean();
        calledUno = playerObj.get("calledUno").getAsBoolean();
        isMyTurn = playerObj.get("isMyTurn").getAsBoolean();
        cardCount = playerObj.get("cardCount").getAsString();

        JsonArray handCards = playerObj.getAsJsonArray("hand");

        hand.clear();
        for(JsonElement card : handCards){
            hand.add( new Card( card.getAsJsonObject() ) );
        }
    }
}

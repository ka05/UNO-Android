package com.envative.uno.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by clay on 5/30/16.
 */
public class UNOGame {

    public String winner;
    public String id;
    public boolean allPlayersInGame = false;
    public String status;
    public Player currPlayer;
    public ArrayList<Player> players;
    public ArrayList<Card> discardPile;

    public UNOGame(){

    }
    public UNOGame(JsonObject gameData){
        winner = gameData.get("username").getAsString();

        JsonArray playersArr = gameData.getAsJsonArray("players");
        for(JsonElement player : playersArr){
            players.add( new Player( player.getAsJsonObject() ) );
        }
    }
}

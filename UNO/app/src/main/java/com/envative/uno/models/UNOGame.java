package com.envative.uno.models;

import com.envative.uno.comms.UNOAppState;
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
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Card> discardPile = new ArrayList<>();

    public UNOGame(){

    }
    public UNOGame(JsonObject gameData){
        winner = gameData.get("winner").getAsString();
        allPlayersInGame = gameData.get("allPlayersInGame").getAsBoolean();
        status = gameData.get("status").getAsString();

        // handle populating players
        JsonArray playersArr = gameData.getAsJsonArray("players");
        for(JsonElement player : playersArr){
            String username = player.getAsJsonObject().get("username").getAsString();

            // if player is current user set as currPlayer
            if(username.equals(UNOAppState.currUser.username)){
                currPlayer = new Player(player.getAsJsonObject());
            }
            // not the current user add them to players array
            else{
                players.add( new Player( player.getAsJsonObject() ) );
            }
        }

        // handle populating discard pile
        JsonArray discardPileArray = gameData.getAsJsonArray("discardPile");
        for(JsonElement card : discardPileArray){
            discardPile.add(new Card(card.getAsJsonObject()));
        }
    }

    //{
    // "msg":"success",
    // "data":{
    // "_id":"575f432f1394fdc57575d181",
    // "discardPile":[{"cardName":"Yellow 6","svgName":"y6","color":"yellow","value":"6"}],
    // "players":[
    // {
    // "id":"5754614e8971debc5fa6f811",
    // "username":"test2",
    // "calledUno":false,
    // "cardCount":7,
    // "inGame":false,
    // "isMyTurn":true
    // },
    // {
    // "id":"57534806b3043ccb37158d9f",
    // "hand":[
    // {"cardName":"Yellow 7","svgName":"y7","color":"yellow","value":"7"},
    // {"cardName":"Red 7","svgName":"r7","color":"red","value":"7"},
    // {"cardName":"Red 5","svgName":"r5","color":"red","value":"5"},
    // {"cardName":"Blue Reverse","svgName":"br","color":"blue","value":"reverse"},
    // {"cardName":"Red 4","svgName":"r4","color":"red","value":"4"},
    // {"cardName":"Yellow 6","svgName":"y6","color":"yellow","value":"6"},
    // {"cardName":"Green 8","svgName":"g8","color":"green","value":"8"}
    // ],
    // "username":"test",
    // "calledUno":false,
    // "inGame":false,
    // "isMyTurn":false
    // }
    // ],
    // "allPlayersInGame":false,
    // "status":"created",
    // "winner":""
    // }
    // }
}

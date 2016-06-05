package com.envative.uno.models;

import com.google.gson.JsonObject;

/**
 * Created by clay on 5/30/16.
 */
public class User {

    public String id;
    public String email;
    public String username;
    public String socketId;
    public String token;
    public int winCount;
    public boolean online;
    public boolean inAGame;

    public User(JsonObject user){
        this.id = user.get("id").getAsString();
        this.email = user.get("email").getAsString();
        this.username = user.get("username").getAsString();
        this.socketId = user.get("socketId").getAsString();
        this.token = (!user.get("token").isJsonNull()) ? user.get("token").getAsString() : "";
        this.winCount = user.get("winCount").getAsInt();
        this.online = user.get("online").getAsBoolean();
        this.inAGame = user.get("inAGame").getAsBoolean();
    }
}

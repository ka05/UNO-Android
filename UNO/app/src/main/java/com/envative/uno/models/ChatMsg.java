package com.envative.uno.models;

import com.google.gson.JsonObject;

/**
 * Created by clay on 5/30/16.
 */
public class ChatMsg {
    public String sender = "";
    public String message = "";
    public String timestamp = "";

    public ChatMsg(JsonObject chatMsgObj){
        sender = chatMsgObj.get("sender").getAsString();
        message = chatMsgObj.get("message").getAsString();
        timestamp = chatMsgObj.get("timestamp").getAsString();
    }
}

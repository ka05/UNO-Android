package com.envative.uno.comms;

import com.envative.uno.models.Challenge;
import com.envative.uno.models.ChatMsg;
import com.envative.uno.models.UNOGame;
import com.envative.uno.models.User;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by clay on 5/29/16.
 */
public class UNOAppState {
    private static final UNOAppState instance = new UNOAppState();

    public static UNOAppState getInstance(){
        return instance;
    }

//    public static final String devURL = "http://192.168.2.2:3001";// "localhost:3001";
    public static final String devURL = "http://192.168.1.135:3001";// "localhost:3001";
    public static final String serviceURL = "http://uno-server.herokuapp.com";

    public static ArrayList<ChatMsg> chatMsgArray = new ArrayList<>();
    public static ArrayList<User> activeUsers = new ArrayList<>();
    public static ArrayList<Challenge> sentChallenges = new ArrayList<>();
    public static ArrayList<Challenge> receivedChallenges = new ArrayList<>();
//    public static ArrayList<String> usersToChallenge = new ArrayList<>();
    public static boolean loggedIn = false;
    public static String currChallengeId= "";
    public static String currGameId= "";
    public static String preGameLobbyMsg= "";
    public static UNOGame currGame = new UNOGame();
    public static JsonObject currGameJSON = null;
    public static UNOGame tempGameJSON;
    public static User currUser;

    public static boolean inGameOrGameLobby = false;
    public static boolean currUserIsChallenger = false;
    public static boolean canSayUno = false;


    public void setLoggedOut() {
        // de initialize everything

    }
}

package com.envative.uno.comms;

import com.envative.uno.R;
import com.envative.uno.models.Challenge;
import com.envative.uno.models.ChatMsg;
import com.envative.uno.models.UNOGame;
import com.envative.uno.models.User;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by clay on 5/29/16.
 */
public class UNOAppState {
    private static final UNOAppState instance = new UNOAppState();

    public static UNOAppState getInstance(){
        instance.initCardNames();

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

    public static HashMap<String, Integer> cardNames = new HashMap<>();


    public void initCardNames(){
        if(!(cardNames.size() > 0)){
            cardNames.put("cb", R.drawable.cb);

            // blue cards
            cardNames.put("b0", R.drawable.b0);
            cardNames.put("b1", R.drawable.b1);
            cardNames.put("b2", R.drawable.b2);
            cardNames.put("b3", R.drawable.b3);
            cardNames.put("b4", R.drawable.b4);
            cardNames.put("b5", R.drawable.b5);
            cardNames.put("b6", R.drawable.b6);
            cardNames.put("b7", R.drawable.b7);
            cardNames.put("b8", R.drawable.b8);
            cardNames.put("b9", R.drawable.b9);
            cardNames.put("bd", R.drawable.bd);
            cardNames.put("br", R.drawable.br);
            cardNames.put("bs", R.drawable.bs);

            // red cards
            cardNames.put("r0", R.drawable.r0);
            cardNames.put("r1", R.drawable.r1);
            cardNames.put("r2", R.drawable.r2);
            cardNames.put("r3", R.drawable.r3);
            cardNames.put("r4", R.drawable.r4);
            cardNames.put("r5", R.drawable.r5);
            cardNames.put("r6", R.drawable.r6);
            cardNames.put("r7", R.drawable.r7);
            cardNames.put("r8", R.drawable.r8);
            cardNames.put("r9", R.drawable.r9);
            cardNames.put("rd", R.drawable.rd);
            cardNames.put("rr", R.drawable.rr);
            cardNames.put("rs", R.drawable.rs);

            // green cards
            cardNames.put("g0", R.drawable.g0);
            cardNames.put("g1", R.drawable.g1);
            cardNames.put("g2", R.drawable.g2);
            cardNames.put("g3", R.drawable.g3);
            cardNames.put("g4", R.drawable.g4);
            cardNames.put("g5", R.drawable.g5);
            cardNames.put("g6", R.drawable.g6);
            cardNames.put("g7", R.drawable.g7);
            cardNames.put("g8", R.drawable.g8);
            cardNames.put("g9", R.drawable.g9);
            cardNames.put("gd", R.drawable.gd);
            cardNames.put("gr", R.drawable.gr);
            cardNames.put("gs", R.drawable.gs);

            // yellow cards
            cardNames.put("y0", R.drawable.y0);
            cardNames.put("y1", R.drawable.y1);
            cardNames.put("y2", R.drawable.y2);
            cardNames.put("y3", R.drawable.y3);
            cardNames.put("y4", R.drawable.y4);
            cardNames.put("y5", R.drawable.y5);
            cardNames.put("y6", R.drawable.y6);
            cardNames.put("y7", R.drawable.y7);
            cardNames.put("y8", R.drawable.y8);
            cardNames.put("y9", R.drawable.y9);
            cardNames.put("yd", R.drawable.yd);
            cardNames.put("yr", R.drawable.yr);
            cardNames.put("ys", R.drawable.ys);

            // wild cards

            // wild draw 4
            cardNames.put("wd", R.drawable.wd);
            cardNames.put("wd_blue", R.drawable.wd_blue);
            cardNames.put("wd_green", R.drawable.wd_green);
            cardNames.put("wd_red", R.drawable.wd_red);
            cardNames.put("wd_yellow", R.drawable.wd_yellow);

            // regular wild
            cardNames.put("ww", R.drawable.ww);
            cardNames.put("ww_blue", R.drawable.ww_blue);
            cardNames.put("ww_green", R.drawable.ww_green);
            cardNames.put("ww_red", R.drawable.ww_red);
            cardNames.put("ww_yellow", R.drawable.ww_yellow);
        }
    }

    public void setLoggedOut() {
        // de initialize everything

    }
}

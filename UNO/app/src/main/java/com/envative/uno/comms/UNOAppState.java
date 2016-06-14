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

    public static final String devURL = "http://192.168.2.2:3001";// "localhost:3001";
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

    public static String helpText = "Setup\n" +
            "The game is for 2-10 players, ages 7 and over. Every player starts with seven cards, and they are dealt face down. The rest of the cards are placed in a Draw Pile face down. Next to the pile a space should be designated for a Discard Pile. The top card should be placed in the Discard Pile, and the game begins!\n" +
            "\n" +
            "Game Play\n" +
            "The first player is normally the player to the left of the dealer (you can also choose the youngest player) and gameplay usually follows a clockwise direction. Every player views his/her cards and tries to match the card in the Discard Pile.You have to match either by the number, color, or the symbol/Action. For instance, if the Discard Pile has a red card that is an 8 you have to place either a red card or a card with an 8 on it. You can also play a Wild card (which can alter current color in play). If the player has no matches or they choose not to play any of their cards even though they might have a match, they must draw a card from the Draw pile. If that card can be played, play it. Otherwise, the game moves on to the next person in turn. You can also play a Wild card, or a Wild Draw Four card on your turn.\n" +
            "\n" +
            "Note: If the first card turned up from the Draw Pile (to form the Discard Pile) is an Action card, the Action from that card applies and must be carried out. The exceptions are if the Wild or Wild Draw Four cards are turned up, in which case – Return them to the Draw Pile, shuffle them, and turn over a new card. At any time, if the Draw Pile becomes depleted and no one has yet won the round, take the Discard Pile, shuffle it, and turn it over to regenerate a new Draw Pile. There are two different ways to play regarding drawing new cards. The Official Uno Rules states that after a card is drawn the player can discard it if it is a match, or if not, play passes on to the next player. The other type is where players continue to draw cards until they have a match, even if it is 10 times. The game continues until a player has one card left. The moment a player has just one card they must yell “UNO!”. If they are caught not saying “Uno” by another player before any card has been played, the player must draw two new cards. Once a player has no cards remaining, the game round is over, points are scored, and the game begins over again. Normally, everyone tries to be the first one to achieve 500 points, but you can also choose whatever points number to win the game, as long as everyone agrees to it.\n" +
            "\n" +
            "Action Cards\n" +
            "Besides the number cards, there are several other cards that help mix up the game. These are called Action or Symbol cards.\n" +
            "\n" +
            "\n" +
            "Reverse – If going clockwise, switch to counterclockwise or vice versa.\n" +
            "Skip – When a player places this card, the next player has to skip their turn. If turned up at the beginning, the first player loses his/her turn.\n" +
            "Draw Two – When a person places this card, the next player will have to pick up two cards and forfeit his/her turn.\n" +
            "Wild – This card represents all four colors, and can be placed on any card. The player has to state which color it will represent for the next player. It can be played regardless of whether another card is available.\n" +
            "Wild Draw Four – This acts just like the wild card except that the next player also has to draw four cards. With this card, you must have no other alternative cards to play that matches the color of the card previously played. If you play this card illegally, you may be challenged by the other player to show your hand. If guilty, you need to draw 4 cards. If not, the challenger needs to draw 6 cards instead.\n" +
            "Two Player & Four Player Rules\n" +
            "For four players (two-partner teams), players sit opposite their partners, and play until one of either partner goes out with one Uno card left. Scoring for the winning team is done by adding up all the points from opposing partner’s hands. For two players, there is a slight change of rules:\n" +
            "\n" +
            "Reverse works like Skip\n" +
            "Play Skip, and you may immediately play another card\n" +
            "If you play a Draw Two or Wild Draw Four card, your opponent has to draw the number of cards required, and then play immediately resumes back on your turn.";

    public static void initCardNames(){
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

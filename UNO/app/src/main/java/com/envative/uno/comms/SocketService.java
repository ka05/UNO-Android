package com.envative.uno.comms;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.envative.emoba.delegates.ActivityWithIndicator;
import com.envative.uno.activities.LoginActivity;
import com.envative.uno.activities.UNOActivity;
import com.envative.uno.fragments.ChallengeFragment;
import com.envative.uno.fragments.ChallengeModalFragment;
import com.envative.uno.fragments.GameFragment;
import com.envative.uno.fragments.LobbyFragment;
import com.envative.uno.fragments.LoginFragment;
import com.envative.uno.fragments.PreGameLobbyFragment;
import com.envative.uno.fragments.SignupFragment;
import com.envative.uno.models.Card;
import com.envative.uno.models.Challenge;
import com.envative.uno.models.ChatMsg;
import com.envative.uno.models.SocketDelegateType;
import com.envative.uno.models.UNOGame;
import com.envative.uno.models.User;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by clay on 5/29/16.
 */
public class SocketService {

    private static final String TAG = "Socket Service";
    private static SocketService instance;
    private Context context;
    private Manager manager; // emitter manager
    private static Socket loginSocket;
    private static Socket gameSocket;
    private Fragment loginDelegate;
    private Fragment signupDelegate;
    private Fragment lobbyDelegate;
    private Fragment preGameLobbyDelegate;
    private Fragment gameDelegate;
    private Fragment challengeModalDelegate;
    private Fragment chatDelegate;

    private boolean loginFromSignupPage = false;

    public boolean isLoginFromSignupPage() {
        return loginFromSignupPage;
    }

    public void setLoginFromSignupPage(boolean loginFromSignupPage) {
        this.loginFromSignupPage = loginFromSignupPage;
    }

    public enum ChallengeResType{
        Cancel(0),
        Accept(1),
        Decline(2);

        public int rawValue;

        ChallengeResType(int value){
            this.rawValue = value;
        }
    }

    private SocketService(Context context){
        this.context  = context;
        initSocketConnection();
    }

    public static SocketService get(Context context) {
        if (instance == null) {
            instance = new SocketService(context.getApplicationContext());
        }

        instance.context = context;
        return instance;
    }

    //region main socket handling

    /**
     *
     */
    private void initSocketConnection() {
        if(loginSocket == null){
            try {
                manager = new Manager(new URI(UNOAppState.devURL));
                loginSocket = manager.socket("/login");
                gameSocket = manager.socket("/game");
                Log.d("setup manager", "login");
                Log.d("setup manager", "game");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }


        if(!loginSocket.connected()){
            Log.d("loginSocket ", "not connected yet");
            Log.d("gameSocket ", "not connected yet");
            loginSocket.connect();

            loginSocket.on("connected", onConnection);
            loginSocket.on("validateLogin", onValidateLogin);
            loginSocket.on("addUser", onAddUser);
            loginSocket.on("validateToken", onValidateToken);

            // Lobby Calls
            loginSocket.on("handleChallenge", onHandleChallenge);
            loginSocket.on("notifyNeedsToUpdateChallenges", onNotifyNeedsToUpdateChallenges);
            loginSocket.on("getOnlineUsers", onGetOnlineUsers);
            loginSocket.on("getSentChallenges", onGetSentChallenges);
            loginSocket.on("getChallenges", onGetChallenges);
            loginSocket.on("getChallenge", onGetChallenge);
            loginSocket.on("sendChallenge", onSendChallenge);


            // Chat Calls
            loginSocket.on("chatMsg", onChatMsg);
            loginSocket.on("getChat", onGetChat);
        }

        if(!gameSocket.connected()){
            gameSocket.connect();

            // Pregame Lobby Calls
            gameSocket.on("getGameByChallengeId", onGetGameByChallengeId);
            gameSocket.on("checkPlayersInGameRoom", onCheckPlayersInGameRoom);
            gameSocket.on("notifyNeedsToUpdateGame", onNotifyNeedsToUpdateGame);

            // Game Calls
            gameSocket.on("createGame", onCreateGame);
            gameSocket.on("quitGame", onQuitGame);
            gameSocket.on("sayUno", onSayUno);
            gameSocket.on("drawCard", onDrawCard);
            gameSocket.on("challengeUno", onChallengeUno);
            gameSocket.on("getGameByGameId", onGetGameByGameId);
            gameSocket.on("setPlayerInGame", onSetPlayerInGame);
            gameSocket.on("validateMove", onValidateMove);
        }
    }

    /**
     *
     */
    public void destroySocketConnection(){
        loginSocket.disconnect();

        // call off for all listeners
        loginSocket.off("connected", onConnection);
        loginSocket.off("validateLogin", onValidateLogin);
        loginSocket.off("addUser", onAddUser);
        loginSocket.off("validateToken", onValidateToken);

        // Lobby Calls
        loginSocket.off("handleChallenge", onHandleChallenge);
        loginSocket.off("notifyNeedsToUpdateChallenges", onNotifyNeedsToUpdateChallenges);
        loginSocket.off("getOnlineUsers", onGetOnlineUsers);
        loginSocket.off("getSentChallenges", onGetSentChallenges);
        loginSocket.off("getChallenges", onGetChallenges);
        loginSocket.off("getChallenge", onGetChallenge);
        loginSocket.off("sendChallenge", onSendChallenge);

        // Pregame Lobby Calls
        gameSocket.off("getGameByChallengeId", onGetGameByChallengeId);
        gameSocket.off("checkPlayersInGameRoom", onCheckPlayersInGameRoom);
        gameSocket.off("notifyNeedsToUpdateGame", onNotifyNeedsToUpdateGame);

        // Game Calls
        gameSocket.off("createGame", onCreateGame);
        gameSocket.off("quitGame", onQuitGame);
        gameSocket.off("sayUno", onSayUno);
        gameSocket.off("drawCard", onDrawCard);
        gameSocket.off("challengeUno", onChallengeUno);
        gameSocket.off("getGameByGameId", onGetGameByGameId);
        gameSocket.off("setPlayerInGame", onSetPlayerInGame);
        gameSocket.off("validateMove", onValidateMove);

        // Chat Calls
        loginSocket.off("chatMsg", onChatMsg);
        loginSocket.off("getChat", onGetChat);
    }

    public void attemptLogin(JsonObject credentials){
        Log.d(TAG, "attemptLogin called");
        loginSocket.emit("validateLogin", credentials);
    }

    private Emitter.Listener onValidateLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("validateLoginRes", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    boolean success = data.get("valid").getAsBoolean();
                    if(success){
                        UNOAppState.currUser = new Gson().fromJson(data.get("user").toString(), User.class);

                        UNOUtil.get(context).setLoggedIn();
                        Toast.makeText(context, "Login success", Toast.LENGTH_LONG).show();
                    }
                    if(loginFromSignupPage){
                        ((LoginActivity)signupDelegate.getActivity()).hideActivityIndicator();
                        ((SignupFragment)signupDelegate).loginCallback.callback(success);
                    }else{
                        ((LoginActivity)loginDelegate.getActivity()).hideActivityIndicator();
                        ((LoginFragment)loginDelegate).attemptLoginCallback.callback(success);
                    }
                }
            });
        }
    };

    private Emitter.Listener onConnection = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("connection", "args: " + args[0]);

                }
            });
        }
    };

    public void signup(JsonObject userData){
        loginSocket.emit("addUser", userData);
    }

    private Emitter.Listener onAddUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("addUser", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String message = data.get("msg").getAsString();
                    if(message.equals("success")){
                        // if successful parse out the user object
                        UNOAppState.currUser = new Gson().fromJson(data.get("user").toString(), User.class);
                    }
                    ((SignupFragment)signupDelegate).attemptSignupCallback.callback(message);
                    ((LoginActivity)signupDelegate.getActivity()).hideActivityIndicator();
                }
            });
        }
    };


    private Emitter.Listener onValidateToken = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("validateToken", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();

                    if(success.equals("success")){
                        Log.d("onValidateToken", "valid");
                    }else{
                        Log.d("onValidateToken", "invalid");
                    }
                }
            });
        }
    };

    public void sayUno(){
        // ensure they only have one card
        if(UNOAppState.currGame.currPlayer.hand.size() <= 2){
            gameSocket.emit("sayUno", buildBasicGamePacket());
        }
    }

    private Emitter.Listener onSayUno = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("validateToken", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        setCurrGame(data.get("data").getAsJsonObject());
                    }else{
                        Log.d(TAG, "ERROR: sayUno error!");
                    }

                }
            });
        }
    };


    public void challengeUno(){
        gameSocket.emit("challengeUno", buildBasicGamePacket());
    }

    private Emitter.Listener onChallengeUno = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onChallengeUno", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        setCurrGame(data.get("data").getAsJsonObject());
                    }else{
                        Log.d(TAG, "ERROR: sayUno error!");
                    }

                }
            });
        }
    };


    public void startGame(){
        gameSocket.emit("createGame", buildBasicChallengePacket());
    }

    private Emitter.Listener onCreateGame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onCreateGame", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("success")){
//                        getCurrChallengeInterval?.invalidate()
                        // show pregame lobby and poll for challenge to make sure everyone has joined and that nobody cancelled
                        UNOAppState.currUserIsChallenger = true;

                        Log.d(TAG,"CURRGAME "+ data.get("data").toString());
                        UNOAppState.currGameId = data.get("data").getAsJsonObject().get("_id").getAsString();
                        setCurrGame(data.get("data").getAsJsonObject());
                        Log.d("currGameId: ", UNOAppState.currGameId +":");

                        UNOAppState.inGameOrGameLobby = true;
                        UNOAppState.preGameLobbyMsg = "Please wait for everyone to join the game.";
                        // navigate to Pre-game-lobby
                        checkPlayersInGameRoom(UNOAppState.currGameId);
                    }else{
                        Log.d(TAG, "ERROR: sayUno error!");
                    }

                }
            });
        }
    };

    public void quitGame(){
        gameSocket.emit("quitGame", buildBasicGamePacket());
    }

    private Emitter.Listener onQuitGame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onCreateGame", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        UNOAppState.inGameOrGameLobby = false; // no longer in game or game lobby

//                        getCurrGameInterval?.invalidate() // clear get game interval
//                        getCurrGameChatInterval?.invalidate() // clear get game chat interval
                    }else{
                        Log.d(TAG, "ERROR: sayUno error!");
                    }

                }
            });
        }
    };


    public void getGameByChallengeId(){
        gameSocket.emit("getGameByChallengeId", buildBasicChallengePacket());
    }


    private Emitter.Listener onGetGameByChallengeId = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onGetGameByChallengeId", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        setPlayerInGame(UNOAppState.currChallengeId);
                        UNOAppState.currGameId = data.get("data").getAsJsonObject().get("_id").getAsString();
                        setCurrGame(data.get("data").getAsJsonObject());

                        ((PreGameLobbyFragment)preGameLobbyDelegate).showGame(); // opens game screen
                    }else{
                        Log.d(TAG, "ERROR: onGetGameByChallengeId error!");
                        getGameByChallengeId();
                    }
                }
            });
        }
    };



    public void getCurrGame(){
        gameSocket.emit("getGameByGameId", buildBasicGamePacket());
    }


    private Emitter.Listener onGetGameByGameId = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onGetGameByGameId", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        JsonObject tempGameJSON = data.get("data").getAsJsonObject();
                        UNOGame tempGame = new UNOGame(tempGameJSON);

                        boolean allPlayersInGame = true;
                        // ensure all players are still in game
                        for(int i = 0, j = tempGame.players.size(); i<j; i++){
                            // if player is not inGame
                            if( !tempGame.players.get(i).inGame ){
//                                getCurrGameInterval?.invalidate() // clear get game interval
//                                getCurrGameChatInterval?.invalidate() // clear get game chat interval

                                allPlayersInGame = false; // for local logic
                                UNOAppState.inGameOrGameLobby = false;
                                Toast.makeText(context, tempGame.players.get(i).username + " has left the game so the game must end!", Toast.LENGTH_LONG).show();
                                // FIND TOAST LIKE MSG TO SHOW HERE
                                // due to materialize toast callback issue
                                quitGame();
                            }
                        }
                        // if everyone is still here
                        if(allPlayersInGame){
                            // if something has changed
                            if(UNOAppState.currGameJSON != tempGameJSON){

                                // if its not my turn update stuff so i know whats going on
                                if( ( !UNOAppState.currGame.currPlayer.isMyTurn ) ||
                                        ( UNOAppState.currGame.currPlayer.hand.size() == 0 ) ){

                                    setCurrGame(tempGameJSON);

                                    // if there is a winner
                                    checkWinner(tempGame);
                                }else{
                                    // it is my turn just check for if someone said uno

                                    // update my hand if the length differs from before
                                    if(tempGame.currPlayer.hand.size() != UNOAppState.currGame.currPlayer.hand.size()){
                                        setCurrGame(tempGameJSON);
                                    }
                                }
                            }
                            // nothing changed in the new game object
                            else{
                                // check if there is a winner
                                checkWinner(tempGame);
                            }
                        }
                    }else{
                        Log.d(TAG, "ERROR: sayUno error!");
                    }
                }
            });
        }
    };

    public void setCurrGame(JsonObject gameObj){
        UNOAppState.currGameJSON = gameObj;
        UNOAppState.currGame = new UNOGame(gameObj); // re initialize
        if(gameDelegate != null){
            if(!gameDelegate.isDetached()){
                ((GameFragment)gameDelegate).updateGameView();
            }
        }
    }

    public void checkWinner(UNOGame newGame){
        Log.d("checkWinner","winner: " + newGame.winner);
        if(newGame.winner.equals("")){
//            getCurrGameInterval.invalidate() // clear get game interval
//            getCurrGameChatInterval.invalidate() // clear get game chat interval
            UNOAppState.inGameOrGameLobby = false;
            UNOAppState.currGame = new UNOGame(); // reset game
//            gameVC!.performSegueWithIdentifier("game-to-lobby", sender: gameVC!)
        }
    }


    /* GAME FUNCTIONS */

    public void drawCard(){
        if (UNOAppState.currGame.currPlayer.isMyTurn) {
            gameSocket.emit("drawCard", buildBasicGamePacket());
        }else{
            Toast.makeText(context, "Its not your turn", Toast.LENGTH_LONG).show();
        }
    }

    private Emitter.Listener onDrawCard = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onDrawCard", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        setCurrGame(data.get("data").getAsJsonObject());
                    }else{
                        Log.d(TAG, "ERROR: onDrawCard error!");
                    }
                }
            });
        }
    };

    // validates card is good to play
    public void playCard(Card card){
        if (UNOAppState.currGame.currPlayer.isMyTurn) {
            // if it is wild or wild draw4
            if(card.svgName == "ww" || card.svgName == "wd"){
                //TODO: complete handling for wild card choice

            }else{
                // regular card - play it
                handleValidateMove(card.svgName);
            }
        }else{
            Toast.makeText(context, "Its not your turn", Toast.LENGTH_LONG).show();
        }
    }

    public void handleValidateMove(String svgName, String chosenColor){
        JsonObject validateMovePacket = new JsonObject();
        validateMovePacket.add("gameId", new JsonPrimitive( UNOAppState.currGameId ));
        validateMovePacket.add("userId", new JsonPrimitive( UNOAppState.currUser.id ));
        validateMovePacket.add("svgName", new JsonPrimitive( svgName ));
        validateMovePacket.add("chosenColor", new JsonPrimitive( chosenColor ));
        Log.d("validateMove Packet", validateMovePacket.toString());
        gameSocket.emit("validateMove", validateMovePacket);
    }


    private Emitter.Listener onValidateMove = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onValidateMove", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        UNOGame tempGame = new UNOGame(data.get("data").getAsJsonObject());
                        checkWinner(tempGame);
                        setCurrGame(data.get("data").getAsJsonObject());
                    }else{
                        Log.d(TAG, "ERROR: onValidateMove error!");
                    }
                }
            });
        }
    };


    private Emitter.Listener onNotifyNeedsToUpdateGame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onNotifyNeedsToUpdateGame", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("true")){
                        getCurrGame();
                    }else{
                        Log.d(TAG, "ERROR: onNotifyNeedsToUpdateGame error!");
                    }
                }
            });
        }
    };



    public void handleValidateMove(String svgName){
        JsonObject validateMovePacket = new JsonObject();
        validateMovePacket.add("gameId", new JsonPrimitive( UNOAppState.currGameId ));
        validateMovePacket.add("userId", new JsonPrimitive( UNOAppState.currUser.id ));
        validateMovePacket.add("svgName", new JsonPrimitive( svgName ));
        Log.d("validateMove Packet", validateMovePacket.toString());
        gameSocket.emit("validateMove", validateMovePacket);
    }

    public void sendChat(String msg){
        JsonObject msgPacket = new JsonObject();
        msgPacket.add("senderId", new JsonPrimitive( UNOAppState.currUser.id ));
        msgPacket.add("roomId", new JsonPrimitive( "1" ));
        msgPacket.add("message", new JsonPrimitive( msg ));

        loginSocket.emit("chatMsg", msgPacket);
    }

    private Emitter.Listener onChatMsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onChatMsg", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        // show success msg in UI

                        ((LobbyFragment)lobbyDelegate).getChatFragment().getAdapter().notifyDataSetChanged();
                    }else{
                        Log.d(TAG, "ERROR: onChatMsg error!");
                    }
                }
            });
        }
    };

    public void getChat(String chatRoomId){
        JsonObject getChatPacket = new JsonObject();
        getChatPacket.add("roomId", new JsonPrimitive( chatRoomId ));

        loginSocket.emit("getChat", getChatPacket);
    }

    private Emitter.Listener onGetChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onGetChat", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = "";
                    success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        if(data.get("data").getAsJsonArray().size() != UNOAppState.chatMsgArray.size()){
                            UNOAppState.chatMsgArray.clear();

                            for(JsonElement chatMsg : data.get("data").getAsJsonArray()) {
                                UNOAppState.chatMsgArray.add(new ChatMsg( chatMsg.getAsJsonObject() ));
                            }
                            ((LobbyFragment)lobbyDelegate).getChatFragment().getAdapter().notifyDataSetChanged();
                        }
                    }else{
                        Log.d(TAG, "ERROR: onCheckPlayersInGameRoom error!");
                    }
                }
            });
        }
    };

    public void getOnlineUsers(){
        JsonObject getOnlineUsersPacket = new JsonObject();
        getOnlineUsersPacket.add("uid", new JsonPrimitive( UNOAppState.currUser.id ));
        loginSocket.emit("getOnlineUsers", getOnlineUsersPacket);
    }

    private Emitter.Listener onGetOnlineUsers = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onGetOnlineUsers", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = "";
                    success = data.get("msg").getAsString();
                    if(success.equals("success")){

                        if(data.get("data").getAsJsonArray().size() != UNOAppState.activeUsers.size()){
                            UNOAppState.activeUsers.clear();

                            for(JsonElement user : data.get("data").getAsJsonArray()) {
                                if(!user.getAsJsonObject().get("username").getAsString().equals(UNOAppState.currUser.username)){
                                    UNOAppState.activeUsers.add(new User( user.getAsJsonObject() ));
                                }
                            }
                        }
                        ((ChallengeModalFragment)challengeModalDelegate).getAdapter().notifyDataSetChanged();
                    }else{
                        Log.d(TAG, "ERROR: onCheckPlayersInGameRoom error!");
                    }
                }
            });
        }
    };

    public void getSentChallenges(){
        if(UNOAppState.currUser != null){
            JsonObject getSentChallengesPacket = new JsonObject();
            getSentChallengesPacket.add("id", new JsonPrimitive( UNOAppState.currUser.id ));

            loginSocket.emit("getSentChallenges", getSentChallengesPacket);
        }
    }

    private Emitter.Listener onGetSentChallenges = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Log.d("onGetSentChallenges", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = "";
                    success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        popChallenges(data.get("data").getAsJsonArray(), "s");
                    }else{
                        Log.d(TAG, "ERROR: onCheckPlayersInGameRoom error!");
                    }
                }
            });
        }
    };

    public void getReceivedChallenges(){
        if(UNOAppState.currUser != null) {
            JsonObject getReceivedChallengePacket = new JsonObject();
            getReceivedChallengePacket.add("id", new JsonPrimitive(UNOAppState.currUser.id));

            loginSocket.emit("getChallenges", getReceivedChallengePacket);
        }
    }

    private Emitter.Listener onGetChallenges = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Log.d("onGetReceivedChallenges", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = "";
                    success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        popChallenges(data.get("data").getAsJsonArray(), "r");
                    }else{
                        Log.d(TAG, "ERROR: onCheckPlayersInGameRoom error!");
                    }
                }
            });
        }
    };

    public void getChallenge(boolean currUserIsChallenger){
        UNOAppState.currUserIsChallenger = currUserIsChallenger;
        JsonObject getChallengePacket = new JsonObject();
        getChallengePacket.add("challengeId", new JsonPrimitive( UNOAppState.currChallengeId ));

        loginSocket.emit("getChallenge", getChallengePacket);
    }

    private Emitter.Listener onGetChallenge = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    Log.d("onGetChallenge", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = "";
                    success = data.get("msg").getAsString();
                    if(success.equals("success")){
    //                getCurrChallengeInterval?.invalidate()
    //                getCurrChallengeInterval = nil

                        JsonObject currChallenge = data.get("data").getAsJsonObject();
                        if(currChallenge.get("status").getAsString().equals("cancelled")){
                            // dismiss view controller here :. go back to lobby
                            ((Activity)context).getFragmentManager().popBackStack(); // go back
                            Toast.makeText(context, "Someone has cancelled this challenge", Toast.LENGTH_LONG).show();
                            // show message saying "Sorry someone has cancelled this challenge"
                        }

                        Log.d(TAG, "UNOAppState.currUserIsChallenger: " + UNOAppState.currUserIsChallenger);
                        UNOAppState.currChallengeId = currChallenge.get("id").getAsString();
                        if(UNOAppState.currUserIsChallenger){


                        }else{
                            // they accepted the challenge
                            // set player in game
                            // the join game

                            setPlayerInGame(UNOAppState.currChallengeId);
                            getGameByChallengeId();
                        }
                    }else{
                        Log.d(TAG, "ERROR: onCheckPlayersInGameRoom error!");
                        getChallenge(UNOAppState.currUserIsChallenger);
                    }
                }
            });
        }
    };

    public void setPlayerInGame(String challengeId){
        UNOAppState.currChallengeId = challengeId;
        gameSocket.emit("setPlayerInGame", buildBasicChallengePacket());
    }

    private Emitter.Listener onSetPlayerInGame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onSetPlayerInGame", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = "";
                    success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        // do nothing as of now
                    }else{
                        Log.d(TAG, "ERROR: onSetPlayerInGame error!");
                    }
                }
            });
        }
    };

    public void checkPlayersInGameRoom(String gameId){
        JsonObject basicGamePacket = new JsonObject();
        basicGamePacket.add("gameId", new JsonPrimitive( gameId ));
        basicGamePacket.add("userId", new JsonPrimitive( UNOAppState.currUser.id));

        gameSocket.emit("checkPlayersInGameRoom", basicGamePacket);
    }

    public void checkPlayersInGameRoom(){
        gameSocket.emit("checkPlayersInGameRoom", buildBasicGamePacket());
    }

    private Emitter.Listener onCheckPlayersInGameRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onCheckPlayersInGameRm", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = "";
                    success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        // go to game via segue
                        if(data.get("data") != null){
                            if(data.get("data").isJsonObject()){
                                setCurrGame(data.get("data").getAsJsonObject());
                            }
                        }
                        ((PreGameLobbyFragment)preGameLobbyDelegate).showGame(); // opens game screen
                    }else{
                        Log.d(TAG, "ERROR: onCheckPlayersInGameRoom error!");
                        checkPlayersInGameRoom();
                    }
                }
            });
        }
    };

    private Emitter.Listener onNotifyNeedsToUpdateChallenges = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onNotifyNeedsToUpdate", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = "";
                    success = data.get("msg").getAsString();
                    if(success.equals("true")){
                        // go to game via segue
                        // TODO: call both getchallenges
                        getSentChallenges();
                        getReceivedChallenges();
                    }else{
                        Log.d(TAG, "ERROR: onNotifyNeedsToUpdateChallenges error!");
                        checkPlayersInGameRoom();
                    }
                }
            });
        }
    };

    public void popChallenges(JsonArray challenges, String sentOrReceived){
//        int prevSize = (sentOrReceived.equals("s")) ? UNOAppState.sentChallenges.size() : UNOAppState.receivedChallenges.size();
//        boolean fetchNew = false;
//
//        if(sentOrReceived.equals("s")){
//            // fetch new if the current size is different than the previous size
//            fetchNew = prevSize != challenges.size();
//            // TODO: more robust solution would be to check if ID's are the same
//
//            // only clear them out if we need to fetch new
//            if(fetchNew){
//                UNOAppState.sentChallenges.clear();
//            }
//        }else{
//            fetchNew = prevSize != challenges.size();
//            if(fetchNew){
//                UNOAppState.receivedChallenges.clear();
//            }
//        }
//
//        Log.d("popChallenges", "sizes: " + prevSize + ":" + challenges.size());
//        if(fetchNew){
//            Log.d("popChallenges", "fetchNew");
//            for(JsonElement challenge : challenges) {
//                if(sentOrReceived.equals("s")){
//                    UNOAppState.sentChallenges.add(new Challenge(challenge.getAsJsonObject()));
//                }else{
//                    UNOAppState.receivedChallenges.add(new Challenge(challenge.getAsJsonObject()));
//                }
//            }
//            ((LobbyFragment)lobbyDelegate).getReceivedChallengesFragment().updateChallengeType(
//                    sentOrReceived.equals("s") ? ChallengeFragment.ChallengeType.Sent : ChallengeFragment.ChallengeType.Received,
//                    false);
//        }
        if(sentOrReceived.equals("s")){
           UNOAppState.sentChallenges.clear();
        }else{
            UNOAppState.receivedChallenges.clear();
        }

        for(JsonElement challenge : challenges) {
            if(sentOrReceived.equals("s")){
                UNOAppState.sentChallenges.add(new Challenge(challenge.getAsJsonObject()));
            }else{
                UNOAppState.receivedChallenges.add(new Challenge(challenge.getAsJsonObject()));
            }
        }
        ChallengeFragment.ChallengeType type = sentOrReceived.equals("s") ? ChallengeFragment.ChallengeType.Sent : ChallengeFragment.ChallengeType.Received;
        if(sentOrReceived.equals("s")){
            ((LobbyFragment)lobbyDelegate).getSentChallengesFragment().updateChallengeType(type, false);
        }else{
            ((LobbyFragment)lobbyDelegate).getReceivedChallengesFragment().updateChallengeType(type, false);
        }
    }

    public void sendChallenge(ArrayList<String> usersToChallenge){
        ((UNOActivity)context).showActivityIndicator();
        Log.d("SendChallenge:UID:", UNOAppState.currUser.id + "::");
        JsonObject sendChallengePacket = new JsonObject();
        sendChallengePacket.add("challengerId", new JsonPrimitive( UNOAppState.currUser.id ));
        JsonArray users = new JsonArray();
        for(String username : usersToChallenge){
            users.add(username);
        }

        sendChallengePacket.add("usersChallenged", users);
        loginSocket.emit("sendChallenge", sendChallengePacket);
    }

    private Emitter.Listener onSendChallenge = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onSendChallenge", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = "";
                    success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        //TODO: update UI
                        Toast.makeText(context, "Challenge Sent!", Toast.LENGTH_LONG).show();
                    }else{
                        Log.d(TAG, "ERROR: sayUno error!");
                        Toast.makeText(context, "Error sending challenge!", Toast.LENGTH_LONG).show();
                    }
                    ((UNOActivity)context).hideActivityIndicator();
                    challengeModalDelegate.getActivity().getFragmentManager().popBackStack();
                    ((ActivityWithIndicator)challengeModalDelegate.getActivity()).hideActivityIndicator();
                }
            });
        }
    };


    public ArrayList<String> buildUsersChallengedArray(JsonObject usersChallenged){
        ArrayList<String> usernames = new ArrayList<>();

        Set<Map.Entry<String, JsonElement>> entrySet = usersChallenged.entrySet();
        for(Map.Entry<String, JsonElement> entry : entrySet) {
            usernames.add( entry.getValue().getAsJsonObject().get("username").getAsString());
        }

        return usernames;
    }

//    public void showChallengeResponseAlert(String title, String msg, boolean sentChallenge){
//        // Create the alert controller
//        let alertController = UIAlertController(title: title, message: msg, preferredStyle: .Alert)
//
//        if(sentChallenge){
//            // Create the actions
//            let cancelChallengeAction = UIAlertAction(title: "Cancel Challenge", style: UIAlertActionStyle.Default) {
//                UIAlertAction in
//                handleChallenge(0){
//                    (result: String) in
//                }
//            }
//            let cancelAction = UIAlertAction(title: "Cancel", style: UIAlertActionStyle.Cancel) {
//                UIAlertAction in
//                NSLog("Cancel Pressed")
//            }
//
//            // Add the actions
//            alertController.addAction(cancelChallengeAction)
//            alertController.addAction(cancelAction)
//        }else{
//            // received challenge
//            // Create the actions
//            let acceptChallengeAction = UIAlertAction(title: "Accept", style: UIAlertActionStyle.Default) {
//                UIAlertAction in
//                handleChallenge(1){
//                    (result: String) in
//                    if(result == "success"){
//                        // challenge successfully accepted
//                        currUserIsChallenger = false
//                        inGameOrGameLobby = true
//                        preGameLobbyMsg = "Please wait for the host to start the game."
//                        lobbyVC!.performSegueWithIdentifier("lobby-pregamelobby", sender: lobbyVC!);
//                    }
//                }
//
//            }
//            let declineChallengeAction = UIAlertAction(title: "Decline", style: UIAlertActionStyle.Default) {
//                UIAlertAction in
//                handleChallenge(2){
//                    (result: String) in
//                }
//            }
//            let cancelAction = UIAlertAction(title: "Cancel", style: UIAlertActionStyle.Cancel) {
//                UIAlertAction in
//            }
//
//            // Add the actions
//            alertController.addAction(acceptChallengeAction);
//            alertController.addAction(declineChallengeAction);
//            alertController.addAction(cancelAction);
//        }
//
//        // Present the controller
//        lobbyVC!.presentViewController(alertController, animated: true, completion: nil)
//    }

    public void handleChallenge(String challengeId, ChallengeResType choice){
        UNOAppState.currChallengeId = challengeId;
        Log.d("handleChallenge","currChallengeId "+ UNOAppState.currChallengeId);

        JsonObject handleChallengePacket = new JsonObject();
        handleChallengePacket.add("id", new JsonPrimitive( UNOAppState.currChallengeId ));
        handleChallengePacket.add("userId", new JsonPrimitive( UNOAppState.currUser.id ));
        handleChallengePacket.add("choice", new JsonPrimitive( choice.rawValue ));

        loginSocket.emit("handleChallenge", handleChallengePacket);
    }

    private Emitter.Listener onHandleChallenge = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onHandleChallenge", "args: " + args[0]);
                    JsonObject data = (new JsonParser()).parse(((JSONObject)args[0]).toString()).getAsJsonObject();
                    String success = data.get("msg").getAsString();
                    if(success.equals("success")){
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(TAG, "ERROR: sayUno error!");
                    }
                }
            });
        }
    };

    //region Utils

    /**
     *
     * @return
     */

    private JsonObject buildBasicGamePacket(){
        JsonObject basicGamePacket = new JsonObject();
        basicGamePacket.add("gameId", new JsonPrimitive( UNOAppState.currGameId ));
        basicGamePacket.add("userId", new JsonPrimitive( UNOAppState.currUser.id));

        return basicGamePacket;
    }

    private JsonObject buildBasicChallengePacket(){
        JsonObject basicGamePacket = new JsonObject();
        basicGamePacket.add("challengeId", new JsonPrimitive( UNOAppState.currChallengeId ));
        basicGamePacket.add("userId", new JsonPrimitive( UNOAppState.currUser.id));

        return basicGamePacket;
    }

    public void setDelegate(Fragment delegate, SocketDelegateType type) {
        switch (type){
            case Login:
                this.loginDelegate = delegate;
                break;
            case Signup:
                this.signupDelegate = delegate;
                break;
            case Lobby:
                this.lobbyDelegate = delegate;
                break;
            case PreGameLobby:
                this.preGameLobbyDelegate = delegate;
                break;
            case Game:
                this.gameDelegate = delegate;
                break;
            case Chat:
                this.chatDelegate = delegate;
                break;
            case ChallengeModal:
                this.challengeModalDelegate = delegate;
                break;
        }
    }
}

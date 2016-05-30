package com.envative.uno.comms;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.envative.emoba.widgets.EMModal;
import com.envative.uno.models.User;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by clay on 5/29/16.
 */
public class SocketService {

    private static SocketService instance;
    private Context context;

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

    private Manager manager;
    private Socket loginSocket;

    // look at how to connect to namespaces

    //region main socket handling

    private void initSocketConnection() {
        try {
            manager = new Manager(new URI(UNOAppState.devURL));
            loginSocket = manager.socket("/login");
            Log.d("setup manager", "login");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(!loginSocket.connected()){
            Log.d("loginSocket ", "not connnected yet");
            loginSocket.connect();

            loginSocket.on("validateLogin", onValidateLogin);
        }
    }

    private void destroySocketConnection(){
        loginSocket.disconnect();

        // call off for all listeners
        loginSocket.off("validateLogin", onValidateLogin);
    }

    public void attemptLogin(JsonObject credentials){
        loginSocket.emit("validateLogin", credentials);
    }

    private Emitter.Listener onValidateLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("validateLoginRes", "args: " + args[0]);
                    JSONObject data = (JSONObject) args[0];
                    boolean success = false;
                    try {
                        success = data.getBoolean("valid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(success){
                        UNOAppState.currUser = new Gson().fromJson(data.toString(), User.class);
                        UNOUtil.get(context).setLoggedIn();
                    }else{
                        EMModal.showMsgDialog(context, "Login Error", "Invalid Combination");
                    }
                }
            });
        }
    };


}

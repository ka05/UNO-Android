package com.envative.uno.comms;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

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
            manager = new Manager(new URI(UNOAppState.serviceURL));
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
                    Log.d("validateLoginRes", "args: " + args);
//                    JSONObject data = (JSONObject) args[0];
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }


                }
            });
        }
    };

    /*

     static func attemptLogin(credentials :[String: String], vc: UIViewController){
        // check logged in
        socket.emitWithAck("validateLogin", credentials)(timeoutAfter: 0) {data in
            var res = JSON(data[0])
            let user = res["user"]
            let valid = res["valid"]
            let username:String = (user["username"].stringValue as AnyObject? as? String)!
            let uid:String = (user["id"].stringValue as AnyObject? as? String)!
            let token:String = (user["token"].stringValue as AnyObject? as? String)!

            if(valid == true){
                // valid login
                UNOUtil.makeLoggedIn(username, uid: uid, token:token)
                vc.dismissViewControllerAnimated(true, completion: nil) // go back to LobbyVC
            }else{
                // invalid login
                UNOUtil.showAlert("Login", msg:"Invalid login", vc: vc)
            }
        }
    }
     */

}

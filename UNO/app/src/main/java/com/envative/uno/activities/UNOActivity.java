package com.envative.uno.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.envative.uno.R;
import com.envative.uno.comms.SocketService;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


public class UNOActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uno);

        SocketService.get(this); // initialize socket service
        setupComponents();
    }

    private void setupComponents() {

        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                Log.d("btnLogin click", "t");
                JsonObject credentials =  new JsonObject();
                credentials.add("username", new JsonPrimitive("clayh"));
                credentials.add("password", new JsonPrimitive("password"));

                SocketService.get(this).attemptLogin(credentials);
                break;
        }
    }


    // NOTES:
    /*

    Activities
    -LoginActivity ( Login / Signup )
    -UNOActivity ( contains fragments for everything post login )

    Fragments
    -Chat Fragment ( re-use in game and lobby )
    -Challenges List Fragment
    -Send Challenge Fragment

    Comms
    -AppState
    -Util
    -SocketService


    Using Socket IO
    http://socket.io/blog/native-socket-io-and-android/

    NEEDED SCREENS

    Login Screen
    Signup Screen
    Lobby ( chat, sent challenges, received challenges, send challenge )
    Pregame Lobby ( waiting for players to join )
    Game Screen
     */
}

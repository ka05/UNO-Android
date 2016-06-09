package com.envative.uno.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.envative.emoba.activities.EMNavigationDrawerActivity;
import com.envative.emoba.widgets.EMModal;
import com.envative.uno.R;
import com.envative.uno.comms.SocketService;
import com.envative.uno.comms.UNOUtil;
import com.envative.uno.fragments.LobbyFragment;
import com.envative.uno.fragments.ProfileFragment;


public class UNOActivity extends EMNavigationDrawerActivity {

    public enum NavigationViews{
        Lobby,
        Profile,
        Logout
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNavigationViewMenu(R.menu.activity_uno_drawer);
        setNavigationViewItemBackgroundColor(R.color.drawer_item);
//        activityIndicatorType = EMActivityWithIndicator.ActivityIndicatorType.Dots;
        EMModal.setModalAttributes(EMModal.RoundedModal.EMModalAnimType.GlideAndGrow, 10, R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.white);

        SocketService.get(this); // initialize socket service
        requestFragmentChange(new LobbyFragment(), "lobby");
        setTitleText("UNO Lobby");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_lobby:
                // navigate to nav one page
                requestFragmentChange(new LobbyFragment(), "lobby");
                setTitleText("UNO Lobby");
                break;
            case R.id.nav_profile:
                // navigate to nav two page
                requestFragmentChange(new ProfileFragment(), "profile");
                setTitleText("UNO Profile");
                break;
            case R.id.nav_logout:
                SocketService.get(this).destroySocketConnection();
                UNOUtil.get(this).setLoggedOut();
                startActivity(new Intent(UNOActivity.this, LoginActivity.class));

                break;
        }

        // super's onNavigationItemSelected will handle everything except for navigation
        super.onNavigationItemSelected(item);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity act) {
        if(act!=null)
            ((InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((act.getWindow().getDecorView().getApplicationWindowToken()), 0);
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


    TODO: Create the following views
    Game
    - PLayers
    - Hand

    Help
     */
}

package com.envative.uno.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.envative.emoba.activities.EMNavigationDrawerActivity;
import com.envative.emoba.delegates.Callback;
import com.envative.emoba.widgets.EMModal;
import com.envative.uno.R;
import com.envative.uno.comms.SocketService;
import com.envative.uno.comms.UNOAppState;
import com.envative.uno.comms.UNOUtil;
import com.envative.uno.fragments.LobbyFragment;
import com.envative.uno.fragments.ProfileFragment;

import java.io.File;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


public class UNOActivity extends EMNavigationDrawerActivity {

    private ProfileFragment profileFragment;

    private boolean pressedBackOnce = false;
    private FragmentManager fm;

    private final String GAME_FRAGMENT = "game_fragment";
    private final String PRE_GAME_LOBBY_FRAGMENT = "pre_game_lobby_fragment";

    public void setProfileFragment(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
    }

    public enum NavigationView{
        Lobby,
        Profile,
        Logout
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setActivityIndicatorType(ActivityIndicatorFragment.ActivityIndicatorType.Dots);
        setNavigationViewMenu(R.menu.activity_uno_drawer);
        setNavigationViewItemBackgroundColor(R.color.drawer_item);
        EMModal.setModalAttributes(EMModal.RoundedModal.EMModalAnimType.GlideAndGrow, 10, R.color.colorLightGray, R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.white);

        SocketService.get(this); // initialize socket service
        requestFragmentChange(new LobbyFragment(), "lobby");
        setTitleText("UNO Lobby");
    }

    @Override
    public void onBackPressed(){
        fm = (fm == null) ? getFragmentManager() : fm;
        // confirm to exit
        if(fm.getBackStackEntryCount() > 1) {

            // check if in game
            // if so then prompt modal
            FragmentManager.BackStackEntry currFragment = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);

            if(currFragment != null) {
                if (currFragment.getBreadCrumbShortTitle() != null) {
                    String title = currFragment.getBreadCrumbShortTitle().toString();

                    if(title.equals(GAME_FRAGMENT)){
                        showQuitConfirmationModal();
                    }else if(title.equals(PRE_GAME_LOBBY_FRAGMENT)){
                        showCancelChallengeConfirmationModal();
                    }else{
                        fm.popBackStack();
                    }
                }
            }

        }else{
            if(pressedBackOnce){
                this.moveTaskToBack(true);
            }else{
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                pressedBackOnce = true;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "called");
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                //Handle the image
                profileFragment.onPhotoReturned(imageFile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getApplicationContext());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
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

    public void handleNavigation(NavigationView navigationView){
        switch(navigationView){
            case Lobby:
                requestFragmentChange(new LobbyFragment(), "lobby");
                setTitleText("UNO Lobby");
                break;
        }
    }

    public void showQuitConfirmationModal() {
        EMModal.showModal(this, EMModal.ModalType.Message, "Quit Game", "Are you sure you want to quit?", new Callback() {
            @Override
            public void callback(Object object) {
                SocketService.get(UNOActivity.this).quitGame();
                handleNavigation(UNOActivity.NavigationView.Lobby);
            }
        }, null);
    }


    public void showCancelChallengeConfirmationModal() {
        EMModal.showModal(this, EMModal.ModalType.Message, "Cancel Challenge", "Are you sure you want to cancel this challenge?", new Callback() {
            @Override
            public void callback(Object object) {
                // TODO:: cancel challenge
                SocketService.get(UNOActivity.this).handleChallenge(UNOAppState.currChallengeId, SocketService.ChallengeResType.Cancel);
                handleNavigation(UNOActivity.NavigationView.Lobby);
            }
        }, null);
    }

    /*
    NOTES:

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

    Help



    TO LOG OUT OF FABRIC

    CTRL + L (not CMD + L)



     */

}

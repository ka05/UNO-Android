package com.envative.uno.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.envative.emoba.activities.EMActivityWithIndicator;
import com.envative.emoba.fragments.ActivityIndicatorFragment;
import com.envative.emoba.widgets.EMModal;
import com.envative.uno.R;
import com.envative.uno.comms.SocketService;
import com.envative.uno.fragments.LoginFragment;
import com.envative.uno.fragments.SignupFragment;

/**
 * Created by clay on 6/4/16.
 */
public class LoginActivity extends EMActivityWithIndicator {

    private boolean pressedBackOnce = false;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();

        SocketService.get(this, true);// initialize socket service
        showLogin();
    }

    private void initComponents() {
        getFragmentContainer().setBackgroundColor(getResources().getColor( R.color.colorPrimaryDark ));
        setActivityIndicatorType(ActivityIndicatorFragment.ActivityIndicatorType.Dots);
        EMModal.setModalAttributes(EMModal.RoundedModal.EMModalAnimType.GlideAndGrow, 10, R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.white);
    }

    @Override
    public void onBackPressed(){
        fm = (fm == null) ? getFragmentManager() : fm;
        // confirm to exit
        if(fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
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

    public void requestFragmentChange(Fragment fragmentToLoad, String title) {
        fm = getFragmentManager();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.enter_from_bottom, R.animator.exit_to_bottom, R.animator.enter_from_bottom, R.animator.exit_to_bottom);
        if (fragmentToLoad != null) {
            ft
                    .add(R.id.fragmentContainer, fragmentToLoad)
                    .addToBackStack(title)
                    .setBreadCrumbShortTitle(title)
                    .commit();
        }
    }

    public void showSignup() {
        pressedBackOnce = false;
        requestFragmentChange(new SignupFragment(), "sign up");
    }

    public void showLogin() {
        pressedBackOnce = false;
        requestFragmentChange(new LoginFragment(), "login");
    }
}

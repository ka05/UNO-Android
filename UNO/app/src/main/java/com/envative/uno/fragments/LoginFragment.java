package com.envative.uno.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.envative.emoba.delegates.Callback;
import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.emoba.widgets.EMModal;
import com.envative.uno.R;
import com.envative.uno.activities.LoginActivity;
import com.envative.uno.activities.UNOActivity;
import com.envative.uno.comms.SocketService;
import com.envative.uno.comms.UNOUtil;
import com.envative.uno.models.SocketDelegateType;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by clay on 6/4/16.
 */
public class LoginFragment extends EMBaseFragment implements View.OnClickListener {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginBtn;
    private Button btnSignup;

    public Callback attemptLoginCallback = new Callback() {
        @Override
        public void callback(Object object) {
            boolean success = (boolean)object;

            if(!success){
                EMModal.showModal(context, EMModal.ModalType.Error, "Login Error", "Invalid Combination");
            }else{
                // navigate to UNOActivity
                UNOUtil.get(getActivity()).setLoggedIn();
                startActivity(new Intent(getActivity(), UNOActivity.class));
                Toast.makeText(getActivity(), "Logged In!", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        findViews(v);

        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.Login);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        SocketService.get(getActivity()).setLoginFromSignupPage(false);
    }

    private void findViews(View v) {

        usernameEditText = (EditText)v.findViewById(R.id.usernameEditText);
        passwordEditText = (EditText)v.findViewById(R.id.passwordEditText);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_DONE) {
                    //do something here.
                    attemptLogin(v);
                    return true;
                }
                return false;
            }
        });

        loginBtn = (Button) v.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        btnSignup = (Button) v.findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);
    }

    public void attemptLogin(View v){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);

        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString();

        // reset errors
        usernameEditText.setError(null);
        passwordEditText.setError(null);

        if(username.equals("") || password.equals("")){
            // a field is empty. please fill out all fields
            Toast.makeText(getActivity(), "Please fill in all fields!", Toast.LENGTH_LONG).show();
            usernameEditText.setError(getString(R.string.error_field_required));
            passwordEditText.setError(getString(R.string.error_field_required));
            EMModal.showModal(getActivity(), EMModal.ModalType.Error, "Login Error!", "Please enter your credentials.");
        }else{

            JsonObject credentials = new JsonObject();
            credentials.add("username", new JsonPrimitive( username ));
            credentials.add("password", new JsonPrimitive( password ));

            SocketService.get(getActivity()).attemptLogin(credentials);
            ((LoginActivity)getActivity()).showActivityIndicator();

            // for when we want to change to use push notifications
//            LCUtil.get(this).registerReceiver(new Callback() {
//                @Override
//                public void callback(Object object) {
                    // if the uuid was set
//                }
//            });

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn:
                attemptLogin(v);
                break;
            case R.id.btnSignup:
                ((LoginActivity)getActivity()).showSignup();
                break;

        }
    }

}

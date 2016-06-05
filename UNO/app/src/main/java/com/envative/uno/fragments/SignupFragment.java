package com.envative.uno.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.emoba.widgets.EMModal;
import com.envative.uno.R;
import com.envative.uno.activities.LoginActivity;
import com.envative.uno.comms.SocketService;
import com.envative.uno.models.SocketDelegateType;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by clay on 6/4/16.
 */
public class SignupFragment extends EMBaseFragment implements View.OnClickListener{

    private Button btnSignup;
    private Button btnCancel;

    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        findViews(v);

        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.Signup);

        return v;
    }

    private void findViews(View v) {

        emailEditText = (EditText)v.findViewById(R.id.emailEditText);
        usernameEditText = (EditText)v.findViewById(R.id.usernameEditText);
        passwordEditText = (EditText)v.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText)v.findViewById(R.id.confirmPasswordEditText);

        btnSignup = (Button)v.findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);
        btnCancel = (Button)v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignup:
                attemptSignup();
                break;
            case R.id.btnCancel:
                getFragmentManager().popBackStack();
                break;
        }
    }

    private void attemptSignup() {
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // if all are filled in
        if(!email.equals("") &&
                !username.equals("") &&
                !password.equals("") &&
                !confirmPassword.equals("") ){

            // if passwords match
            if(password.equals(confirmPassword)){
                JsonObject userData = new JsonObject();
                userData.add("email", new JsonPrimitive( email ));
                userData.add("username", new JsonPrimitive( username ));
                userData.add("password", new JsonPrimitive( password ));

                SocketService.get(getActivity()).signup(userData);
                ((LoginActivity)getActivity()).showActivityIndicator();
            }else{
                // passwords dont match
                EMModal.showModal(context, EMModal.ModalType.Error, "Signup Error", "Passwords do not match!");
            }

        }else{
            // not all fields filled in
            EMModal.showModal(context, EMModal.ModalType.Error, "Signup Error", "Please fill in all fields!");
        }
    }

}

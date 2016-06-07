package com.envative.uno.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.uno.R;
import com.envative.uno.comms.SocketService;
import com.envative.uno.models.SocketDelegateType;

/**
 * Created by clay on 6/4/16.
 */
public class PreGameLobbyFragment extends EMBaseFragment {

    private TextView btnCancelChallenge;
    private TextView txtPregameLobbyMsg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_preg_game_lobby, container, false);
        findViews(v);

        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.Challenges);
        return v;
    }

    private void findViews(View v) {

        txtPregameLobbyMsg = (TextView)v.findViewById(R.id.txtPregameLobbyMsg);
        btnCancelChallenge = (TextView)v.findViewById(R.id.btnCancelChallenge);
        btnCancelChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO:: cancel challenge
//                SocketService.get(getActivity()).handleChallenge();
            }
        });
    }
}

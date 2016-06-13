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
import com.envative.uno.comms.UNOAppState;
import com.envative.uno.models.Challenge;
import com.envative.uno.models.SocketDelegateType;

/**
 * Created by clay on 6/4/16.
 */
public class PreGameLobbyFragment extends EMBaseFragment {

    private TextView btnCancelChallenge;
    private TextView txtPregameLobbyMsg;

    private Challenge currChallenge;
    private String msg = "";
    // challenger or challengee?


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_preg_game_lobby, container, false);

        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.PreGameLobby);

        boolean currUserIsChallenger = currChallenge.challenger.equals(UNOAppState.currUser.username);
        UNOAppState.currChallengeId = currChallenge.id;
        SocketService.get(getActivity()).getChallenge(currUserIsChallenger);
        msg = (!currUserIsChallenger) ? "Please wait for the host to start the game." : "Waiting for other players, game will start when all players have joined.";
        if(currUserIsChallenger)
            SocketService.get(getActivity()).checkPlayersInGameRoom();

        findViews(v);

        return v;
    }

    private void findViews(View v) {

        txtPregameLobbyMsg = (TextView)v.findViewById(R.id.txtPregameLobbyMsg);
        txtPregameLobbyMsg.setText(msg);

        btnCancelChallenge = (TextView)v.findViewById(R.id.btnCancelChallenge);
        btnCancelChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO:: cancel challenge
                SocketService.get(getActivity()).handleChallenge(currChallenge.id, SocketService.ChallengeResType.Cancel);
                getActivity().getFragmentManager().popBackStack(); // go back to lobby
            }
        });
    }

    public Challenge getChallenge() {
        return currChallenge;
    }

    public void setChallenge(Challenge currChallenge) {
        this.currChallenge = currChallenge;
    }

}

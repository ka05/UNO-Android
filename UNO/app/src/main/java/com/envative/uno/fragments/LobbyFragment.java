package com.envative.uno.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.uno.R;
import com.envative.uno.comms.SocketService;
import com.envative.uno.models.Challenge;
import com.envative.uno.models.SocketDelegateType;

/**
 * Created by clay on 6/4/16.
 */
public class LobbyFragment extends EMBaseFragment implements View.OnClickListener {

    private FrameLayout chatFragmentContainer;
    private FrameLayout sentChallengesContainer;
    private FrameLayout receivedChallengesContainer;

    private ChatFragment chatFragment;
    private ChallengeFragment sentChallengesFragment;
    private ChallengeFragment receivedChallengesFragment;
    private ChallengeModalFragment challengeModalFragment;

    private TextView btnSendChallenge;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lobby, container, false);
        findViews(v);

        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.Lobby);
        return v;
    }

    private void findViews(View v) {
        btnSendChallenge = (TextView)v.findViewById(R.id.btnSendChallenge);
        btnSendChallenge.setOnClickListener(this);

        chatFragmentContainer = (FrameLayout)v.findViewById(R.id.chatFragmentContainer);

        chatFragment = new ChatFragment();
        chatFragment.setChatRoomId("1");
        sentChallengesFragment = new ChallengeFragment();
        sentChallengesFragment.setLobbyDelegate(this);
        sentChallengesFragment.setChallengeType(ChallengeFragment.ChallengeType.Sent);
        receivedChallengesFragment = new ChallengeFragment();
        receivedChallengesFragment.setLobbyDelegate(this);
        receivedChallengesFragment.setChallengeType(ChallengeFragment.ChallengeType.Received);

        populateFragmentContainer(chatFragment, R.id.chatFragmentContainer);
        populateFragmentContainer(sentChallengesFragment, R.id.sentChallengesContainer);
        populateFragmentContainer(receivedChallengesFragment, R.id.receivedChallengesContainer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSendChallenge:
                showChallengeModal();
                break;
        }
    }

    private void showChallengeModal() {
        challengeModalFragment = new ChallengeModalFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.enter_from_bottom, R.animator.exit_to_bottom, R.animator.enter_from_bottom, R.animator.exit_to_bottom);
        ft
                .add(R.id.fragmentContainer, challengeModalFragment)
                .addToBackStack("challenge modal")
                .setBreadCrumbShortTitle("challenge modal")
                .commit();
    }

    private void populateFragmentContainer(Fragment fragment, int containerId){
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (fragment != null) {
            ft.replace(containerId, fragment).commit();
        }
    }

    public void showPregameLobby(Challenge challenge){
        PreGameLobbyFragment preGameLobbyFragment = new PreGameLobbyFragment();
        preGameLobbyFragment.setChallenge(challenge);
        delegate.requestFragmentChange(preGameLobbyFragment, "pregame-lobby", true);
    }

    public ChatFragment getChatFragment() {
        return chatFragment;
    }

    public ChallengeFragment getSentChallengesFragment(){
        return sentChallengesFragment;
    }
    public ChallengeFragment getReceivedChallengesFragment(){
        return receivedChallengesFragment;
    }
}

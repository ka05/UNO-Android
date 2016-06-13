package com.envative.uno.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.emoba.widgets.EMModal;
import com.envative.uno.R;
import com.envative.uno.comms.SocketService;
import com.envative.uno.comms.UNOAppState;
import com.envative.uno.models.Challenge;
import com.envative.uno.models.SocketDelegateType;

import java.util.ArrayList;

/**
 * Created by clay on 6/4/16.
 */
public class ChallengeFragment extends EMBaseFragment {

    private static final String SENT_CHALLENGES = "Sent Challenges";
    private static final String RECEIVED_CHALLENGES = "Received Challenges";
    private TextView txtChallengesHeader;
    private ListView lvChallenges;
    private ChallengeType type = ChallengeType.Sent;
    private ChallengesAdapter adapter;
    private ArrayList<Challenge> filteredChallenges = new ArrayList<>();
    private TextView emptyListView;
    private LobbyFragment lobbyDelegate;

    public void setLobbyDelegate(LobbyFragment lobbyDelegate) {
        this.lobbyDelegate = lobbyDelegate;
    }

    public enum ChallengeType{
        Sent,
        Received
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_challenge, container, false);
        findViews(v);

        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.Challenges);
        return v;
    }

    private void findViews(View v) {
        txtChallengesHeader = (TextView)v.findViewById(R.id.txtChallengesHeader);

        lvChallenges = (ListView) v.findViewById(R.id.lvChallenges);

        adapter = new ChallengesAdapter(getActivity(), R.layout.item_challenge, filteredChallenges);
        lvChallenges.setAdapter(adapter); // set up ListView
        emptyListView = (TextView)v.findViewById(R.id.emptyListViewText);
        lvChallenges.setEmptyView(v.findViewById(R.id.emptyListViewText));
        updateChallengeType(type);

    }

    public void updateChallengeType(ChallengeType type){
        filteredChallenges.clear();
        if(type == ChallengeType.Sent){
            txtChallengesHeader.setText(SENT_CHALLENGES);
            filteredChallenges.addAll(UNOAppState.sentChallenges);
            SocketService.get(getActivity()).getSentChallenges();
        }else if(type == ChallengeType.Received){
            txtChallengesHeader.setText(RECEIVED_CHALLENGES);
            filteredChallenges.addAll(UNOAppState.receivedChallenges);
            SocketService.get(getActivity()).getReceivedChallenges();
        }
        adapter.notifyDataSetChanged();
    }

    public void setChallengeType(ChallengeType type){
        this.type = type;
    }

    public class ChallengesAdapter extends ArrayAdapter<Challenge> {

        Context context;
        int layoutResourceId;
        ArrayList<Challenge> data = null;

        public ChallengesAdapter(Context context, int layoutResourceId, ArrayList<Challenge> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ChallengeHolder holder = null;

            if(row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ChallengeHolder();
                holder.challengeMsg = (TextView)row.findViewById(R.id.txtChallengeMsg);
                holder.btnStartGame = (TextView) row.findViewById(R.id.btnStartGame);
                holder.statusIndicator = (View)row.findViewById(R.id.statusIndicator);

                row.setTag(holder);
            } else {
                holder = (ChallengeHolder)row.getTag();
            }

            final Challenge challenge = data.get(position);

            // TODO: handle formatting for these
            holder.challengeMsg.setText(challenge.displayText);

            //mak start game btn visible only if all responded and user is the challenger
            int visibility = (challenge.status.equals("all responded") && challenge.challenger.equals(UNOAppState.currUser.username)) ? View.VISIBLE : View.INVISIBLE;
            holder.btnStartGame.setVisibility(visibility);
            holder.btnStartGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleStartGame(challenge);
                }
            });

            holder.statusIndicator.setBackgroundColor(getResources().getColor( getChallengeStatusColor(challenge.status) ));
            holder.challengeMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // launch modal with 3 options:
                    // accept
                    // decline
                    // cancel
                    handleChallengeTapped(challenge);
                }
            });


            return row;
        }

        class ChallengeHolder {
            TextView challengeMsg;
            TextView btnStartGame;
            View statusIndicator;
        }
    }

    private void handleStartGame(Challenge challenge) {
        lobbyDelegate.showPregameLobby(challenge);
    }

    private void handleChallengeTapped(final Challenge challenge) {
        // TODO: setup various custom modals

        // if they sent the challenge they can cancel it but cannot accept or decline
        if(type == ChallengeType.Sent){

            // if it was cancelled already they cant do anything
            if(challenge.status.equals("cancelled") || challenge.status.equals("declined")){
                Toast.makeText(getActivity(), "This challenge has been cancelled or declined already.", Toast.LENGTH_LONG).show();
            }else{
                if( challenge.status.equals("ready") || challenge.status.equals("accepted") ||  challenge.status.equals("pending")) {
                    final EMModal.RoundedModal modal = EMModal.showCustomModal(
                            getActivity(),
                            R.layout.modal_multi_buttons,
                            "Challenge to: " + TextUtils.join(", ", challenge.usersChallenged),
                            "", "OK", "CLOSE", null);

                    // hide btn1
                    TextView btn1 = (TextView) modal.getDialog().findViewById(R.id.btn1);
                    btn1.setVisibility(View.GONE);

                    TextView btnCancel = (TextView) modal.getDialog().findViewById(R.id.btn2);
                    btnCancel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnCancel.setText("Cancel");
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SocketService.get(getActivity()).handleChallenge(challenge.id, SocketService.ChallengeResType.Cancel);
                            modal.getDialog().dismiss();
                        }
                    });
                }
            }
        }
        // they received the challenge so they can accept or decline
        else{
            // if it was cancelled already they cant do anything
            if(challenge.status.equals("cancelled") || challenge.status.equals("declined")){
                Toast.makeText(getActivity(), "This challenge has been cancelled or declined already.", Toast.LENGTH_LONG).show();
            }else {

                final EMModal.RoundedModal modal = EMModal.showCustomModal(
                        getActivity(),
                        R.layout.modal_multi_buttons,
                        "Challenge from: " + challenge.challenger,
                        "", "OK", "CLOSE", null);

                TextView btnAccept = (TextView) modal.getDialog().findViewById(R.id.btn1);
                btnAccept.setBackgroundColor(getResources().getColor(R.color.legend_color3));
                btnAccept.setText("Accept");
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SocketService.get(getActivity()).handleChallenge(challenge.id, SocketService.ChallengeResType.Accept);
                        modal.getDialog().dismiss();
                        lobbyDelegate.showPregameLobby(challenge);
                    }
                });
                TextView btnDecline = (TextView) modal.getDialog().findViewById(R.id.btn2);
                btnDecline.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnDecline.setText("Decline");
                btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SocketService.get(getActivity()).handleChallenge(challenge.id, SocketService.ChallengeResType.Decline);
                        modal.getDialog().dismiss();
                    }
                });
            }
        }
    }

    private int getChallengeStatusColor(String status) {
        int color = R.color.black;
        if(status.equals("all responded")){
            color = R.color.colorCardGreen;
        }else if(status.equals("accepted")){
            color = R.color.colorCardGreen;
        }else if(status.equals("declined")){
            color = R.color.challengeOrange;
        }else if(status.equals("cancelled")){
            color = R.color.colorPrimary;
        }else if(status.equals("pending")){
            color = R.color.black;
        }
        return color;
    }
}

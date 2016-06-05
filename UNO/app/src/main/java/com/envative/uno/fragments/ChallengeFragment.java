package com.envative.uno.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.envative.emoba.fragments.EMBaseFragment;
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

    public enum ChallengeType{
        Sent,
        Received
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_challenge, container, false);
        findViews(v);

        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.Chat);
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

            holder.btnStartGame.setVisibility((challenge.status.equals("all responded")) ? View.VISIBLE : View.INVISIBLE);
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

    private void handleChallengeTapped(Challenge challenge) {

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
        }
        return color;
    }
}

package com.envative.uno.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.uno.R;
import com.envative.uno.comms.SocketService;
import com.envative.uno.comms.UNOAppState;
import com.envative.uno.models.Card;
import com.envative.uno.models.Player;
import com.envative.uno.models.SocketDelegateType;

import java.util.ArrayList;

/**
 * Created by clay on 6/4/16.
 */
public class GameFragment extends EMBaseFragment implements View.OnClickListener {

    private GridView gvPlayers;
    private GridView gvPlayerHand;

    private PlayerHandGridViewAdapter playerHandGridViewAdapter;
    private PlayersGridViewAdapter playersGridViewAdapter;

    private Button btnSayUno;
    private Button btnChallenge;
    private Button btnQuit;
    private Button btnHelp;

    private TextView txtPlayerUsername; // for current player usernam - highlight when active
    private ImageView btnDeck;
    private ImageView ivCurrGameCard;
    private ImageView ivPrevGameCard;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        findViews(v);

        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.Game);
        return v;
    }

    private void findViews(View v) {
        gvPlayers = (GridView)v.findViewById(R.id.gvPlayers);
        gvPlayerHand = (GridView)v.findViewById(R.id.gvPlayerHand);

        //TODO: setup grid views
        playerHandGridViewAdapter = new PlayerHandGridViewAdapter(getActivity(), R.layout.item_hand_card, UNOAppState.currGame.currPlayer.hand);
        playersGridViewAdapter = new PlayersGridViewAdapter(getActivity(), R.layout.item_player, UNOAppState.currGame.players);

        gvPlayers.setAdapter(playersGridViewAdapter);
        gvPlayerHand.setAdapter(playerHandGridViewAdapter);
        gvPlayerHand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                                    // selected card
                                                    Card card = (Card) parent.getItemAtPosition(position);
                                                    // validate move with this card
                                                }
                                            });
        btnSayUno = (Button)v.findViewById(R.id.btnSayUno);
        btnSayUno.setOnClickListener(this);
        btnChallenge = (Button)v.findViewById(R.id.btnChallenge);
        btnChallenge.setOnClickListener(this);
        btnQuit = (Button)v.findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(this);
        btnHelp = (Button)v.findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(this);
        btnDeck = (ImageView)v.findViewById(R.id.btnDeck);
        btnDeck.setOnClickListener(this);

        txtPlayerUsername = (TextView)v.findViewById(R.id.txtPlayerUsername);
        ivCurrGameCard = (ImageView)v.findViewById(R.id.ivCurrGameCard);
        ivPrevGameCard = (ImageView)v.findViewById(R.id.ivPrevGameCard);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSayUno:
                break;
            case R.id.btnChallenge:
                break;
            case R.id.btnQuit:
                break;
            case R.id.btnHelp:
                break;
            case R.id.btnDeck:
                break;

        }
    }

    /**
     * sets the discard pile game card
     * Pass in svgName
     * @param cardResId
     */
    private void setCurrGameCard(String cardResId){
        // TODO: setup hashmap of string to resid values for use when retrieving cards
        ivCurrGameCard.setImageResource(UNOAppState.cardNames.get(cardResId));
    }

    private void setPrevGameCard(String cardResId){
        ivPrevGameCard.setImageResource(UNOAppState.cardNames.get(cardResId));
    }

    /**
     *
     */
    private void setPlayerUsernameActive(){
        // if its the curr players turn highlight background
        if(UNOAppState.currGame.currPlayer.isMyTurn){
            txtPlayerUsername.setBackgroundColor(getResources().getColor( R.color.colorCardGreen ));
        }else{
            txtPlayerUsername.setBackgroundColor(getResources().getColor( R.color.black ));
        }
    }

    public class PlayerHandGridViewAdapter extends ArrayAdapter {
        private Context context;
        private int layoutResourceId;
        private ArrayList<Card> data = new ArrayList();

        public PlayerHandGridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) row.findViewById(R.id.image);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            Card card = data.get(position);
            // TODO: use hashmap to get image id
            holder.image.setImageResource(UNOAppState.cardNames.get(card.svgName));
            return row;
        }

        class ViewHolder {
            ImageView image;
        }
    }

    public class PlayersGridViewAdapter extends ArrayAdapter {
        private Context context;
        private int layoutResourceId;
        private ArrayList<Player> data = new ArrayList();

        public PlayersGridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.username = (TextView) row.findViewById(R.id.txtUsername);
                holder.calledUno = (TextView) row.findViewById(R.id.txtUno);
                holder.cardCount = (TextView) row.findViewById(R.id.txtNumCards);
                holder.playerImage = (ImageView) row.findViewById(R.id.ivPlayer);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            Player player = data.get(position);
            holder.username.setText(player.username);
            holder.playerImage.setImageResource(R.drawable.profile_img);

            if(player.isMyTurn){
                holder.username.setBackgroundColor(getResources().getColor( R.color.colorCardGreen ));
            }else{
                holder.username.setBackgroundColor(getResources().getColor( R.color.black ));
            }

            if(player.calledUno){
                holder.calledUno.setVisibility(View.VISIBLE);
            }else{
                holder.calledUno.setVisibility(View.GONE);
            }

            return row;
        }

        class ViewHolder {
            TextView username;
            TextView calledUno;
            TextView cardCount;
            ImageView playerImage;
        }
    }

}

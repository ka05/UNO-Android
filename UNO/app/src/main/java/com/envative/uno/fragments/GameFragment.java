package com.envative.uno.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.envative.emoba.delegates.Callback;
import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.emoba.utils.EMDrawingUtils;
import com.envative.emoba.widgets.EMModal;
import com.envative.uno.R;
import com.envative.uno.activities.UNOActivity;
import com.envative.uno.comms.SocketService;
import com.envative.uno.comms.UNOAppState;
import com.envative.uno.comms.UNOUtil;
import com.envative.uno.models.Card;
import com.envative.uno.models.Player;
import com.envative.uno.models.SocketDelegateType;
import com.envative.uno.widgets.RoundedImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by clay on 6/4/16.
 */
public class GameFragment extends EMBaseFragment implements View.OnClickListener {

//    private GridView gvPlayers;
//    private GridView gvPlayerHand;

    private LinearLayout llPlayers;
    private LinearLayout llPlayerHand;

    private PlayerHandGridViewAdapter playerHandGridViewAdapter;
    private PlayersGridViewAdapter playersGridViewAdapter;

    private Button btnSayUno;
    private Button btnChallenge;
    private Button btnQuit;
    private Button btnHelp;
    private Button btnChat;

    private TextView txtPlayerUsername; // for current player usernam - highlight when active
    private RoundedImageView ivCurrPlayer;
    private ImageView btnDeck;
    private ImageView ivCurrGameCard;
    private ImageView ivPrevGameCard;

    private BottomSheetBehavior bsWildCardColorPicker;
    private BottomSheetBehavior bsInGameChat;

    private LinearLayout chatFragmentContainer;
    private ChatFragment chatFragment;

    private int activeGreenColor;
    private int inactiveBlackColor;


    public enum WildCardColors{
        Red("red"),
        Green("green"),
        Blue("blue"),
        Yellow("yellow");

        public String color;

        WildCardColors(String color){
            this.color = color;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        findViews(v);

        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.Game);
        ((UNOActivity)getActivity()).setTitleText("UNO Game");

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;
        if (context instanceof Activity){
            a=(Activity) context;
        }
        SocketService.get(getActivity()).updateGameFragmentAttached(true);
    }

    private void findViews(View v) {

        View wildCardColorPickerView = v.findViewById(R.id.bsWildCardColorPicker);
        bsWildCardColorPicker = BottomSheetBehavior.from(wildCardColorPickerView);
        View inGameChatView = v.findViewById(R.id.bsInGameChat);
        bsInGameChat = BottomSheetBehavior.from(inGameChatView);

        chatFragmentContainer = (LinearLayout)v.findViewById(R.id.chatFragmentContainer);
        chatFragment = new ChatFragment();
        chatFragment.setChatRoomId(UNOAppState.currGameId);
        chatFragment.setChatType(ChatFragment.ChatType.Game);

        llPlayers = (LinearLayout)v.findViewById(R.id.llPlayers);
        llPlayerHand = (LinearLayout)v.findViewById(R.id.llPlayerHand);
        ivCurrPlayer = (RoundedImageView)v.findViewById(R.id.ivCurrPlayer);

        //TODO : fix profile Image
        String profileImgPath = UNOAppState.currUser.profileImgPath;
        if(!profileImgPath.equals("")){
            ivCurrPlayer.setImageBitmap(BitmapFactory.decodeFile(profileImgPath));
        }else{

        }

        getActivity().getFragmentManager().beginTransaction().replace(R.id.chatFragmentContainer, chatFragment).commit();

        //TODO: setup grid views
//        gvPlayers = (GridView)v.findViewById(R.id.gvPlayers);
//        gvPlayerHand = (GridView)v.findViewById(R.id.gvPlayerHand);

//        playerHandGridViewAdapter = new PlayerHandGridViewAdapter(getActivity(), R.layout.item_hand_card, UNOAppState.currGame.currPlayer.hand);
//        playersGridViewAdapter = new PlayersGridViewAdapter(getActivity(), R.layout.item_player, UNOAppState.currGame.players);

//        gvPlayers.setAdapter(playersGridViewAdapter);
//        gvPlayerHand.setAdapter(playerHandGridViewAdapter);
//        gvPlayerHand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                                                    // selected card
//                                                    Card card = (Card) parent.getItemAtPosition(position);
//                                                    // validate move with this card
//                                                }
//                                            });
        btnChat = (Button)v.findViewById(R.id.btnChat);
        btnChat.setOnClickListener(this);
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

        txtPlayerUsername = (TextView)v.findViewById(R.id.txtUsername);
        txtPlayerUsername.setText(UNOAppState.currGame.currPlayer.username);

        ivCurrGameCard = (ImageView)v.findViewById(R.id.ivCurrGameCard);
        ivPrevGameCard = (ImageView)v.findViewById(R.id.ivPrevGameCard);

        if(UNOAppState.currGame != null){
            updateGameView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSayUno:
                sayUno();
                break;
            case R.id.btnChallenge:
                SocketService.get(getActivity()).challengeUno();
                break;
            case R.id.btnQuit:
                ((UNOActivity)getActivity()).showQuitConfirmationModal();
                break;
            case R.id.btnHelp:
                showHelp();
                break;
            case R.id.btnDeck:
                SocketService.get(getActivity()).drawCard();
                break;
            case R.id.btnChat:
                bsInGameChat.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
        }
    }



    private void sayUno() {
        if(UNOAppState.currGame.currPlayer.hand.size() <= 2){
            SocketService.get(getActivity()).sayUno();
        }else{
            showToast("You can't say UNO yet!");
        }
    }

    private void showHelp() {
        ((UNOActivity)getActivity()).requestFragmentChange(new HelpFragment(), "help", true);
    }

    public void updatePlayersView(){
        llPlayers.removeAllViews();
        for(Player player : UNOAppState.currGame.players){
            llPlayers.addView(initPlayerView(player));
        }
    }

    private View initPlayerView(final Player player) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.item_player, null);

        TextView username = (TextView) view.findViewById(R.id.txtUsername);
        TextView calledUno = (TextView) view.findViewById(R.id.txtUno);
        TextView cardCount = (TextView) view.findViewById(R.id.txtNumCards);
        final RoundedImageView playerImage = (RoundedImageView) view.findViewById(R.id.ivPlayer);

        username.setText(player.username);
        playerImage.setImageResource(R.drawable.profile_img);

        checkImageExists(player, new Callback() {
            @Override
            public void callback(Object object) {
                // update player image path
                playerImage.setImageBitmap(BitmapFactory.decodeFile(player.profileImgPath));
            }
        });

        cardCount.setText(player.cardCount + "");

        if(player.isMyTurn){
            EMDrawingUtils.setDrawableLayerColor(username.getBackground(), com.envative.emoba.R.id.background, activeGreenColor);
//            username.setBackgroundColor(getResources().getColor( R.color.colorCardGreen ));
        }else{
            EMDrawingUtils.setDrawableLayerColor(username.getBackground(), com.envative.emoba.R.id.background, inactiveBlackColor);
//            username.setBackgroundColor(getResources().getColor( R.color.black ));
        }

        if(player.calledUno){
            calledUno.setVisibility(View.VISIBLE);
        }else{
            calledUno.setVisibility(View.GONE);
        }

        return view;
    }


    public void updatePlayerHandView(){
        llPlayerHand.removeAllViews();
        for(Card card : UNOAppState.currGame.currPlayer.hand){
            llPlayerHand.addView(initCardView(card));
        }
    }

    private View initCardView(final Card card) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.item_hand_card, null);

        ImageView cardImage = (ImageView) view.findViewById(R.id.ivCard);
        cardImage.setImageDrawable(getResources().getDrawable( UNOAppState.cardNames.get(card.svgName) ));
        cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: see why this is null
            if(UNOAppState.currGame.currPlayer != null){
                if(UNOAppState.currGame.currPlayer.isMyTurn){
                    // if card's color is "none" it is a wild card that has not been set yet.
                    Log.d("Playing card", card.cardName);
                    if(card.color.equals("none")){
                        showWildCardColorChoiceModal(new Callback() {
                            @Override
                            public void callback(Object object) {
                                WildCardColors chosenColor = (WildCardColors)object;
                                SocketService.get(getActivity()).handleValidateMove(card.svgName, chosenColor.color);
                            }
                        });
                    }else{
                        SocketService.get(getActivity()).handleValidateMove(card.svgName);
                    }
                }else{
                    showToast("It's not your turn!");
                }
            }else{
                Log.d("Card Played", "Game is null");
            }
            }
        });

        return view;
    }

    private void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    private void showWildCardColorChoiceModal(final Callback callback) {
        final EMModal.RoundedModal modal = EMModal.showCustomModal(
                getActivity(),
                R.layout.modal_wild_card_color_picker,
                "Please Choose a color to set",
                "", "OK", "CLOSE", null);

        TextView btnRed = (TextView) modal.getDialog().findViewById(R.id.btnRed);
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callback(WildCardColors.Red);
                modal.getDialog().dismiss();
            }
        });

        TextView btnBlue = (TextView) modal.getDialog().findViewById(R.id.btnBlue);
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callback(WildCardColors.Blue);
                modal.getDialog().dismiss();
            }
        });

        TextView btnGreen = (TextView) modal.getDialog().findViewById(R.id.btnGreen);
        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callback(WildCardColors.Green);
                modal.getDialog().dismiss();
            }
        });

        TextView btnYellow = (TextView) modal.getDialog().findViewById(R.id.btnYellow);
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callback(WildCardColors.Yellow);
                modal.getDialog().dismiss();
            }
        });
    }

    /**
     * sets the discard pile game card
     * Pass in svgName
     * @param cardResId
     */
    private void setCurrGameCard(String cardResId){
        // TODO: setup hashmap of string to resid values for use when retrieving cards
        ivCurrGameCard.setImageDrawable(getResources().getDrawable(UNOAppState.cardNames.get(cardResId)));
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
            EMDrawingUtils.setDrawableLayerColor(txtPlayerUsername.getBackground(), com.envative.emoba.R.id.background, activeGreenColor);
//            txtPlayerUsername.setBackgroundColor(getResources().getColor( R.color.colorCardGreen ));
        }else{
            EMDrawingUtils.setDrawableLayerColor(txtPlayerUsername.getBackground(), com.envative.emoba.R.id.background, inactiveBlackColor);
//            txtPlayerUsername.setBackgroundColor(getResources().getColor( R.color.black ));
        }
    }


    public void updateGameView(){
        UNOAppState.initCardNames();
        activeGreenColor = getResources().getColor( R.color.colorCardGreen );
        inactiveBlackColor = getResources().getColor( R.color.transparent_black );

        setPlayerUsernameActive();

        // new game has been fetched update entire view
        txtPlayerUsername.setText(UNOAppState.currGame.currPlayer.username);
        int discardPileSize = UNOAppState.currGame.discardPile.size();
        if(discardPileSize >= 1) {
            setCurrGameCard(UNOAppState.currGame.discardPile.get(discardPileSize - 1).svgName);
        }
        // if there is more than 1 card in the discard pile then there was a previous card
        if(discardPileSize > 1){
            setPrevGameCard(UNOAppState.currGame.discardPile.get(discardPileSize-2).svgName);
        }

        updatePlayersView();
        updatePlayerHandView();
    }


    private void checkImageExists(Player player, Callback callback){
        File imageFile = new File(player.profileImgPath);

        // if the file doesnt exist save it
        if(!imageFile.exists() ) {
            UNOUtil.get(getActivity()).saveImage(getActivity(), player.profileImgUrl, callback);
        }else{
            callback.callback(null);
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
            if(card != null) holder.image.setImageResource(UNOAppState.cardNames.get(card.svgName));
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

            holder.cardCount.setText(player.cardCount + "");

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

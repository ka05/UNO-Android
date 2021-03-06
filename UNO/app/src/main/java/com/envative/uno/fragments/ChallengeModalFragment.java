package com.envative.uno.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.envative.emoba.delegates.ActivityWithIndicator;
import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.emoba.utils.EMDrawingUtils;
import com.envative.emoba.widgets.EMModal;
import com.envative.uno.R;
import com.envative.uno.comms.SocketService;
import com.envative.uno.comms.UNOAppState;
import com.envative.uno.models.SocketDelegateType;
import com.envative.uno.models.User;
import com.envative.uno.widgets.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by clay on 6/4/16.
 */
public class ChallengeModalFragment extends EMBaseFragment implements View.OnClickListener {

    private View inGameLegendCircleView;
    private View availableLegendCircleView;
    private ListView lvOnlinePlayers;
    private Button btnSendChallenge;
    private Button btnCancel;

    private ArrayList<String> usersToChallenge = new ArrayList<>();
    private OnlineUsersAdapter adapter;
    private TextView emptyListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_challenge_modal, container, false);
        findViews(v);

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.ChallengeModal);
    }

    @Override
    public void onPause(){
        super.onPause();
        SocketService.get(getActivity()).setDelegate(null, SocketDelegateType.ChallengeModal); // remove delegate
    }

    private void findViews(View v) {

        inGameLegendCircleView = v.findViewById(R.id.inGameLegendCircleView);
        EMDrawingUtils.setDrawableLayerColor(inGameLegendCircleView.getBackground(), R.id.circle, getResources().getColor( R.color.challengeOrange) );
        availableLegendCircleView = v.findViewById(R.id.availableLegendCircleView);
        EMDrawingUtils.setDrawableLayerColor(availableLegendCircleView.getBackground(), R.id.circle, getResources().getColor( R.color.colorCardGreen) );

        inGameLegendCircleView = (View)v.findViewById(R.id.inGameLegendCircleView);
        btnSendChallenge = (Button)v.findViewById(R.id.btnSendChallenge);
        btnSendChallenge.setOnClickListener(this);
        btnCancel = (Button)v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        lvOnlinePlayers = (ListView)v.findViewById(R.id.lvOnlinePlayers);
        adapter = new OnlineUsersAdapter(getActivity(), R.layout.item_online_user, UNOAppState.activeUsers);
        lvOnlinePlayers.setAdapter(adapter); // set up ListView
        emptyListView = (TextView)v.findViewById(R.id.emptyListViewText);
        lvOnlinePlayers.setEmptyView(v.findViewById(R.id.emptyListViewText));
    }


    public OnlineUsersAdapter getAdapter(){
        return adapter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSendChallenge:
                // build challenge packet here
                if(usersToChallenge.size() > 0){
                    ((ActivityWithIndicator)getActivity()).showActivityIndicator();
                    SocketService.get(getActivity()).sendChallenge(usersToChallenge);
                    getActivity().getFragmentManager().popBackStack();
                }else{
                    EMModal.showModal(getActivity(), EMModal.ModalType.Warning, "Warning!", "Please select at least one person to challenge");
                }
                break;
            case R.id.btnCancel:
                getActivity().getFragmentManager().popBackStack();
                break;
        }
    }

    public class OnlineUsersAdapter extends ArrayAdapter<User> {

        Context context;
        int layoutResourceId;
        ArrayList<User> data = null;

        public OnlineUsersAdapter(Context context, int layoutResourceId, ArrayList<User> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            UserHolder holder = null;

            if(row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new UserHolder();
                holder.username = (TextView)row.findViewById(R.id.txtUsername);
                holder.profileImage = (RoundedImageView)row.findViewById(R.id.ivUserProfileImage);
                holder.selected = (CheckBox)row.findViewById(R.id.cbSelectedItem);

                row.setTag(holder);
            } else {
                holder = (UserHolder)row.getTag();
            }

            final User user = data.get(position);
            holder.username.setText(user.username);

            if(user.inAGame){
                holder.profileImage.setColor(getResources().getColor(R.color.challengeOrange));
            }else{
                holder.profileImage.setColor(getResources().getColor(R.color.colorCardGreen));
            }

            Log.d("Chall profileImgPath", ":" +user.profileImgPath);
            if(!user.profileImgPath.equals("")){
                Bitmap b = BitmapFactory.decodeFile(user.profileImgPath);
                // if bitmap is null then use the default one
                b = (b != null) ? b : BitmapFactory.decodeResource(getResources(), R.drawable.user);
                holder.profileImage.setImageBitmap(b);
            }

            holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        usersToChallenge.add(user.id);
                    }else{
                        if(usersToChallenge.contains(user.id)){
                            usersToChallenge.remove(user.id);
                        }
                    }
                }
            });

            return row;
        }

        class UserHolder {
            TextView username;
            RoundedImageView profileImage;
            CheckBox selected;
        }
    }
}

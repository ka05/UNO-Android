package com.envative.uno.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.emoba.widgets.CircleAnimation;
import com.envative.emoba.widgets.PercentView;
import com.envative.emoba.widgets.PercentViewPiece;
import com.envative.uno.R;
import com.envative.uno.comms.UNOAppState;

import java.util.ArrayList;

/**
 * Created by clay on 6/4/16.
 */
public class ProfileFragment extends EMBaseFragment {

    private TextView txtUsername;
    private TextView txtEmail;
    private TextView txtWinCount;
    private PercentView userPercentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(v);

        initPercentView();
        popFields();
        return v;
    }

    private void popFields() {
        txtUsername.setText(UNOAppState.currUser.username);
        txtEmail.setText(UNOAppState.currUser.email);
        txtWinCount.setText(UNOAppState.currUser.winCount +"");
    }

    @Override
    public void onResume(){
        super.onResume();

        initPercentView();
    }

    private void findViews(View v) {

        txtUsername = (TextView)v.findViewById(R.id.txtUsername);
        txtEmail = (TextView)v.findViewById(R.id.txtEmail);
        txtWinCount = (TextView)v.findViewById(R.id.txtWinCount);
        userPercentView = (PercentView)v.findViewById(R.id.userPercentView);
    }

    private void initPercentView(){
        userPercentView.reset();

        double percent1 = .75;

        ArrayList<PercentViewPiece> pieces = new ArrayList<>();
        pieces.add(new PercentViewPiece(getResources().getColor(R.color.colorCardGreen), (float)(percent1 * 360), 0));

        CircleAnimation animation = new CircleAnimation(userPercentView, pieces);
        animation.setDuration(1000);

        // if you want a background color for the border circle
        userPercentView.setBackgroundCircleColor(getResources().getColor(R.color.colorLightGray));

        userPercentView.setDisplayLabelColor(getResources().getColor(R.color.white));
        userPercentView.setDisplayLabel("PROFILE");
        // if you want to change the starting angle
        userPercentView.setInitialAngle(-90); // start at 45 degrees
        userPercentView.startAnimation(animation);
    }
}

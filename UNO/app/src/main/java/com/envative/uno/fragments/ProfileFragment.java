package com.envative.uno.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.emoba.widgets.PercentViewPiece;
import com.envative.uno.R;
import com.envative.uno.activities.UNOActivity;
import com.envative.uno.comms.UNOAppState;
import com.envative.uno.comms.UNOUtil;
import com.envative.uno.widgets.CircleAnimation;
import com.envative.uno.widgets.ProfilePercentView;

import java.io.File;
import java.util.ArrayList;

import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by clay on 6/4/16.
 */
public class ProfileFragment extends EMBaseFragment {

    private TextView txtUsername;
    private TextView txtEmail;
    private TextView txtWinCount;
    private ProfilePercentView userPercentView;
    private TextView btnRetakePhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(v);

        ((UNOActivity)getActivity()).setProfileFragment(this);

        initPercentView();
        popFields();

        EasyImage.configuration(getActivity())
                .setImagesFolderName("uno_profile")
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true);

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

    @Override
    public void onDestroy() {
        // Clear any configuration that was done!
        EasyImage.clearConfiguration(getActivity());
        super.onDestroy();
    }

    private void findViews(View v) {

        btnRetakePhoto = (TextView)v.findViewById(R.id.btnRetakePhoto);
        btnRetakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleProfileImgClicked();
            }
        });
        txtUsername = (TextView)v.findViewById(R.id.txtUsername);
        txtEmail = (TextView)v.findViewById(R.id.txtEmail);
        txtWinCount = (TextView)v.findViewById(R.id.txtWinCount);
        userPercentView = (ProfilePercentView)v.findViewById(R.id.userPercentView);
    }



    private void initPercentView(){
        userPercentView.reset();

        //if there is a profile photo already saved
        String profilePhotoPath = UNOUtil.get(getActivity()).getProfilePhoto();
        if(!profilePhotoPath.equals("")){
            userPercentView.setCenterBackgroundImageFile(new File(profilePhotoPath));
            btnRetakePhoto.setVisibility(View.VISIBLE);
        }
        // no profile image taken yet. set listener
        else{
            btnRetakePhoto.setVisibility(View.GONE);
            userPercentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleProfileImgClicked();
                }
            });
        }

        double percent1 = .75;

        ArrayList<PercentViewPiece> pieces = new ArrayList<>();
        pieces.add(new PercentViewPiece(getResources().getColor(R.color.colorCardGreen), (float)(percent1 * 360), 0));

        CircleAnimation animation = new CircleAnimation(userPercentView, pieces, CircleAnimation.AnimationType.Anticipate);
        animation.setDuration(1000);

        // if you want a background color for the border circle
        userPercentView.setBackgroundCircleColor(getResources().getColor(R.color.colorLightGray));

        userPercentView.setDisplayLabelColor(getResources().getColor(R.color.white));
        userPercentView.setDisplayLabel("");
        // if you want to change the starting angle
        userPercentView.setInitialAngle(-90); // start at 45 degrees
        userPercentView.startAnimation(animation);
    }

    private void handleProfileImgClicked() {
        Assent.setActivity((android.app.Activity) context, (android.app.Activity)context);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Marshmallow+
            if (!Assent.isPermissionGranted(Assent.CAMERA)) {
                // The if statement checks if the permission has already been granted before
                Assent.requestPermissions(new AssentCallback() {
                    @Override
                    public void onPermissionResult(PermissionResultSet result) {
                        // Permission granted or denied

                        // http://stackoverflow.com/questions/21872789/how-to-take-pic-with-camera-and-save-it-to-database-and-show-in-listview-in-andr
                        EasyImage.openCamera(getActivity(), 0);
                    }
                }, 69, Assent.CAMERA);
            }else{
                EasyImage.openCamera(getActivity(), 0);
            }
        }else{
            EasyImage.openCamera(getActivity(), 0);
        }
    }


    public void onPhotoReturned(File photoFile) {
//        Picasso.with(getActivity())
//                .load(photoFile)
//                .fit()
//                .centerCrop()
//                .into(userPercentView.getImageView());

        //TODO: upload image to server so it can be used in game for players

        UNOUtil.get(getActivity()).savePhoto(photoFile);
        userPercentView.setCenterBackgroundImageFile(photoFile);
        userPercentView.requestLayout();
    }


}

package com.envative.uno.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.envative.emoba.delegates.Callback;
import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.emoba.widgets.PercentViewPiece;
import com.envative.uno.R;
import com.envative.uno.activities.UNOActivity;
import com.envative.uno.comms.SocketService;
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
    private LinearLayout btnRetakePhotoContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(v);

        ((UNOActivity)getActivity()).setProfileFragment(this);

        initPercentView();
        popFields();

        EasyImage.configuration(getActivity())
                .setImagesFolderName("profile_images")
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

        btnRetakePhotoContainer = (LinearLayout) v.findViewById(R.id.btnRetakePhotoContainer);
        btnRetakePhoto = (TextView)v.findViewById(R.id.btnRetakePhoto);
        btnRetakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String profileImg = UNOUtil.get(getActivity()).getProfilePhoto();
//                if(!profileImg.equals("")){
//                    handleImageUpload(profileImg);
//                }
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
        String profilePhotoPath = UNOAppState.currUser.profileImgPath;
        Log.d("profilePhotoPath", profilePhotoPath + ":");

        // if its not the default image
        if(!profilePhotoPath.equals("")){
            userPercentView.setCenterBackgroundImageFile(new File(profilePhotoPath));
            userPercentView.requestLayout();
            btnRetakePhotoContainer.setVisibility(View.VISIBLE);
        }
        // no profile image taken yet. set listener
        else{
            btnRetakePhotoContainer.setVisibility(View.GONE);
            userPercentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleProfileImgClicked();
                }
            });
        }

        // handle checking if its saved
        UNOAppState.currUser.handleSaveProfileImage(getActivity(), new Callback(){
            @Override
            public void callback(Object object) {
                userPercentView.setCenterBackgroundImageFile(new File(UNOAppState.currUser.profileImgPath));
                userPercentView.requestLayout();
            }
        });

        double percent1 = .75;

        ArrayList<PercentViewPiece> pieces = new ArrayList<>();
        pieces.add(new PercentViewPiece(getResources().getColor(R.color.colorPrimaryDark), (float)(percent1 * 360), 0));

        CircleAnimation animation = new CircleAnimation(userPercentView, pieces, CircleAnimation.AnimationType.Overshoot);
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

    public void handleImageUpload(String path){
        SocketService.get(getActivity()).uploadProfileImg(path);
    }

    public void onPhotoReturned(File photoFile) {
        //TODO: upload image to server so it can be used in game for players
        Log.d("PATH::::", photoFile.getPath());
        handleImageUpload(photoFile.getPath());

        userPercentView.setCenterBackgroundImageFile(photoFile);
        userPercentView.requestLayout();

        String newFilename = UNOAppState.currUser.username + "_profile_img";

        UNOUtil.get(getActivity()).saveImage(getActivity(), photoFile.getPath(), newFilename, true, new Callback() {
            @Override
            public void callback(Object object) {
                String savedFilePath = (String)object;

                if(savedFilePath.equals("error")){
                    Toast.makeText(getActivity(), "Unable to save Image.", Toast.LENGTH_LONG).show();
                }else{
                    Log.d("path saved as:", savedFilePath);
                    UNOAppState.currUser.profileImgPath = savedFilePath;
                    UNOUtil.get(getActivity()).saveUser();
                    Log.d("currUser.profileImg:", UNOAppState.currUser.profileImgPath);
                }
            }
        });
    }

}

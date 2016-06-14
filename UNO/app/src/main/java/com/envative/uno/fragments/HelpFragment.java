package com.envative.uno.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.envative.uno.R;
import com.envative.uno.activities.UNOActivity;
import com.envative.uno.comms.UNOAppState;

/**
 * Created by clay on 6/13/16.
 */
public class HelpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);
        findViews(v);
        ((UNOActivity)getActivity()).setTitleText("UNO Help");
        return v;
    }

    private void findViews(View v) {
        TextView btnClose = (TextView)v.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        TextView txtHelp = (TextView)v.findViewById(R.id.txtHelp);
        txtHelp.setText(UNOAppState.helpText);
    }
}

package com.envative.uno.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.envative.emoba.fragments.EMBaseFragment;
import com.envative.emoba.widgets.EMModal;
import com.envative.uno.R;
import com.envative.uno.activities.UNOActivity;
import com.envative.uno.comms.SocketService;
import com.envative.uno.comms.UNOAppState;
import com.envative.uno.models.ChatMsg;
import com.envative.uno.models.SocketDelegateType;

import java.util.ArrayList;

/**
 * Created by clay on 6/4/16.
 */
public class ChatFragment extends EMBaseFragment implements View.OnClickListener{

    private ListView listView;
    private EditText chatMsgEditText;
    private TextView btnSend;

    private TextView emptyListView;
    private ChatMsgAdapter adapter;

    private int chatRoomId = 1; // overwrite when in game

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        findViews(v);

        SocketService.get(getActivity()).setDelegate(this, SocketDelegateType.Chat);
        SocketService.get(getActivity()).getChat(chatRoomId +"");
        return v;
    }

    private void findViews(View v) {

        chatMsgEditText = (EditText)v.findViewById(R.id.chatMsgEditText);
        btnSend = (TextView)v.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        listView = (ListView)v.findViewById(R.id.listView);
        emptyListView = (TextView)v.findViewById(R.id.emptyListViewText);
        listView.setEmptyView(v.findViewById(R.id.emptyListViewText));
        adapter = new ChatMsgAdapter(getActivity(), R.layout.item_chat_msg, UNOAppState.chatMsgArray);
        listView.setAdapter(adapter); // set up ListView
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSend){
            if(chatMsgEditText.getText().toString().trim().length() > 0){
                SocketService.get(getActivity()).sendChat(chatMsgEditText.getText().toString().trim());
                chatMsgEditText.setText(""); // clear out after message sends
            }else{
                EMModal.showModal(getActivity(), EMModal.ModalType.Warning, "Warning!", "Must enter text in input to send chat!");
            }
            ((UNOActivity)getActivity()).hideKeyboard(getActivity());
        }
    }

    public ChatMsgAdapter getAdapter() {
        return adapter;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public class ChatMsgAdapter extends ArrayAdapter<ChatMsg> {

        Context context;
        int layoutResourceId;
        ArrayList<ChatMsg> data = null;

        public ChatMsgAdapter(Context context, int layoutResourceId, ArrayList<ChatMsg> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ChatMsgHolder holder = null;

            if(row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ChatMsgHolder();
                holder.name = (TextView)row.findViewById(R.id.txtName);
                holder.msg = (TextView)row.findViewById(R.id.txtMsg);
                holder.date = (TextView)row.findViewById(R.id.txtDate);

                row.setTag(holder);
            } else {
                holder = (ChatMsgHolder)row.getTag();
            }

            final ChatMsg chatMsg = data.get(position);

            // TODO: handle formatting for these
            holder.name.setText(chatMsg.sender +":");

            // if the message is from me highlight the name
            if(chatMsg.sender.equals(UNOAppState.currUser.username)){
                holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else{
                holder.name.setTextColor(getResources().getColor(R.color.black));
            }

            holder.msg.setText(chatMsg.message);
            holder.date.setText(chatMsg.timestamp);

            return row;
        }

        class ChatMsgHolder {
            TextView name;
            TextView msg;
            TextView date;
        }
    }
}

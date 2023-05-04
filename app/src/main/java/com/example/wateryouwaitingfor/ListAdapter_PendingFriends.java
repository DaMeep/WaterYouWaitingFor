package com.example.wateryouwaitingfor;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter_PendingFriends extends ArrayAdapter<User> {

    private Activity activity;
    private int layoutResourceID;
    private ArrayList<User> pendingFriendsList;

    public ListAdapter_PendingFriends (Activity activity, int resource, ArrayList<User> objects){
        super(activity.getApplicationContext(), resource, objects);

        this.activity = activity;
        layoutResourceID = resource;
        pendingFriendsList = objects;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pendingFriendsList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceID, parent, false);
        }

        User pendingFriend = pendingFriendsList.get(position);
        String name = pendingFriend.username;
        String userID = pendingFriend.userID;

        TextView tv = null;

        tv = (TextView) convertView.findViewById(R.id.pendingFriendName);
        if (name != null && name.length() > 0) {
            tv.setText(name);
        }
        else {
            tv.setText("User");
        }

        tv = (TextView) convertView.findViewById(R.id.pendingFriendID);
        if (userID != null && userID.length() > 0) {
            tv.setText(userID);
        }
        else {
            tv.setText("UserID");
        }

        return convertView;
    }

}

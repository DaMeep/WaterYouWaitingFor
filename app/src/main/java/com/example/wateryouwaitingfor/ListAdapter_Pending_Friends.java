package com.example.wateryouwaitingfor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter_Pending_Friends extends ArrayAdapter<User> {

    private Activity activity;
    private int layoutResourceID;
    private ArrayList<User> pendingFriendsList;
    private ArrayList<String> userIDs;

    public ListAdapter_Pending_Friends(Activity activity, int resource, ArrayList<User> objects, ArrayList<String> ids){
        super(activity.getApplicationContext(), resource, objects);

        this.activity = activity;
        layoutResourceID = resource;
        pendingFriendsList = objects;
        userIDs = ids;
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
        String name = pendingFriend.getUsername();
        String userID = userIDs.get(position);
        int userScore = pendingFriend.getPoints();

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

        tv = (TextView) convertView.findViewById(R.id.friendScoreText);
        tv.setText(userScore);

        ImageButton acceptRequestButton = (ImageButton) convertView.findViewById(R.id.acceptFriendButton);
        ImageButton denyRequestButton = (ImageButton) convertView.findViewById(R.id.denyFriendButton);

        PendingFriendsFragment pendingFriendsFragment = (PendingFriendsFragment)((MainActivity)activity).getSupportFragmentManager().findFragmentByTag("PendingFriendsFragment");
        if (pendingFriendsFragment != null && pendingFriendsFragment.isVisible()) {
            acceptRequestButton.setOnClickListener(pendingFriendsFragment);
            acceptRequestButton.setTag(new String(userID));
            denyRequestButton.setOnClickListener(pendingFriendsFragment);
            denyRequestButton.setTag(new String(userID));
        }







        return convertView;
    }

}

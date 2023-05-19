package com.example.wateryouwaitingfor;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter_Accepted_Friends extends ArrayAdapter<User> {
    private Activity activity;
    private int layoutResourceID;
    private ArrayList<User> friendsList;
    private ArrayList<String> userIDs;

    public ListAdapter_Accepted_Friends(Activity activity, int resource, ArrayList<User> objects, ArrayList<String> ids){
        super(activity.getApplicationContext(), resource, objects);

        this.activity = activity;
        layoutResourceID = resource;
        friendsList = objects;
        userIDs = ids;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceID, parent, false);
        }

        // Get information of the User

        User friend = friendsList.get(position);
        String name = friend.username;
        String userID = userIDs.get(position);
        int userScore = friend.points;

        // Populate the list item with the data

        TextView tv;
        Resources res = activity.getResources();

        tv = convertView.findViewById(R.id.acceptedFriendName);
        if (name != null && name.length() > 0) {
            tv.setText(name);
        }
        else {
            tv.setText(res.getString(R.string.usernameDefault));
        }

        tv = convertView.findViewById(R.id.acceptedFriendID);
        if (userID != null && userID.length() > 0) {
            tv.setText(userID);
        }
        else {
            tv.setText(res.getString(R.string.user_id));
        }

        tv = convertView.findViewById(R.id.friendScoreText);
        tv.setText(String.valueOf(userScore));

        // Pair & Set the delete button

        ImageButton deleteFriendButton = convertView.findViewById(R.id.deleteFriendButton);

        FriendsFragment friendsFragment = (FriendsFragment)((MainActivity)activity).getSupportFragmentManager().findFragmentByTag("FriendsFragment");
        if (friendsFragment != null && friendsFragment.isVisible()) {
            deleteFriendButton.setOnClickListener(friendsFragment);
            deleteFriendButton.setTag(userID);
        }

        return convertView;
    }

}

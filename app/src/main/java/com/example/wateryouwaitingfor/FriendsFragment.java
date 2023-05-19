package com.example.wateryouwaitingfor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A {@link Fragment} subclass for
 * handling the friend list
 */
public class FriendsFragment extends Fragment implements View.OnClickListener{

    private SharedPreferences sharedpreferences; // Shared Preferences Reference
    private DatabaseReference mUsersReference; // Firebase Reference

    private User currentUser; // Application's User Data
    private HashMap<String, User> listOfUsers; // List of all Users

    private ArrayList<User> friendList; // List of friended User objects
    private ArrayList<String> friendIDs; // List of friends' User IDs

    private ListAdapter_Accepted_Friends adapter; // Adapter for ListView display of friends

    private TextView userPoints; // Text displaying the current User's points

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){

        mUsersReference = ((MainActivity)getActivity()).getUserReference();

        listOfUsers = ((MainActivity)getActivity()).getUsers();

        currentUser = ((MainActivity)getActivity()).getCurrentUser();

        friendList = new ArrayList<>();
        friendIDs = new ArrayList<>();

        adapter = new ListAdapter_Accepted_Friends(getActivity(), R.layout.accepted_friends_item, friendList, friendIDs);
        ListView friendView = view.findViewById(R.id.friendsView);
        friendView.setAdapter(adapter);

        updateFriendList();

        view.findViewById(R.id.pendingFriendsButton).setOnClickListener(this);

        userPoints = view.findViewById(R.id.userPointsView);
        Resources res = getResources();
        userPoints.setText(String.format(res.getString(R.string.userPointsText), currentUser.points));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }


    @Override
    public void onClick(View view) {
        String currentUserID = sharedpreferences.getString("userID", "null");
        String targetUser = (String)view.getTag();
        switch (view.getId()){
            case R.id.pendingFriendsButton:
                //Swap Fragment with the Pending Friends List
                MainActivity ma = ((MainActivity)getActivity());
                ma.replaceFragment(new PendingFriendsFragment());
                break;
            case R.id.deleteFriendButton:
                // Delete Friend from current User
                currentUser.deleteFriend(targetUser);
                mUsersReference.child(currentUserID).child("acceptedFriendsList").setValue(currentUser.acceptedFriendsList);
                updateFriendList();

                // Delete Friend from target User
                User targetUserObject = listOfUsers.get(targetUser);
                targetUserObject.deleteFriend(currentUserID);
                mUsersReference.child(targetUser).child("acceptedFriendsList").setValue(targetUserObject.acceptedFriendsList);
                break;
            default:
                break;
        }
    }

    /**
     * Updates the Local Friend List visually to match up with the Firebase
     */
    private void updateFriendList(){
        friendList.clear();
        friendIDs.clear();

        for(String key : listOfUsers.keySet()){
            if (currentUser.acceptedFriendsList.contains(key)){
                friendList.add(listOfUsers.get(key));
                friendIDs.add(key);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
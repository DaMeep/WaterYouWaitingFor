package com.example.wateryouwaitingfor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A {@link Fragment} subclass for
 * handling the pending friend list
 */
public class PendingFriendsFragment extends Fragment implements View.OnClickListener {

    private EditText friendRequestEdit; // EditText for entering wanted friend's User ID

    private SharedPreferences sharedpreferences; // Shared Preferences Reference
    private DatabaseReference mUsersReference; // Firebase Reference

    private User currentUser; // Application's User Data
    private HashMap<String, User> listOfUsers; // List of all Users

    private ArrayList<User> pendingFriendList; // List of pending friend User objects
    private ArrayList<String> pendingFriendIDs; // List of pending friends' User IDs

    private ListAdapter_Pending_Friends adapter; // Adapter for ListView display of pending friends

    public PendingFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mUsersReference = ((MainActivity)getActivity()).getUserReference();

        listOfUsers = ((MainActivity)getActivity()).getUsers();

        currentUser = ((MainActivity)getActivity()).getCurrentUser();

        pendingFriendList = new ArrayList<>();
        pendingFriendIDs = new ArrayList<>();

        adapter = new ListAdapter_Pending_Friends(getActivity(), R.layout.pending_friends_item, pendingFriendList, pendingFriendIDs);
        ListView pendingFriendsView = (ListView) view.findViewById(R.id.pendingFriendsView);
        pendingFriendsView.setAdapter(adapter);

        updatePendingFriends();

        ((Button) view.findViewById(R.id.backToFriendsButton)).setOnClickListener(this);

        friendRequestEdit = (EditText) view.findViewById(R.id.editTextID);
        ((Button) view.findViewById(R.id.sendFriendRequestButton)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String currentUserID = sharedpreferences.getString("userID", "null");
        String targetUser = (String)view.getTag();

        switch (view.getId()){
            case R.id.backToFriendsButton:
                MainActivity ma = ((MainActivity)getActivity());
                ma.replaceFragment(new FriendsFragment());
                break;
            case R.id.sendFriendRequestButton:
                String userID = friendRequestEdit.getText().toString();
                friendRequestEdit.setText("");
                if (listOfUsers.containsKey(userID)){
                    ArrayList<String> pendingFriendsList = listOfUsers.get(userID).pendingFriendsList;
                    if ( !pendingFriendsList.contains(currentUserID) && !listOfUsers.get(userID).acceptedFriendsList.contains(currentUserID)){
                        pendingFriendsList.add(currentUserID);
                        mUsersReference.child(userID).child("pendingFriendsList").setValue(pendingFriendsList);
                    }
                }
                else{
                    Utils.toast(getActivity().getApplicationContext(), "Error: User Not Found");
                }
                break;
            case R.id.acceptFriendButton:
                currentUser.acceptFriend(targetUser);
                mUsersReference.child(currentUserID).child("pendingFriendsList").setValue(currentUser.pendingFriendsList);
                mUsersReference.child(currentUserID).child("acceptedFriendsList").setValue(currentUser.acceptedFriendsList);
                updatePendingFriends();

                ArrayList<String> targetFriendsList =  listOfUsers.get(targetUser).acceptedFriendsList;
                targetFriendsList.add(currentUserID);
                mUsersReference.child(targetUser).child("acceptedFriendsList").setValue(targetFriendsList);
                break;
            case R.id.denyFriendButton:
                currentUser.denyFriend(targetUser);
                mUsersReference.child(currentUserID).child("pendingFriendsList").setValue(currentUser.pendingFriendsList);
                updatePendingFriends();
                break;
            default:
                break;
        }
    }

    /**
     * Updates the Local Pending Friend List visually
     * to match up with the Firebase
     */
    private void updatePendingFriends(){
        pendingFriendList.clear();
        pendingFriendIDs.clear();

        for(String key : listOfUsers.keySet()){
            Log.e("TESTING", currentUser.pendingFriendsList.toString());
            if (currentUser.pendingFriendsList.contains(key)){
                Log.e("TESTING", "WE GODEM");
                pendingFriendList.add(listOfUsers.get(key));
                pendingFriendIDs.add(key);
            }
        }

        Log.e("TESTING", listOfUsers.keySet().toString());

        adapter.notifyDataSetChanged();
    }
}
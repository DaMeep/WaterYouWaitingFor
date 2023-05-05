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
 * A simple {@link Fragment} subclass.
 * Use the {@link PendingFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingFriendsFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText friendRequestEdit;

    private SharedPreferences sharedpreferences;
    private DatabaseReference mUsersReference;

    private User currentUser;
    private HashMap<String, User> listOfUsers;

    private ArrayList<User> pendingFriendList;
    private ArrayList<String> pendingFriendIDs;

    private ListAdapter_Pending_Friends adapter;

    public PendingFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PendingFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingFriendsFragment newInstance(String param1, String param2) {
        PendingFriendsFragment fragment = new PendingFriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
                ArrayList<String> pendingFriendsList = listOfUsers.get(userID).pendingFriendsList;
                if (!pendingFriendsList.contains(currentUserID) && !listOfUsers.get(userID).acceptedFriendsList.contains(currentUserID)){
                    pendingFriendsList.add(currentUserID);
                    mUsersReference.child(userID).child("pendingFriendsList").setValue(pendingFriendsList);
                }
                break;
            case R.id.acceptFriendButton:
                currentUser.acceptFriend(targetUser);
                mUsersReference.child(currentUserID).child("pendingFriendsList").setValue(currentUser.pendingFriendsList);
                mUsersReference.child(currentUserID).child("acceptedFriendsList").setValue(currentUser.acceptedFriendsList);
                updatePendingFriends();
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
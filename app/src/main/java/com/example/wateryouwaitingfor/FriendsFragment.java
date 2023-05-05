package com.example.wateryouwaitingfor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SharedPreferences sharedpreferences;
    private DatabaseReference mUsersReference;

    private User currentUser;
    private HashMap<String, User> listOfUsers;

    private ArrayList<User> friendList;
    private ArrayList<String> friendIDs;

    private ListAdapter_Accepted_Friends adapter;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        mUsersReference = ((MainActivity)getActivity()).getUserReference();

        listOfUsers = ((MainActivity)getActivity()).getUsers();

        currentUser = ((MainActivity)getActivity()).getCurrentUser();

        adapter = new ListAdapter_Accepted_Friends(getActivity(), R.layout.accepted_friends_item, friendList, friendIDs);
        ListView friendView = (ListView) view.findViewById(R.id.friendsView);
        friendView.setAdapter(adapter);

        updateFriendList();

//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.putString("userID", "53289HDUIW8932");
//        editor.apply();

//        String userId = sharedpreferences.getString("userID", "null");
//        User userTest = new User(sharedpreferences.getString("username", "User"));
//        mUsersReference.child(userId).setValue(userTest);



        view.findViewById(R.id.pendingFriendsButton).setOnClickListener(this);
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
                MainActivity ma = ((MainActivity)getActivity());
                ma.replaceFragment(new PendingFriendsFragment());
                break;
            case R.id.deleteFriendButton:
                currentUser.deleteFriend(targetUser);
                mUsersReference.child(currentUserID).child("acceptedFriendsList").setValue(currentUser.getAcceptedFriends());
                updateFriendList();
                break;
            default:
                break;
        }
    }

    private void updateFriendList(){
        friendList.clear();
        friendIDs.clear();

        for(String key : listOfUsers.keySet()){
            if (currentUser.getAcceptedFriends().contains(key)){
                friendList.add(listOfUsers.get(key));
                friendIDs.add(key);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
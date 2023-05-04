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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

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

    private SharedPreferences sharedpreferences;
    private DatabaseReference mDatabaseReference;

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

        ArrayList<User> pendingFriendsList = new ArrayList<>();

        String userId = sharedpreferences.getString("userID", "null");

        mDatabaseReference = ((MainActivity)getActivity()).getFirebaseReference();

        mDatabaseReference.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    User currentUser = (User) task.getResult().getValue();
                    for(String id : currentUser.pendingFriendsList){

                    }
//                    pendingFriendsList.addAll(currentUser.pendingFriendsList());
                }
            }
        });

        ListAdapter_PendingFriends pendingFriendsAdapter = new ListAdapter_PendingFriends(getActivity(), R.layout.pending_friends_item, pendingFriendsList);

        ListView listView = (ListView) view.findViewById(R.id.pendingFriendsView);
        listView.setAdapter(pendingFriendsAdapter);

        ((Button) view.findViewById(R.id.backToFriendsButton)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backToFriendsButton:
                MainActivity ma = ((MainActivity)getActivity());
                ma.replaceFragment(new FriendsFragment());
                break;
            default:
                break;
        }
    }
}
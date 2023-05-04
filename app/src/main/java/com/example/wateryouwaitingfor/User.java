package com.example.wateryouwaitingfor;


import java.util.ArrayList;

public class User {
    public String username;
    public String userID;
    public ArrayList<String> acceptedFriendsList;
    public ArrayList<String> pendingFriendsList;


    public User(){

    }

    public User(String username, String userID){
        this.username = username;
        this.userID = userID;
        acceptedFriendsList = new ArrayList<String>();
        pendingFriendsList = new ArrayList<String>();
    }

    public void acceptFriend(String friendID){
        int position = pendingFriendsList.indexOf(friendID);

        if (position != -1){
            acceptedFriendsList.add(pendingFriendsList.get(position));
            pendingFriendsList.remove(position);
        }
    }

    public void denyFriend(String friendID){
        int position = pendingFriendsList.indexOf(friendID);

        if (position != -1){
            pendingFriendsList.remove(position);
        }
    }
}

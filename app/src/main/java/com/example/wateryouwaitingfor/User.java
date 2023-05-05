package com.example.wateryouwaitingfor;

import java.util.ArrayList;

public class User {
    public String username;
    public ArrayList<String> acceptedFriendsList = new ArrayList<>();
    public ArrayList<String> pendingFriendsList  = new ArrayList<>();;
    public int points = 0;

    public User(){

    }

    public User(String username){
        this.username = username;
    }

    public void addPoints(int points){
        if (points > 0){
            this.points += points;
        }
    }

    public void resetPoints(){
        points = 0;
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

    public void deleteFriend(String friendID){
        int position = acceptedFriendsList.indexOf(friendID);

        if (position != -1){
            acceptedFriendsList.remove(position);
        }
    }
}

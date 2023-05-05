package com.example.wateryouwaitingfor;

import java.util.ArrayList;

public class User {
    private String username;
    private ArrayList<String> acceptedFriendsList = new ArrayList<>();
    private ArrayList<String> pendingFriendsList  = new ArrayList<>();;
    private int points;

    public User(){

    }

    public User(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public int getPoints(){
        return points;
    }

    public ArrayList<String> getAcceptedFriends(){
        return acceptedFriendsList;
    }

    public ArrayList<String> getPendingFriends(){
        return pendingFriendsList;
    }

    public void setUsername(String username){
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

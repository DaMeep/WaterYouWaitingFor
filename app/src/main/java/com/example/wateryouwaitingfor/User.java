package com.example.wateryouwaitingfor;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * A class for storing User and Friend data
 * on Firebase
 */
public class User {
    public String username; // the username of the user
    public ArrayList<String> acceptedFriendsList = new ArrayList<>(); // the list of User IDs in the Friends List
    public ArrayList<String> pendingFriendsList  = new ArrayList<>(); // the list of User IDs who have requested to be friends
    public int points = 0; // the amount of points held by the user

    public User(){
        // Necessary default constructor
    }

    public User(String username){
        this.username = username;
    }

    /**
     * Adds points to the User
     *
     * @param points the number of points to be added
     */
    public void addPoints(int points){
        if (points > 0){
            this.points += points;
        }
    }

    /**
     * Resets the number of points held by the user
     */
    public void resetPoints(){
        points = 0;
    }

    /**
     * Moves the specified User ID from the PendingFriendsList
     * to the AcceptedFriendsList
     *
     * @param friendID the target User ID
     */
    public void acceptFriend(String friendID){
        int position = pendingFriendsList.indexOf(friendID);

        if (position != -1){
            acceptedFriendsList.add(pendingFriendsList.get(position));
            pendingFriendsList.remove(position);
        }
    }

    /**
     * Deletes the specific User ID from the PendingFriendsList
     *
     * @param friendID the target User ID
     */
    public void denyFriend(String friendID){
        int position = pendingFriendsList.indexOf(friendID);

        if (position != -1){
            pendingFriendsList.remove(position);
        }
    }

    /**
     * Deletes the specific User ID from the AcceptedFriendsList
     *
     * @param friendID the target User ID
     */
    public void deleteFriend(String friendID){
        int position = acceptedFriendsList.indexOf(friendID);

        if (position != -1){
            acceptedFriendsList.remove(position);
        }
    }
}

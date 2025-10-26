package com.example.chatmessenger.user.entity;

import java.util.ArrayList;
import java.util.List;

public class Client extends User {
    private List<String> friendRequests = new ArrayList<>();
    private List<String> friendList = new ArrayList<>();
    private Status status;
    private boolean isApproved;

    public Client(String username, String password) {
        super(username, password);
    }

    public Client(String username) {
        super(username);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<String> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public void addRequest(String requester) {
        this.friendRequests.add(requester);
    }

    public void removeRequest(String requester) {
        this.friendRequests.remove(requester);
    }

    public void addFriend(String friend) {
        this.friendList.add(friend);
    }

    public void removeFriend(String friend) {
        this.friendList.remove(friend);
    }
}

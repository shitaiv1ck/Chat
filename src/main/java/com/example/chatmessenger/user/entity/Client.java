package com.example.chatmessenger.user.entity;

import java.util.HashSet;
import java.util.Set;

public class Client extends User {
    private Set<Client> friendRequests = new HashSet<>();
    private Set<Client> friendList = new HashSet<>();
    private Status status;
    private boolean isApproved;

    public Client(String username, String password) {
        super(username, password);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

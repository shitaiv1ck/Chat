package com.example.chatmessenger.friendship;

import com.example.chatmessenger.user.entity.Client;

public interface FriendshipService {
    public void sendFriendRequest(Client from, Client  to);

    public boolean acceptFriendRequest(Client  accepter, Client  requster);

    public void removeFriend(Client  client, Client friendToRemove);

    public boolean areFriends(Client client1, Client client2);
}

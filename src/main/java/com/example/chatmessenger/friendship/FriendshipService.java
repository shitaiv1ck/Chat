package com.example.chatmessenger.friendship;

import com.example.chatmessenger.user.entity.Client;

public interface FriendshipService {
    public boolean sendFriendRequest(Client from, Client  to);

    public boolean acceptFriendRequest(Client  accepter, Client  requster);

    public boolean removeFriend(Client  client, Client friendToRemove);

    public boolean areFriends(Client client1, Client client2);
}

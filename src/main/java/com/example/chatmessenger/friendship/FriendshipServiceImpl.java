package com.example.chatmessenger.friendship;

import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.repository.UserRepository;

public class FriendshipServiceImpl implements FriendshipService {
    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;

    public FriendshipServiceImpl(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public boolean sendFriendRequest(Client from, Client to) {
        if (userRepository.findClientByUsername(to) && !areFriends(from, to)) {
            friendshipRepository.saveRequest(from, to);

            return true;
        }

        return false;
    }

    @Override
    public boolean acceptFriendRequest(Client accepter, Client requster) {
        if (friendshipRepository.findFriendRequest(requster, accepter) && !areFriends(accepter, requster)) {
            friendshipRepository.saveFriendship(requster, accepter);
            friendshipRepository.deleteFriendRequest(requster, accepter);

            return true;
        }

        return false;
    }

    @Override
    public boolean removeFriend(Client client, Client friendToRemove) {
        if (areFriends(client, friendToRemove)) {
            friendshipRepository.deleteFriendship(client, friendToRemove);

            return true;
        }

        return false;
    }

    @Override
    public boolean areFriends(Client client1, Client client2) {
        return friendshipRepository.findFriendship(client1, client2) || friendshipRepository.findFriendship(client2, client1);
    }
}

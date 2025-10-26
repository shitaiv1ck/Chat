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
    public void sendFriendRequest(Client from, Client to) {
        if (userRepository.findClientByUsername(to) && !areFriends(from, to)) {
            friendshipRepository.saveRequest(from, to);
        }
    }

    @Override
    public boolean acceptFriendRequest(Client accepter, Client requster) {
        if (friendshipRepository.findFriendRequest(requster, accepter) && !areFriends(accepter, requster)) {
            friendshipRepository.saveFriendship(requster, accepter);
            friendshipRepository.deleteFriendRequest(requster, accepter);
            friendshipRepository.deleteFriendRequest(accepter, requster);

            return true;
        }

        return false;
    }

    @Override
    public void removeFriend(Client client, Client friendToRemove) {

    }

    @Override
    public boolean areFriends(Client client1, Client client2) {
        return friendshipRepository.findFriendship(client1, client2) || friendshipRepository.findFriendship(client2, client1);
    }
}

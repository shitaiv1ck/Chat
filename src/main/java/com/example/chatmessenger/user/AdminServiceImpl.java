package com.example.chatmessenger.user;

import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.repository.UserRepository;

public class AdminServiceImpl implements AdminService {
    UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void approveRegistration(Client client) {
        if (!userRepository.findClientByApproved(client)) {
            userRepository.updateApprovedClient(client);
        }
    }
}

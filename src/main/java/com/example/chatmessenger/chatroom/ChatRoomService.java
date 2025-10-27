package com.example.chatmessenger.chatroom;

import com.example.chatmessenger.user.entity.Client;

public interface ChatRoomService {
    public ChatRoom createChat(Client client1, Client client2);
}

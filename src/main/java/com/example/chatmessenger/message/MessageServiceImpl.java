package com.example.chatmessenger.message;

import com.example.chatmessenger.chatroom.ChatRoom;
import com.example.chatmessenger.chatroom.ChatRoomRepository;
import com.example.chatmessenger.user.entity.Client;

public class MessageServiceImpl implements MessageService{
    private MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void sendMessage(Message message) {
        messageRepository.saveMessage(message);
    }
}

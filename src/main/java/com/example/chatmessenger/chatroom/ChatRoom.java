package com.example.chatmessenger.chatroom;

import com.example.chatmessenger.message.Message;
import com.example.chatmessenger.user.entity.Client;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private Client client1;
    private Client client2;
    private List<Message> messages = new ArrayList<>();

    public ChatRoom(Client client1, Client client2) {
        this.client1 = client1;
        this.client2 = client2;
    }

    public Client getClient1() {
        return client1;
    }

    public void setClient1(Client client1) {
        this.client1 = client1;
    }

    public Client getClient2() {
        return client2;
    }

    public void setClient2(Client client2) {
        this.client2 = client2;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}

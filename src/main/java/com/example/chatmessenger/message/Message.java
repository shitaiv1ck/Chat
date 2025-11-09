package com.example.chatmessenger.message;

import com.example.chatmessenger.user.entity.Client;

public class Message {
    private Client sender;
    private String text;
    private int chatId;

    public Message(Client sender, String text, int chatId) {
        this.sender = sender;
        this.text = text;
        this.chatId = chatId;
    }

    public Client getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public int getChatId() {
        return chatId;
    }
}

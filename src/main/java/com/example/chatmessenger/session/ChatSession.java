package com.example.chatmessenger.session;

import com.example.chatmessenger.chatroom.ChatRoom;

public class ChatSession {
    private static ChatRoom chatRoom;

    public ChatSession() {
    }

    public static ChatRoom getChatRoom() {
        return chatRoom;
    }

    public static void setChatRoom(ChatRoom chatRoom) {
        ChatSession.chatRoom = chatRoom;
    }
}

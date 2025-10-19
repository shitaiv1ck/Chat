package com.example.chatmessenger.session;

import com.example.chatmessenger.user.entity.Client;

public class ClientSession {
    private static Client currentClient;

    public ClientSession() {}

    public static Client getCurrentClient() {
        return currentClient;
    }

    public static void setCurrentClient(Client currentClient) {
        ClientSession.currentClient = currentClient;
    }
}

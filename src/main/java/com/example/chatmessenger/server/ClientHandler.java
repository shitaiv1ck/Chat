package com.example.chatmessenger.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String clientInfo;
    private String username; // Добавляем поле для имени пользователя

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received: " + message);

                if (message.startsWith("REGISTER:")) {
                    this.clientInfo = message.substring(9);
                    this.username = extractUsername(clientInfo);
                    System.out.println("Client registered: " + clientInfo);
                    broadcastStatusUpdate(username, "ONLINE");

                } else if (message.startsWith("MESSAGE:")) {
                    ChatServer.broadcastMessage(message, this);
                } else if (message.startsWith("NOTIFY:")) {
                    ChatServer.broadcastMessage(message, this);
                } else if (message.startsWith("STATUS:")) {
                    handleStatusUpdate(message);
                } else if (message.startsWith("FRIEND_REQUEST:")) {
                    handleFriendRequest(message);
                } else if (message.startsWith("REQUEST_ACCEPTED:")) {
                    handleRequestAccepted(message);
                } else if (message.startsWith("FRIEND_REMOVED:")) {
                    handleFriendRemoved(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + clientInfo);
        } finally {
            try {
                if (username != null) {
                    broadcastStatusUpdate(username, "OFFLINE");
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this);
        }
    }

    private void handleFriendRequest(String message) {
        String[] parts = message.split(":");
        if (parts.length >= 3) {
            String sender = parts[1];
            String receiver = parts[2];

            System.out.println("Friend request from " + sender + " to " + receiver);

            ChatServer.sendToUser(receiver, "NEW_REQUEST:" + sender);
        }
    }

    private void handleRequestAccepted(String message) {
        String[] parts = message.split(":");
        if (parts.length >= 3) {
            String accepter = parts[1];
            String requester = parts[2];

            System.out.println("Friend request accepted by " + accepter + " from " + requester);

            ChatServer.sendToUser(requester, "REQUEST_ACCEPTED:" + accepter);
            ChatServer.sendToUser(accepter, "FRIEND_ADDED:" + requester);
        }
    }

    private void handleFriendRemoved(String message) {
        String[] parts = message.split(":");
        if (parts.length >= 3) {
            String remover = parts[1];
            String removedUser = parts[2];

            System.out.println("Friend removed: " + removedUser + " by " + remover);

            ChatServer.sendToUser(removedUser, "FRIEND_REMOVED_BY:" + remover);
            ChatServer.sendToUser(remover, "FRIEND_REMOVED:" + removedUser);
        }
    }

    private String extractUsername(String clientInfo) {
        String[] parts = clientInfo.split(":");
        return parts[0];
    }

    private void handleStatusUpdate(String message) {
        String[] parts = message.split(":");
        if (parts.length >= 3) {
            String username = parts[1];
            String newStatus = parts[2];
            broadcastStatusUpdate(username, newStatus);
        }
    }

    private void broadcastStatusUpdate(String username, String newStatus) {
        String statusMessage = "STATUS_UPDATE:" + username + ":" + newStatus;

        for (ClientHandler client : ChatServer.getClients()) {
            if (!username.equals(client.getUsername())) {
                client.sendMessage(statusMessage);
            }
        }

        System.out.println("Broadcasted status update for " + username + " to others");
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public String getUsername() {
        return username;
    }
}
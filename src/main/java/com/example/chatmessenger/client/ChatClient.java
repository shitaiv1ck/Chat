package com.example.chatmessenger.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private MessageListener messageListener;
    private String currentUsername;

    public interface MessageListener {
        void onMessageReceived(String message);
        void onStatusUpdate(String username, String newStatus); // Новый метод
    }

    public ChatClient(MessageListener listener) {
        this.messageListener = listener;
    }

    public boolean connect() {
        try {
            socket = new Socket("localhost", 12345);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            new Thread(this::listenForMessages).start();
            return true;
        } catch (IOException e) {
            System.out.println("Failed to connect to server: " + e.getMessage());
            return false;
        }
    }

    public void registerClient(String clientInfo) {
        this.currentUsername = extractUsername(clientInfo);
        sendMessage("REGISTER:" + clientInfo);
    }

    public void updateStatus(String newStatus) {
        String message = String.format("STATUS:%s:%s", currentUsername, newStatus);
        sendMessage(message);
    }


    public void notifyNewMessage(int chatId) {
        sendMessage("NOTIFY:" + chatId);
    }

    private String extractUsername(String clientInfo) {
        String[] parts = clientInfo.split(":");
        return parts[0];
    }

    private void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }

    public interface RequestListener {
        void onNewFriendRequest(String sender);
        void onRequestAccepted(String accepter);
        void onFriendAdded(String newFriend);
        void onFriendRemoved(String removedFriend); // Удаление друга
        void onFriendRemovedBy(String remover);     // Удаление друга
    }

    private RequestListener requestListener;

    public void setRequestListener(RequestListener listener) {
        this.requestListener = listener;
    }

    public void sendFriendRequest(String sender, String receiver) {
        String message = String.format("FRIEND_REQUEST:%s:%s", sender, receiver);
        sendMessage(message);
    }

    public void acceptFriendRequest(String accepter, String requester) {
        String message = String.format("REQUEST_ACCEPTED:%s:%s", accepter, requester);
        sendMessage(message);
    }

    public void notifyFriendRemoved(String remover, String removedUser) {
        String message = String.format("FRIEND_REMOVED:%s:%s", remover, removedUser);
        sendMessage(message);
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received from server: " + message);

                if (message.startsWith("STATUS_UPDATE:")) {
                    String[] parts = message.split(":");
                    if (parts.length >= 3) {
                        String username = parts[1];
                        String newStatus = parts[2];
                        if (messageListener != null) {
                            messageListener.onStatusUpdate(username, newStatus);
                        }
                    }
                } else if (message.startsWith("NOTIFY:")) {
                    if (messageListener != null) {
                        messageListener.onMessageReceived(message);
                    }
                } else if (message.startsWith("MESSAGE:")) {
                    if (messageListener != null) {
                        messageListener.onMessageReceived(message);
                    }
                } else if (message.startsWith("NEW_REQUEST:")) {
                    String sender = message.substring(12);
                    if (requestListener != null) {
                        requestListener.onNewFriendRequest(sender);
                    }
                } else if (message.startsWith("REQUEST_ACCEPTED:")) {
                    String accepter = message.substring(17);
                    if (requestListener != null) {
                        requestListener.onRequestAccepted(accepter);
                    }
                } else if (message.startsWith("FRIEND_ADDED:")) {
                    String newFriend = message.substring(13);
                    if (requestListener != null) {
                        requestListener.onFriendAdded(newFriend);
                    }
                } else if (message.startsWith("FRIEND_REMOVED:")) {
                    String removedFriend = message.substring(15);
                    if (requestListener != null) {
                        requestListener.onFriendRemoved(removedFriend);
                    }
                } else if (message.startsWith("FRIEND_REMOVED_BY:")) {
                    String remover = message.substring(18);
                    if (requestListener != null) {
                        requestListener.onFriendRemovedBy(remover);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server");
        }
    }

    public void disconnect() {
        if (currentUsername != null) {
            updateStatus("OFFLINE");
        }

        try {
            Thread.sleep(100);

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
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

            // Запускаем поток для прослушивания сообщений
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

    // Новый метод: отправка обновления статуса
    public void updateStatus(String newStatus) {
        String message = String.format("STATUS:%s:%s", currentUsername, newStatus);
        sendMessage(message);
    }

    // Остальные методы без изменений
    public void sendChatMessage(int chatId, String username, String text) {
        String message = String.format("MESSAGE:%d:%s:%s", chatId, username, text);
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

    // Установка листенера
    public void setRequestListener(RequestListener listener) {
        this.requestListener = listener;
    }

    // Новые методы для работы с заявками
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
                    // Обрабатываем обновление статуса
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
                    // Новый тип: пришла заявка в друзья
                    String sender = message.substring(12);
                    if (requestListener != null) {
                        requestListener.onNewFriendRequest(sender);
                    }
                } else if (message.startsWith("REQUEST_ACCEPTED:")) {
                    // Заявка принята (для отправителя заявки)
                    String accepter = message.substring(17);
                    if (requestListener != null) {
                        requestListener.onRequestAccepted(accepter);
                    }
                } else if (message.startsWith("FRIEND_ADDED:")) {
                    // Добавлен новый друг (для принявшего заявку)
                    String newFriend = message.substring(13);
                    if (requestListener != null) {
                        requestListener.onFriendAdded(newFriend);
                    }
                } else if (message.startsWith("FRIEND_REMOVED:")) {
                    // Удалили друга
                    String removedFriend = message.substring(15);
                    if (requestListener != null) {
                        requestListener.onFriendRemoved(removedFriend);
                    }
                } else if (message.startsWith("FRIEND_REMOVED_BY:")) {
                    // Друг удалил вас
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
        // При отключении уведомляем сервер о смене статуса
        if (currentUsername != null) {
            updateStatus("OFFLINE");
        }

        try {
            Thread.sleep(100); // Даем время отправить статус

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
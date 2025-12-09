package com.example.chatmessenger.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private MessageListener messageListener;

    public interface MessageListener {
        void onMessageReceived(String message);
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
        sendMessage("REGISTER:" + clientInfo);
    }

    public void sendChatMessage(int chatId, String username, String text) {
        String message = String.format("MESSAGE:%d:%s:%s", chatId, username, text);
        sendMessage(message);
    }

    public void notifyNewMessage(int chatId) {
        sendMessage("NOTIFY:" + chatId);
    }

    private void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received from server: " + message);

                if (message.startsWith("NOTIFY:")) {
                    String[] parts = message.split(":");
                    int chatId = Integer.parseInt(parts[1]);
                    // Уведомляем UI о новом сообщении
                    if (messageListener != null) {
                        messageListener.onMessageReceived(message);
                    }
                } else if (message.startsWith("MESSAGE:")) {
                    // Обрабатываем прямое сообщение (если нужно)
                    if (messageListener != null) {
                        messageListener.onMessageReceived(message);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server");
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
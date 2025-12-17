package com.example.chatmessenger.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private static final int PORT = 12345;
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Chat Server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Старый метод для рассылки сообщений (оставляем для обратной совместимости)
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client removed. Total: " + clients.size());
    }

    public static List<ClientHandler> getClients() {
        return new ArrayList<>(clients);
    }

    // Метод отправки конкретному пользователю по username
    public static void sendToUser(String username, String message) {
        for (ClientHandler client : clients) {
            if (username.equals(client.getUsername())) {
                client.sendMessage(message);
                System.out.println("Sent to " + username + ": " + message);
                break;
            }
        }
    }
}
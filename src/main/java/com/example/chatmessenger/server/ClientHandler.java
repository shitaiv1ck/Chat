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
                    System.out.println("Client registered: " + clientInfo);
                } else if (message.startsWith("MESSAGE:")) {
                    ChatServer.broadcastMessage(message, this);
                } else if (message.startsWith("NOTIFY:")) {
                    ChatServer.broadcastMessage(message, this);
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + clientInfo);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}

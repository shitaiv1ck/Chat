package com.example.chatmessenger.message;

import com.example.chatmessenger.chatroom.ChatRoom;
import com.example.chatmessenger.user.entity.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageRepository {
    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String URL_LOCATE = "localhost/";
    private static final String DATABASE_NAME = "chat_messenger";
    private static final String DATABASE_URL = PROTOCOL + URL_LOCATE + DATABASE_NAME;

    private static final String USER_NAME = "postgres";
    private static final String DATABASE_PASS = "postgres";

    public MessageRepository() {
    }

    public void saveMessage(Message message) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("insert into messages(sender, textMessage, chat_id) values (?, ?, ?)");

            statement.setString(1, message.getSender().getUsername());
            statement.setString(2, message.getText());
            statement.setInt(3, message.getChatId());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("error_save_message");
        }
    }

    public List<Message> findAllMessagesByChatId(ChatRoom chatRoom) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select sender, textMessage from messages where chat_id = ?");

            statement.setInt(1, chatRoom.getChatId());

            List<Message> messages = new ArrayList<>();

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Client sender = new Client(rs.getString("sender"));
                String text = rs.getString("textMessage");

                Message message = new Message(sender, text, chatRoom.getChatId());

                messages.add(message);
            }

            return messages;
        } catch (SQLException e) {
            System.out.println("error_find_messages");
            return Collections.emptyList();
        }
    }
}

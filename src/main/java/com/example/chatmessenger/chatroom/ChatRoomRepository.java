package com.example.chatmessenger.chatroom;

import com.example.chatmessenger.user.entity.Client;

import java.sql.*;

public class ChatRoomRepository {
    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String URL_LOCATE = "localhost/";
    private static final String DATABASE_NAME = "chat_messenger";
    private static final String DATABASE_URL = PROTOCOL + URL_LOCATE + DATABASE_NAME;

    private static final String USER_NAME = "postgres";
    private static final String DATABASE_PASS = "postgres";

    public ChatRoomRepository() {
    }

    public void saveChatRoom(ChatRoom chatRoom) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("insert into chatrooms (client1, client2) values (?, ?)");

            statement.setString(1, chatRoom.getClient1().getUsername());
            statement.setString(2, chatRoom.getClient2().getUsername());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("error_save_chat");
        }
    }

    public int findChatRoomByClient(ChatRoom chatRoom) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select chat_id from chatrooms where (client1 = ? and client2 = ?) or (client1 = ? and client2 = ?)");

            statement.setString(1, chatRoom.getClient1().getUsername());
            statement.setString(2, chatRoom.getClient2().getUsername());
            statement.setString(3, chatRoom.getClient2().getUsername());
            statement.setString(4, chatRoom.getClient1().getUsername());

            ResultSet rs = statement.executeQuery();

            int chat_id = 0;

            while (rs.next()) {
                chat_id = rs.getInt("chat_id");
            }

            return chat_id;
        } catch (SQLException e) {
            System.out.println("error_find_chat_by_client");
            return 0;
        }
    }
}

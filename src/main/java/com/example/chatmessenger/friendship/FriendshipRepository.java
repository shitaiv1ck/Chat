package com.example.chatmessenger.friendship;

import com.example.chatmessenger.user.entity.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendshipRepository {
    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String URL_LOCATE = "localhost/";
    private static final String DATABASE_NAME = "chat_messenger";
    private static final String DATABASE_URL = PROTOCOL + URL_LOCATE + DATABASE_NAME;

    private static final String USER_NAME = "postgres";
    private static final String DATABASE_PASS = "postgres";

    public FriendshipRepository() {}

    public void saveRequest(Client client1, Client client2) {

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("insert into friendrequests (from_user, to_user) values (?, ?)");

            statement.setString(1, client1.getUsername());
            statement.setString(2, client2.getUsername());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("error_save");
        }
    }

    public boolean findFriendRequest(Client client1, Client client2) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select from_user, to_user from friendrequests where from_user = ? and to_user = ?");

            statement.setString(1, client1.getUsername());
            statement.setString(2, client2.getUsername());

            ResultSet rs = statement.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.out.println("error_findFQ");
            return false;
        }
    }

    public List<String> findAllFriendRequestByReceiver(Client client1) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("SELECT from_user FROM friendrequests WHERE to_user = ?");

            statement.setString(1, client1.getUsername());

            ResultSet rs = statement.executeQuery();

            List<String> senders = new ArrayList<>();

            while (rs.next()) {
                String sender = rs.getString("from_user");

                senders.add(sender);
            }

            return senders;
        } catch (SQLException e) {
            System.out.println("error_findALLFQ");
            return Collections.emptyList();
        }
    }

    public void deleteFriendRequest(Client client1, Client client2) {

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("delete from friendrequests where (from_user = ? and to_user = ?) or (from_user = ? and to_user = ?)");

            statement.setString(1, client1.getUsername());
            statement.setString(2, client2.getUsername());
            statement.setString(3, client2.getUsername());
            statement.setString(4, client1.getUsername());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("error");
        }
    }

    public void saveFriendship(Client client1, Client client2) {

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("insert into friendships (client1, client2) values (?, ?)");

            statement.setString(1, client1.getUsername());
            statement.setString(2, client2.getUsername());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("error_save_FS");
        }
    }

    public void deleteFriendship(Client client1, Client client2) {

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {

            PreparedStatement statement = connection.prepareStatement("delete from friendships where (client1 = ? and client2 = ?) or (client1 = ? and client2 = ?)");

            statement.setString(1, client1.getUsername());
            statement.setString(2, client2.getUsername());
            statement.setString(3, client2.getUsername());
            statement.setString(4, client1.getUsername());

            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            System.out.println("error_delete_friend");
        }
    }

    public boolean findFriendship(Client client1, Client client2) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select client1, client2 from friendships where client1 = ? and client2 = ?");

            statement.setString(1, client1.getUsername());
            statement.setString(2, client2.getUsername());

            ResultSet rs = statement.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.out.println("error_findFS");
            return false;
        }
    }

    public List<String> findAllFriendsByClient(Client client) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE client1 = ? or client2 = ?");

            statement.setString(1, client.getUsername());
            statement.setString(2, client.getUsername());

            ResultSet rs = statement.executeQuery();

            List<String> friends = new ArrayList<>();

            while (rs.next()) {
                String client1 = rs.getString("client1");
                String client2 = rs.getString("client2");

                if (!client1.equals(client.getUsername())) {
                    friends.add(client1);
                } else {
                    friends.add(client2);
                }
            }

            return friends;
        } catch (SQLException e) {
            System.out.println("error_findALLFQ");
            return Collections.emptyList();
        }
    }
}

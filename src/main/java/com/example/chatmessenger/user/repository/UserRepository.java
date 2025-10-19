package com.example.chatmessenger.user.repository;

import com.example.chatmessenger.user.entity.Admin;
import com.example.chatmessenger.user.entity.Client;

import java.sql.*;

public class UserRepository {
    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String URL_LOCATE = "localhost/";
    private static final String DATABASE_NAME = "chat_messenger";
    private static final String DATABASE_URL = PROTOCOL + URL_LOCATE + DATABASE_NAME;

    private static final String USER_NAME = "postgres";
    private static final String DATABASE_PASS = "postgres";

    public UserRepository() {

    }

    public boolean saveClient(Client client) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)){
            PreparedStatement statement = connection.prepareStatement("insert into clients (username, password) values (?, ?)");

            statement.setString(1, client.getUsername());
            statement.setString(2, client.getPassword());

            int rows = statement.executeUpdate();

            statement.close();

            return rows > 0;
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.out.println("Произошло дублирование данных");
            } else {
                System.out.println("invalid value");
            }
            return false;
        }
    }

    public boolean saveAdmin(Admin admin) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("insert into admins (username, password) values (?, ?)");

            statement.setString(1, admin.getUsername());
            statement.setString(2, admin.getPassword());

            int rows = statement.executeUpdate();

            statement.close();

            return rows > 0;
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.out.println("Произошло дублирование данных");
            } else {
                System.out.println("invalid value");
            }
            return false;
        }
    }

    public boolean findClientByUsernameAndPassword(Client client) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select username, password from clients where username = ? and password = ?");

            statement.setString(1, client.getUsername());
            statement.setString(2, client.getPassword());

            ResultSet rs = statement.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("invalid value");
            return false;
        }
    }
}

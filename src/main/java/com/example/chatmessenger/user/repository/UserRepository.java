package com.example.chatmessenger.user.repository;

import com.example.chatmessenger.user.entity.Admin;
import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.entity.Status;

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
            System.out.println("=== SQL ERROR ===");
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());

            if ("23505".equals(e.getSQLState())) {
                System.out.println("Duplicate username");
            } else if ("23502".equals(e.getSQLState())) {
                System.out.println("Null constraint violation");
            } else if ("22001".equals(e.getSQLState())) {
                System.out.println("Data too long for column");
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
                System.out.println("invalid value2");
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
            System.out.println("invalid value3");
            return false;
        }
    }

    public boolean findClientByUsername(Client client) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select username from clients where username = ?");

            statement.setString(1, client.getUsername());

            ResultSet rs = statement.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("invalid value4");
            return false;
        }
    }

    public void updateClientStatus(Client client, Status status) {
        client.setStatus(status);

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)){
            PreparedStatement statement = connection.prepareStatement("update clients set status = ? where username = ?");

            statement.setString(1, client.getStatus().toString());
            statement.setString(2, client.getUsername());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("invalid value5");;
        }
    }
}

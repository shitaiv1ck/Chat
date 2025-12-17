package com.example.chatmessenger.user.repository;

import com.example.chatmessenger.user.entity.Admin;
import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.entity.Status;
import org.checkerframework.checker.units.qual.C;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public boolean findAdminByUsernameAndPassword(Admin admin) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select username, password from admins where username = ? and password = ?");

            statement.setString(1, admin.getUsername());
            statement.setString(2, admin.getPassword());

            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("error_find_admin");
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

    public boolean findAdminByUsername(Admin admin) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select username, password from admins where username = ?");

            statement.setString(1, admin.getUsername());

            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("error_find_admin");
            return false;
        }
    }

    public boolean findClientByApproved(Client client) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select isApproved from clients where username = ?");

            statement.setString(1, client.getUsername());

            boolean isApproved = false;

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                isApproved = rs.getBoolean("isApproved");
            }

            return isApproved;

        } catch (SQLException e) {
            System.out.println("error_find_not_approved_client");
            return false;
        }
    }

    public List<String> findAllClientsByNotApproved() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select username from clients where isApproved = ?");

            statement.setBoolean(1, false);

            List<String> clients = new ArrayList<>();

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String clientUsername = rs.getString("username");
                clients.add(clientUsername);
            }

            return clients;
        } catch (SQLException e) {
            System.out.println("error_find_not_approved_clients");
            return Collections.emptyList();
        }
    }

    public List<String> findAllClientsByApproved() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select username from clients where isApproved = ?");

            statement.setBoolean(1, true);

            List<String> clients = new ArrayList<>();

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String clientUsername = rs.getString("username");
                clients.add(clientUsername);
            }

            return clients;
        } catch (SQLException e) {
            System.out.println("error_find_not_approved_clients");
            return Collections.emptyList();
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

    public void updateApprovedClient(Client client) {
        client.setApproved(true);

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)){
            PreparedStatement statement = connection.prepareStatement("update clients set isApproved = ? where username = ?");

            statement.setBoolean(1, true);
            statement.setString(2, client.getUsername());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("error_approved");;
        }
    }

    public String getClientStatusByUsername(String username) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            PreparedStatement statement = connection.prepareStatement("select status from clients where username = ?");

            statement.setString(1, username);

            String clientStatus = "";

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                clientStatus = rs.getString("status");
            }

            return clientStatus;
        } catch (SQLException e) {
            System.out.println("error_get_status");
            return "";
        }
    }
}

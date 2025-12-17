package com.example.chatmessenger.guiControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.chatmessenger.friendship.FriendshipRepository;
import com.example.chatmessenger.session.AdminSession;
import com.example.chatmessenger.session.ClientSession;
import com.example.chatmessenger.user.entity.Admin;
import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.entity.Status;
import com.example.chatmessenger.user.repository.UserRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AuthController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label chatField;

    @FXML
    private Button toSignUpButton;

    @FXML
    private CheckBox adminCheck;

    @FXML
    private Button authButton;

    @FXML
    private CheckBox clientCheck;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    @FXML
    void initialize() {
        authButton.setOnAction(event -> {
            auth();
        });

        toSignUpButton.setOnAction(event -> {
            toSignUp();
        });

        clientCheck.setOnAction(event -> {
            adminCheck.setSelected(false);
        });

        adminCheck.setOnAction(event -> {
            clientCheck.setSelected(false);
        });
    }

    private void auth() {
        UserRepository userRepository = new UserRepository();

        String username = login.getText();
        String pass = password.getText();

        if (clientCheck.isSelected()) {
            Client client = new Client(username, pass);

            if (userRepository.findClientByUsernameAndPassword(client)) {
                userRepository.updateClientStatus(client, Status.ONLINE);

                FriendshipRepository friendshipRepository = new FriendshipRepository();
                client.setFriendRequests(friendshipRepository.findAllFriendRequestByReceiver(client));

                client.setFriendList(friendshipRepository.findAllFriendsByClient(client));

                ClientSession.setCurrentClient(client);

                if (!userRepository.findClientByApproved(client)) {
                    toWaitingWindow();
                } else {
                    toProfile();
                }
            } else if (userRepository.findClientByUsername(client)){
                showNotification("Ошибка входа!", "Неверный пароль!");
            } else {
                showNotification("Ошибка входа!", "Клиента " + client.getUsername() + " не существует!");
            }
        } else if (adminCheck.isSelected()) {
            Admin admin = new Admin(username, pass);

            if (userRepository.findAdminByUsernameAndPassword(admin)) {
                AdminSession.setCurrentAdmin(admin);

                toAdminWindow();
            } else if (userRepository.findAdminByUsername(admin)) {
                showNotification("Ошибка входа!", "Неверный пароль");
            } else {
                showNotification("Ошибка входа!", "Администратора " + admin.getUsername() + " не существует!");
            }

        }
    }

    private void showNotification(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(chatField.getScene().getWindow());
            alert.show();
        });
    }

    private void toSignUp() {
        toSignUpButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/chatmessenger/signUp-view.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void toProfile() {
        authButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/chatmessenger/profile-view.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void toWaitingWindow() {
        authButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/chatmessenger/waiting-view.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void toAdminWindow() {
        authButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/chatmessenger/admin-view.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

}

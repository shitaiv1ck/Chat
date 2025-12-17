package com.example.chatmessenger.guiControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.chatmessenger.user.entity.Admin;
import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.repository.UserRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SignUpController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label chatLabel;

    @FXML
    private Button logOutButton;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    @FXML
    private Button signUpButton;

    @FXML
    void initialize() {
        signUpButton.setOnAction(event -> {
            signUp();
        });

        logOutButton.setOnAction(event -> {
            toAuth();
        });
    }

    private void signUp() {
        UserRepository userRepository = new UserRepository();

        String username = login.getText();
        String pass = password.getText();

        if (!username.isBlank() && !pass.isBlank()) {

            Client client = new Client(username, pass);

            if (!userRepository.findClientByUsername(client)) {
                userRepository.saveClient(client);
                toAuth();
            } else {
                showNotification("Ошибка регистрации!", "Клиент " + client.getUsername() + " уже существует!");
            }
        }
    }

    private void showNotification(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(chatLabel.getScene().getWindow());
            alert.show();
        });
    }

    private void toAuth() {
        signUpButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/chatmessenger/auth-view.fxml"));

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

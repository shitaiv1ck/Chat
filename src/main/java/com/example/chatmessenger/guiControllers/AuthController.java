package com.example.chatmessenger.guiControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuthController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
                toChat();
            }
        }
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

    private void toChat() {
        authButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/chatmessenger/chat-view.fxml"));

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

package com.example.chatmessenger.guiControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.chatmessenger.user.entity.Admin;
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

public class SignUpController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox adminCheck;

    @FXML
    private CheckBox clientCheck;

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

        clientCheck.setOnAction(event -> {
            adminCheck.setSelected(false);
        });

        adminCheck.setOnAction(event -> {
            clientCheck.setSelected(false);
        });
    }

    private void signUp() {
        UserRepository userRepository = new UserRepository();

        String username = login.getText();
        String pass = password.getText();

        if (!username.isBlank() && !pass.isBlank()) {
            boolean isSave = false;

            if (clientCheck.isSelected()) {
                Client client = new Client(username, pass);
                isSave = userRepository.saveClient(client);
            } else if (adminCheck.isSelected()) {
                Admin admin = new Admin(username, pass);
                isSave = userRepository.saveAdmin(admin);
            }

            toAuth(isSave);
        }
    }

    private void toAuth(boolean isSave) {
        if (isSave) {
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
}

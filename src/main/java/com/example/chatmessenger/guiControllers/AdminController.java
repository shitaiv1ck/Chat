package com.example.chatmessenger.guiControllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.example.chatmessenger.session.AdminSession;
import com.example.chatmessenger.user.AdminServiceImpl;
import com.example.chatmessenger.user.entity.Admin;
import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdminController {

    UserRepository userRepository = new UserRepository();
    AdminServiceImpl adminService = new AdminServiceImpl(userRepository);

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuButton allClientsMenu;

    @FXML
    private Button logOutButton;

    @FXML
    private MenuButton requestesMenu;

    @FXML
    private Button toAcceptButton;

    @FXML
    private TextField toAcceptField;

    @FXML
    private Label userLabel;

    @FXML
    void initialize() {
        Admin admin = AdminSession.getCurrentAdmin();

        userLabel.setText(admin.getUsername());

        showClientsToRegistration();

        toAcceptButton.setOnAction(event -> {
            approve();
        });

        logOutButton.setOnAction(event -> {
            toAuth();
        });

    }

    private void approve() {
        Client client = new Client(toAcceptField.getText());

        adminService.approveRegistration(client);

        toAcceptField.setText("");

        showClientsToRegistration();
    }

    private void showClientsToRegistration() {
        requestesMenu.getItems().clear();

        List<String> clients = userRepository.findAllClientsByNotApproved();

        if (!clients.isEmpty()) {
            for (String client : clients) {
                MenuItem item = new MenuItem(client);
                item.setOnAction(event -> {
                    toAcceptField.setText(item.getText());
                });
                requestesMenu.getItems().add(item);
            }
        } else {
            MenuItem item = new MenuItem("Нет клиентов");
            requestesMenu.getItems().add(item);
        }
    }

    private void toAuth() {
        logOutButton.getScene().getWindow().hide();

        AdminSession.setCurrentAdmin(null);

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

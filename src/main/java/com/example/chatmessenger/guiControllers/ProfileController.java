package com.example.chatmessenger.guiControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.chatmessenger.friendship.FriendshipRepository;
import com.example.chatmessenger.friendship.FriendshipService;
import com.example.chatmessenger.friendship.FriendshipServiceImpl;
import com.example.chatmessenger.session.ClientSession;
import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.entity.Status;
import com.example.chatmessenger.user.repository.UserRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.C;

public class ProfileController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button logOutButton;

    @FXML
    private Button acceptRequestButton;

    @FXML
    private MenuButton friendlistMenu;

    @FXML
    private MenuButton friendRequestsMenu;

    @FXML
    private TextField receiverUsername;

    @FXML
    private Button sendRequestButton;

    @FXML
    private TextField senderUsername;

    @FXML
    private Button toChatButton;

    @FXML
    private TextField toChatField;

    @FXML
    private Label userLabel;

    @FXML
    void initialize() {
        Client client = ClientSession.getCurrentClient();

        userLabel.setText(client.getUsername());

        showFriendRequests(client);

        showFriends(client);

        sendRequestButton.setOnAction(event -> {
            sendRequest(client);
        });

        acceptRequestButton.setOnAction(event -> {
            acceptRequest(client);
        });

        logOutButton.setOnAction(event -> {
            toAuth();
        });

        Platform.runLater(() -> {
            Stage stage = (Stage) userLabel.getScene().getWindow();
            stage.setOnCloseRequest(windowEvent -> {
                closeTheWindow();
            });
        });
    }

    private void closeTheWindow () {
        UserRepository userRepository = new UserRepository();
        userRepository.updateClientStatus(ClientSession.getCurrentClient(), Status.OFFLINE);
    }

    private void showFriendRequests(Client client) {
        friendRequestsMenu.getItems().clear();

        if (!client.getFriendRequests().isEmpty()) {
            for (String sender : client.getFriendRequests()) {
                MenuItem item = new MenuItem(sender);
                item.setOnAction(event -> {
                    senderUsername.setText(item.getText());
                });

                friendRequestsMenu.getItems().add(item);
            }
        } else {
            MenuItem item = new MenuItem("Нет заявок");
            friendRequestsMenu.getItems().add(item);
        }
    }

    private void showFriends(Client client) {
        friendlistMenu.getItems().clear();

        if (!client.getFriendList().isEmpty()) {
            for (String friend : client.getFriendList()) {
                MenuItem item = new MenuItem(friend);
                item.setOnAction(event -> {
                    toChatField.setText(item.getText());
                });

                friendlistMenu.getItems().add(item);
            }
        } else {
            MenuItem item = new MenuItem("Нет друзей");
            friendlistMenu.getItems().add(item);
        }
    }

    private void sendRequest(Client sender) {
        UserRepository userRepository = new UserRepository();
        FriendshipRepository friendshipRepository = new FriendshipRepository();
        FriendshipServiceImpl friendshipService = new FriendshipServiceImpl(userRepository, friendshipRepository);

        Client receiver = new Client(receiverUsername.getText());

        friendshipService.sendFriendRequest(sender, receiver);
    }

    private void acceptRequest(Client accepter) {
        UserRepository userRepository = new UserRepository();
        FriendshipRepository friendshipRepository = new FriendshipRepository();
        FriendshipServiceImpl friendshipService = new FriendshipServiceImpl(userRepository, friendshipRepository);

        Client requester = new Client(senderUsername.getText());

        if (friendshipService.acceptFriendRequest(accepter, requester)) {
            accepter.setFriendList(friendshipRepository.findAllFriendsByClient(accepter));
            accepter.setFriendRequests(friendshipRepository.findAllFriendRequestByReceiver(accepter));

            showFriends(accepter);
            showFriendRequests(accepter);
        }
    }

    private void toAuth() {
        logOutButton.getScene().getWindow().hide();

        UserRepository userRepository = new UserRepository();
        userRepository.updateClientStatus(ClientSession.getCurrentClient(), Status.OFFLINE);

        ClientSession.setCurrentClient(null);

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

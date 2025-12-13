package com.example.chatmessenger.guiControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.chatmessenger.chatroom.ChatRoomRepository;
import com.example.chatmessenger.chatroom.ChatRoomServiceImpl;
import com.example.chatmessenger.friendship.FriendshipRepository;
import com.example.chatmessenger.friendship.FriendshipService;
import com.example.chatmessenger.friendship.FriendshipServiceImpl;
import com.example.chatmessenger.session.ChatSession;
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

    private UserRepository userRepository = new UserRepository();
    private FriendshipRepository friendshipRepository = new FriendshipRepository();
    private FriendshipServiceImpl friendshipService = new FriendshipServiceImpl(userRepository, friendshipRepository);

    private ChatRoomRepository chatRoomRepository = new ChatRoomRepository();
    private ChatRoomServiceImpl chatRoomService = new ChatRoomServiceImpl(chatRoomRepository);

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
    private MenuButton friendlistMenu1;

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
    private Button toDeleteButton;

    @FXML
    private TextField toDeleteField;

    @FXML
    private Label userLabel;

    @FXML
    void initialize() {
        Client client = ClientSession.getCurrentClient();

        userLabel.setText(client.getUsername());

        showFriendRequests(client);

        showFriends(client);

        showFriendsToDelete(client);

        toChatButton.setOnAction(event -> {
            startChat(client);
        });

        sendRequestButton.setOnAction(event -> {
            sendRequest(client);
        });

        acceptRequestButton.setOnAction(event -> {
            acceptRequest(client);
        });

        toDeleteButton.setOnAction(event -> {
            removeFriendship(client);
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
                String friendWithStatus = friend + " | " + userRepository.getClientStatusByUsername(friend);
                MenuItem item = new MenuItem(friendWithStatus);
                item.setOnAction(event -> {
                    toChatField.setText(friendWithStatus.substring(0, friendWithStatus.indexOf("|") - 1));
                });

                friendlistMenu.getItems().add(item);
            }
        } else {
            MenuItem item = new MenuItem("Нет друзей");
            friendlistMenu.getItems().add(item);
        }
    }

    private void showFriendsToDelete(Client client) {
        friendlistMenu1.getItems().clear();

        if (!client.getFriendList().isEmpty()) {
            for (String friend : client.getFriendList()) {
                MenuItem item = new MenuItem(friend);
                item.setOnAction(event -> {
                    toDeleteField.setText(item.getText());
                });

                friendlistMenu1.getItems().add(item);
            }
        } else {
            MenuItem item = new MenuItem("Нет друзей");
            friendlistMenu1.getItems().add(item);
        }
    }

    private void startChat(Client client1) {

        if (!toChatField.getText().isBlank()) {
            Client client2 = new Client(toChatField.getText());

            if (friendshipService.areFriends(client1, client2)) {
                ChatSession.setChatRoom(chatRoomService.createChat(client1, client2));

                toChat();
            }
        }
    }

    private void sendRequest(Client sender) {

        if (!receiverUsername.getText().isBlank()) {
            Client receiver = new Client(receiverUsername.getText());

            if (friendshipService.sendFriendRequest(sender, receiver)) {
                receiverUsername.setText("");
            }
        }
    }

    private void acceptRequest(Client accepter) {
        if (!senderUsername.getText().isBlank()) {
            Client requester = new Client(senderUsername.getText());

            if (friendshipService.acceptFriendRequest(accepter, requester)) {
                accepter.setFriendList(friendshipRepository.findAllFriendsByClient(accepter));
                accepter.setFriendRequests(friendshipRepository.findAllFriendRequestByReceiver(accepter));

                showFriends(accepter);
                showFriendRequests(accepter);
                showFriendsToDelete(accepter);

                senderUsername.setText("");
            }
        }
    }

    private void removeFriendship(Client client) {
        if (!toDeleteField.getText().isBlank()) {
            Client friendToRemove = new Client(toDeleteField.getText());

            if (friendshipService.removeFriend(client, friendToRemove)) {
                client.setFriendList(friendshipRepository.findAllFriendsByClient(client));

                showFriendsToDelete(client);
                showFriends(client);

                toDeleteField.setText("");
            }

        }
    }

    private void toAuth() {
        logOutButton.getScene().getWindow().hide();

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

    private void toChat() {
        toChatButton.getScene().getWindow().hide();

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

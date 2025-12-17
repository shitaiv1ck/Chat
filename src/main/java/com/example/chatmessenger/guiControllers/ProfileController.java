package com.example.chatmessenger.guiControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import com.example.chatmessenger.client.ChatClient;
import javafx.application.Platform;


public class ProfileController {

    private UserRepository userRepository = new UserRepository();
    private FriendshipRepository friendshipRepository = new FriendshipRepository();
    private FriendshipServiceImpl friendshipService = new FriendshipServiceImpl(userRepository, friendshipRepository);

    private ChatRoomRepository chatRoomRepository = new ChatRoomRepository();
    private ChatRoomServiceImpl chatRoomService = new ChatRoomServiceImpl(chatRoomRepository);

    private ChatClient chatClient;
    private final List<String> friendsWithStatus = new ArrayList<>();

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
    private Button changeStatusButton;

    @FXML
    private Label currentStatusLabel;

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
        currentStatusLabel.setText(userRepository.getClientStatusByUsername(client.getUsername()));

        initStatusUpdates(client);

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

        changeStatusButton.setOnAction(event -> {
            changeStatus(client);
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

    private void closeTheWindow() {
        userRepository.updateClientStatus(ClientSession.getCurrentClient(), Status.OFFLINE);

        if (chatClient != null) {
            chatClient.updateStatus("OFFLINE");
            try {
                Thread.sleep(100);
                chatClient.disconnect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeStatus(Client client) {
        if (currentStatusLabel.getText().equals(String.valueOf(Status.ONLINE))) {
            userRepository.updateClientStatus(ClientSession.getCurrentClient(), Status.BUSY);

            if (chatClient != null) {
                chatClient.updateStatus("BUSY");
            }

            currentStatusLabel.setText("BUSY");
        } else if (currentStatusLabel.getText().equals(String.valueOf(Status.BUSY))) {
            userRepository.updateClientStatus(ClientSession.getCurrentClient(), Status.ONLINE);

            if (chatClient != null) {
                chatClient.updateStatus("ONLINE");
            }

            currentStatusLabel.setText("ONLINE");
        }
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
        friendsWithStatus.clear();

        if (!client.getFriendList().isEmpty()) {
            for (String friend : client.getFriendList()) {
                String currentStatus = userRepository.getClientStatusByUsername(friend);
                String friendWithStatus = friend + " | " + currentStatus;

                MenuItem item = new MenuItem(friendWithStatus);
                item.setOnAction(event -> {
                    toChatField.setText(friendWithStatus.substring(0, friendWithStatus.indexOf("|") - 1));
                });

                friendlistMenu.getItems().add(item);
                friendsWithStatus.add(friendWithStatus);
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

            String clientStatus = userRepository.getClientStatusByUsername(client2.getUsername());

            if (friendshipService.areFriends(client1, client2) && clientStatus.equals(String.valueOf(Status.ONLINE))) {
                ChatSession.setChatRoom(chatRoomService.createChat(client1, client2));

                toChat();
            } else {
                showNotification("Не удалось начать чат", "Клиент " + client2.getUsername() + " в данный момент " + clientStatus);
            }
        }
    }

    private void sendRequest(Client sender) {
        if (!receiverUsername.getText().isBlank()) {
            Client receiver = new Client(receiverUsername.getText());

            if (friendshipService.sendFriendRequest(sender, receiver)) {
                if (chatClient != null) {
                    chatClient.sendFriendRequest(sender.getUsername(), receiver.getUsername());
                }

                receiverUsername.setText("");
                showNotification("Заявка отправлена", "Заявка в друзья отправлена пользователю " + receiver.getUsername());
            }
        }
    }

    private void acceptRequest(Client accepter) {
        if (!senderUsername.getText().isBlank()) {
            Client requester = new Client(senderUsername.getText());

            if (friendshipService.acceptFriendRequest(accepter, requester)) {
                if (chatClient != null) {
                    chatClient.acceptFriendRequest(accepter.getUsername(), requester.getUsername());
                }

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
                if (chatClient != null) {
                    chatClient.notifyFriendRemoved(client.getUsername(), friendToRemove.getUsername());
                }

                client.setFriendList(friendshipRepository.findAllFriendsByClient(client));

                showFriendsToDelete(client);
                showFriends(client);

                toDeleteField.setText("");

            }
        }
    }

    private void initStatusUpdates(Client client) {
        chatClient = new ChatClient(new ChatClient.MessageListener() {
            @Override
            public void onMessageReceived(String message) {

            }

            @Override
            public void onStatusUpdate(String username, String newStatus) {
                Platform.runLater(() -> {
                    updateFriendStatus(username, newStatus);
                });
            }
        });

        // Заявки в друзья
        chatClient.setRequestListener(new ChatClient.RequestListener() {
            @Override
            public void onNewFriendRequest(String sender) {
                Platform.runLater(() -> {
                    handleNewFriendRequest(sender);
                });
            }

            @Override
            public void onRequestAccepted(String accepter) {
                Platform.runLater(() -> {
                    handleRequestAccepted(accepter);
                });
            }

            @Override
            public void onFriendAdded(String newFriend) {
                Platform.runLater(() -> {
                    handleFriendAdded(newFriend);
                });
            }

            @Override
            public void onFriendRemoved(String removedFriend) {
                Platform.runLater(() -> {
                    handleFriendRemoved(removedFriend);
                });
            }

            @Override
            public void onFriendRemovedBy(String remover) {
                Platform.runLater(() -> {
                    handleFriendRemovedBy(remover);
                });
            }
        });

        if (chatClient.connect()) {
            String status = userRepository.getClientStatusByUsername(client.getUsername());
            if (status.isEmpty()) {
                status = "ONLINE";
            }
            chatClient.registerClient(client.getUsername() + ":" + status);

        }
    }

    private void handleNewFriendRequest(String sender) {
        System.out.println("Real-time: New friend request from " + sender);

        Client client = ClientSession.getCurrentClient();

        client.setFriendRequests(friendshipRepository.findAllFriendRequestByReceiver(client));

        showFriendRequests(client);

        showNotification("Новая заявка в друзья", "Пользователь " + sender + " отправил вам заявку в друзья!");
    }

    private void handleRequestAccepted(String accepter) {
        System.out.println("Real-time: Your request to " + accepter + " was accepted!");

        Client client = ClientSession.getCurrentClient();

        client.setFriendList(friendshipRepository.findAllFriendsByClient(client));

        showFriends(client);
        showFriendsToDelete(client);

        showNotification("Заявка принята", "Пользователь " + accepter + " принял вашу заявку в друзья!");
    }

    private void handleFriendAdded(String newFriend) {
        System.out.println("Real-time: New friend added - " + newFriend);

        Client client = ClientSession.getCurrentClient();

        client.setFriendList(friendshipRepository.findAllFriendsByClient(client));

        showFriends(client);
        showFriendsToDelete(client);

        showFriendRequests(client);
    }

    private void handleFriendRemoved(String removedFriend) {
        System.out.println("Real-time: You removed friend - " + removedFriend);

        Client client = ClientSession.getCurrentClient();

        client.setFriendList(friendshipRepository.findAllFriendsByClient(client));

        showFriends(client);
        showFriendsToDelete(client);

        showNotification("Друг удален", "Вы удалили пользователя " + removedFriend + " из друзей");
    }

    private void handleFriendRemovedBy(String remover) {
        System.out.println("Real-time: You were removed by friend - " + remover);

        Client client = ClientSession.getCurrentClient();

        client.setFriendList(friendshipRepository.findAllFriendsByClient(client));

        showFriends(client);
        showFriendsToDelete(client);

        showNotification("Друг удалил вас", "Пользователь " + remover + " удалил вас из друзей");
    }

    private void showNotification(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(userLabel.getScene().getWindow());
            alert.show();
        });
    }

    private void updateFriendStatus(String friendUsername, String newStatus) {

        for (MenuItem item : friendlistMenu.getItems()) {
            String itemText = item.getText();
            if (itemText.contains(friendUsername)) {
                String newText = friendUsername + " | " + newStatus;
                item.setText(newText);
                break;
            }
        }
    }

    private void toAuth() {
        logOutButton.getScene().getWindow().hide();

        userRepository.updateClientStatus(ClientSession.getCurrentClient(), Status.OFFLINE);

        if (chatClient != null) {
            chatClient.updateStatus("OFFLINE");
            try {
                Thread.sleep(100);
                chatClient.disconnect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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

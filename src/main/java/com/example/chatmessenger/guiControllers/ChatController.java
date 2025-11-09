package com.example.chatmessenger.guiControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.chatmessenger.chatroom.ChatRoom;
import com.example.chatmessenger.message.Message;
import com.example.chatmessenger.message.MessageRepository;
import com.example.chatmessenger.message.MessageServiceImpl;
import com.example.chatmessenger.session.ChatSession;
import com.example.chatmessenger.session.ClientSession;
import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.entity.Status;
import com.example.chatmessenger.user.repository.UserRepository;
import com.example.chatmessenger.util.Position;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChatController {

    private MessageRepository messageRepository = new MessageRepository();
    private MessageServiceImpl messageService = new MessageServiceImpl(messageRepository);

    private UserRepository userRepository = new UserRepository();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane paneForMsg;

    @FXML
    private ScrollPane scrollForChat;

    @FXML
    private Button profileButton;

    @FXML
    private Button sendButton;

    @FXML
    private TextField text;

    @FXML
    private Label userLabel;

    @FXML
    void initialize() {
        Client client = ClientSession.getCurrentClient();

        userLabel.setText(client.getUsername());

        ChatRoom chatRoom = ChatSession.getChatRoom();

        sendButton.setOnAction(event -> {
            sendingMessage(client, chatRoom);
        });

        profileButton.setOnAction(event -> {
            toProfile();
        });

        showAllMessages(chatRoom, client);

        Platform.runLater(() -> {
            Stage stage = (Stage) userLabel.getScene().getWindow();
            stage.setOnCloseRequest(windowEvent -> {
                closeTheWindow();
            });
        });
    }

    private void sendingMessage(Client client, ChatRoom chatRoom) {
        if (!text.getText().isBlank()) {

            Message message = new Message(client, text.getText(), chatRoom.getChatId());

            messageService.sendMessage(message);

            text.setText("");

            showAllMessages(chatRoom, client);
        }
    }

    private void showAllMessages(ChatRoom chatRoom, Client client) {
        List<Message> messages = messageRepository.findAllMessagesByChatId(chatRoom);

        Position position = new Position(10, 10);

        paneForMsg.getChildren().clear();

        for (Message message : messages) {
            Label msg = new Label(message.getSender().getUsername() + ": " + message.getText());
            paneForMsg.getChildren().add(msg);

            msg.setTextFill(Color.web("LAVENDER"));
            msg.setFont(Font.font(16));

            if (message.getSender().getUsername().equals(client.getUsername())) {
                msg.setStyle("-fx-background-color: #9370DB; -fx-background-radius: 2;");
            } else {
                msg.setStyle("-fx-background-color: #1f1f2d; -fx-background-radius: 2;");
            }

            msg.setLayoutX(position.getX());
            msg.setLayoutY(position.getY());

            position.setY(position.getY() + 40);
        }

        paneForMsg.setPrefHeight(position.getY() + 40);
        scrollForChat.setVvalue(1);
    }

    private void closeTheWindow() {
        userRepository.updateClientStatus(ClientSession.getCurrentClient(), Status.OFFLINE);
    }

    private void toProfile() {
        profileButton.getScene().getWindow().hide();

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

}

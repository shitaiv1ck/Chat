package com.example.chatmessenger.guiControllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.chatmessenger.session.ClientSession;
import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.entity.Status;
import com.example.chatmessenger.user.repository.UserRepository;
import com.example.chatmessenger.util.Position;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChatController {

    private Position position = new Position(10, 10);

    private int x = 92;
    private int y = 252;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane paneForMsg;

    @FXML
    private ScrollPane scrollForChat;

    @FXML
    private Button friendlistButton;

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

        sendButton.setOnAction(event -> {
            sendMessage();
        });

        Platform.runLater(() -> {
            Stage stage = (Stage) userLabel.getScene().getWindow();
            stage.setOnCloseRequest(windowEvent -> {
                closeTheWindow();
            });
        });
    }

    private void sendMessage() {
        Label msg = new Label(text.getText());
        paneForMsg.getChildren().add(msg);
        msg.setLayoutX(position.getX());
        msg.setLayoutY(position.getY());
        msg.setStyle("-fx-background-color: #9370DB; -fx-background-radius: 2;");
        msg.setTextFill(Color.web("LAVENDER"));
        msg.setFont(Font.font(16));
        position.setY(position.getY() + 40);
        paneForMsg.setPrefHeight(paneForMsg.getHeight() + 40);
        text.setText("");
        scrollForChat.setVvalue(1);
    }

    private void closeTheWindow() {
        UserRepository userRepository = new UserRepository();
        userRepository.updateClientStatus(ClientSession.getCurrentClient(), Status.OFFLINE);
    }

}

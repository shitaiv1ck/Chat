module com.example.chatmessenger {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires org.jetbrains.annotations;
    requires org.checkerframework.checker.qual;


    opens com.example.chatmessenger to javafx.fxml;
    exports com.example.chatmessenger;
    exports com.example.chatmessenger.guiControllers;
    opens com.example.chatmessenger.guiControllers to javafx.fxml;
    exports com.example.chatmessenger.util;
    opens com.example.chatmessenger.util to javafx.fxml;
}
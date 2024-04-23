package org.example.omictincrud;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;

public class Mainpage {

    public Button btnOpenReminder;

    public void OpenReminder(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reminderview.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = btnOpenReminder.getScene();
            scene.setRoot(root);




        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package org.example.omictincrud;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {

    public TextField username;
  
    public Button btnOpenLogin;
    public Button btnOpenRegister;

    public static int storedUserId = 1;
    public PasswordField password;


    public void loginValidation(ActionEvent event) {
        String name = username.getText();
        String pass = password.getText();

        if (name.isEmpty() || pass.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both username and password.");
            alert.showAndWait();
            return;
        }

        boolean auth = authentication(name, pass);

        if (auth) {

            storedUserId = retrieveUserIdFromDatabase(name);



            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reminderview.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = btnOpenLogin.getScene();
                scene.setRoot(root);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password.");
            alert.showAndWait();
        }

        password.clear();
    }

    public boolean authentication(String username, String password) {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement statement = c.prepareStatement("SELECT * FROM USERS where username =? AND password = ?")) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet res = statement.executeQuery();


            return res.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int retrieveUserIdFromDatabase(String username) {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement statement = c.prepareStatement("SELECT id FROM USERS where username = ?")) {

            statement.setString(1, username);
            ResultSet res = statement.executeQuery();

            if (res.next()) {
                return res.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void openRegister(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registerview.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Scene scene = btnOpenRegister.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
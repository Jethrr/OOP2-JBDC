package org.example.omictincrud;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Registerview {

    public TextField username;
    public TextField password;
    public Button btnReturnLogin;
    public Button btnSubmitReg;

    public void btnOpenLogin(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginview.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Scene scene = btnReturnLogin.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void submitReg(ActionEvent event) {
         String user = username.getText();
         String pword = password.getText();

        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement statement = c.prepareStatement("INSERT INTO  users(username,password) VALUES (?,?)")) {


            statement.setString(1, user);
            statement.setString(2, pword);

            int row = statement.executeUpdate();

//            messagebox.setText("Register Succesfully");


        } catch (SQLException e) {
            e.printStackTrace();
        }


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
}

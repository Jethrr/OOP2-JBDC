package org.example.omictincrud;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Registerview {

    public TextField username;

    public Button btnReturnLogin;
    public Button btnSubmitReg;
    public PasswordField password;

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

        if (user.isEmpty() || pword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter all fields to register.");
            alert.showAndWait();
            return;
        }

        try (Connection c = MySQLConnection.getConnection()) {

            c.setAutoCommit(false);

            try (PreparedStatement insertStatement = c.prepareStatement("INSERT INTO users(username, password) VALUES (?, ?)")) {
                insertStatement.setString(1, user);
                insertStatement.setString(2, pword);


                insertStatement.addBatch();


                int[] updateCounts = insertStatement.executeBatch();


                for (int updateCount : updateCounts) {
                    if (updateCount != 1) {
                        throw new SQLException("Failed to insert user.");
                    }
                }

                c.commit();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Registered Successfully. You can now sign in.");
                alert.showAndWait();
            } catch (SQLException e) {

                c.rollback();
                throw e;
            } finally {

                c.setAutoCommit(true);
            }
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

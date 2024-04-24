package org.example.omictincrud;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserSettings {

    public TextField usernameSettings;
    public Button btnDeleteUser;
    public Button btnUpdateUser;
    public Button btnReturn;

    public void initialize() {

        usernameSettings.setText(getCurrentUserUsername());
    }

    public void updateUserData(ActionEvent event) {
        String newUsername = usernameSettings.getText();

        if (newUsername.isEmpty()) {
            showAlert("Error", "Username cannot be empty.");
            return;
        }

        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement statement = c.prepareStatement("UPDATE users SET username = ? WHERE id = ?")) {

            statement.setString(1, newUsername);
            statement.setInt(2, HelloController.storedUserId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                showAlert("Success", "Username updated successfully.");
            } else {
                showAlert("Error", "Failed to update username.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update username.");
        }
    }

    public void deleteUserData(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection c = MySQLConnection.getConnection();
                 PreparedStatement statement = c.prepareStatement("DELETE FROM users WHERE id = ?")) {

                statement.setInt(1, HelloController.storedUserId);

                int rowsDeleted = statement.executeUpdate();

                if (rowsDeleted > 0) {
                    showAlert("Success", "User account deleted successfully.");
                    navigateToLoginView();
                } else {
                    showAlert("Error", "Failed to delete user account.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete user account.");
            }
        }
    }

    private void navigateToLoginView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginview.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Scene scene = btnDeleteUser.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getCurrentUserUsername() {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement statement = c.prepareStatement("SELECT username FROM users WHERE id = ?")) {

            statement.setInt(1, HelloController.storedUserId);

            ResultSet res = statement.executeQuery();

            if (res.next()) {
                return res.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void returnRemView(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reminderview.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Scene scene = btnReturn.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

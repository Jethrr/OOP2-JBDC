package org.example.omictincrud;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.example.omictincrud.HelloController.storedUserId;

public class Reminderview {

    public TextField tfReminder;
    public Button btnReturnMain;
    public TableColumn<ObservableList<Object>, Object> reminderIdCol;
    public TableColumn<ObservableList<Object>, Object> reminderCol;
    public TableColumn<ObservableList<Object>, Object> opCol;
    public TableView<ObservableList<Object>> reminderTabe;
//    public TableColumn<ObservableList<Object>, Object> userIdCol;
    public Button btnAddRen;




    public void initialize() {
        reminderIdCol.setCellValueFactory(data ->
                Bindings.createObjectBinding(() -> (Integer)data.getValue().get(0), data.getValue()));
        reminderCol.setCellValueFactory(data ->
                Bindings.createObjectBinding(() -> (String)data.getValue().get(1), data.getValue()));

        getReminders();


        opCol.setCellFactory(param -> new TableCell<>() {
            private final Button Edit = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                Edit.setOnAction(event -> {
                    ObservableList<Object> reminderData = getTableView().getItems().get(getIndex());
                    int reminderId = (int) reminderData.get(0);
                    String currentDescription = (String) reminderData.get(1);

                    TextInputDialog dialog = new TextInputDialog(currentDescription);
                    dialog.setTitle("Edit Reminder");
                    dialog.setHeaderText(null);
                    dialog.setContentText("New Description:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()){
                        String newDescription = result.get();
                        updateReminder(reminderId, newDescription);
                        reminderData.set(1, newDescription);
                    }
                });

                deleteButton.setOnAction(event -> {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Delete");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure you want to delete this reminder?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        ObservableList<Object> rowData = getTableRow().getItem();
                        if (rowData != null) {
                            int id = (int) rowData.get(0);
                            deleteReminder(id);
                            reminderTabe.getItems().remove(rowData);
                        }
                    }
                });




            }

            private void deleteReminder(int reminderId) {

                try (Connection c = MySQLConnection.getConnection();
                     PreparedStatement statement = c.prepareStatement("DELETE FROM tbl2 WHERE reminderId = ?")) {
                    statement.setInt(1, reminderId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            private void updateReminder(int reminderId, String newDescription) {
                String query = "UPDATE tbl2 SET description = ? WHERE reminderId = ?";
                try (Connection connection = MySQLConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, newDescription);
                    preparedStatement.setInt(2, reminderId);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(Edit, deleteButton);
                    buttons.setSpacing(5);
                    setGraphic(buttons);
                }
            }



        });

//        reminderTabe.getColumns().add(opCol);
    }


//    public void getReminders(){
////        try (Connection c = MySQLConnection.getConnection();
////             PreparedStatement statement = c.prepareStatement("SELECT * FROM tbl2 WHERE userId = ?");
////        ) {
////            statement.setInt(1, storedUserId);
////            try (ResultSet resultSet = statement.executeQuery()) {
////                while (resultSet.next()) {
////
////                    int remId = resultSet.getInt("reminderId");
////                    int userId  = resultSet.getInt("userId");
////                    String description  = resultSet.getString("description");
////
////                    ObservableList<Object> row = FXCollections.observableArrayList();
////                    row.add(remId);
////                    row.add(userId);
////                    row.add(description);
////                    reminderTabe.getItems().add(row);
////
////                    System.out.println("remId: " + remId);
////                    System.out.println("userId: " + userId);
////                    System.out.println("description: " + description);
////
////                }
////            }
////
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
//
//        try (Connection c = MySQLConnection.getConnection();
//             PreparedStatement statement = c.prepareStatement("SELECT * FROM tbl2 WHERE userId = ?");
//        ) {
//            statement.setInt(1, storedUserId);
//            try (ResultSet resultSet = statement.executeQuery()) {
//                while (resultSet.next()) {
//                    int remId = resultSet.getInt("reminderId");
//                    String description = resultSet.getString("description");
//
//                    Reminder reminder = new Reminder(remId, description);
//                    reminderTabe.getItems().add((ObservableList<Object>) reminder);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    public void getReminders() {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement statement = c.prepareStatement("SELECT * FROM tbl2 WHERE userId = ?");
        ) {
            statement.setInt(1, storedUserId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int remId = resultSet.getInt("reminderId");
                    String description = resultSet.getString("description");

                    ObservableList<Object> row = FXCollections.observableArrayList();
                    row.add(remId);
                    row.add(description);

                    reminderTabe.getItems().add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    public void insertReminder(ActionEvent event) {
        int currentUserLoggedIn = storedUserId;
        String reminderContent = tfReminder.getText();

        reminderQuery(currentUserLoggedIn,reminderContent);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reminderview.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = btnAddRen.getScene();
            scene.setRoot(root);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void reminderQuery(int userId, String reminders){
        String query = "INSERT INTO tbl2 (userId, description) VALUES (?, ?)";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, reminders);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void ReturnMain(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = btnReturnMain.getScene();
            scene.setRoot(root);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

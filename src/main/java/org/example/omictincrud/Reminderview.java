package org.example.omictincrud;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.example.omictincrud.HelloController.storedUserId;

public class Reminderview {

    public TextField tfReminder;
    public Button btnSignout;
    public TableColumn<ObservableList<Object>, Object> reminderIdCol;
    public TableColumn<ObservableList<Object>, Object> reminderCol;
    public TableColumn<ObservableList<Object>, Object> opCol;
    public TableView<ObservableList<Object>> reminderTabe;
//    public TableColumn<ObservableList<Object>, Object> userIdCol;
    public Button btnAddRen;
    public TableColumn<ObservableList<Object>, Object>  dateCol;
    public DatePicker dateP;


    public void initialize() {

        Label emptyLabel = new Label("No reminders.");
        reminderTabe.setPlaceholder(emptyLabel);

        reminderIdCol.setCellValueFactory(data ->
                Bindings.createObjectBinding(() -> (Integer)data.getValue().get(0), data.getValue()));
        reminderCol.setCellValueFactory(data ->
                Bindings.createObjectBinding(() -> (String)data.getValue().get(1), data.getValue()));
        dateCol.setCellValueFactory(data ->
                Bindings.createObjectBinding(() -> (String)data.getValue().get(2), data.getValue()));


        getReminders();


        reminderTabe.setOnScroll(event -> {
            if (event.getDeltaY() != 0) {
                reminderTabe.getParent().requestFocus();
            }
        });

        reminderTabe.setFixedCellSize(25);

        reminderTabe.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        reminderTabe.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.doubleValue() != oldValue.doubleValue()) {
                TableHeaderRow header = (TableHeaderRow) reminderTabe.lookup("TableHeaderRow");
                header.reorderingProperty().addListener((observable1, oldValue1, newValue1) -> header.setReordering(false));
            }
        });


        opCol.setCellFactory(param -> new TableCell<>() {
            private final Button Edit = new Button("");
            private final Button deleteButton = new Button("");

            {

                try {
                    Image editImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/omictincrud/edit.png")));
                    ImageView editImageView = new ImageView(editImage);
                    editImageView.setFitWidth(16);
                    editImageView.setFitHeight(16);
                    Edit.setGraphic(editImageView);
                    Edit.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
                } catch (Exception e) {
                    System.err.println("Failed to load edit button image: " + e.getMessage());
                }
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

                try {
                    Image deleteImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/omictincrud/delete.png")));
                    ImageView deleteImageView = new ImageView(deleteImage);
                    deleteImageView.setFitWidth(16);
                    deleteImageView.setFitHeight(16);
                    deleteButton.setGraphic(deleteImageView);
                    deleteButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
                } catch (Exception e) {
                    System.err.println("Failed to load delete button image: " + e.getMessage());
                }

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
                    buttons.setAlignment(Pos.CENTER);
                    buttons.setSpacing(5);
                    setGraphic(buttons);
                }
            }



        });


    }




    public void getReminders() {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement statement = c.prepareStatement("SELECT * FROM tbl2 WHERE userId = ?");
        ) {
            statement.setInt(1, storedUserId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int remId = resultSet.getInt("reminderId");
                    String description = resultSet.getString("description");
                    String date = resultSet.getString("date");

                    ObservableList<Object> row = FXCollections.observableArrayList();
                    row.add(remId);
                    row.add(description);
                    row.add(date);

                    reminderTabe.getItems().add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    public void insertReminder(ActionEvent event) {

        if (tfReminder.getText().isEmpty() || dateP.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Fields");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a reminder and select a date.");
            alert.showAndWait();
            return;
        }

        int currentUserLoggedIn = storedUserId;
        String reminderContent = tfReminder.getText();
        LocalDate date = dateP.getValue();
        String dateString = date.toString();

        reminderQuery(currentUserLoggedIn,reminderContent,dateString);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reminderview.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = btnAddRen.getScene();
            scene.setRoot(root);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void reminderQuery(int userId, String reminders,String date){
        String query = "INSERT INTO tbl2 (userId, description,date) VALUES (?, ?, ?)";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, reminders);
            preparedStatement.setString(3, date);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void ReturnMain(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Sign Out");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to sign out?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginview.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = btnSignout.getScene();
                scene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

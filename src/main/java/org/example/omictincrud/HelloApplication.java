package org.example.omictincrud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        createUserTable();
        createTable2();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginview.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 685, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        stage.getIcons().add(new Image(getClass().getResourceAsStream("mockup-logo.png")));


        stage.setTitle("Reminder");
        stage.setScene(scene);
        stage.show();
    }

    private void createUserTable() {
        try (Connection c = MySQLConnection.getConnection();
             Statement statement = c.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(100) NOT NULL," +
                    "password VARCHAR(100) NOT NULL)";
            System.out.println("USER TABLE CREATED");
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void createTable2() {
        try (Connection c = MySQLConnection.getConnection();
             Statement statement = c.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS tbl2 (" +
                    "reminderId INT PRIMARY KEY AUTO_INCREMENT," +
                    "userId INT," +
                    "description TEXT NOT NULL," +
                    "date TEXT NOT NULL" +
                    " );";
            statement.execute(query);
            System.out.println("TABLE 2 CREATED");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch();
    }
}
module org.example.omictincrud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens org.example.omictincrud to javafx.fxml;
    exports org.example.omictincrud;
}
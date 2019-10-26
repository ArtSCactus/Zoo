package util;

import model.database.Storage;
import exceptions.DriverNotFoundException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;


public class Main extends Application {

    //  Database credentials
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/postgres";
    static final String USER = "postgres";
    static final String PASS = "root";

    @Override
    public void start(Stage stage) throws Exception {
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.setVgap(10);

        Button button = new Button("Button with Text");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Some information");

                alert.showAndWait();
            }
        });
        root.add(button, 1, 0);


        ObservableList<Person> people = FXCollections.observableArrayList(

                new Person("Tom", 34),
                new Person("Bob", 22),
                new Person("Sam", 28),
                new Person("Alice", 29)
        );
        // определяем таблицу и устанавливаем данные
        TableView<Person> table = new TableView<Person>(people);
        table.setPrefWidth(250);
        table.setPrefHeight(200);

        // столбец для вывода имени
        TableColumn<Person, String> nameColumn = new TableColumn<Person, String>("Name");
        // определяем фабрику для столбца с привязкой к свойству name
        nameColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("name"));
        // добавляем столбец
        table.getColumns().add(nameColumn);

        // столбец для вывода возраста
        TableColumn<Person, Integer> ageColumn = new TableColumn<Person, Integer>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<Person, Integer>("age"));
        table.getColumns().add(ageColumn);
        stage.setTitle("Java Button");

        root.getChildren().add(table);

        Scene scene = new Scene(root, 350, 150);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        try {
            Storage.checkDriver();
        } catch (DriverNotFoundException e) {
            System.out.println("JDBC driver not found. Please, install JDBC or common.");
        }
        Storage storage = new Storage();

        try {
            storage.connect(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DriverNotFoundException e) {
            e.printStackTrace();
        }

        String selectTableSQL = "SELECT * from task2.details";

        try {
            ResultSet rs = storage.executeRequest(selectTableSQL);

            while (rs.next()) {
                String detail_code_number = rs.getString("code_number");
                String detail_name = rs.getString("name");
                String detail_code = rs.getString("color");

                System.out.println("private_number: " + detail_code_number);
                System.out.println("surname: " + detail_name);
                System.out.println("color: " + detail_code);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            launch(args);
        }
    }
}


package View;

import exceptions.DriverNotFoundException;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.postgresql.util.PSQLException;
import util.Controller;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UI extends Application {
  private  Controller controller = new Controller();

    @Override
    public void start(Stage stage) throws Exception {
        try {
            controller.connect();
        }catch(PSQLException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Cannot connect to database");
            alert.setHeaderText("Connection refused. Possibly sql server is not running.");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return;
        }catch(DriverNotFoundException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("SQL driver not found.");
            alert.setHeaderText("Please, install JDBC/PostgreSQL driver or postgreSQL manager.");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return;
        }
        loginWindow(stage);
    }

    private void loginWindow(Stage stage){

        GridPane gridPane = new GridPane();
        Scene loginWindowScene = new Scene(gridPane,300,125);
        Stage loginStage = new Stage();

        gridPane.setPadding(new Insets(10));

        Label usernameFieldLabel = new Label();
        usernameFieldLabel.setText("Login:");
        gridPane.add(usernameFieldLabel,0,0);

        TextField usernameField = new TextField();
        usernameField.setPromptText("login here");
        gridPane.add(usernameField,1,0);

        Label passwordFieldLabel = new Label();
        passwordFieldLabel.setText("Password: ");
        gridPane.add(passwordFieldLabel,0,1);

        TextField passwordField = new TextField();
        passwordField.setPromptText("password here");
        gridPane.add(passwordField,1,1);

        Button submitButton = new Button();
        submitButton.setText("Sign in");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (usernameField.getText().length()==0 | passwordField.getText().length()==0){
                    showMessage(Alert.AlertType.ERROR,"Empty field", "Cannot send an empty user name and password",
                            "One of the fields are empty. Please, fill all fields.");
                    return;
                }
                controller.setUsername(usernameField.getText());
                controller.setPassword(passwordField.getText());
                loginStage.close();
                try {
                  if (controller.connect()){
                      mainWindow(stage);
                  }
                } catch (SQLException e) {
                    exceptionWindow(e);
                } catch (DriverNotFoundException e) {
                    exceptionWindow(e);
                }
            }
        });
        gridPane.add(submitButton,1,4);
        loginStage.setScene(loginWindowScene);
        loginStage.showAndWait();
    }

    private void mainWindow(Stage stage) throws SQLException {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(50);
        gridPane.setVgap(50);

        ResultSet requestResult=controller.executeRequest("select * from birds");
        ObservableList<ObservableList> data = FXCollections.observableArrayList();


        TableView<ObservableList> table = new TableView<>(data);
        table.setPrefWidth(550);
        table.setPrefHeight(350);

        //autofilling table by columns
        for (int i = 0; i < requestResult.getMetaData().getColumnCount(); i++) {
            final int j = i;
            TableColumn col = new TableColumn(requestResult.getMetaData().getColumnName(i + 1));
            //dynamic filling columns
            col.setCellValueFactory(
                    (Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                            param -> new SimpleStringProperty(param.getValue().get(j).toString()));
            table.getColumns().addAll(col);
        }

        //Adding data from sql request to ObservableList
        while (requestResult.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= requestResult.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(requestResult.getString(i));
            }
            data.add(row);

        }

        //adding data to table`
        table.setItems(data);
        gridPane.getChildren().add(table);
        Scene scene = new Scene(gridPane, 550, 350);
        stage.setScene(scene);
        stage.show();
    }

    public void showMessage(Alert.AlertType messageType, String windowTitle, String headerText, String contentText){
        Alert alert = new Alert(messageType);
        alert.setTitle(windowTitle);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void showMessage(Alert.AlertType messageType, String windowTitle, String contentText){
        Alert alert = new Alert(messageType);
        alert.setTitle(windowTitle);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void exceptionWindow(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(""+e.getClass());
        alert.setHeaderText(e.getClass().toString());
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }


    public void run(String[] args){
        launch(args);
    }


}

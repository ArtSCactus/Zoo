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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.postgresql.util.PSQLException;
import util.Controller;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UI extends Application {
    private Controller controller;
    private BorderPane mainMenuInterface;
    private TableView<ObservableList> table;
    private Accordion accordion;

    public UI() {
        controller = new Controller();
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            controller.connect();
        } catch (PSQLException ex) {
            showMessage(Alert.AlertType.ERROR, "Cannot connect tot database",
                    "Connection refused. Possibly sql server is not running.");
            return;

        } catch (DriverNotFoundException ex) {
            showMessage(Alert.AlertType.ERROR, "SQL driver not found",
                    "Please, install JDBC/PostgreSQL driver or postgreSQL manager.");
            return;
        }
        //loginWindow(stage); - temporary disabled due to developing period
        runMainWindow(stage);
    }


    private void loginWindow(Stage stage) {

        GridPane gridPane = new GridPane();
        Scene loginWindowScene = new Scene(gridPane, 300, 125);
        Stage loginStage = new Stage();

        gridPane.setPadding(new Insets(10));

        Label usernameFieldLabel = new Label();
        usernameFieldLabel.setText("Login:");
        gridPane.add(usernameFieldLabel, 0, 0);

        TextField usernameField = new TextField();
        usernameField.setPromptText("login here");
        gridPane.add(usernameField, 1, 0);

        Label passwordFieldLabel = new Label();
        passwordFieldLabel.setText("Password: ");
        gridPane.add(passwordFieldLabel, 0, 1);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("password here");
        gridPane.add(passwordField, 1, 1);

        Button submitButton = new Button();
        submitButton.setText("Sign in");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (usernameField.getText().length() == 0 | passwordField.getText().length() == 0) {
                    showMessage(Alert.AlertType.ERROR, "Empty field",
                            "Cannot send an empty user name and password",
                            "One of the fields are empty. Please, fill all fields.");
                    return;
                }
                controller.setUsername(usernameField.getText());
                controller.setPassword(passwordField.getText());
                loginStage.close();
                try {
                    if (controller.connect()) {
                        runMainWindow(stage);
                    }
                } catch (SQLException | DriverNotFoundException e) {
                    exceptionWindow(e);
                }
            }
        });
        gridPane.add(submitButton, 1, 4);
        loginStage.setScene(loginWindowScene);
        loginStage.showAndWait();
    }

    /**
     * Builds whole main window.
     */
    private void buildMainMenu() {
        mainMenuInterface = new BorderPane();
        mainMenuInterface.setPadding(new Insets(0, 0, 0, 0));
        buildAccordion();

        table = new TableView<>();
        table.setPrefWidth(550);
        table.setPrefHeight(350);
        table.setEditable(true);

        MenuBar menuBar = new MenuBar();
        Menu options = new Menu("Options");
        Menu view = new Menu("View");
        MenuItem changeUser = new MenuItem("Sign in");
        CheckMenuItem showTextArea = new CheckMenuItem("Show data sets");
        showTextArea.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (showTextArea.isSelected())
                    mainMenuInterface.setRight(accordion);
                else {
                    mainMenuInterface.setRight(null);
                }
            }
        });
        options.getItems().add(changeUser);
        view.getItems().add(showTextArea);
        menuBar.getMenus().addAll(options, view);
        mainMenuInterface.setTop(menuBar);
        mainMenuInterface.setCenter(table);
        mainMenuInterface.autosize();
    }

    /**
     * Builds accordion.
     */
    private void buildAccordion() {
        accordion = new Accordion();
        TitledPane animalTitledPane = new TitledPane();
        animalTitledPane.setText("Animal");

        VBox animalContent = new VBox();
        animalContent.setSpacing(0);
        animalContent.setFillWidth(true);
        Button animalsTable = new Button("Animals");
        animalsTable.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    fillTable(controller.executeRequest("select * from birds"), table);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        animalContent.getChildren().add(animalsTable);
        animalContent.getChildren().add(new Label("Rations"));
        animalTitledPane.setContent(animalContent);

        TitledPane watcherTitledPane = new TitledPane();
        watcherTitledPane.setText("Watcher");

        VBox watcherContent = new VBox();
        watcherContent.getChildren().add(new Label("Watchers"));

        watcherTitledPane.setContent(watcherContent);
        TitledPane veterinarianTitledPane = new TitledPane();
        veterinarianTitledPane.setText("Veterinarians");
        accordion.getPanes().addAll(animalTitledPane, watcherTitledPane, veterinarianTitledPane);
    }

    /**
     * Builds and runs main window (main part of program).
     *
     * @param stage Stage on which will be shown main window
     * @see Stage
     */
    private void runMainWindow(Stage stage) {
        buildMainMenu();
        Scene scene = new Scene(mainMenuInterface, 700, 700);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Shows message windows with given configuration.
     *
     * @param messageType Alert.AlertType enum object
     * @param windowTitle Title of whole window
     * @param headerText  Text, that will be shown in the header
     * @param contentText Main text in the message window
     */
    private void showMessage(Alert.AlertType messageType, String windowTitle, String headerText, String contentText) {
        Alert alert = new Alert(messageType);
        alert.setTitle(windowTitle);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Shows message windows with given configuration.
     *
     * @param messageType Alert.AlertType enum object
     * @param windowTitle Title of whole window
     * @param contentText Main text in the message window
     */
    private void showMessage(Alert.AlertType messageType, String windowTitle, String contentText) {
        Alert alert = new Alert(messageType);
        alert.setTitle(windowTitle);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Shows exception in javaFX Alert window.
     * Creates alert window and setts:
     * -on {@code setTitle()} shows {@code e.getClass()} ;
     * -on {@code setHeaderText()} shows {@code e.getClass().toString()};
     * -on {@code setContentText()} shows {@code e.getMessage}
     *
     * @param e an Exception or Exception class child object
     */
    private void exceptionWindow(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("" + e.getClass());
        alert.setHeaderText(e.getClass().toString());
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }


    public void run(String[] args) {
        launch(args);
    }

    /**
     * Finds index of given column in given ResultSet.
     * <p>
     * If such column wasn't found in given ResultSet, would be returned -1;
     *
     * @param resultSet
     * @param column
     * @return index of column in resultSet; -1 if
     * @throws SQLException
     */
    private int findColumnIndexInResultSet(ResultSet resultSet, TableColumn column) throws SQLException {
        for (int index = 1; index <= resultSet.getMetaData().getColumnCount(); index++) {
            if (resultSet.getMetaData().getColumnLabel(index).equals(column.getText())) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Dynamic table filling.
     * <p>
     * WARNING:
     * <p>
     * Before table filling will be called method {@code table.getColumns().clear()}, to clear previous
     * information.
     *
     * @param requestResult Data, that will be shown in the table
     * @param table         Table, where this data will be shown
     * @throws SQLException
     */
    private void fillTable(ResultSet requestResult, TableView<ObservableList> table) throws SQLException {
        table.getColumns().clear();
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        //autofilling table by columns
        for (int index = 0; index < requestResult.getMetaData().getColumnCount(); index++) {
            final int j = index;
            TableColumn col = new TableColumn(requestResult.getMetaData().getColumnName(index + 1));
            col.setEditable(true);
            //dynamic filling columns
            col.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                            param -> new SimpleStringProperty(param.getValue().get(j).toString()));
            col.setCellFactory(TextFieldTableCell.forTableColumn());
            col.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {

                @Override
                public void handle(TableColumn.CellEditEvent event) {
                    try {
                        String name = requestResult.getMetaData().getTableName(findColumnIndexInResultSet(requestResult, event.getTablePosition().getTableColumn()));
                        controller.executeUpdate("update " +
                                name + " set " + col.getText() + " = " + "\'" + event.getNewValue() + "\'" +
                                " where " + col.getText() + " = " + "\'" + event.getOldValue() + "\';");
                        //Adding data from sql request to ObservableList
                        updateTable(table, controller.getLastRequest());
                    } catch (SQLException e) {
                        exceptionWindow(e);
                    }
                }
            });
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
        table.setItems(data);
    }

    private void updateTable(TableView table, String request) {
        ResultSet newSet = null;
        try {
            newSet = controller.executeRequest(request);
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        table.getItems().clear();
        try {
            while (newSet.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= newSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(newSet.getString(i));
                }
                data.add(row);
            }
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        table.setItems(data);
    }

    private void updateTable(TableView table, ResultSet dataSet) {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        table.getItems().clear();
        try {
            while (dataSet.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dataSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(dataSet.getString(i));
                }
                data.add(row);
            }
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        table.setItems(data);
    }
}

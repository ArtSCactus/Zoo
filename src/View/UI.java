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
import model.entity.animals.Bird;
import org.postgresql.util.PSQLException;
import util.Controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class UI extends Application {
    private Controller controller;
    private BorderPane mainMenuInterface;
    private TableView<ObservableList> table;
    private Accordion dataDisplayAccordion;
    private TitledPane dataAddingPane;
    private Stage mainStage;

    public UI() {
        controller = new Controller();
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
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
        runMainWindow();
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
                        runMainWindow();
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
        buildDataDisplayAccordion();
        buildDataAddingMenu();
        table = new TableView<>();
        table.setPrefWidth(550);
        table.setPrefHeight(350);
        table.setEditable(true);

        MenuBar menuBar = new MenuBar();
        Menu options = new Menu("Options");
        Menu view = new Menu("View");
        MenuItem changeUser = new MenuItem("Sign in");
        CheckMenuItem showDataPresets = new CheckMenuItem("Show data presets");
        showDataPresets.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (showDataPresets.isSelected()) {
                    mainMenuInterface.setRight(dataDisplayAccordion);
                } else {
                    mainMenuInterface.setRight(null);
                }
            }
        });
        CheckMenuItem showDataAddingMenu = new CheckMenuItem("Show data adding menu");
        showDataAddingMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (showDataAddingMenu.isSelected()) {
                    mainMenuInterface.setLeft(dataAddingPane);
                } else {
                    mainMenuInterface.setLeft(null);
                }
            }
        });
        options.getItems().add(changeUser);
        view.getItems().addAll(showDataPresets, showDataAddingMenu);
        menuBar.getMenus().addAll(options, view);
        mainMenuInterface.setTop(menuBar);
        mainMenuInterface.setCenter(table);
        mainMenuInterface.autosize();
    }

    /**
     * Builds accordion, that allows to choose current data, that showing in the table.
     */
    private void buildDataDisplayAccordion() {
        dataDisplayAccordion = new Accordion();
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
        dataDisplayAccordion.getPanes().addAll(animalTitledPane, watcherTitledPane, veterinarianTitledPane);
    }

    /**
     * Builds accordion, that allows to choose current data, that showing in the table.
     */
    private void buildDataAddingMenu() {
        dataAddingPane = new TitledPane();
        dataAddingPane.setText("Add new data");
        // Content for TitledPane
        VBox content = new VBox();
        Button addBird = new Button();
        addBird.setText("Bird");
        addBird.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showAddBirdDialog();
            }
        });
        content.getChildren().add(addBird);
        dataAddingPane.setContent(content);
        dataAddingPane.setExpanded(true);
    }

    /**
     * Builds and runs main window (main part of program).
     *
     * @see Stage
     */
    private void runMainWindow() {
        buildMainMenu();
        Scene scene = new Scene(mainMenuInterface, 700, 700);
        mainStage.setScene(scene);
        mainStage.show();
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
     * <p>
     * -on {@code setTitle()} shows {@code e.getClass()} ;
     * <p>
     * -on {@code setHeaderText()} shows {@code e.getClass().toString()};
     * <p>
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
                        String tableName = requestResult.getMetaData().getTableName(findColumnIndexInResultSet(requestResult,
                                event.getTablePosition().getTableColumn()));
                        //Watching how much rows can be affected by editing this cell (getting this rows)
                        ResultSet anonymousSet = controller.executeAnonymousRequest("select * from " + tableName +
                                " where " + col.getText() + " = \'" + event.getOldValue() + "\'");
                        anonymousSet.last();
                        //counting it
                        int rowAmount = anonymousSet.getRow();
                        //Checking, if such same cells more, than 1, showing choose dialog
                        if (anonymousSet.getRow() > 1) {
                            //Creating list with uniques codes of possible affected cells
                            List<String> codes = new ArrayList<>();
                            anonymousSet.first();
                            //filling codes list
                             do {
                                codes.add(anonymousSet.getString(1));
                            }while (anonymousSet.next());
                            //Watching what user had chose
                            String selectedCode = showConflictDialog(codes, rowAmount);
                            if (selectedCode == null) {
                                updateTable(table, controller.getCurrentLocalData());
                            } else {
                                //Executing custom update request to change only 1 cell
                                controller.executeUpdate("update " +
                                        tableName + " set " + col.getText() + " = " + "\'" + event.getNewValue() + "\'" +
                                        " where " + col.getText() + " = " + "\'" + event.getOldValue() + "\' and " +
                                        anonymousSet.getMetaData().getColumnName(1) + "=" + "\'" + selectedCode + "\'");
                                updateTable(table, controller.getLastRequest());
                            }
                        } else {
                            //Executing update for all founded matches
                            controller.executeUpdate("update " +
                                    tableName + " set " + col.getText() + " = " + "\'" + event.getNewValue() + "\'" +
                                    " where " + col.getText() + " = " + "\'" + event.getOldValue() + "\';");
                            updateTable(table, controller.getLastRequest());
                        }
                        //Adding data from sql request to ObservableList
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
            do {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= newSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(newSet.getString(i));
                }
                data.add(row);
            }while (newSet.next());
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        table.setItems(data);
    }

    private void updateTable(TableView table) {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        table.getItems().clear();
        try {
            ResultSet dataSet = controller.executeRequest(controller.getLastRequest());
           do {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dataSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(dataSet.getString(i));
                }
                data.add(row);
            }while (dataSet.next());
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        table.setItems(data);
    }

    /**
     * Refills table from given request.
     * <p>
     * In process calling method {@code dataSet.first()}
     *
     * @param table   TableView where this result set will be shown
     * @param dataSet ResultSet object
     */
    private void updateTable(TableView table, ResultSet dataSet) {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        table.getItems().clear();
        try {
            ObservableList<String> row = FXCollections.observableArrayList();
            //Possible given ResultSet already was used, and cursor is outside of rows border. Then we need to
            // set cursor to the first row.
            dataSet.first();
           do  {
                //Iterate Row
                row = FXCollections.observableArrayList();
                for (int i = 1; i <= dataSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(dataSet.getString(i));
                }
                data.add(row);
            }while (dataSet.next()); // do {} while() because if we would use just while(){}, we would lost first
            //row because of calling method dataSet.next();
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        table.setItems(data);
    }

    private Bird showAddBirdDialog() {
        Stage addBirdStage = new Stage();
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));

        TextField birdNumber = new TextField();
        birdNumber.setPromptText("Bird number");
        gridPane.add(birdNumber, 0, 0);

        TextField birdName = new TextField();
        birdName.setPromptText("Bird name");
        gridPane.add(birdName, 0, 1);

        DatePicker birthdayDate = new DatePicker();
        birthdayDate.setValue(LocalDate.now());
        birthdayDate.setPromptText("Bird birthday date");
        gridPane.add(birthdayDate, 0, 2);

        Scene addBirdScene = new Scene(gridPane, 300, 300);
        addBirdStage.setScene(addBirdScene);
        addBirdStage.show();
        return null;
    }

    /**
     * Counts amount of row in result set.
     *
     * @param resultSet
     * @return
     */
    private int countResultSetRows(ResultSet resultSet) {
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException e) {
            exceptionWindow(e);
        } finally {
            try {
                resultSet.moveToCurrentRow();
            } catch (SQLException e) {
                exceptionWindow(e);
            }
        }
        return 0;
    }

    /**
     * Shows dialog, that informing user about conflict while data editing.
     * <p>
     * This dialog should be used to show user, that changing 1 cell can affect to all same cells.
     * Dialog allows user to set cell number, tht he want to change (if only one).
     *
     * @param codes     List, that contains unique codes of elements, that can be changed
     * @param rowAmount Amount of rows, that potential would be affected.
     * @return String {@code getSelectedItem()}
     */
    private String showConflictDialog(List<String> codes, int rowAmount) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Possible conflict");
        dialog.setHeaderText("Changing of this cell can affect on " + rowAmount + " rows");
        dialog.setContentText("Please, choose code number of element, that you want to change");
        dialog.getItems().addAll(codes);
        dialog.showAndWait();
        return dialog.getSelectedItem();
    }

}

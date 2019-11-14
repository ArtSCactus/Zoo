package View;

import exceptions.DriverNotFoundException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.postgresql.util.PSQLException;
import util.Controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UI extends Application {
    private Controller controller;
    private BorderPane mainMenuInterface;
    private TableComponent tableComponent;
    private Accordion dataDisplayAccordion;
    private TitledPane dataAddingPane;
    private Stage mainStage;
    {
    }
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
        tableComponent = new TableComponent();

        MenuBar menuBar = new MenuBar();
        Menu options = new Menu("Options");
        Menu view = new Menu("View");
        MenuItem changeUser = new MenuItem("Sign in");
        CheckMenuItem showDataPresets = new CheckMenuItem("Show data presets");
        showDataPresets.setOnAction(event -> {
            if (showDataPresets.isSelected()) {
                mainMenuInterface.setRight(dataDisplayAccordion);
            } else {
                mainMenuInterface.setRight(null);
            }
        });
        CheckMenuItem showDataAddingMenu = new CheckMenuItem("Show data adding menu");
        showDataAddingMenu.setOnAction(event -> {
            if (showDataAddingMenu.isSelected()) {
                mainMenuInterface.setLeft(dataAddingPane);
            } else {
                mainMenuInterface.setLeft(null);
            }
        });
        options.getItems().add(changeUser);
        view.getItems().addAll(showDataPresets, showDataAddingMenu);
        menuBar.getMenus().addAll(options, view);
        mainMenuInterface.setTop(menuBar);
        mainMenuInterface.setCenter(tableComponent.getTable());
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
                    tableComponent.fillTable(controller.executeRequest("select * from birds"), controller);// fillTable(controller.executeRequest("select * from birds"), table);
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
        alert.setContentText(e.getMessage() + "\nStacktrace: " + e.getStackTrace().toString());
        alert.showAndWait();
    }


    public void run(String[] args) {
        launch(args);
    }

    private List<Node> buildLabelList(List<String> names) {
        List<Node> labels = new ArrayList<>();
        Node label;
        for (String name : names) {
            label = new Label(name);
            labels.add(label);
        }
        return labels;
    }

    private void addNodesToGridColumn(List<Node> nodes, int column, int firstRow, GridPane gridPane) {
        for (Node node : nodes) {
            gridPane.add(node, column, firstRow++);
        }
    }

    private void buildChoiceBoxes(GridPane gridPane) {

    }

    //TODO: разбить на методы
    private void showAddBirdDialog() {
        Stage addBirdStage = new Stage();
        GridPane gridPane = new GridPane();
        Scene addBirdScene = new Scene(gridPane, 300, 300);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(40);
        gridPane.setHgap(40);
        List<String> labelNames = new ArrayList<>();
        labelNames.add("Bird number: ");
        labelNames.add("Bird name: ");
        labelNames.add("Gender: ");
        labelNames.add("Birthday date: ");
        labelNames.add("Ration type: ");
        labelNames.add("Watcher code: ");
        labelNames.add("Veterinarian code: ");
        labelNames.add("Habitat: ");
        labelNames.add("Wintering: ");
        addNodesToGridColumn(buildLabelList(labelNames), 0, 0, gridPane);

        TextField birdNumber = new TextField();
        birdNumber.setPromptText("Bird number");
        gridPane.add(birdNumber, 1, 0);

        TextField birdName = new TextField();
        birdName.setPromptText("Bird name");
        gridPane.add(birdName, 1, 1);

        ChoiceBox<String> birdGender = new ChoiceBox<>();
        List<String> birdGenderItems = new ArrayList<>();
        birdGenderItems.add("самец");
        birdGenderItems.add("самка");
        birdGenderItems.add("не попределён");
        birdGender.setItems(FXCollections.observableArrayList(birdGenderItems));
        birdGender.setValue(birdGenderItems.get(birdGenderItems.size() - 1));
        gridPane.add(birdGender, 1, 2);

        DatePicker birthdayDate = new DatePicker();
        birthdayDate.setValue(LocalDate.now());
        birthdayDate.setPromptText("Bird birthday date");
        gridPane.add(birthdayDate, 1, 3);


        ChoiceBox<String> birdRation = null;
        try {
            birdRation = buildChoiceBox(controller.executeAnonymousRequest("select code from ration"),
                    1);
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        gridPane.add(birdRation, 1, 4);

        ChoiceBox<String> birdWatcher = null;
        try {
            birdWatcher = buildChoiceBox(
                    controller.executeAnonymousRequest("select code from watchers"), 1);
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        gridPane.add(birdWatcher, 1, 5);

        ChoiceBox<String> birdVeterinarian = null;
        try {
            birdVeterinarian = buildChoiceBox(
                    controller.executeAnonymousRequest("select code from veterinarians"), 1);
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        gridPane.add(birdVeterinarian, 1, 6);

        ChoiceBox<String> birdHabitat = null;
        try {
            birdHabitat = buildChoiceBox(
                    controller.executeAnonymousRequest("select name from habitat"), 1);
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        gridPane.add(birdHabitat, 1, 7);

        ChoiceBox<String> birdWintering = null;
        try {
            birdWintering = buildChoiceBox(
                    controller.executeAnonymousRequest("select code_name from wintering"), 1);
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        gridPane.add(birdWintering, 1, 8);
        ChoiceBox<String> finalBirdWatcher = birdWatcher;
        ChoiceBox<String> finalBirdVeterinarian = birdVeterinarian;
        ChoiceBox<String> finalBirdRation = birdRation;
        ChoiceBox<String> finalBirdWintering = birdWintering;
        ChoiceBox<String> finalBirdHabitat = birdHabitat;
        Button addBtn = new Button();
        addBtn.setText("Add bird");
        addBtn.setOnAction(event -> {
            if (birdNumber.getText().isEmpty() | birdName.getText().isEmpty()) {
                showMessage(Alert.AlertType.ERROR, "Empty field", "One of fields is empty. Please, " +
                        "fill all fields");
                return;
            }
            try {
                controller.executeInsert("insert into birds (unique_number, name, birthday, sex, watcher, " +
                        "veterinarian, ration, habitat, wintering) values (" + "\'" + birdNumber.getText() + "\'" + ", " + "\'" + birdName.getText() + "\'" + ", " +
                        "\'" + birthdayDate.getValue() + "\'" + ", \'" + birdGender.getValue() + "\', " + "\'" + finalBirdWatcher.getValue() + "\'" + ", " + "\'" + finalBirdVeterinarian.getValue() + "\'" +
                        ", " + "\'" + finalBirdRation.getValue() + "\'" + ", " + "\'" + finalBirdHabitat.getValue() + "\'" + ", " + "\'" + finalBirdWintering.getValue() + "\'" + ");");
                showMessage(Alert.AlertType.INFORMATION, "Success", "Successfully added");
            } catch (SQLException e) {
                exceptionWindow(e);
            }

        });

        gridPane.add(addBtn, 0, 9);

        Button closeBtn = new Button();
        closeBtn.setText("Close");
        closeBtn.setOnAction(event -> addBirdStage.close());

        gridPane.add(closeBtn, 1, 9);
        gridPane.autosize();

        addBirdStage.setMinHeight(600);
        addBirdStage.setMinWidth(600);
        addBirdStage.setScene(addBirdScene);
        addBirdStage.showAndWait();
    }

    private ChoiceBox<String> buildChoiceBox(ResultSet resultSet, int columnNumber) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        List<String> codes = new ArrayList<>();
        try {
            resultSet.next(); // in case of cursor out of result set
            resultSet.first(); // in case if result set already was used/watched
            do {
                codes.add(resultSet.getString(columnNumber));
            } while (resultSet.next());
            codes.add("None");
            choiceBox.setItems(FXCollections.observableArrayList(codes));
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        choiceBox.setValue(codes.get(codes.size() - 1));
        return choiceBox;
    }
}

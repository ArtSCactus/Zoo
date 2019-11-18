package view;

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
import view.components.tablecomponent.TableComponent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*Yes, I know that this code needs to be refactored. But the development deadlines have already gone beyond the scope,
        and here the creation of the main menu components, each of which is interconnected, is mainly described here.
        So you won’t especially push them into separate components, because get a mess with dependencies.
        If you try to deal with decomposition, it will produce too many methods and extra variables.
        Therefore, it was decided to leave everything as it is.*/
public class UI extends Application {
    private Controller controller;
    private BorderPane mainMenuInterface;
    private TableComponent tableComponent;
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
                    "Connection refused. Possibly sql server is offline.");
            return;

        } catch (DriverNotFoundException ex) {
            showMessage(Alert.AlertType.ERROR, "SQL driver not found",
                    "Please, install JDBC:PostgreSQL driver or postgreSQL editor (pgAdmin).");
            return;
        }
        loginWindow(stage); //- temporary disabled due to developing period
        //runMainWindow();
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

    private void loadCustomColumnNamesToTableComponent() {
        tableComponent.addCustomColumnName("unique_number", "Code number");
        tableComponent.addCustomColumnName("name", "Name");
        tableComponent.addCustomColumnName("wintering", "Wintering code");
        tableComponent.addCustomColumnName("gender", "Gender");
        tableComponent.addCustomColumnName("habitat", "Habitat zone");
        tableComponent.addCustomColumnName("birthday", "Birthday date");
        tableComponent.addCustomColumnName("ration", "Ration");
        tableComponent.addCustomColumnName("watcher", "Watcher");
        tableComponent.addCustomColumnName("veterinarian", "Veterinarian");
        tableComponent.addCustomColumnName("first_name", "First name");
        tableComponent.addCustomColumnName("second_name", "Second name");
        tableComponent.addCustomColumnName("third_name", "Third name");
        tableComponent.addCustomColumnName("phone_number", "Phone number");
        tableComponent.addCustomColumnName("code", "Employee code");
        tableComponent.addCustomColumnName("marriage", "Marriage status");
        tableComponent.addCustomColumnName("type", "Type");
        tableComponent.addCustomColumnName("departure_date", "arrival_date");
        tableComponent.addCustomColumnName("first_name", "First name");
        tableComponent.addCustomColumnName("description", "Description");
        tableComponent.addCustomColumnName("hibernation_beginning", "Beginning of hibernation");
        tableComponent.addCustomColumnName("hibernation_end", "End of hibernation");
        tableComponent.addCustomColumnName("normal_temperature", "Normal temperature");


    }

    /**
     * Builds whole main window.
     */
    private void buildMainMenu() {
        mainMenuInterface = new BorderPane();
        mainMenuInterface.setPadding(new Insets(0, 0, 0, 0));
        buildDataDisplayAccordion();
        buildDataAddingMenu();
        tableComponent = new TableComponent(controller);
        loadCustomColumnNamesToTableComponent();
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
        animalTitledPane.setPadding(new Insets(0));
        animalTitledPane.setText("Animal");

        VBox animalContent = new VBox();
        animalContent.setSpacing(0);
        animalContent.setPrefWidth(200);
        animalContent.setPadding(new Insets(0));

        Button birdsSet = new Button("Birds");
        birdsSet.setMinWidth(animalContent.getPrefWidth());
        birdsSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tableComponent.fillTable(controller.executeRequest("select * from birds"), controller);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });

        animalContent.getChildren().add(birdsSet);
        Button reptilesSet = new Button("Reptiles");
        reptilesSet.setMinWidth(animalContent.getPrefWidth());
        reptilesSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tableComponent.fillTable(controller.executeRequest("select * from reptiles"), controller);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        animalContent.getChildren().add(reptilesSet);
        Button rationsSet = new Button("Rations");
        rationsSet.setMinWidth(animalContent.getPrefWidth());
        rationsSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tableComponent.fillTable(controller.executeRequest("select * from ration"), controller);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        animalContent.getChildren().add(rationsSet);
        Button winteringSet = new Button("Wintering");
        winteringSet.setMinWidth(animalContent.getPrefWidth());
        winteringSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tableComponent.fillTable(controller.executeRequest("select * from wintering"), controller);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        animalContent.getChildren().add(winteringSet);

        Button habitatSet = new Button("Habitat");
        habitatSet.setMinWidth(animalContent.getPrefWidth());
        habitatSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tableComponent.fillTable(controller.executeRequest("select * from habitat"), controller);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });

        Button animalsByTypeAndName = new Button("Animals by type and name");
        animalsByTypeAndName.setMinWidth(animalContent.getPrefWidth());
        animalsByTypeAndName.setOnAction(event -> {
            showPresetByTypeAndNameDialog();
        });
        animalContent.getChildren().add(animalsByTypeAndName);
        animalContent.getChildren().add(habitatSet);
        Button allAnimalsSet = new Button("All animals");
        allAnimalsSet.setMinWidth(animalContent.getPrefWidth());
        allAnimalsSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tableComponent.fillTable(controller.executeRequest("select * from animals"), controller);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        animalContent.getChildren().add(allAnimalsSet);
        animalTitledPane.setContent(animalContent);


        TitledPane watcherTitledPane = new TitledPane();
        watcherTitledPane.setText("Watcher");


        VBox watchersContent = new VBox();
        watchersContent.setSpacing(0);
        watchersContent.setPrefWidth(200);
        watchersContent.setPadding(new Insets(0));

        Button watchersSet = new Button("Watchers");
        watchersSet.setMinWidth(animalContent.getPrefWidth());
        watchersSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tableComponent.fillTable(controller.executeRequest("select * from watchers"), controller);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        watchersContent.getChildren().add(watchersSet);

        watcherTitledPane.setContent(watchersContent);
        TitledPane veterinarianTitledPane = new TitledPane();
        veterinarianTitledPane.setText("Veterinarians");
        VBox veterinariansContent = new VBox();
        veterinariansContent.setSpacing(0);
        veterinariansContent.setPrefWidth(200);
        veterinariansContent.setPadding(new Insets(0));

        Button veterinariansSet = new Button("Veterinarians");
        veterinariansSet.setMinWidth(animalContent.getPrefWidth());
        veterinariansSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tableComponent.fillTable(controller.executeRequest("select * from veterinarians"), controller);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        veterinariansContent.getChildren().add(veterinariansSet);
        veterinarianTitledPane.setContent(veterinariansContent);
        TitledPane otherTitledPane = new TitledPane();
        otherTitledPane.setText("Other");
        VBox otherContent = new VBox();
        otherContent.setSpacing(0);
        otherContent.setPrefWidth(200);
        otherContent.setPadding(new Insets(0));

        Button allEmployeesSet = new Button("All employees");
        allEmployeesSet.setMinWidth(animalContent.getPrefWidth());
        allEmployeesSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tableComponent.fillTable(controller.executeRequest("select * from employee"), controller);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        otherContent.getChildren().add(allEmployeesSet);

        Button familyPairsSet = new Button("Family pairs");
        familyPairsSet.setMinWidth(animalContent.getPrefWidth());
        familyPairsSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tableComponent.fillTable(controller.executeRequest("select distinct  main.* from employee as main inner" +
                            " join employee as main_copy on main.marriage=main_copy.marriage\n" +
                            "where main.marriage='женат/замужем' and main.second_name " +
                            "like concat('%',main_copy.second_name,'%')"), controller);
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        otherContent.getChildren().add(familyPairsSet);

        otherTitledPane.setContent(otherContent);
        dataDisplayAccordion.getPanes().addAll(animalTitledPane, watcherTitledPane, veterinarianTitledPane, otherTitledPane);
    }

    private void showPresetByTypeAndNameDialog() {
        Stage stage = new Stage();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(10));
        Scene scene = new Scene(gridPane, 300, 170);
        stage.setScene(scene);
        Label typeLabel = new Label("Type");
        Label nameLabel = new Label("Name");
        gridPane.add(typeLabel, 0, 0);
        gridPane.add(nameLabel, 0, 1);
        ChoiceBox typeSelection = new ChoiceBox();
        List<String> animalPresetItems = new ArrayList<>();
        animalPresetItems.add("Птицы");
        animalPresetItems.add("Рептилии");
        typeSelection.setItems(FXCollections.observableArrayList(animalPresetItems));
        typeSelection.setValue(animalPresetItems.get(0));
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        gridPane.add(nameField, 1, 1);
        gridPane.add(typeSelection, 1, 0);
        Button showBtn = new Button("Show");
        showBtn.setOnAction(event -> {
            if (nameField.getText().isEmpty()) {
                showMessage(Alert.AlertType.ERROR, "Empty field", "An empty field found.",
                        "Please, fill all fields.");
                return;
            }
            String tableName = (String) typeSelection.getValue();
            switch (tableName) {
                case ("Птицы"):
                    tableName = "birds";
                    break;
                case ("Рептилии"):
                    tableName = "reptiles";
                    break;
            }
            try {
                tableComponent.fillTable(controller.executeRequest("select * from " + tableName +
                        " where name = \'" + nameField.getText() + "\'"), controller);
            } catch (SQLException e) {
                exceptionWindow(e);
            }
        });

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(event -> {
            stage.close();
        });
        gridPane.add(showBtn, 0, 2);
        gridPane.add(closeBtn, 1, 2);
        stage.showAndWait();
    }

    /**
     * Builds accordion, that allows to choose current data, that showing in the table.
     */
    private void buildDataAddingMenu() {
        dataAddingPane = new TitledPane();
        dataAddingPane.setPadding(new Insets(0));
        dataAddingPane.setText("Add new data");
        // Content for TitledPane
        VBox content = new VBox();
        content.setSpacing(0);
        content.setPrefWidth(200);
        content.setPadding(new Insets(0));

        Button addBird = new Button("Bird");
        addBird.setOnAction(event -> showAddBirdDialog());
        addBird.setMinWidth(content.getPrefWidth());
        content.getChildren().add(addBird);

        Button addReptile = new Button("Reptile");
        addReptile.setMinWidth(content.getPrefWidth());
        addReptile.setOnAction(event -> showAddReptileDialog());
        content.getChildren().add(addReptile);

        Button addWatcher = new Button("Watcher");
        addWatcher.setMinWidth(content.getPrefWidth());
        addWatcher.setOnAction(event -> showAddWatcherDialog());
        content.getChildren().add(addWatcher);

        Button addVeterinarian = new Button("Veterinarian");
        addVeterinarian.setMinWidth(content.getPrefWidth());
        addVeterinarian.setOnAction(event -> showAddVeterinarianDialog());
        content.getChildren().add(addVeterinarian);

        Button addRation = new Button("Ration");
        addRation.setMinWidth(content.getPrefWidth());
        addRation.setOnAction(event -> showAddRationDialog());
        content.getChildren().add(addRation);

        Button addHabitat = new Button("Habitat");
        addHabitat.setMinWidth(content.getPrefWidth());
        addHabitat.setOnAction(event -> showAddHabitatDialog());
        content.getChildren().add(addHabitat);
        Button addWintering = new Button("Wintering");
        addWintering.setMinWidth(content.getPrefWidth());
        addWintering.setOnAction(event -> showAddWinteringDialog());
        content.getChildren().add(addWintering);
        dataAddingPane.setContent(content);
        dataAddingPane.setExpanded(true);
    }

    /**
     * Builds and runs main window (main part of program).
     *
     */
    private void runMainWindow() {
        buildMainMenu();
        Scene scene = new Scene(mainMenuInterface, 700, 700);
        mainStage.setScene(scene);
        mainStage.setOnCloseRequest(event -> {
            try {
                controller.disconnect();
            } catch (SQLException e) {
                exceptionWindow(e);
            }
        });
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
        alert.setHeaderText(e.getMessage());
        TextArea stackTrace = new TextArea();
        StringWriter stackTraceWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTraceWriter));
        stackTrace.setText(e.toString() + "\n" + stackTraceWriter.toString());
        stackTrace.setText(Arrays.toString(e.getStackTrace()));
        alert.getDialogPane().setContent(stackTrace);
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

    private void loadLabelsInAddBirdDialog(List<String> labelNames) {
        labelNames.add("Bird number: ");
        labelNames.add("Bird name: ");
        labelNames.add("Gender: ");
        labelNames.add("Birthday date: ");
        labelNames.add("Ration type: ");
        labelNames.add("Watcher code: ");
        labelNames.add("Veterinarian code: ");
        labelNames.add("Habitat: ");
        labelNames.add("Wintering: ");
    }

    private void loadLabelsInAddWatcherDialog(List<String> labelNames) {
        labelNames.add("Watcher number: ");
        labelNames.add("Name: ");
        labelNames.add("Second name: ");
        labelNames.add("Third name: ");
        labelNames.add("Phone number: ");
        labelNames.add("Marriage status: ");
    }


    private void showAddWatcherDialog() {
        Stage addWatcherStage = new Stage();
        addWatcherStage.setTitle("New watcher");
        GridPane gridPane = new GridPane();
        Scene addWatcherScene = new Scene(gridPane, 300, 300);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(40);
        gridPane.setHgap(20);
        gridPane.setPrefWidth(200);
        List<String> labelNames = new ArrayList<>();
        loadLabelsInAddWatcherDialog(labelNames);
        addNodesToGridColumn(buildLabelList(labelNames), 0, 0, gridPane);

        TextField numberField = new TextField();
        numberField.setPromptText("Code number");
        gridPane.add(numberField, 1, 0);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        gridPane.add(nameField, 1, 1);

        TextField secondNameField = new TextField();
        secondNameField.setPromptText("Second name");
        gridPane.add(secondNameField, 1, 2);

        TextField thirdNameField = new TextField();
        thirdNameField.setPromptText("Third name");
        gridPane.add(thirdNameField, 1, 3);

        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("Phone number");
        gridPane.add(phoneNumberField, 1, 4);

        ChoiceBox<String> marriageStatus = new ChoiceBox<>();
        List<String> marriageStatusItems = new ArrayList<>();
        marriageStatusItems.add("женат/замужем");
        marriageStatusItems.add("не женат/Не замужем");
        marriageStatusItems.add("неизвестно");
        marriageStatus.setItems(FXCollections.observableArrayList(marriageStatusItems));
        marriageStatus.setValue(marriageStatusItems.get(1));
        gridPane.add(marriageStatus, 1, 5);
        DatePicker birthday = new DatePicker();
        birthday.setValue(LocalDate.now());
        birthday.setPromptText("Departure date");
        gridPane.add(birthday, 1, 6);
        Button addBtn = new Button();
        addBtn.setText("Add watcher");
        addBtn.setMinWidth(gridPane.getPrefWidth());
        addBtn.setOnAction(event -> {
            if (numberField.getText().isEmpty() |
                    nameField.getText().isEmpty() |
                    secondNameField.getText().isEmpty() |
                    thirdNameField.getText().isEmpty() |
                    phoneNumberField.getText().isEmpty()) {
                showMessage(Alert.AlertType.ERROR, "Empty fields", "An empty fields found",
                        "Please, fill all the fields.");
            } else {
                try {
                    controller.executeInsert("insert into watchers (code, first_name, second_name, third_name," +
                            " phone_number, marriage, birthday)" +
                            " values(" + "\'" + numberField.getText() + "\', " +
                            "\'" + nameField.getText() + "\', " +
                            "\'" + secondNameField.getText() + "\', " +
                            "\'" + thirdNameField.getText() + "\', " +
                            "\'" + phoneNumberField.getText() + "\', " +
                            "\'" + marriageStatus.getValue() + "\'," +
                            "\'" + birthday.getValue() + "\'" + ") ");
                    numberField.clear();
                    nameField.clear();
                    secondNameField.clear();
                    thirdNameField.clear();
                    phoneNumberField.clear();
                    showMessage(Alert.AlertType.INFORMATION, "Success", "Successfully added new watcher");
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        gridPane.add(addBtn, 0, 7);
        Button closeBtn = new Button();
        closeBtn.setText("Close");
        closeBtn.setOnAction(event -> {
            addWatcherStage.close();
        });
        closeBtn.setMinWidth(gridPane.getPrefWidth());
        gridPane.add(closeBtn, 1, 7);
        addWatcherStage.setMinHeight(600);
        addWatcherStage.setMinWidth(500);
        addWatcherStage.setMaxHeight(600);
        addWatcherStage.setMaxWidth(500);
        addWatcherStage.setScene(addWatcherScene);
        addWatcherStage.showAndWait();
    }

    private void loadLabelsInAddVeterinarianDialog(List<String> labelNames) {
        labelNames.add("Veterinarian number: ");
        labelNames.add("Name: ");
        labelNames.add("Second name: ");
        labelNames.add("Third name: ");
        labelNames.add("Phone number: ");
        labelNames.add("Marriage status: ");
    }

    private void showAddVeterinarianDialog() {
        Stage addVeterinarianStage = new Stage();
        addVeterinarianStage.setTitle("New veterinarian");
        GridPane gridPane = new GridPane();
        Scene addVeterinarianScene = new Scene(gridPane, 300, 300);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(40);
        gridPane.setHgap(20);
        gridPane.setPrefWidth(200);
        List<String> labelNames = new ArrayList<>();
        loadLabelsInAddVeterinarianDialog(labelNames);
        addNodesToGridColumn(buildLabelList(labelNames), 0, 0, gridPane);

        TextField numberField = new TextField();
        numberField.setPromptText("Code number");
        gridPane.add(numberField, 1, 0);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        gridPane.add(nameField, 1, 1);

        TextField secondNameField = new TextField();
        secondNameField.setPromptText("Second name");
        gridPane.add(secondNameField, 1, 2);

        TextField thirdNameField = new TextField();
        thirdNameField.setPromptText("Third name");
        gridPane.add(thirdNameField, 1, 3);

        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("Phone number");
        gridPane.add(phoneNumberField, 1, 4);

        ChoiceBox<String> marriageStatus = new ChoiceBox<>();
        List<String> marriageStatusItems = new ArrayList<>();
        marriageStatusItems.add("женат/замужем");
        marriageStatusItems.add("не женат/Не замужем");
        marriageStatusItems.add("неизвестно");
        marriageStatus.setItems(FXCollections.observableArrayList(marriageStatusItems));
        marriageStatus.setValue(marriageStatusItems.get(1));
        gridPane.add(marriageStatus, 1, 5);

        Button addBtn = new Button();
        addBtn.setText("Add veterinarian");
        addBtn.setMinWidth(gridPane.getPrefWidth());
        addBtn.setOnAction(event -> {
            if (numberField.getText().isEmpty() |
                    nameField.getText().isEmpty() |
                    secondNameField.getText().isEmpty() |
                    thirdNameField.getText().isEmpty() |
                    phoneNumberField.getText().isEmpty()) {
                showMessage(Alert.AlertType.ERROR, "Empty fields", "An empty fields found",
                        "Please, fill all the fields.");
            } else {
                try {
                    controller.executeInsert("insert into veterinarians (code, first_name, second_name, " +
                            "third_name, phone_number, marriage)" +
                            " values(" + "\'" + numberField.getText() + "\', " +
                            "\'" + nameField.getText() + "\', " +
                            "\'" + secondNameField.getText() + "\', " +
                            "\'" + thirdNameField.getText() + "\', " +
                            "\'" + phoneNumberField.getText() + "\', " +
                            "\'" + marriageStatus.getValue() + "\') ");
                    numberField.clear();
                    nameField.clear();
                    secondNameField.clear();
                    thirdNameField.clear();
                    phoneNumberField.clear();
                    showMessage(Alert.AlertType.INFORMATION, "Success", "Successfully " +
                            "added new veterinarian");
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });

        gridPane.add(addBtn, 0, 6);
        Button closeBtn = new Button();
        closeBtn.setText("Close");
        closeBtn.setOnAction(event -> {
            addVeterinarianStage.close();
        });
        closeBtn.setMinWidth(gridPane.getPrefWidth());
        gridPane.add(closeBtn, 1, 6);
        addVeterinarianStage.setMinHeight(500);
        addVeterinarianStage.setMinWidth(500);
        addVeterinarianStage.setScene(addVeterinarianScene);
        addVeterinarianStage.showAndWait();
    }

    private void loadLabelsInAddHabitatDialog(List<String> labelNames) {
        labelNames.add("Habitat name: ");
        labelNames.add("Description: ");
    }

    private void showAddHabitatDialog() {
        Stage addHabitatStage = new Stage();
        addHabitatStage.setTitle("New veterinarian");
        GridPane gridPane = new GridPane();
        Scene addHabitatScene = new Scene(gridPane, 300, 300);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(40);
        gridPane.setHgap(20);
        gridPane.setPrefWidth(200);
        List<String> labelNames = new ArrayList<>();
        loadLabelsInAddHabitatDialog(labelNames);
        addNodesToGridColumn(buildLabelList(labelNames), 0, 0, gridPane);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        gridPane.add(nameField, 1, 0);

        TextArea descriptionArea = new TextArea();
        descriptionArea.setMaxSize(200, 200);
        descriptionArea.setPromptText("Habitat description here");
        gridPane.add(descriptionArea, 0, 2);

        Button addBtn = new Button();
        addBtn.setText("Add ration");
        addBtn.setMinWidth(gridPane.getPrefWidth());
        addBtn.setOnAction(event -> {
            if (nameField.getText().isEmpty() |
                    descriptionArea.getText().isEmpty()) {
                showMessage(Alert.AlertType.ERROR, "Empty fields", "An empty fields found",
                        "Please, fill all the fields.");
            } else {
                try {
                    controller.executeInsert("insert into habitat (name, description)" +
                            " values(" + "\'" + nameField.getText() + "\', " +
                            "\'" + descriptionArea.getText() + "\')");
                    nameField.clear();
                    descriptionArea.clear();
                    showMessage(Alert.AlertType.INFORMATION, "Success", "Successfully added new habitat");
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        gridPane.add(addBtn, 0, 3);
        Button closeBtn = new Button();
        closeBtn.setText("Close");
        closeBtn.setOnAction(event -> {
            addHabitatStage.close();
        });
        closeBtn.setMinWidth(gridPane.getPrefWidth());
        gridPane.add(closeBtn, 1, 3);
        addHabitatStage.setMinHeight(350);
        addHabitatStage.setMinWidth(500);
        addHabitatStage.setMaxWidth(500);
        addHabitatStage.setMaxHeight(350);
        addHabitatStage.setScene(addHabitatScene);
        addHabitatStage.showAndWait();
    }

    private void loadLabelsInAddRationDialog(List<String> labelNames) {
        labelNames.add("Code: ");
        labelNames.add("Name: ");
        labelNames.add("Type: ");
    }

    private void showAddRationDialog() {
        Stage addRationStage = new Stage();
        addRationStage.setTitle("New veterinarian");
        GridPane gridPane = new GridPane();
        Scene addRationScene = new Scene(gridPane, 300, 300);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(40);
        gridPane.setHgap(20);
        gridPane.setPrefWidth(200);
        List<String> labelNames = new ArrayList<>();
        loadLabelsInAddRationDialog(labelNames);
        addNodesToGridColumn(buildLabelList(labelNames), 0, 0, gridPane);

        TextField numberField = new TextField();
        numberField.setPromptText("Code number: ");
        gridPane.add(numberField, 1, 0);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        gridPane.add(nameField, 1, 1);

       /* TextField rationTypeField = new TextField();
        rationTypeField.setPromptText("Type");
        gridPane.add(rationTypeField, 1, 2);*/

        ChoiceBox<String> rationType = new ChoiceBox<>();
        List<String> rationTypeItems = new ArrayList<>();
        rationTypeItems.add("Детский");
        rationTypeItems.add("Стандартный");
        rationTypeItems.add("Усиленный");
        rationTypeItems.add("Взрослый");
        rationTypeItems.add("Взрослый усиленный");
        rationType.setItems(FXCollections.observableArrayList(rationTypeItems));
        rationType.setValue(rationTypeItems.get(1));
        gridPane.add(rationType, 1, 2);


        Button addBtn = new Button();
        addBtn.setText("Add ration");
        addBtn.setMinWidth(gridPane.getPrefWidth());
        addBtn.setOnAction(event -> {
            if (numberField.getText().isEmpty() |
                    nameField.getText().isEmpty()) {
                showMessage(Alert.AlertType.ERROR, "Empty fields", "An empty fields found",
                        "Please, fill all the fields.");
            } else {
                try {
                    controller.executeInsert("insert into rations (code, name, type)" +
                            " values(" + "\'" + numberField.getText() + "\', " +
                            "\'" + nameField.getText() + "\', " +
                            "\'" + rationType.getValue() + "\')");
                    numberField.clear();
                    nameField.clear();
                    showMessage(Alert.AlertType.INFORMATION, "Success", "Successfully added" +
                            " new ration");
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        gridPane.add(addBtn, 0, 3);
        Button closeBtn = new Button();
        closeBtn.setText("Close");
        closeBtn.setOnAction(event -> {
            addRationStage.close();
        });
        closeBtn.setMinWidth(gridPane.getPrefWidth());
        gridPane.add(closeBtn, 1, 3);
        addRationStage.setMinHeight(300);
        addRationStage.setMinWidth(500);
        addRationStage.setMaxWidth(500);
        addRationStage.setMaxHeight(300);
        addRationStage.setScene(addRationScene);
        addRationStage.showAndWait();
    }

    private void loadLabelsInAddWinteringDialog(List<String> labelNames) {
        labelNames.add("Code name: ");
        labelNames.add("Country: ");
        labelNames.add("Departure date: ");
        labelNames.add("Arrival date: ");
    }

    private void showAddWinteringDialog() {
        Stage addWinteringStage = new Stage();
        addWinteringStage.setTitle("New wintering");
        GridPane gridPane = new GridPane();
        Scene addWinteringScene = new Scene(gridPane, 300, 300);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(40);
        gridPane.setHgap(20);
        gridPane.setPrefWidth(200);
        List<String> labelNames = new ArrayList<>();
        loadLabelsInAddWinteringDialog(labelNames);
        addNodesToGridColumn(buildLabelList(labelNames), 0, 0, gridPane);

        TextField numberField = new TextField();
        numberField.setPromptText("Code name: ");
        gridPane.add(numberField, 1, 0);

        TextField countryField = new TextField();
        countryField.setPromptText("Country");
        gridPane.add(countryField, 1, 1);

       /* TextField rationTypeField = new TextField();
        rationTypeField.setPromptText("Type");
        gridPane.add(rationTypeField, 1, 2);*/

        DatePicker departureDate = new DatePicker();
        departureDate.setValue(LocalDate.now());
        departureDate.setPromptText("Departure date");
        gridPane.add(departureDate, 1, 2);

        DatePicker arriavalDate = new DatePicker();
        arriavalDate.setValue(LocalDate.now());
        arriavalDate.setPromptText("Arrival date");
        gridPane.add(arriavalDate, 1, 3);


        Button addBtn = new Button();
        addBtn.setText("Add ration");
        addBtn.setMinWidth(gridPane.getPrefWidth());
        addBtn.setOnAction(event -> {
            if (numberField.getText().isEmpty() |
                    countryField.getText().isEmpty()) {
                showMessage(Alert.AlertType.ERROR, "Empty fields", "An empty fields found",
                        "Please, fill all the fields.");
            } else {
                try {
                    controller.executeInsert("insert into wintering (code_name, country, departure_date, arrival_date)" +
                            " values(" +
                            "\'" + numberField.getText() + "\', " +
                            "\'" + countryField.getText() + "\', " +
                            "\'" + departureDate.getValue() + "\', " +
                            "\'" + arriavalDate.getValue() + "\')");
                    numberField.clear();
                    countryField.clear();
                    showMessage(Alert.AlertType.INFORMATION, "Success", "Successfully added" +
                            " new wintering");
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }
        });
        gridPane.add(addBtn, 0, 4);
        Button closeBtn = new Button();
        closeBtn.setText("Close");
        closeBtn.setMinWidth(gridPane.getPrefWidth());
        closeBtn.setOnAction(event -> {
            addWinteringStage.close();
        });
        gridPane.add(closeBtn, 1, 4);
        addWinteringStage.setMinHeight(600);
        addWinteringStage.setMinWidth(500);
        addWinteringStage.setMaxWidth(500);
        addWinteringStage.setMaxHeight(600);
        addWinteringStage.setScene(addWinteringScene);
        addWinteringStage.showAndWait();
    }

    private void showAddBirdDialog() {
        Stage addBirdStage = new Stage();
        addBirdStage.setTitle("New bird");
        GridPane gridPane = new GridPane();
        Scene addBirdScene = new Scene(gridPane, 300, 300);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(40);
        gridPane.setHgap(40);
        List<String> labelNames = new ArrayList<>();
        loadLabelsInAddBirdDialog(labelNames);
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
                controller.executeInsert("insert into birds (unique_number, name, birthday, gender, watcher, " +
                        "veterinarian, ration, habitat, wintering) values (" +
                        "\'" + birdNumber.getText() + "\', " +
                        "\'" + birdName.getText() + "\', " +
                        "\'" + birthdayDate.getValue() + "\', \'"
                        + birdGender.getValue() + "\', \'"
                        + finalBirdWatcher.getValue() + "\', " +
                        "\'" + finalBirdVeterinarian.getValue() + "\', " +
                        "\'" + finalBirdRation.getValue() + "\', " +
                        "\'" + finalBirdHabitat.getValue() + "\', " +
                        "\'" + finalBirdWintering.getValue() + "\'" + ");");
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

        addBirdStage.setMinHeight(750);
        addBirdStage.setMinWidth(550);
        addBirdStage.setMaxHeight(750);
        addBirdStage.setMaxWidth(550);
        addBirdStage.setScene(addBirdScene);
        addBirdStage.showAndWait();
    }

    private void loadLabelNamesToAddReptileDialog(List<String> labelNames) {
        labelNames.add("Reptile number: ");
        labelNames.add("Reptile name: ");
        labelNames.add("Gender: ");
        labelNames.add("Birthday date: ");
        labelNames.add("Ration type: ");
        labelNames.add("Watcher code: ");
        labelNames.add("Veterinarian code: ");
        labelNames.add("Habitat: ");
        labelNames.add("Normal temperature: ");
        labelNames.add("Hibernation beginning: ");
        labelNames.add("Hibernation end: ");
    }

    private void showAddReptileDialog() {
        Stage addReptileStage = new Stage();
        addReptileStage.setTitle("New reptile");
        GridPane gridPane = new GridPane();
        Scene addReptileScene = new Scene(gridPane, 300, 450);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(40);
        gridPane.setHgap(40);
        List<String> labelNames = new ArrayList<>();
        loadLabelNamesToAddReptileDialog(labelNames);
        addNodesToGridColumn(buildLabelList(labelNames), 0, 0, gridPane);

        TextField reptileNumber = new TextField();
        reptileNumber.setPromptText("Reptile number");
        gridPane.add(reptileNumber, 1, 0);

        TextField reptileName = new TextField();
        reptileName.setPromptText("Reptile name");
        gridPane.add(reptileName, 1, 1);

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
        birthdayDate.setPromptText("Reptile birthday date");
        gridPane.add(birthdayDate, 1, 3);


        ChoiceBox<String> reptileRation = null;
        try {
            reptileRation = buildChoiceBox(controller.executeAnonymousRequest("select code from ration"),
                    1);
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        gridPane.add(reptileRation, 1, 4);

        ChoiceBox<String> reptileWatcher = null;
        try {
            reptileWatcher = buildChoiceBox(
                    controller.executeAnonymousRequest("select code from watchers"), 1);
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        gridPane.add(reptileWatcher, 1, 5);

        ChoiceBox<String> reptileVeterinarian = null;
        try {
            reptileVeterinarian = buildChoiceBox(
                    controller.executeAnonymousRequest("select code from veterinarians"), 1);
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        gridPane.add(reptileVeterinarian, 1, 6);

        ChoiceBox<String> reptileHabitat = null;
        try {
            reptileHabitat = buildChoiceBox(
                    controller.executeAnonymousRequest("select name from habitat"), 1);
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        gridPane.add(reptileHabitat, 1, 7);


        TextField normalTemp = new TextField();
        normalTemp.setPromptText("Normal temperature like 36.6");
        gridPane.add(normalTemp, 1, 8);

        DatePicker hibernationBeg = new DatePicker();
        hibernationBeg.setValue(LocalDate.now());
        hibernationBeg.setPromptText("Reptile hibernation beginning");
        gridPane.add(hibernationBeg, 1, 9);

        DatePicker hibernationEnd = new DatePicker();
        hibernationEnd.setValue(LocalDate.now());
        hibernationEnd.setPromptText("Reptile hibernation ending");
        gridPane.add(hibernationEnd, 1, 10);
        Button addBtn = new Button();
        addBtn.setText("Add reptile");
        ChoiceBox<String> finalReptileWatcher = reptileWatcher;
        ChoiceBox<String> finalReptileVeterinarian = reptileVeterinarian;
        addBtn.setOnAction(event -> {
            if (reptileNumber.getText().isEmpty() | reptileName.getText().isEmpty()) {
                showMessage(Alert.AlertType.ERROR, "Empty field", "One of fields is empty. Please, " +
                        "fill all fields");
                return;
            }
            double normalTemperature;
            try {
                normalTemperature = Double.parseDouble(normalTemp.getText());
            } catch (NumberFormatException e) {
                showMessage(Alert.AlertType.ERROR, "Wrong number format",
                        "Please, input a number in the field \"Normal temperature\" in format \"36.6\"");
                return;
            }
            try {
                controller.executeInsert("insert into reptiles (unique_number, name, birthday, gender," +
                        " hibernation_beginning, hibernation_ending, normal_temperature, watcher, veterinarian) values (" +
                        "\'" + reptileNumber.getText() + "\', " +
                        "\'" + reptileName.getText() + "\', " +
                        "\'" + birthdayDate.getValue() + "\', " +
                        "\'" + birdGender.getValue() + "\', " +
                        "\'" + hibernationBeg.getValue() + "\', " +
                        "\'" + hibernationEnd.getValue() + "\', " +
                        +normalTemperature +
                        ", \'" + finalReptileWatcher.getValue() + "\', " +
                        "\'" + finalReptileVeterinarian.getValue() + "\', " +
                        ")");
                showMessage(Alert.AlertType.INFORMATION, "Success", "Successfully added");
            } catch (SQLException e) {
                exceptionWindow(e);
            }

        });

        gridPane.add(addBtn, 0, 11);

        Button closeBtn = new Button();
        closeBtn.setText("Close");
        closeBtn.setOnAction(event -> addReptileStage.close());

        gridPane.add(closeBtn, 1, 11);
        gridPane.autosize();

        addReptileStage.setMinHeight(890);
        addReptileStage.setMinWidth(550);
        addReptileStage.setMaxWidth(550);
        addReptileStage.setMaxHeight(890);
        addReptileStage.setScene(addReptileScene);
        addReptileStage.showAndWait();
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

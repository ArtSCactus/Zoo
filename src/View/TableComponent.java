package View;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import util.Controller;

import javax.xml.crypto.Data;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TableComponent {
    private ObservableList tableData;
    private TableView<ObservableList> table;
    private ContextMenu contextMenu;
    private BidiMap<String, String> userFriendlyColumnNames;
    private Controller controller;

    {
        table = new TableView<>();
        buildContextMenu();
        table.autosize();
        table.setEditable(true);
        table.setOnContextMenuRequested(event -> {
            contextMenu.show(table, event.getScreenX(), event.getScreenY());
        });
        userFriendlyColumnNames = new DualHashBidiMap<>();
    }

    public TableComponent(ObservableList tableData, TableView<ObservableList> table) {
        this.tableData = tableData;
        this.table = table;
        table.setItems(tableData);
    }

    public TableComponent(Controller controller) {
        tableData = FXCollections.observableArrayList();
        this.controller = controller;
    }

    public void addCustomColumnName(String systemName, String viewingName) {
        if (systemName == null | viewingName == null) {
            throw new NullPointerException("Cannot put null value to map");
        }
        userFriendlyColumnNames.put(systemName, viewingName);
    }

    private String translateNameForUser(String columnSystemName) {
        return userFriendlyColumnNames.get(columnSystemName);
    }

    private String translateNameForSystem(String columnCustomName) {
        return userFriendlyColumnNames.getKey(columnCustomName);
    }

    private String getColumnSystemName(TableColumn column) {
        if (userFriendlyColumnNames.getKey(column.getText()) == null) {
            return column.getText();
        } else {
            return userFriendlyColumnNames.getKey(column.getText());
        }
    }

    public Object getCellValue(int column, int row) {
        if (column < 0 | row < 0) {
            throw new IllegalArgumentException("Cell by coordinates col=" + column + ", row=" + row + " does not exists.");
        }
        return table.getColumns().get(column).getCellData(row);
    }

    /**
     * Returns primary key column as ArrayList.
     * <p>
     * In this case will be returned first met primary key column.
     * If in current table is no primary key column, will be returned first column in table as
     * {@code ArrayList<String>()}
     *
     * @param catalog   catalog, where schema is placed
     * @param schema    schema, where catalog is placed
     * @param tableName name of table
     * @return (String) type {@code ArrayList<>()}
     */
    public List<String> getPrimaryKeys(String catalog, String schema, String tableName) {
        List<String> primaryKeys = new ArrayList<>();
        try {
            DatabaseMetaData metaData = controller.getConnection().getMetaData();
            ResultSet primaryKeysRS = metaData.getPrimaryKeys(catalog, schema, tableName);
            if (primaryKeysRS.getMetaData().getColumnCount() == 0) {
                primaryKeysRS = controller.executeAnonymousRequest("select * from " + tableName);
                primaryKeysRS.first();
                do {
                    primaryKeys.add(primaryKeysRS.getString(1));
                } while (primaryKeysRS.next());
                primaryKeysRS.close();
            } else {
                while (primaryKeysRS.next()) {
                    primaryKeys.add(primaryKeysRS.getString(1));
                }
                primaryKeysRS.close();
                primaryKeys.remove(0);
            }
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        return primaryKeys;
    }

    /**
     * Returns primary key column as ArrayList by its serial number.
     * <p>
     * Serial number is number of primary key column in ResultSet from
     * {@code DatabaseMetaData.getPrimaryKeys(catalog,schema,tableName);} method.
     * In this case will be returned first met primary key column.
     * If in current table is no primary key column, will be returned column by primaryColumnIndex in table as
     * {@code ArrayList<String>()}
     *
     * @param catalog            catalog, where schema is placed
     * @param schema             schema, where catalog is placed
     * @param tableName          name of table
     * @param primaryColumnIndex serial number of primary key column.
     * @return (String) type {@code ArrayList<>()}
     */
    public List<String> getPrimaryKeys(String catalog, String schema, String tableName, int primaryColumnIndex) {
        List<String> primaryKeys = new ArrayList<>();
        try {
            DatabaseMetaData metaData = controller.getConnection().getMetaData();
            ResultSet primaryKeysRS = metaData.getPrimaryKeys(catalog, schema, tableName);
            if (primaryKeysRS.getMetaData().getColumnCount() == 0) {
                primaryKeysRS = controller.executeAnonymousRequest("select * from " + tableName);
                primaryKeysRS.first();
                do {
                    primaryKeys.add(primaryKeysRS.getString(primaryColumnIndex));
                } while (primaryKeysRS.next());
                primaryKeysRS.close();
            } else {
                while (primaryKeysRS.next()) {
                    primaryKeys.add(primaryKeysRS.getString(primaryColumnIndex));
                }
                primaryKeysRS.close();
                primaryKeys.remove(0);
            }
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        return primaryKeys;
    }

    private void buildContextMenu() {
        contextMenu = new ContextMenu();
        MenuItem addRow = new MenuItem();
        addRow.setText("Add row");
        addRow.setOnAction(event -> {
            //Dialog handler here
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < table.getColumns().size(); i++) {
                //Iterate Column
                row.add("New row");
            }
            tableData.add(row);
        });
        MenuItem deleteRow = new MenuItem();
        deleteRow.setText("Delete");
        deleteRow.setOnAction(event -> {
            //Dialog handler here
            int row = table.getSelectionModel().getSelectedIndex();
            if (row == -1) {
                showMessage(Alert.AlertType.ERROR, "Row is not selected", "You did not selected the row",
                        "Please, select row and try again");
            }
            TablePosition position = table.getSelectionModel().getSelectedCells().get(0);
            Optional<ButtonType> userRespond = confirmationWindow("Confirm removing", "Are you sure want to remove row №" +
                    position.getRow() + "?", "Please, confirm removing row.");
            if (userRespond.get() == ButtonType.CANCEL) {
                return;
            } else {
                try {
                    ResultSet data = controller.getCurrentLocalData();
                    String tableName = data.getMetaData().getTableName(findColumnIndexInResultSet(data, position.getTableColumn()));
                    String columnName = getColumnSystemName(position.getTableColumn());
                    ResultSet anonymousSet = controller.executeAnonymousRequest("select * from " + tableName +
                            " where " + columnName + " = \'" + getCellValue(position.getColumn(), position.getRow()) + "\'");
                    anonymousSet.last();
                    //counting it
                    int rowAmount = anonymousSet.getRow();
                    if (anonymousSet.getRow() > 1) {
                        //Creating list with uniques codes of possible affected cells
                        List<String> codes = getPrimaryKeys(anonymousSet.getMetaData().getCatalogName(1),
                                anonymousSet.getMetaData().getCatalogName(1), tableName);
                        anonymousSet.first();
                        //filling codes list
                        do {
                            codes.add(anonymousSet.getString(1));
                        } while (anonymousSet.next());
                        codes.add("Remove all");
                        String selectedCode;
                        //Watching what user had chose
                        selectedCode = showRemovingConflictDialog(codes, rowAmount);
                        if (selectedCode == null) {
                            updateTable(table, controller.getCurrentLocalData());
                        } else if (selectedCode.equals("Remove all")) {// Removing all cells,
                            // if user chose such option
                            //Executing removing for all founded matches
                            controller.executeUpdate("delete from " +
                                    tableName + " where " + columnName
                                    + " = " + "\'" + getCellValue(position.getColumn(), position.getRow()) + "\';");
                            updateTable(controller.getLastRequest(), controller);
                        } else {
                            //Executing custom removing request to change only 1 cell
                            controller.executeUpdate("delete from " +
                                    tableName + " where " + columnName
                                    + " = " + "\'" + getCellValue(position.getColumn(), position.getRow()) + "\' and " +
                                    anonymousSet.getMetaData().getColumnName(1) + "=" + "\'" + selectedCode + "\'");
                            updateTable(controller.getLastRequest(), controller);
                        }
                    } else {
                        //Removing without conflict
                        controller.executeUpdate("delete from " +
                                tableName + " where " + columnName
                                + " = " + "\'" + getCellValue(position.getColumn(), position.getRow()) + "\';");
                        updateTable(controller.getLastRequest(), controller);
                    }
                } catch (SQLException e) {
                    exceptionWindow(e);
                }
            }

        });
        contextMenu.getItems().addAll(addRow, deleteRow);
    }

    public ObservableList getTableData() {
        return tableData;
    }

    public void setTableData(ObservableList tableData) {
        this.tableData = tableData;
    }

    public TableView<ObservableList> getTable() {
        return table;
    }

    public void setTable(TableView<ObservableList> table) {
        this.table = table;
    }

    public void addRow(ObservableList<String> row) {
        if (row == null) {
            throw new NullPointerException("Cannot add null row to table");
        }
        for (String column : row) {
            if (column == null) {
                throw new NullPointerException("A null value has been found in row");
            }
        }
        tableData.add(row);
    }

    /**
     * Finds index of given column in given ResultSet.
     * <p>
     * If such column wasn't found in given ResultSet, would be returned -1;
     *
     * @param resultSet ResultSet where will be searched column
     * @param column    Column, that need to be find in ResultSet
     * @return index of column in resultSet; -1 if such column wasn't found
     * @throws SQLException
     */
    private int findColumnIndexInResultSet(ResultSet resultSet, TableColumn column) throws SQLException {
        String columnCustomName = userFriendlyColumnNames.getKey(column.getText());
        if (columnCustomName == null) {
            for (int index = 1; index <= resultSet.getMetaData().getColumnCount(); index++) {

                if (resultSet.getMetaData().getColumnLabel(index).equals(column.getText())) {
                    return index;
                }
            }
        } else {
            for (int index = 1; index <= resultSet.getMetaData().getColumnCount(); index++) {

                if (resultSet.getMetaData().getColumnLabel(index).equals(columnCustomName)) {
                    return index;
                }
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
     * @param controller    Controller needs to request same rows from table, in case of conflict
     * @throws SQLException
     */
    public void fillTable(ResultSet requestResult, Controller controller) throws SQLException {
        table.getColumns().clear();
        tableData = FXCollections.observableArrayList();
        TableColumn numberCol = new TableColumn("№");
        numberCol.setSortable(false);
        numberCol.setEditable(false);
        table.getColumns().add(numberCol);
        String columnName;
        //autofilling table by columns
        for (int index = 0; index < requestResult.getMetaData().getColumnCount(); index++) {
            final int j = index;
            //  columnSystemNames.add(requestResult.getMetaData().getColumnName(index + 1));
            String temp = requestResult.getMetaData().getColumnName(index + 1);
            columnName = translateNameForUser(temp) == null ?
                    temp :
                    userFriendlyColumnNames.get(requestResult.getMetaData().getColumnName(index + 1));
            numberCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String, String>, ObservableValue<String>>) p ->
                    new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + ""));
            TableColumn col = new TableColumn(columnName);
            col.setEditable(true);
            //dynamic filling columns
            col.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> {
                        try {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        } catch (NullPointerException e) {
                            return new SimpleStringProperty("null");
                        }
                    });
            col.setCellFactory(TextFieldTableCell.forTableColumn());
            col.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                @Override
                public void handle(TableColumn.CellEditEvent event) {
                    try {
                        String tableName = requestResult.getMetaData().getTableName(findColumnIndexInResultSet(requestResult,
                                event.getTablePosition().getTableColumn()));
                        String columnName = getColumnSystemName(col);
                        //Watching how much rows can be affected by editing this cell (getting this rows)
                        ResultSet anonymousSet = controller.executeAnonymousRequest("select * from " + tableName +
                                " where " + columnName + " = \'" + event.getOldValue() + "\'");
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
                            } while (anonymousSet.next());
                            codes.add("Apply for all");
                            String selectedCode;
                            //Watching what user had chose
                            selectedCode = showEditConflictDialog(codes, rowAmount);
                            if (selectedCode == null) {
                                updateTable(table, controller.getCurrentLocalData());
                            } else if (selectedCode.equals("Apply for all")) {// applying changes for all cells,
                                //Executing update for all founded matches
                                controller.executeUpdate("update " +
                                        tableName + " set " + columnName
                                        + " = " + "\'" + event.getNewValue() + "\'" +
                                        " where " + columnName
                                        + " = " + "\'" + event.getOldValue() + "\';");
                                updateTable(controller.getLastRequest(), controller);
                            } else {
                                //Executing custom update request to change only 1 cell
                                controller.executeUpdate("update " +
                                        tableName + " set " + columnName
                                        + " = " + "\'" + event.getNewValue() + "\'" +
                                        " where " + columnName
                                        + " = " + "\'" + event.getOldValue() + "\' and " +
                                        anonymousSet.getMetaData().getColumnName(1) + "=" + "\'" + selectedCode + "\'");
                                updateTable(controller.getLastRequest(), controller);
                            }
                        } else {
                            controller.executeUpdate("update " +
                                    tableName + " set " + columnName
                                    + " = " + "\'" + event.getNewValue() + "\'" +
                                    " where " + columnName
                                    + " = " + "\'" + event.getOldValue() + "\';");
                            updateTable(controller.getLastRequest(), controller);
                        }
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
            tableData.add(row);
        }
        table.setItems(tableData);
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
        alert.setContentText(Arrays.toString(e.getStackTrace()));
        alert.showAndWait();
    }


    private void updateTable(String request, Controller controller) {
        ResultSet newSet = null;
        try {
            newSet = controller.executeRequest(request);
            newSet.next();
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        tableData = FXCollections.observableArrayList();
        table.getItems().clear();
        try {
            do {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= newSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(newSet.getString(i));
                }
                tableData.add(row);
            } while (newSet.next());
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        table.setItems(tableData);
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
        tableData = FXCollections.observableArrayList();
        table.getItems().clear();
        try {
            ObservableList<String> row;
            //Possible given ResultSet already was used, and cursor is outside of rows border. Then we need to
            // set cursor to the first row.
            dataSet.first();
            do {
                //Iterate Row
                row = FXCollections.observableArrayList();
                for (int i = 1; i <= dataSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(dataSet.getString(i));
                }
                tableData.add(row);
            } while (dataSet.next());
            // do {} while() because if we would use just while(){}, we would lost first
            //row because of calling method dataSet.next();
        } catch (SQLException e) {
            exceptionWindow(e);
        }
        table.setItems(tableData);
    }

    /**
     * Shows dialog, that informing user about conflict while data editing.
     * <p>
     * This dialog should be used to show user, that changing 1 cell can affect to all same cells.
     * Dialog allows user to set cell number, that he want to change (Only one or all).
     *
     * @param codes     List, that contains unique codes of elements, that can be changed
     * @param rowAmount Amount of rows, that potential would be affected.
     * @return String {@code getSelectedItem()}
     */
    private String showEditConflictDialog(List<String> codes, int rowAmount) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Possible conflict");
        dialog.setHeaderText("Changing of this cell can affect on " + rowAmount + " rows");
        dialog.setContentText("Please, choose code number of element, that you want to change");
        dialog.getItems().addAll(codes);
        dialog.setSelectedItem(codes.get(codes.size() - 1));
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return dialog.getSelectedItem();
        } else {
            return null;
        }
    }

    /**
     * Shows dialog, that informing user about conflict while data editing.
     * <p>
     * This dialog should be used to show user, that changing 1 cell can affect to all same cells.
     * Dialog allows user to set cell number, that he want to change (Only one or all).
     *
     * @param codes     List, that contains unique codes of elements, that can be changed
     * @param rowAmount Amount of rows, that potential would be affected.
     * @return String {@code getSelectedItem()}
     */
    private String showRemovingConflictDialog(List<String> codes, int rowAmount) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("A conflict founded");
        dialog.setHeaderText("Removing of this row can also affect (remove) on " + rowAmount + " rows");
        dialog.setContentText("Please, choose code number of element, that you want to remove");
        dialog.getItems().addAll(codes);
        dialog.setSelectedItem(codes.get(codes.size() - 1));
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return dialog.getSelectedItem();
        } else {
            return null;
        }
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

    private Optional<ButtonType> confirmationWindow(String titleText, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titleText);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        Optional<ButtonType> userRespond = alert.showAndWait();
        return userRespond;
    }

}

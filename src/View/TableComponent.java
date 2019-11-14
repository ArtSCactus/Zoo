package View;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TableComponent {
    private ObservableList tableData;
    private TableView<ObservableList> table;
    private ContextMenu contextMenu;
    private List<String> columnSystemNames;
    private BidiMap<String, String> userFriendlyColumnNames;

    {
        table = new TableView<>();
        columnSystemNames = new ArrayList<>();
        buildContextMenu();
        table.autosize();
        table.setEditable(true);
        table.setOnContextMenuRequested(event -> {
            contextMenu.show(table, event.getScreenX(), event.getScreenY());
        });
        columnSystemNames = new ArrayList<>();
        userFriendlyColumnNames = new DualHashBidiMap<>();
    }

    public TableComponent(ObservableList tableData, TableView<ObservableList> table) {
        this.tableData = tableData;
        this.table = table;
        table.setItems(tableData);
    }

    public TableComponent() {
        tableData = FXCollections.observableArrayList();
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

    private String translateNameForSystem(String columnCustomName){
       return userFriendlyColumnNames.getKey(columnCustomName);
    }

    private String getColumnSystemName(TableColumn column){
       if(userFriendlyColumnNames.getKey(column.getText())==null){
           return column.getText();
       }else{
           return userFriendlyColumnNames.getKey(column.getText());
       }
    }

    private void buildContextMenu() {
        contextMenu = new ContextMenu();
        MenuItem addRow = new MenuItem();
        addRow.setText("Add row");
        addRow.setOnAction(event -> {
            //Dialog handler here
        });
        MenuItem deleteRow = new MenuItem();
        deleteRow.setText("Delete");
        deleteRow.setOnAction(event -> {
            //Dialog handler here
        });
        contextMenu.getItems().addAll(addRow);
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
        String columnCustomName=userFriendlyColumnNames.getKey(column.getText());
        if (columnCustomName==null) {
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
        columnSystemNames.clear();
        tableData = FXCollections.observableArrayList();
        String columnName;
        //autofilling table by columns
        for (int index = 0; index < requestResult.getMetaData().getColumnCount(); index++) {
            final int j = index;
          //  columnSystemNames.add(requestResult.getMetaData().getColumnName(index + 1));
            String temp =requestResult.getMetaData().getColumnName(index + 1);
            columnName = translateNameForUser(temp)==null?
                    temp :
                    userFriendlyColumnNames.get(requestResult.getMetaData().getColumnName(index + 1));
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
                        //Watching how much rows can be affected by editing this cell (getting this rows)
                        ResultSet anonymousSet = controller.executeAnonymousRequest("select * from " + tableName +
                                " where " + getColumnSystemName(col) + " = \'" + event.getOldValue() + "\'");
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
                            selectedCode = showConflictDialog(codes, rowAmount);
                            if (selectedCode == null) {
                                updateTable(table, controller.getCurrentLocalData());
                            } else if (selectedCode.equals("Apply for all")) {// applying changes for all cells,
                                // if user chose such option
                                //Executing update for all founded matches
                                controller.executeUpdate("update " +
                                        tableName + " set " + getColumnSystemName(col)
                                        + " = " + "\'" + event.getNewValue() + "\'" +
                                        " where " + getColumnSystemName(col)
                                        + " = " + "\'" + event.getOldValue() + "\';");
                                updateTable(table, controller.getLastRequest(), controller);
                            } else {
                                //Executing custom update request to change only 1 cell
                                controller.executeUpdate("update " +
                                        tableName + " set " + getColumnSystemName(col)
                                        + " = " + "\'" + event.getNewValue() + "\'" +
                                        " where " + getColumnSystemName(col)
                                        + " = " + "\'" + event.getOldValue() + "\' and " +
                                        anonymousSet.getMetaData().getColumnName(1) + "=" + "\'" + selectedCode + "\'");
                                updateTable(table, controller.getLastRequest(), controller);
                            }}else {
                            controller.executeUpdate("update " +
                                    tableName + " set " + getColumnSystemName(col)
                                    + " = " + "\'" + event.getNewValue() + "\'" +
                                    " where " + getColumnSystemName(col)
                                    + " = " + "\'" + event.getOldValue() + "\';");
                            updateTable(table, controller.getLastRequest(), controller);
                        }
                        //Adding data from sql request to ObservableList
                    } catch (SQLException e) {
                       // exceptionWindow(e);
                        e.printStackTrace();
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


    private void updateTable(TableView table, String request, Controller controller) {
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
    private String showConflictDialog(List<String> codes, int rowAmount) {
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

}

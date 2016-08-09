package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.SiecModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by igor on 04.08.16.
 */
public class TableManager<T extends SiecModel> {
    private ObservableList<T> data = FXCollections.observableArrayList();
    private TableView<T> table;
    private List<String> columnsName = new ArrayList<>();

    public void setColumnsName(List<String> columnsName) {
        this.columnsName = columnsName;
    }

    public ObservableList<T> getData() {
        return data;
    }

    public TableManager(TableView<T> table) {
        this.table = table;

    }

    public void setData(Collection<? extends T> collection){
        data.clear();
        data.setAll(collection);
        table.setItems(data);
    }

    public void setColumns(Collection<String> collection, Callback<TableColumn<T, String>, TableCell<T, String>> callback){
        if(columnsName.size() != 0) {
            columnsName.clear();
            table.getColumns().removeAll();
        }

        columnsName.addAll(collection);
        for (String str :
                columnsName) {

            TableColumn tableColumn = new TableColumn();
            tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<T, String> param) {
                    return new SimpleStringProperty(param.getValue().getValue(str));
                }
            });
            tableColumn.setCellFactory(callback);
            table.getColumns().add(tableColumn);
        }
    }

    public List<String> getColumnsName() {
        return columnsName;
    }
    public void addColumns(String columnName, Callback<TableColumn<T, String>, TableCell<T, String>> callback){
        columnsName.add(columnName);
        TableColumn tableColumn = new TableColumn(columnName);
        tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<T, String> param) {
                return new SimpleStringProperty(param.getValue().getValue(columnName));
            }
        });
        tableColumn.setCellFactory(callback);
        table.getColumns().add(tableColumn);
    }

    public void removeAllData(){
        data.removeAll(data);
    }
    public void clearColumnsName(){ columnsName.clear();}
}

package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.Siec6;
import com.Igor.SearchIp.SiecModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public TableView<T> getTable() {
        return table;
    }

    public void setColumnsName(List<String> columnsName) {
        this.columnsName = columnsName;
    }

    public ObservableList<T> getData() {
        return data;
    }

    public TableManager(TableView<T> table) {
        this.table = table;
        this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.table.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        AnchorPane.setTopAnchor(this.table, 0.0);
        AnchorPane.setBottomAnchor(this.table, 0.0);
        AnchorPane.setLeftAnchor(this.table, 0.0);
        AnchorPane.setRightAnchor(this.table, 0.0);
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

            TableColumn tableColumn = new TableColumn(str);
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
        TableColumn<T, String> tableColumn = new TableColumn<>(columnName);
        tableColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue(columnName)));
        Callback<TableColumn<T, String>, TableCell<T, String>> cellFactory = //
                new Callback<TableColumn<T, String>, TableCell<T, String>>()
                {
                    @Override
                    public TableCell call( final TableColumn<T, String> param )
                    {
                        final TableCell<T, String> cell = new TableCell<T, String>()
                        {
                            final ComboBox<ImageView> btn = new ComboBox<>();
                            @Override
                            public void updateItem( String item, boolean empty )
                            {
                                super.updateItem( item, empty );
                                if ( empty )
                                {
                                    setGraphic( null );
                                    setText( null );
                                }
                                else
                                {
                                    btn.setMaxWidth(50);
                                    btn.getItems().clear();
                                    btn.getItems().addAll(
                                            new ImageView(new Image("Icons/plus.png")),
                                            new ImageView(new Image("Icons/close_network.png")),
                                            new ImageView(new Image("Icons/network.png"))
                                    );
                                    btn.setCellFactory(new Callback<ListView<ImageView>, ListCell<ImageView>>() {
                                        @Override public ListCell<ImageView> call(ListView<ImageView> p) {
                                            return new ListCell<ImageView>() {
                                                private final ImageView rectangle;
                                                {
                                                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                                    rectangle = new ImageView();
                                                }
                                                @Override protected void updateItem(ImageView item, boolean empty) {
                                                    super.updateItem(item, empty);

                                                    if (item == null || empty) {
                                                        setGraphic(null);
                                                    } else {
                                                        rectangle.setImage(item.getImage());
                                                        setGraphic(rectangle);
                                                    }
                                                }
                                            };
                                        }
                                    });
                                    btn.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                                            Siec6 siec = (Siec6) getTableView().getItems().get(getIndex());
                                            siec.setStatus(newValue.intValue() == 0 ? "n" : newValue.intValue() == 1 ? "z" : "");
                                    });
                                    btn.getSelectionModel().select(1);
                                    setGraphic( btn );
                                    setText( null );
                                }
                            }
                        };
                        return cell;
                    }
                };

        switch (columnName) {
            case "status":
                tableColumn.setCellFactory(cellFactory);
                break;
            case "priority":
                tableColumn.setCellFactory(column -> new TableCell<T, String>() {
                    final ComboBox<String> btn = new ComboBox<>();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            btn.getItems().clear();
                            btn.getItems().addAll("1", "2", "3", "4", "5");
                            btn.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                                Siec6 siec = (Siec6) getTableView().getItems().get(getIndex());
                                siec.setPriority(newValue);
                            });
                            btn.getSelectionModel().select(4);
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                });
                break;
            case "date":
                tableColumn.setCellFactory(column -> new TableCell<T, String>() {
                    final DatePicker datePicker = new DatePicker();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            String pattern = "yyyy-MM-dd";
                            StringConverter converter = new StringConverter<LocalDate>() {
                                DateTimeFormatter dateFormatter =
                                        DateTimeFormatter.ofPattern(pattern);

                                @Override
                                public String toString(LocalDate date) {
                                    if (date != null) {
                                        return dateFormatter.format(date);
                                    } else {
                                        return "";
                                    }
                                }

                                @Override
                                public LocalDate fromString(String string) {
                                    if (string != null && !string.isEmpty()) {
                                        return LocalDate.parse(string, dateFormatter);
                                    } else {
                                        return null;
                                    }
                                }
                            };
                            datePicker.setConverter(converter);
                            datePicker.setPromptText(pattern.toLowerCase());
                            datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                                Siec6 siec = (Siec6) getTableView().getItems().get(getIndex());
                                siec.setDate(newValue.toString());
                            });
                            datePicker.setValue(LocalDate.now());
                            getTableView().getItems().get(getIndex()).setValue("date", LocalDate.now().toString());
                            setGraphic(datePicker);
                            setText(null);
                        }
                    }
                });
                break;
            default:
                tableColumn.setCellFactory(callback);
                break;
        }
        table.getColumns().add(tableColumn);
    }

    public void addColumns(TableColumn column){
        table.getColumns().add(column);
    }

    public void removeAllData(){
        data.removeAll(data);
    }
    public void clearColumnsName(){ columnsName.clear();}
}

package com.Igor.SearchIp;

import com.Igor.SearchIp.Containers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.tools.jar.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by igor on 02.08.16.
 */
public class Controller {
    CSVManager manager;
    TableManager<Siec6> tableManager;
    Diagram diagram;
    SiecBox siecBox;

    @FXML
    private PieChart pieChartCountIp;
    @FXML
    private TableView<Siec6> table;
    @FXML
    private VBox VBoxMain;
    @FXML
    private TextField textField_Find;
    @FXML
    private ChoiceBox choiceBox;
    @FXML
    private CheckBox CHSetSelect;
    @FXML
    private HBox HBoxFind;
    @FXML
    private VBox MainBoxDivide;
    @FXML
    private Button okButton;
    @FXML
    private Button ApplayButton;
    @FXML
    private ListView<Siec6> ListSiecToDivide;
    @FXML
    private ObservableList<Siec6> listToDivide = FXCollections.observableArrayList();

    @FXML
    public void openFile(ActionEvent actionEvent) {

        table.setEditable(true);
        table.getColumns().removeAll(table.getColumns());
        tableManager.removeAllData();

        pieChartCountIp.dataProperty().get().removeAll(pieChartCountIp.dataProperty().get());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open .csv file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV File", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if(selectedFile != null){
            manager = new CSVManager(selectedFile.getPath(), ',');
            List list = null;
            try {
                list = manager.readData(Siec6.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(list);
            for (String str :
                    new String[]{"address", "mask", "countIp"}){
                tableManager.addColumns(str, TextFieldTableCell.forTableColumn());
            }
            tableManager.addColumns("status", param -> new TableCell<Siec6, String>(){
                VBox vb;
                ImageView imgVw;

                {
                    vb = new VBox();
                    vb.setAlignment(Pos.CENTER);
                    imgVw = new ImageView();
                    imgVw.setFitHeight(20);
                    imgVw.setFitWidth(20);
                    vb.getChildren().addAll(imgVw);
                    setGraphic(vb);
                }
                @Override
                public void updateItem(String item, boolean empty){
                    if(item != null) {
                        if(item.equals("z"))
                            imgVw.setImage(new Image("Icons/locked.png"));
                        if(item.equals("n"))
                            imgVw.setImage(new Image("Icons/locked-1.png"));
                    }
                }
            });
            for (String str :
                    new String[]{"priority", "client", "type"}) {
                tableManager.addColumns(str, TextFieldTableCell.forTableColumn());
            }
            tableManager.setData(list);

            //Find
            choiceBox.setItems(FXCollections.observableArrayList(tableManager.getColumnsName()));


            double wolnych = 0;
            double zajetych = 0;

            for(SiecModel s : tableManager.getData()){
                String str;
                if((str = s.getValue("status")) != null){
                    int n = Integer.parseInt(s.getValue("countIp"));
                    if(str.equals("n"))
                        wolnych += n;
                    if(str.equals("z"))
                        zajetych += n;
                }
            }


            double sto = wolnych + zajetych;

            double wolnev2 = wolnych/sto*100;
            double zajetev2 = zajetych/sto*100;

            ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList(
                            new PieChart.Data("Zajete Ip(" + (int)wolnych + ")", zajetev2),
                            new PieChart.Data("Wolne Ip(" + (int)zajetych + ")", wolnev2));

            diagram.setPieChartData(pieChartData);
            diagram.Drow();

            VBoxMain.setVisible(true);
            for(Node node : HBoxFind.getChildren()){
                node.setDisable(false);
            }

            ListSiecToDivide.getItems().clear();
            listToDivide.clear();

            for(Siec6 siec: tableManager.getData()){
                if(siec.getStatus().equals("n")){
                    listToDivide.add(siec);
                }
            }

            FXCollections.sort(listToDivide, (o1, o2) -> {
                return Integer.parseInt(o2.getPriority()) - Integer.parseInt(o1.getPriority());
            });

        }
    }

    public void clickSave(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");

        File selectedFile = fileChooser.showSaveDialog(null);
        if(selectedFile == null) {
            try {
                manager.writeData(tableManager.getData(), selectedFile.getPath() + ".csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void initialize(){
        tableManager = new TableManager<>(table);
        diagram = new Diagram(pieChartCountIp);
        VBoxMain.setVisible(false);
        if(choiceBox.getItems().size() != 0) choiceBox.getItems().clear();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        for(Node node :HBoxFind.getChildren()){
            node.setDisable(true);
        }

        // Пошук по таблиці
        textField_Find.textProperty().addListener((observable, oldValue, newValue) -> {
            findIntoTable();
        });

        okButton.setGraphic(new ImageView(new Image("Icons/scissors.png")));
        ListSiecToDivide.setItems(listToDivide);
    }

    private void findIntoTable(){
        table.getSelectionModel().clearSelection();
        String str = (String) choiceBox.getSelectionModel().getSelectedItem();
        if(str != null){
            for (Siec6 siec : tableManager.getData()) {
                if(siec.getValue(str).equals(textField_Find.getText())){
                    if(CHSetSelect.isSelected()) {
                        table.getSelectionModel().select(siec);
                    }
                    else {
                        int numberRow = tableManager.getData().indexOf(siec);
                        table.getSelectionModel().select(numberRow, table.getColumns().get(choiceBox.getSelectionModel().getSelectedIndex()));
                    }
                }
            }
        }
    }

    public void clickCHSetSelect(ActionEvent actionEvent) {
        if(CHSetSelect.isSelected()){
            table.getSelectionModel().setCellSelectionEnabled(false);
            findIntoTable();
        }
        else
            table.getSelectionModel().setCellSelectionEnabled(true);
            findIntoTable();
    }

    public void clieckExit() {
        Stage stage = (Stage) table.getScene().getWindow();
        stage.close();
    }


    public void clickButton(ActionEvent actionEvent) {
        if(siecBox != null){
            siecBox.clear();
            MainBoxDivide.getChildren().clear();
        }
        Siec6 siec = ListSiecToDivide.getSelectionModel().getSelectedItem();
        if(siec == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Choose item!");
            alert.showAndWait();
            return;
        }
        VBox.setVgrow(MainBoxDivide, Priority.ALWAYS);
        siecBox = new SiecBox(siec);
        MainBoxDivide.getChildren().add(siecBox.getMainBox());
    }

    public void applyClick() {
        if(siecBox == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("First, divide the network.!");
            alert.showAndWait();
            return;
        }

        List<Siec6> siec6List = new ArrayList<>();

        MainBoxDivide.getChildren().clear();


    }
}

package com.Igor.SearchIp;

import com.Igor.SearchIp.Containers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by igor on 02.08.16.
 */
public class Controller {
    CSVManager manager;
    TableManager<Siec6> tableManager;
    Diagram diagram;
    SiecBox siecBox;

    @FXML
    private AnchorPane scrollPaneAnchorn;
    @FXML
    private Button nextDivideButton;
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

        choiceBox.getItems().clear();
        table.setEditable(true);
        table.getColumns().removeAll(table.getColumns());
        tableManager.clearColumnsName();

        pieChartCountIp.dataProperty().get().removeAll(pieChartCountIp.dataProperty().get());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open .csv file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV File", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if(selectedFile != null){
            manager = new CSVManager(selectedFile.getPath(), ';');
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
                            new PieChart.Data("Used ip addresses(" + (int)wolnych + ")", zajetev2),
                            new PieChart.Data("Unused ip addresses(" + (int)zajetych + ")", wolnev2));

            diagram.setPieChartData(pieChartData);
            diagram.Drow();

            VBoxMain.setVisible(true);
            for(Node node : HBoxFind.getChildren()){
                node.setDisable(false);
            }
            addSiecToListToDivide();;
        }
    }

    public void addSiecToListToDivide(){
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

    public void clickSave(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV File", "*.csv"));

        File selectedFile = fileChooser.showSaveDialog(null);
        if(selectedFile != null) {
            try {
                manager.writeData(tableManager.getData(), selectedFile.getPath());
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
        textField_Find.setPromptText("Enter text...");
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

    public void nextDivide(ActionEvent actionEvent){
        if(siecBox == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("First, divide the network.!");
            alert.showAndWait();
            return;
        }
        nextDivideButton.setDisable(true);
        ApplayButton.setDisable(false);

        List<Siec6> siec6List = new ArrayList<>();

        MainBoxDivide.getChildren().clear();

        SiecBox.searchLastNodes(siecBox, siec6List);
        for (Siec6 siec: siec6List){
            siec.setStatus("z");
        }

        TableView<Siec6> siec6TableView = new TableView<>();
        siec6TableView.setEditable(true);
        TableManager<Siec6> table = new TableManager<>(siec6TableView);

        table.setData(siec6List);

        for(String str : new String[]{"address", "mask", "countIp", "status", "priority"}){
            table.addColumns(str, TextFieldTableCell.forTableColumn());
        }

        table.getTable().getColumns().get(3).setOnEditCommit(event -> {
            String newValue = (String)event.getNewValue();
            if(!newValue.equals("z") && !newValue.equals("n")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Status : {'z' - used, 'n' - unused}!");
                alert.showAndWait();
                event.getTableView().getSelectionModel().select(event.getTablePosition().getRow());
                return;
            }
            event.getTableView().getItems().get(event.getTablePosition().getRow())
                    .setValue(event.getTableColumn().getText(), newValue);
        });

        table.getTable().getColumns().get(4).setOnEditCommit(event -> {
            String newValue = (String)event.getNewValue();
            if(!newValue.equals("1") && !newValue.equals("2") && !newValue.equals("3") &&
                    !newValue.equals("4") && !newValue.equals("5")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Priority : {1-5}!");
                alert.showAndWait();
                event.getTableView().getSelectionModel().select(event.getTablePosition().getRow());
                return;
            }else
            event.getTableView().getItems().get(event.getTablePosition().getRow())
                    .setValue(event.getTableColumn().getText(), newValue);
        });
        int i = 5;
        for(String str : new String[] {"client", "type"}){
            table.addColumns(str, TextFieldTableCell.forTableColumn());
            table.getTable().getColumns().get(i).setOnEditCommit(event -> {
                event.getTableView().getItems().get(event.getTablePosition().getRow())
                        .setValue(event.getTableColumn().getText(), (String) event.getNewValue());
            });
            i++;
        }
        i = 0;
        for (; i < 3;i++){
            table.getTable().getColumns().get(i).setEditable(false);
        }

//        MainBoxDivide.getChildren().add(table.getTable());
        scrollPaneAnchorn.getChildren().clear();
        scrollPaneAnchorn.getChildren().add(table.getTable());
        ApplayButton.setOnAction(event -> {
            for(int j = 0; j < table.getData().size(); j++){
                if(table.getData().get(j).getPriority().isEmpty() ||
                        table.getData().get(j).getClient().isEmpty() ||
                        table.getData().get(j).getType().isEmpty()){

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("Some data have not been filled!");
                    alert.setContentText("Are you ok with this?");
                    Optional<ButtonType> result = alert.showAndWait();
                    table.getTable().getSelectionModel().select(table.getData().get(j));
                    if(result.get() == ButtonType.CANCEL){
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Information");
                        alert1.setContentText("Cancel recording!");
                        alert1.showAndWait();
                        return;
                    }
                }
            }

            tableManager.getData().remove(siecBox.getData());
            tableManager.getData().addAll(table.getData());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("New networks was added!");
            alert.showAndWait();
            addSiecToListToDivide();
        });

    }

    public void clickButton(ActionEvent actionEvent) {
        scrollPaneAnchorn.getChildren().clear();
        ApplayButton.setDisable(true);
        nextDivideButton.setDisable(false);
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
        siecBox = new SiecBox(siec, null);
        MainBoxDivide.getChildren().add(siecBox.getMainBox());
        scrollPaneAnchorn.getChildren().add(MainBoxDivide);
    }

}

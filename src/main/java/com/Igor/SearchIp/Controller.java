package com.Igor.SearchIp;

import com.Igor.SearchIp.Containers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 * Created by igor on 02.08.16.
 */
public class Controller {
    private CSVManager manager;
    private Diagram diagram;
    private SiecBox siecBox;

    private  TreeViewManager treeViewManager;

    @FXML
    private AnchorPane secondAnchornPane;
    @FXML
    private AnchorPane scrollPaneAnchorn;
    @FXML
    private Button nextDivideButton;
    @FXML
    private PieChart pieChartCountIp;
    @FXML
    private TreeView<Siec6> treeView;
    @FXML
    private VBox VBoxMain;
    @FXML
    private TextField textField_Find;
    @FXML
    private ChoiceBox choiceBox;
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

        pieChartCountIp.dataProperty().get().removeAll(pieChartCountIp.dataProperty().get());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open .csv file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV File", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if(selectedFile != null){
            manager = new CSVManager(selectedFile.getPath(), ';');
            List<Siec6> list = null;
            try {
                list = manager.readData(Siec6.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(list);

            treeViewManager.setData(list);
            treeViewManager.upload();


            double wolnych = 0;
            double zajetych = 0;

            for(SiecModel s : list){
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
            addSiecToListToDivide();
        }
    }

    public void addSiecToListToDivide(){
        ListSiecToDivide.getItems().clear();
        listToDivide.clear();

        for(Siec6 siec: treeViewManager.getData()){
            if(siec.getStatus() != null)
                if(siec.getStatus().equals("n")){
                    listToDivide.add(siec);
                }
        }

        FXCollections.sort(listToDivide, (o1, o2) -> Integer.parseInt(o2.getPriority()) - Integer.parseInt(o1.getPriority()));
    }

    public void clickSave(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV File", "*.csv"));

        File selectedFile = fileChooser.showSaveDialog(null);
        if(selectedFile != null) {
            try {
                if(manager == null){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("You have nothing to save!");
                    alert.showAndWait();
                    return;
                }
                manager.writeData(treeViewManager.getData(), selectedFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void initialize(){
        treeViewManager = new TreeViewManager(treeView);
        treeViewManager.getTreeView().getSelectionModel().setSelectionMode(MULTIPLE);
        diagram = new Diagram(pieChartCountIp);
        VBoxMain.setVisible(false);
        if(choiceBox.getItems().size() != 0) choiceBox.getItems().clear();

        for(Node node :HBoxFind.getChildren()){
            node.setDisable(true);
        }

        textField_Find.textProperty().addListener((observable, oldValue, newValue) -> findSiec(newValue));

        choiceBox.setItems(FXCollections.observableArrayList(new String[]{
            "address", "mask", "countIp", "status", "priority", "client", "type"
        }));

        // Пошук ЗРОБИТИ

        okButton.setGraphic(new ImageView(new Image("Icons/scissors.png")));
        ListSiecToDivide.setItems(listToDivide);
        ListSiecToDivide.setCellFactory(param -> new ListCell<Siec6>(){
            @Override
            protected void updateItem(Siec6 item, boolean empty) {
                super.updateItem(item, empty);
                if (isEmpty()) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (item == null || empty) {
                    setText(null);
                } else {
                    HBox hBox = new HBox(10);
                    Label labelAddrMaskCount = new Label(item.getAddress() + " [" + item.getMask() + "] {" + item.getCountIp() + "}");
                    String url = "Icons/battery_unknow.png";
                    if(item.getPriority() != null || item.getPriority().isEmpty())
                        switch (Integer.parseInt(item.getPriority())){
                            case 1: url = "Icons/battery_broke.png";
                                break;
                            case 2: url = "Icons/battery-1.png";
                                break;
                            case 3: url = "Icons/battery-2.png";
                                break;
                            case 4: url = "Icons/battery-3.png";
                                break;
                            case 5: url = "Icons/battery.png";
                                break;
                        }
                    Label image = new Label(null, new ImageView(new Image(url)));
                    hBox.getChildren().addAll(labelAddrMaskCount, image);
                    setText(null);
                    setGraphic(hBox);
                }
            }
        });

        textField_Find.setPromptText("Enter text...");
    }

    public void findSiec(String newValue){
        treeViewManager.getTreeView().getSelectionModel().clearSelection();
        String str = (String) choiceBox.getSelectionModel().getSelectedItem();
        if(str != null){
            treeViewManager.selectItems(treeViewManager.getRootNode(), str, newValue);
        }
    }

    public void clieckExit() {
        Stage stage = (Stage) treeView.getScene().getWindow();
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
            if(siec.getStatus().isEmpty())
                siec.setStatus("z");
        }

        System.out.println(siec6List);

        TableView<Siec6> siec6TableView = new TableView<>();
        siec6TableView.setEditable(true);
        TableManager<Siec6> table = new TableManager<>(siec6TableView);

        table.setData(siec6List);

        for(String str : new String[]{"address", "mask", "countIp", "status", "priority"}){
            table.addColumns(str, TextFieldTableCell.forTableColumn());
        }

        int i = 5;
        for(String str : new String[] {"client", "type"}){
            table.addColumns(str, TextFieldTableCell.forTableColumn());
            table.getTable().getColumns().get(i).setOnEditCommit(event -> event.getTableView().getItems().get(event.getTablePosition().getRow())
                    .setValue(event.getTableColumn().getText(), (String) event.getNewValue()));
            i++;
        }
        table.addColumns("date", TextFieldTableCell.forTableColumn());

        i = 0;
        for (; i < 3;i++){
            table.getTable().getColumns().get(i).setEditable(false);
        }

        scrollPaneAnchorn.getChildren().clear();
        scrollPaneAnchorn.getChildren().add(table.getTable());

        ApplayButton.setOnAction(event -> {
            for(int j = 0; j < table.getData().size(); j++){
                if(table.getData().get(j).getPriority() == null || table.getData().get(j).getPriority().isEmpty() ||
                        table.getData().get(j).getClient() == null || table.getData().get(j).getClient().isEmpty() ||
                        table.getData().get(j).getType() == null || table.getData().get(j).getType().isEmpty()){

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

            int index = treeViewManager.getData().indexOf(siecBox.getData());

            if(table.getData().size() == 1){
                if(table.getData().get(0).getAddress().equals(siecBox.getData().getAddress()) &&
                        table.getData().get(0).getCountIp().equals(siecBox.getData().getCountIp())){
                    treeViewManager.getData().remove(index);
                }
            }else {
                treeViewManager.getData().get(index).setStatus("");
                treeViewManager.getData().get(index).setPriority("");
                treeViewManager.getData().get(index).setClient("");
                treeViewManager.getData().get(index).setType("");
            }
                treeViewManager.getData().addAll(table.getData());
                treeViewManager.upload();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("New networks was added!");
            alert.showAndWait();
            addSiecToListToDivide();
            table.getTable().setDisable(true);
            ApplayButton.setDisable(true);
        });

    }

    public void clickButton(ActionEvent actionEvent) {
        Siec6 siec = ListSiecToDivide.getSelectionModel().getSelectedItem();
        if(siec == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Choose item!");
            alert.showAndWait();
            return;
        }
        scrollPaneAnchorn.getChildren().clear();
        ApplayButton.setDisable(true);
        nextDivideButton.setDisable(false);
        if(siecBox != null){
            siecBox.clear();
            MainBoxDivide.getChildren().clear();
        }
        VBox.setVgrow(MainBoxDivide, Priority.ALWAYS);
        siecBox = new SiecBox(siec, null);
        MainBoxDivide.getChildren().add(siecBox.getMainBox());
        scrollPaneAnchorn.getChildren().add(MainBoxDivide);
        treeViewManager.getTreeView().getSelectionModel().clearSelection();
        treeViewManager.selectItem(treeViewManager.getRootNode(), siec);
    }

    public void aboutProgram() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(null);
        VBox dialogVbox = new VBox();
        VBox.setVgrow(dialogVbox, Priority.ALWAYS);
        Label label = new Label("Ip Divider\nBuilt on August, 2016.");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(10,10,10,10));
        label.setFont(new Font("Arial", 16));
        label.setAlignment(Pos.CENTER);
        dialogVbox.getChildren().add(label);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

}

package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.Containers.Callbacks.ComboBoxCallback;
import com.Igor.SearchIp.MyMath;
import com.Igor.SearchIp.Siec6;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Игорь on 11.08.2016.
 */


@SuppressWarnings("unchecked")
public class TreeViewManager {
    private TreeItem<Siec6> rootNode =
            new TreeItem<>(new Siec6("Main", "", "", "", "", "", "", ""));
    private TreeView<Siec6> treeView;
    private HBox editPanel;
    private List<Siec6> data;
    private Siec6 currentEditSiec;
    public TreeView<Siec6> getTreeView() {
        return treeView;
    }

    public TreeViewManager(TreeView treeView, HBox editPanel) {
        this.editPanel = editPanel;
        final ContextMenu contextMenu = new ContextMenu();

        Menu newItem = new Menu("New");
        MenuItem addHomeSiec = new MenuItem("Home network", new ImageView(new Image("Icons/network.png")));
        MenuItem addFreeSiec = new MenuItem("Free network", new ImageView(new Image("Icons/open_network.png")));
        MenuItem addBusySiec = new MenuItem("Busy network", new ImageView(new Image("Icons/close_network.png")));

        newItem.getItems().addAll(addHomeSiec, addFreeSiec, addBusySiec);
        contextMenu.setOnShowing(observable -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
            if(siec == rootNode){
                new MyLittleAlert(Alert.AlertType.INFORMATION, "Information", "This is main node", "").showAndWait();
                return;
            }
            if(siec != null){
                if(siec.getValue().getStatus() != null &&
                        !siec.getValue().getStatus().equals("")){
                    newItem.hide();
                    newItem.setDisable(true);
                }else newItem.setDisable(false);
            }
        });

        addHomeSiec.setOnAction(event -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
            if(siec != null){
                new AddSiecDialog(siec, this, AddSiecDialog.NETWORK_TYPE.HOME_NETWORK).show();
            }else
                new AddSiecDialog(rootNode, this, AddSiecDialog.NETWORK_TYPE.HOME_NETWORK).show();
        });
        addFreeSiec.setOnAction(event -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
            if(siec != null){
                new AddSiecDialog(siec, this, AddSiecDialog.NETWORK_TYPE.FREE_NETWORK).show();
            }else
                new AddSiecDialog(rootNode, this, AddSiecDialog.NETWORK_TYPE.FREE_NETWORK).show();
        });
        addBusySiec.setOnAction(event -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
            if(siec != null){
                new AddSiecDialog(siec, this, AddSiecDialog.NETWORK_TYPE.BUSY_NETWORK).show();
            }else
                new AddSiecDialog(rootNode, this, AddSiecDialog.NETWORK_TYPE.BUSY_NETWORK).show();
        });

        MenuItem margeIntoOne = new MenuItem("Marge...");
        margeIntoOne.setOnAction(event -> {
            ObservableList<TreeItem<Siec6>> observableList = getTreeView().getSelectionModel().getSelectedItems();
            if(observableList.size() == 0){
                new MyLittleAlert(Alert.AlertType.INFORMATION, "Information", "Select item!", "").showAndWait();
                return;
            }
            if(observableList.size() == 1){
                new MyLittleAlert(Alert.AlertType.WARNING, "Waring", "You can not marge 1 network", "").showAndWait();
                return;
            }

            // Перевірити чи знаходяиться в одній підсети
            TreeItem<Siec6> parrent = observableList.get(0).getParent();
            int count = 0;
            for (TreeItem<Siec6> obj :
                    observableList) {
                if(obj.getParent() != parrent){
                    new MyLittleAlert(Alert.AlertType.WARNING, "Waring","These networks must be in the same home network", "").showAndWait();
                    return;
                }
                if(obj.getValue().getStatus() == null || obj.getValue().getStatus().equals("")){
                    new MyLittleAlert(Alert.AlertType.WARNING, "Waring", "You can not marge with home network", "").showAndWait();
                    return;
                }
                count += Integer.parseInt(obj.getValue().getCountIp());
            }

            if(!MyMath.isDivideBy2Entirely(count)){
                new MyLittleAlert(Alert.AlertType.WARNING, "Waring", "You can not create a network consisting of " + count +" computers.", "").showAndWait();
                return;
            }

            List<Siec6> list = new ArrayList<>();
            for (TreeItem<Siec6> item1 : observableList){
                list.add(item1.getValue());
            }
            Collections.sort(list, (o1, o2) ->
                    Siec6.isBigger(o1.getAddress(), o2.getAddress()) ? 1 : -1);

            for (Siec6 s :
                    list) {
                System.out.println(s);
            }

            for (int i = 0; i < list.size()-1; i++){
                Siec6 siec6 = list.get(i);
                Siec6 siec6_2 = list.get(i+1);
                String str = Siec6.generatedIpSiec(siec6.getAddress(), Integer.parseInt(siec6.getCountIp()));
                System.out.println("str = " + str + ", str2 = " + siec6_2.getAddress());
                if(!str.equals(siec6_2.getAddress())){
                    new MyLittleAlert(Alert.AlertType.WARNING, "Waring", "There is a space or other network between networks", "").showAndWait();
                    return;
                }
            }

            new MargeSiecDialog(parrent,this, observableList).show();

        });

        MenuItem deleteItem = new MenuItem("Delete...");
        deleteItem.setOnAction(e -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
//            TreeItem<Siec6> parrentSiec = siec.getParent();
            if(siec != null){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Delete network: " + siec.getValue());
                alert.setContentText("Are you sure?");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    remove(siec);
                    siec.getParent().getChildren().remove(siec);
                    treeView.getSelectionModel().clearSelection();
//                    upload();
//                    selectItem(rootNode, parrentSiec.getValue());
                }
            }
        });

        contextMenu.getItems().addAll(newItem, new SeparatorMenuItem(), margeIntoOne, deleteItem);

        rootNode.setExpanded(true);
        this.treeView = treeView;
        this.treeView.setRoot(rootNode);
        treeView.setContextMenu(contextMenu);

        treeView.setCellFactory(e -> new CustomCell());
        ComboBox<ImageView> statusComboBox = (ComboBox<ImageView>) editPanel.getChildren().get(3);
        statusComboBox.getItems().addAll(
                new ImageView(new Image("Icons/plus.png")),
                new ImageView(new Image("Icons/close_network.png")),
                new ImageView(new Image("Icons/network.png"))
        );
        statusComboBox.setCellFactory(new ComboBoxCallback());

        DatePicker datePicker = (DatePicker) editPanel.getChildren().get(7);
        datePicker.setPromptText("yyyy-MM-dd".toLowerCase());
        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        ComboBox<String> priorityComboBox = (ComboBox<String>) editPanel.getChildren().get(4);
        priorityComboBox.getItems().addAll("1", "2", "3", "4", "5");

        Button saveButton = (Button) editPanel.getChildren().get(editPanel.getChildren().size()-1);
        saveButton.setOnAction(event -> {
            if(currentEditSiec == null)
                return;
            int selectedItem = statusComboBox.getSelectionModel().getSelectedIndex();
            currentEditSiec.setStatus(selectedItem == 0 ? "n" : selectedItem == 1 ? "z" : "");
            currentEditSiec.setPriority(priorityComboBox.getSelectionModel().getSelectedItem());
            currentEditSiec.setClient(((TextField)editPanel.getChildren().get(5)).getText());
            currentEditSiec.setType(((TextField)editPanel.getChildren().get(6)).getText());
            currentEditSiec.setDate(((DatePicker)editPanel.getChildren().get(7)).getConverter().toString());
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentEditSiec = ((TreeItem<Siec6>)newValue).getValue();
            ((TextField)editPanel.getChildren().get(0)).setText(currentEditSiec.getAddress());
            ((TextField)editPanel.getChildren().get(1)).setText(currentEditSiec.getMask());
            ((TextField)editPanel.getChildren().get(2)).setText(currentEditSiec.getCountIp());
            ((ComboBox<ImageView>) editPanel.getChildren().get(3)).getSelectionModel().select(
                    currentEditSiec.getStatus() == null || currentEditSiec.getStatus().isEmpty() ?
            2 : currentEditSiec.getStatus().equals("z") ? 1 : 0);
            ((ComboBox<String>) editPanel.getChildren().get(4)).getSelectionModel().select(
                    currentEditSiec.getPriority() == null || currentEditSiec.getPriority().isEmpty() ? 0 : Integer.parseInt(currentEditSiec.getPriority())-1);
            ((TextField)editPanel.getChildren().get(5)).setText(currentEditSiec.getClient());
            ((TextField)editPanel.getChildren().get(6)).setText(currentEditSiec.getType());
            ((DatePicker)editPanel.getChildren().get(7)).setValue(((DatePicker)editPanel.getChildren().get(7)).getConverter().fromString(currentEditSiec.getDate()));
        });
    }


    public void setData(List<Siec6> Siec6List){

        data = Siec6List;
    }

    public List<Siec6> getData() {
        return data;
    }

    public void upload() {
        Collections.sort(data,(o1, o2) -> o1.isBigger(o2));
        rootNode.getChildren().clear();
        for (Siec6 siec :
                data) {
            TreeItem<Siec6> empLeaf = new TreeItem<>(siec);
            TreeItem<Siec6> node = searchParent(rootNode, siec);
            node.getChildren().add(empLeaf);
        }
    }

    private TreeItem<Siec6> searchParent(TreeItem<Siec6> node, Siec6 siec){
        for (TreeItem<Siec6> depNode : node.getChildren()){
            if(siec.thisIsParentNetwortk(depNode.getValue())){
                return searchParent(depNode, siec);
            }
        }
        return node;
    }

    public TreeItem<Siec6> find(TreeItem<Siec6> startNode,Siec6 siec){
        for(TreeItem<Siec6> depNode : startNode.getChildren()){
            if(depNode.getValue().equals(siec)){
                return depNode;
            }else{
                TreeItem<Siec6> node = find(depNode, siec);
            if(node == null)
                continue;
            else return node;
            }
        }
        return null;
    }

    public TreeItem<Siec6> find(TreeItem<Siec6> startNode, String column, String value){

        for(TreeItem<Siec6> depNode : startNode.getChildren()){
            if(depNode.getValue().getValue(column).equals(value)){
                return depNode;
            }else {
                TreeItem<Siec6> node = find(depNode, column, value);
                if(node == null)
                    continue;
                else return node;
            }
        }
        return null;
    }

    public void selectItems(TreeItem<Siec6> start, String key, String value){
        for(TreeItem<Siec6> depNode : start.getChildren()){
            if(depNode.getValue().getValue(key) != null){
                if(depNode.getValue().getValue(key).equals(value))
                    treeView.getSelectionModel().select(depNode);
            }
            selectItems(depNode, key, value);
        }
    }
    public void selectItem(TreeItem<Siec6> start, Siec6 siec){
        for(TreeItem<Siec6> depNode : start.getChildren()){
            if(depNode.getValue() == siec){
                treeView.getSelectionModel().select(depNode);
                return;
            }
            selectItem(depNode, siec);
        }
    }

    public void remove(TreeItem<Siec6> start){
        for(TreeItem<Siec6> depNode : start.getChildren()){
            data.remove(depNode.getValue());
            remove(depNode);
        }
        data.remove(start.getValue());
    }

    public static void getFreeSiecs(TreeItem<Siec6> treeItem, List list){
        for (int i = 0; i < treeItem.getChildren().size(); i++) {
            getFreeSiecs(treeItem.getChildren().get(i), list);
        }

        if(treeItem.getChildren().size() == 0 && (treeItem.getValue().getStatus() == null || treeItem.getValue().getStatus().isEmpty())) {
            list.add(new Siec6(treeItem.getValue().getAddress(), treeItem.getValue().getMask(), treeItem.getValue().getCountIp(),
                    "", "", "", "", ""));
        }else {
            List<Siec6> siec6s = new ArrayList<>();
            for(int i = 0; i < treeItem.getChildren().size(); i++){
                siec6s.add(treeItem.getChildren().get(i).getValue());
            }

            Collections.sort(siec6s, (o1, o2) -> Siec6.isBigger(o1,o2));

            for (int i = 0; i < siec6s.size(); i++) {
                Siec6 siec = siec6s.get(i);
                Siec6 siec2;

                if (i == 0) {
                    if (!treeItem.getValue().getAddress().equals(siec.getAddress())) {
                        list.add(new Siec6(treeItem.getValue().getAddress(), "",
                                String.valueOf(Siec6.minus(siec.getAddress(), treeItem.getValue().getAddress()))));
                    }
                }
                if (i == siec6s.size() - 1) {
                    siec2 = new Siec6(Siec6.generatedIpSiec(treeItem.getValue().getAddress(),
                            Integer.parseInt(treeItem.getValue().getCountIp())), "", "", "", "", "", "", "");
                    if (Siec6.generatedIpSiec(siec.getAddress(), Integer.parseInt(siec.getCountIp())).equals(
                            Siec6.generatedIpSiec(treeItem.getValue().getAddress(), Integer.parseInt(treeItem.getValue().getCountIp()))))
                        break;
                } else
                    siec2 = siec6s.get(i+1);

                if (Siec6.generatedIpSiec(siec.getAddress(), Integer.parseInt(siec.getCountIp())).equals(siec2.getAddress())) {
                    continue;
                }
                String addr1 = Siec6.generatedIpSiec(siec.getAddress(), Integer.parseInt(siec.getCountIp()));
                list.add(new Siec6(addr1, "",
                        String.valueOf(Siec6.minus(siec2.getAddress(), addr1)), "", "", "", "", ""));
            }
        }
    }

    public TreeItem<Siec6> getRootNode() {
        return rootNode;
    }

    class CustomCell extends TreeCell<Siec6> {
        @Override
        protected void updateItem(Siec6 item, boolean empty) {
            super.updateItem(item, empty);

            // If the cell is empty we don't show anything.
            if (isEmpty()) {
                setGraphic(null);
                setText(null);
            } else {

                HBox cellBox = new HBox(10);
                Label labelAddMasCount = new Label(item.getAddress() + " [" + item.getMask() + "]   {"+item.getCountIp()+"}");
                Label labelOpenInfo = new Label(null, new ImageView(new Image("Icons/more.png")));
                Label labelInfo = new Label();
                Label icon;
                String url;
                if(item.getStatus() == null || item.getStatus().isEmpty()){
                    url = "Icons/network.png";
                    cellBox.setStyle("-fx-border-color: darkgrey;");
                }else
                if(item.getStatus().equals("n")) {
//                    labelAddMasCount.setStyle("-fx-background-color: #CEFFCE;");
                    url = "Icons/open_network.png";
                }else {
//                    labelAddMasCount.setStyle("-fx-background-color: #FFE2E2;");
                    url = "Icons/close_network.png";
                }
                icon = new Label(null, new ImageView(new Image(url)));
                labelOpenInfo.setOnMouseClicked(event ->{
                    if(labelInfo.getText().isEmpty()){
                        labelInfo.setText("{'priority='" + item.getPriority() + "', client='"+item.getClient()+
                                "', type='"+item.getType()+"', date='"+ item.getDate() +"'}");
                    }else
                        labelInfo.setText("");
                });
                cellBox.getChildren().addAll(icon, labelAddMasCount);
                if(getTreeItem().getValue().getStatus() == null ||
                        getTreeItem().getValue().getStatus().equals("")) {
                    List<Siec6> list = new ArrayList<>();
                    TreeViewManager.getFreeSiecs(getTreeItem(), list);
                    int n = 0;
                    for (Siec6 siec : list) {
                        n += Integer.parseInt(siec.getCountIp());
                    }
                    Label countFreeIp = new Label(String.valueOf(n));
                    countFreeIp.setAlignment(Pos.CENTER);
                    countFreeIp.setFont(new Font("System", 14));
                    if (n > 0)
//                        countFreeIp.setStyle("-fx-background-color: greenyellow; -fx-background-radius: 5em;");
                        countFreeIp.setStyle("-fx-background-color: greenyellow; ");
                    else
//                        countFreeIp.setStyle("-fx-background-color: tomato; -fx-background-radius: 5em;");
                        countFreeIp.setStyle("-fx-background-color: tomato; ");
                    cellBox.getChildren().add(countFreeIp);
                }
                 cellBox.getChildren().addAll(labelOpenInfo, labelInfo);
                // We set the cellBox as the graphic of the cell.
                setGraphic(cellBox);
                setText(null);

            }
        }
    }
}

package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.Siec6;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Игорь on 11.08.2016.
 */


public class TreeViewManager {
    private TreeItem<Siec6> rootNode =
            new TreeItem<>(new Siec6("Main", "", "", "", "", "", "", ""));
    TreeView<Siec6> treeView;

    private List<Siec6> data;

    public TreeView<Siec6> getTreeView() {
        return treeView;
    }

    public TreeViewManager(TreeView treeView) {

        final ContextMenu contextMenu = new ContextMenu();
        contextMenu.setOnShowing(event -> System.out.println("Showing"));
        contextMenu.setOnShown(event -> System.out.println("Show"));

        MenuItem item1 = new MenuItem("Add");
        item1.setOnAction(e -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
            if(siec != null){
                new AddSiecDialog(siec, this).show();
            }else
                new AddSiecDialog(rootNode, this).show();
        });
        MenuItem item2 = new MenuItem("Delete");
        item2.setOnAction(e -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
            TreeItem<Siec6> parrentSiec = siec.getParent();
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
                    upload();
                }
            }
        });
        contextMenu.getItems().addAll(item1, item2);

        rootNode.setExpanded(true);
        this.treeView = treeView;
        this.treeView.setRoot(rootNode);
        treeView.setContextMenu(contextMenu);

        treeView.setCellFactory(e -> new CustomCell());
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
            TreeItem<Siec6> empLeaf = new TreeItem<Siec6>(siec);
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
//                    labelAddMasCount.setStyle("-fx-background-color: #F7FFFE;");
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
                        labelInfo.setText("{client='"+item.getClient()+
                                "', type='"+item.getType()+"', priority='" + item.getPriority()+"', date='"+ item.getDate() +"'}");
                    }else
                        labelInfo.setText("");
                });
                 cellBox.getChildren().addAll(icon, labelAddMasCount, labelOpenInfo, labelInfo);

                // We set the cellBox as the graphic of the cell.
                setGraphic(cellBox);
                setText(null);

            }
        }
    }
}

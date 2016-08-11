package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.Siec6;
import com.sun.org.apache.bcel.internal.generic.LADD;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.WindowEvent;
import sun.reflect.generics.tree.Tree;

import java.util.Collections;
import java.util.List;

/**
 * Created by Игорь on 11.08.2016.
 */


public class TreeViewManager {
    private TreeItem<Siec6> rootNode =
            new TreeItem<Siec6>(new Siec6("Main", "", "", "", "", "",""));
    TreeView<Siec6> treeView;

    private List<Siec6> data;

    public TreeView<Siec6> getTreeView() {
        return treeView;
    }

    public TreeViewManager(TreeView treeView) {

        final ContextMenu contextMenu = new ContextMenu();
        contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                System.out.println("Showing");
            }
        });
        contextMenu.setOnShown(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                System.out.println("Show");
            }
        });

        MenuItem item1 = new MenuItem("Add");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("About");
            }
        });
        MenuItem item2 = new MenuItem("Delete");
        item2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("Preferences");
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
                if(item.getStatus() == null){
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
                                "', type='"+item.getType()+"', priority='" + item.getPriority()+"'}");
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

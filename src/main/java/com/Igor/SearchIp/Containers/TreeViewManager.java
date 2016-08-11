package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.Siec6;
import com.sun.org.apache.bcel.internal.generic.LADD;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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

        MenuItem item1 = new MenuItem("About");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("About");
            }
        });
        MenuItem item2 = new MenuItem("Preferences");
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

        System.out.println("Pered sort: \n");
        for (Siec6 net :
                data) {
            System.out.println(net);
        }

        Collections.sort(data,(o1, o2) -> o1.isBigger(o2));

        System.out.println("After sort: \n");
        for (Siec6 net :
                data) {
            System.out.println(net);
        }
        System.out.println();
    }

    public List<Siec6> getData() {
        return data;
    }

    public void upload() {
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
                // We only show the custom cell if it is a leaf, meaning it has
                // no children.
                if (this.getTreeItem().isLeaf()) {

                    // A custom HBox that will contain your check box, label and
                    // button.
                    HBox cellBox = new HBox(10);
                    if(item.getStatus().equals("n"))
                        cellBox.setStyle("-fx-border-color: lime; -fx-border-style: dotted; -fx-border-width: 2px;");
                    else
                        cellBox.setStyle("-fx-border-color: crimson; -fx-border-style: dotted; -fx-border-width: 2px;");

                    Label label = new Label(item.getAddress() + "/" + item.getMask() + "/{"+item.getCountIp()+"}");
                    Label label1 = new Label();
                    Button button = new Button(">");
                    button.setOnAction(event -> {
                        label1.setText(item.toString());
                    });

                    cellBox.getChildren().addAll(label, button, label1);

                    // We set the cellBox as the graphic of the cell.
                    setGraphic(cellBox);
                    setText(null);
                } else {
                    HBox cellBox = new HBox(10);
                    Label label = new Label(item.getAddress() + "/" + item.getMask());
                    cellBox.getChildren().add(label);
                    setGraphic(cellBox);
                    setText(null);
                }
            }
        }
    }
}

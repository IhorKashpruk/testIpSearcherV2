package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.MyMath;
import com.Igor.SearchIp.Siec6;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Игорь on 18.08.2016.
 */
public class MargeSiecDialog {
    private final Stage dialog;
    private VBox leftBox;
    private HBox mainBox;
    private VBox ridthBox;
    private ObservableList<TreeItem<Siec6>> observableList;
    private TreeItem<Siec6> itemParent;
    private TreeViewManager manager;

    public MargeSiecDialog(TreeItem<Siec6> itemParent, TreeViewManager manager, ObservableList observableList) {
        this.manager = manager;
        this.itemParent = itemParent;
        this.observableList = observableList;
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(null);

        createElement();

        Scene dialogScene = new Scene(mainBox, 900, 140);
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.setTitle("Add network");
        dialog.initStyle(StageStyle.UTILITY);
    }

    private void createElement(){
        mainBox = new HBox();

        List<Siec6> list = new ArrayList<>();
        for (TreeItem<Siec6> item1 : observableList){
            list.add(item1.getValue());
        }
        Collections.sort(list, (o1, o2) ->
                Siec6.isBigger(o1.getAddress(), o2.getAddress()) ? 1 : -1);

        ListView<Siec6> leftNetworks = new ListView<>();
        leftNetworks.setStyle("-fx-background: white;");
        leftNetworks.setMinWidth(200);
        leftNetworks.setItems(FXCollections.observableArrayList(list));


        leftBox = new VBox(5);
        VBox.setVgrow(leftBox, Priority.ALWAYS);
        Label textNetworks = new Label("Networks: ");
        textNetworks.setFont(new Font("System", 14));

        leftBox.getChildren().addAll(textNetworks, leftNetworks);

        ridthBox = new VBox();
        Label titleText = new Label("Enter data...");
        titleText.setPadding(new Insets(10,10,10,10));
        titleText.setFont(new Font("System", 14));
        titleText.setAlignment(Pos.CENTER);
        titleText.setMaxWidth(Double.MAX_VALUE);

        HBox mainPanelEditing = new HBox(5);
        mainPanelEditing.setAlignment(Pos.CENTER_LEFT);
        mainPanelEditing.setPadding(new Insets(10,10,10,10));
        TextField labelIp = new TextField(list.get(0).getAddress());
        labelIp.setEditable(false); labelIp.setMinWidth(110);
        TextField labelMask = new TextField();
        labelMask.setEditable(false); labelMask.setMinWidth(50);
        TextField labelCountIp = new TextField();
        int count = 0;
        for (TreeItem<Siec6> siecItem:
            observableList){
            count += Integer.parseInt(siecItem.getValue().getCountIp());
        }
        labelCountIp.setText(String.valueOf(count));
        labelMask.setText(String.valueOf(32-MyMath.countDividedBy(count, 2)));
        labelCountIp.setEditable(false); labelCountIp.setMinWidth(60);

        ComboBox<ImageView> labelStatus = new ComboBox<>();
        labelStatus.setMaxWidth(50);
        labelStatus.getItems().addAll(
                new ImageView(new Image("Icons/plus.png")),
                new ImageView(new Image("Icons/close_network.png")),
                new ImageView(new Image("Icons/network.png"))
        );
        labelStatus.setCellFactory(new Callback<ListView<ImageView>, ListCell<ImageView>>() {
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
        labelStatus.getSelectionModel().select(0);

        ComboBox<String> labelPriority = new ComboBox<>();
        labelPriority.setMinWidth(50);
        labelPriority.getItems().addAll("1", "2", "3", "4", "5");
        labelPriority.getSelectionModel().select(4);

        TextField labelClient = new TextField();
        labelClient.setMinWidth(100);
        TextField labelType = new TextField();
        labelType.setMinWidth(80);
        DatePicker datePicker = new DatePicker();
        String pattern = "yyyy-MM-dd";
        datePicker.setMinWidth(100);
        datePicker.setPromptText(pattern.toLowerCase());
        datePicker.setValue(LocalDate.now());
        mainPanelEditing.getChildren().addAll(labelIp, labelMask, labelCountIp, labelStatus, labelPriority, labelClient, labelType, datePicker);


        HBox bottonBox = new HBox(5);
        bottonBox.setAlignment(Pos.CENTER_RIGHT);
        bottonBox.setPadding(new Insets(10,10,10,10));
        Button buttonOk = new Button("Ok");
        Button buttonCancel = new Button("Cancel");
        bottonBox.getChildren().addAll(buttonCancel, buttonOk);
        buttonCancel.setOnAction(event -> dialog.close());
        ridthBox.getChildren().addAll(titleText, new Separator(Orientation.HORIZONTAL), mainPanelEditing, new Separator(Orientation.HORIZONTAL), bottonBox);

        mainBox.getChildren().addAll(leftBox, ridthBox);

        // Listeners
        leftNetworks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            labelClient.setText(newValue.getClient());
            labelType.setText(newValue.getType());
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
            datePicker.getConverter().fromString(newValue.getDate());

            int n = newValue.getStatus() == null || newValue.getStatus().equals("") ? 2 : newValue.getStatus().equals("n") ? 0 : 1;
            labelStatus.getSelectionModel().select(n);
            labelPriority.getSelectionModel().select(newValue.getPriority() == null ? 0 : Integer.parseInt(newValue.getPriority())-1);
        });
        leftNetworks.setCellFactory(param -> new ListCell<Siec6>() {
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
                    Label labelAddrMaskCount = new Label(item.getAddress() + " [" + item.getMask() + "] |" + item.getCountIp() + "| {'" +
                            item.getPriority() + "', '" + item.getClient() + "', '" + item.getType() + "', '" + item.getDate() + "'}");
                    String url = item.getStatus() == null || item.getStatus().equals("") ? "Icons/network.png" :
                            item.getStatus().equals("z") ? "Icons/close_network.png" : "Icons/open_network.png";
                    Label image = new Label(null, new ImageView(new Image(url)));
                    hBox.getChildren().addAll(image, labelAddrMaskCount);
                    setText(null);
                    setGraphic(hBox);
                }
            }
        });

        buttonOk.setOnAction(event -> {
            for(TreeItem<Siec6> siec:
                    observableList) {
                manager.getData().remove(siec.getValue());
            }
            itemParent.getChildren().removeAll(observableList);
            String status = labelStatus.getSelectionModel().getSelectedIndex() == 0 ? "n" : labelStatus.getSelectionModel().getSelectedIndex() == 1 ? "z" : "";
            Siec6 newSiec = new Siec6(labelIp.getText(), labelMask.getText(), labelCountIp.getText(),
                    status, labelPriority.getSelectionModel().getSelectedItem(), labelClient.getText(), labelType.getText(),
                    datePicker.getValue().toString());
            System.out.println("New siec = " + newSiec);
            manager.getData().add(newSiec);
            manager.upload();
            manager.selectItem(itemParent, newSiec);
            dialog.close();
        });
    }

    public void show(){
        dialog.show();
    }
}

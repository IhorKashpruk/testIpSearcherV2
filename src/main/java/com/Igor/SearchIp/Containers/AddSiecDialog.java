package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.Siec6;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import sun.reflect.generics.tree.Tree;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Игорь on 12.08.2016.
 */
public class AddSiecDialog {
    private final Stage dialog;
    private Siec6 item;
    private TreeViewManager manager;
    private VBox mainBox;
    private Label labelCountIp;

    public AddSiecDialog(Siec6 item, TreeViewManager manager) {
        this.manager = manager;
        this.item = item;
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(null);

        createElement();

        Scene dialogScene = new Scene(mainBox, 800, 130);
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.setTitle("Add network");
        dialog.initStyle(StageStyle.UTILITY);
    }

    private void createElement(){
        HBox topBox = new HBox(5);
        topBox.setAlignment(Pos.CENTER);
        mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: white;");
        VBox.setVgrow(mainBox, Priority.ALWAYS);
        Label text = new Label("Current network: " + item.getAddress()+ ", available IP addresses: ");
        text.setPadding(new Insets(10,10,10,10));
        text.setFont(new Font("System", 14));
        text.setAlignment(Pos.CENTER);
        text.setMaxWidth(Double.MAX_VALUE);
        labelCountIp = new Label();
        labelCountIp.setMaxWidth(Double.MAX_VALUE);
        labelCountIp.setAlignment(Pos.CENTER);

        topBox.getChildren().addAll(text, labelCountIp);

        HBox centerBox = new HBox(5);
        centerBox.setAlignment(Pos.CENTER_LEFT);
        centerBox.setPadding(new Insets(10,10,10,10));

        TextField labelIp = new TextField();
        labelIp.setPromptText("Network address..."); labelIp.setMinWidth(110);

        TextField labelMask = new TextField();
        labelMask.setPromptText("Mask..."); labelMask.setMinWidth(50);
        TextField labelCountIp = new TextField();
        labelCountIp.setPromptText("Count ip..."); labelCountIp.setMinWidth(60);
        TextField labelStatus = new TextField();
        labelStatus.setPromptText("Status..."); labelStatus.setMinWidth(30);
        TextField labelPriority = new TextField();
        labelPriority.setPromptText("Priority...{1-5}"); labelPriority.setMinWidth(30);
        HBox secreetBox = new HBox(5);
        TextField labelClient = new TextField();
        labelClient.setPromptText("Client..."); labelClient.setMinWidth(100);
        TextField labelType = new TextField();
        labelType.setPromptText("Type..."); labelType.setMinWidth(80);

        secreetBox.getChildren().addAll(labelClient,labelType);

        Label labelOpenInfo = new Label(null, new ImageView(new Image("Icons/more.png")));
        labelOpenInfo.setOnMouseClicked(event ->{
            if(centerBox.getChildren().indexOf(secreetBox) == -1){
                centerBox.getChildren().add(secreetBox);
            }else
                centerBox.getChildren().remove(secreetBox);
        });

        centerBox.getChildren().addAll(labelIp, labelMask, labelCountIp, labelStatus, labelPriority, labelOpenInfo);

        HBox bottonBox = new HBox(5);
        bottonBox.setAlignment(Pos.CENTER_RIGHT);
        bottonBox.setPadding(new Insets(10,10,10,10));
        Button buttonOk = new Button("Ok");
        Button buttonCancel = new Button("Cancel");

        buttonCancel.setOnAction(event -> {
            dialog.close();
        });


        bottonBox.getChildren().addAll(buttonCancel, buttonOk);


        // Listeners
        // lableIp
        labelIp.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue.length() != 0){
                int len = newValue.length();
                char symbol = newValue.charAt(len-1);
                if((symbol < '0' || symbol > '9') && symbol != '.'){
                    labelIp.replaceText(len-1, len, "");
                }
            }
        });

        // buttonOk
        buttonOk.setOnAction(event -> {
            // Перевірка на правильність
            // IP
            String IPADDRESS_PATTERN =
                    "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

            Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
            Matcher matcher = pattern.matcher(labelIp.getText());

            if(matcher.find()){
                String text1 = matcher.group();
                System.out.println(text1);
                String parrentAddress = item.getAddress();
                String maxAddress = Siec6.generatedIpSiec(parrentAddress, Integer.parseInt(item.getCountIp()));
                int countIp = 0;
                if(!labelCountIp.getText().isEmpty()){
                    countIp = Integer.parseInt(labelCountIp.getText());
                }
                if((Siec6.isBigger(parrentAddress, text1) && !text1.equals(parrentAddress))
                        || Siec6.isBigger(Siec6.generatedIpSiec(text1, countIp), maxAddress)) {
                    labelIp.setStyle("-fx-border-color: lightcoral;-fx-border-width: 2px;");
                }else
                    labelIp.setStyle("");
            }else
                labelIp.setStyle("-fx-border-color: lightcoral;-fx-border-width: 2px;");

            // Mask


            // Після перевірок
            Siec6 siec6 = new Siec6(labelIp.getText(), labelMask.getText(), labelCountIp.getText(),
                    labelStatus.getText(), labelPriority.getText(), labelClient.getText(), labelType.getText());
            manager.getData().add(siec6);
            item.setStatus(null);
            manager.upload();
            manager.selectItem(manager.getRootNode(), siec6);
            dialog.close();
        });


        mainBox.getChildren().addAll(topBox, new Separator(Orientation.HORIZONTAL), centerBox, new Separator(Orientation.HORIZONTAL),bottonBox);
    }

    public void show(){
        dialog.show();
    }
}

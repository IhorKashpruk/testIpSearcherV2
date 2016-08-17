package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.MyMath;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Игорь on 12.08.2016.
 */
class AddSiecDialog {

    enum NETWORK_TYPE {
        HOME_NETWORK,
        FREE_NETWORK,
        BUSY_NETWORK
    }

    private final Stage dialog;
    private TreeItem<Siec6> item;
    private TreeViewManager manager;
    private HBox mainBox;
    private VBox ridthMainBox;
    private Label labelCountIpTitle;
    private TextField labelIp;
    private TextField labelMask;
    private TextField labelCountIp;
    private ImageView imageView;
    private Label labelLog;
    private List<Siec6> siec6List;
    private Label labelIconLog;
    private NETWORK_TYPE networkType;

    AddSiecDialog(TreeItem<Siec6> item, TreeViewManager manager, NETWORK_TYPE type) {
        networkType = type;
        siec6List = new ArrayList<>();
        this.manager = manager;
        this.item = item;
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(null);

        createElement();

        Scene dialogScene = new Scene(mainBox, 720, 170);
        String iconPath = networkType == NETWORK_TYPE.HOME_NETWORK ?
                "Icons/network.png" : networkType == NETWORK_TYPE.BUSY_NETWORK ?
                "Icons/close_network.png" : "Icons/open_network.png";
        dialog.getIcons().add(new Image(iconPath));
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.setTitle("Add network");
        dialog.initStyle(StageStyle.UTILITY);
    }

    private void createElement(){
        HBox topBox = new HBox(5);
        topBox.setAlignment(Pos.CENTER);
        mainBox = new HBox();

        ListView<String> leftSiecs = new ListView<>();
        leftSiecs.setStyle("-fx-background: white;");
        leftSiecs.setMinWidth(200);
        VBox leftMainBox = new VBox();
        VBox.setVgrow(leftMainBox, Priority.ALWAYS);

        // додаю вільні місця
        Label textFreeAddres = new Label("Available address:");
        textFreeAddres.setFont(new Font("System", 14));
        leftMainBox.getChildren().addAll(textFreeAddres, leftSiecs);

        manager.getFreeSiecs(item, siec6List);

        for(int i = 0; i < siec6List.size(); i++){
            Siec6 siec6 = siec6List.get(i);
            leftSiecs.getItems().add(siec6.getAddress() + " - " + Siec6.generatedIpSiec(siec6.getAddress(), Integer.parseInt(siec6.getCountIp())));
        }


        leftSiecs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            labelIp.setText(newValue.substring(0, newValue.indexOf(' ')));
        });

        mainBox.setStyle("-fx-background-color: white;");
        HBox.setHgrow(mainBox, Priority.ALWAYS);
        ridthMainBox = new VBox();
        ridthMainBox.setStyle("-fx-background-color: white;");
        VBox.setVgrow(ridthMainBox, Priority.ALWAYS);
        Label text = new Label("Current network: " + item.getValue().getAddress()+ ", available IP addresses: ");
        text.setPadding(new Insets(10,10,10,10));
        text.setFont(new Font("System", 14));
        text.setAlignment(Pos.CENTER);
        text.setMaxWidth(Double.MAX_VALUE);
        int countIp = 0;
        for(int i = 0; i < siec6List.size(); i++){
            countIp += Integer.parseInt(siec6List.get(i).getCountIp());
        }
        labelCountIpTitle = new Label(String.valueOf(countIp));
        labelCountIpTitle.setMaxWidth(Double.MAX_VALUE);
        labelCountIpTitle.setAlignment(Pos.CENTER);
        labelCountIpTitle.setPadding(new Insets(10,10,10,10));
        labelCountIpTitle.setFont(new Font("System", 14));

        topBox.getChildren().addAll(text, labelCountIpTitle);

        HBox centerBox = new HBox(5);
        centerBox.setAlignment(Pos.CENTER_LEFT);
        centerBox.setPadding(new Insets(10,10,10,10));

        labelIp = new TextField();
        labelIp.setPromptText("Network address..."); labelIp.setMinWidth(110);

        labelMask = new TextField();
        labelMask.setPromptText("Mask..."); labelMask.setMinWidth(50);
        labelCountIp = new TextField();
        labelCountIp.setPromptText("Count ip..."); labelCountIp.setMinWidth(60);

        ComboBox<String> labelPriority = new ComboBox();
        labelPriority.setMinWidth(50);
        labelPriority.getItems().addAll("1", "2", "3", "4", "5");
        labelPriority.getSelectionModel().select(0);
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

        centerBox.getChildren().addAll(labelIp, labelMask, labelCountIp, labelPriority, labelOpenInfo);

        HBox logBox = new HBox(10);
        imageView = new ImageView();
        labelIconLog = new Label("", imageView);
        labelIconLog.setPadding(new Insets(0,5,0,10));
        labelLog = new Label();

        logBox.getChildren().addAll(labelIconLog, labelLog);

        HBox bottonBox = new HBox(5);
        bottonBox.setAlignment(Pos.CENTER_RIGHT);
        bottonBox.setPadding(new Insets(10,10,10,10));
        Button buttonOk = new Button("Ok");
        Button buttonCancel = new Button("Cancel");

        buttonCancel.setOnAction(event -> dialog.close());


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
            isGood();
        });

        labelMask.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(labelMask.getText().isEmpty())
                return;
            if(Integer.parseInt(labelMask.getText()) > 32){
                labelMask.setText("32");
            }
            int localMask = Integer.parseInt(labelMask.getText());
            if(!labelCountIp.getText().equals(String.valueOf(Math.pow(2, 32 - localMask))))
                labelCountIp.setText(String.valueOf((int)Math.pow(2, 32-localMask)));
        });
        labelMask.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                labelMask.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        labelCountIp.focusedProperty().addListener((observable, oldValue, newValue) ->{
            String str = labelCountIp.getText();
            if(str.isEmpty() || str.length() > 9 || str.equals("1") || str.equals("0"))
                return;
            int localCountIp = Integer.parseInt(str);

            int countDivided2 = MyMath.countDividedBy(localCountIp, 2);
            if(MyMath.isDivideBy2Entirely(localCountIp)){
                labelMask.setText(String.valueOf(32 - countDivided2));
            }else {
                switch (networkType){
                    case HOME_NETWORK:
                        break;
                    default:
                        labelCountIp.setText(String.valueOf((int)Math.pow(2, countDivided2+1)));
                        labelMask.setText(String.valueOf(32 - countDivided2-1));
                }
            }
            isGood();
        });
        labelCountIp.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                labelCountIp.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // buttonOk
        buttonOk.setOnAction(event -> {
            if(!isGood()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Enter the correct data!");
                alert.showAndWait();
                return;
            }

            // Після перевірок
            String networkId =  networkType == NETWORK_TYPE.FREE_NETWORK ? "n" : networkType == NETWORK_TYPE.BUSY_NETWORK ? "z" : "";
            Siec6 siec6 = new Siec6(labelIp.getText(), labelMask.getText(), labelCountIp.getText(),
                   networkId, labelPriority.getSelectionModel().getSelectedItem(), labelClient.getText(), labelType.getText(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            manager.getData().add(siec6);
            item.getValue().setStatus(null);
            manager.upload();
            manager.selectItem(manager.getRootNode(), siec6);
            dialog.close();
        });


        ridthMainBox.getChildren().addAll(topBox, new Separator(Orientation.HORIZONTAL),
                centerBox, new Separator(Orientation.HORIZONTAL),
                logBox, new Separator(Orientation.HORIZONTAL),bottonBox);
        mainBox.getChildren().addAll(leftMainBox, ridthMainBox);
    }

    private boolean isGood(){

        if(labelIp.getText().isEmpty()){
            showError("Ip is empty!", labelIp);
            return false;
        }
        if(labelCountIp.getText().isEmpty()){
           showError("Count ip is empty!", labelCountIp);
            return false;
        }

        // Перевірка на правильність
        // IP
        String IPADDRESS_PATTERN =
                "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(labelIp.getText());


        if (matcher.find()) {
            String ipAddress = matcher.group();
            Siec6 siec6 = new Siec6(ipAddress, labelMask.getText(), labelCountIp.getText(), "", "", "", "", "");
            boolean isInto = false;
            for (Siec6 siec : siec6List) {
                if (siec6.thisIsParentNetwortk(siec)) {
                    isInto = true;
                    break;
                }
            }
            if (!isInto) {
               showError("You haven't enough ip address!!", labelIp);
                return false;
            }
        } else {
            showError("You haven't enough ip address!!", labelIp);
            return false;
        }

       showSuccess();
        return true;
    }

    public void show(){
        dialog.show();
    }

    private void showError(String message, TextField textField){
        String error_style = "-fx-border-color: lightcoral;-fx-border-width: 2px;";
        if(textField != null)
            textField.setStyle(error_style);
        labelIconLog.setGraphic(new ImageView(new Image("Icons/error.png")));

        labelLog.setText(message);
    }
    private void showSuccess(){
        labelIconLog.setGraphic(new ImageView(new Image("Icons/success.png")));
        labelCountIp.setStyle("");
        labelIp.setStyle("");
        labelMask.setStyle("");
    }

}

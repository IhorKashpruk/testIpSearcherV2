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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import sun.reflect.generics.tree.Tree;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Игорь on 12.08.2016.
 */
public class AddSiecDialog {
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


    public AddSiecDialog(TreeItem<Siec6> item, TreeViewManager manager) {
        siec6List = new ArrayList<>();
        this.manager = manager;
        this.item = item;
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(null);

        createElement();

        Scene dialogScene = new Scene(mainBox, 800, 170);
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.setTitle("Add network");
        dialog.initStyle(StageStyle.UTILITY);
    }

    private void createElement(){
        HBox topBox = new HBox(5);
        topBox.setAlignment(Pos.CENTER);
        mainBox = new HBox();

        ScrollPane leftMainBox = new ScrollPane();
        leftMainBox.setStyle("-fx-background: white;");
        leftMainBox.setMinWidth(200);
        VBox boxInScrollPane = new VBox();
        VBox.setVgrow(boxInScrollPane, Priority.ALWAYS);
        leftMainBox.setContent(boxInScrollPane);

        // додаю вільні місця
        Label textFreeAddres = new Label("Available address:");
        textFreeAddres.setFont(new Font("System", 14));
        boxInScrollPane.getChildren().addAll(textFreeAddres, new Separator(Orientation.HORIZONTAL));

        createLeftPanel(item, boxInScrollPane);

        for(int i = 2; i < boxInScrollPane.getChildren().size(); i++){
            ((Label)boxInScrollPane.getChildren().get(i)).setFont(new Font("System", 14));
        }

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
        labelStatus.setMinWidth(50);
        labelStatus.getSelectionModel().select(0);
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

        centerBox.getChildren().addAll(labelIp, labelMask, labelCountIp, labelStatus, labelPriority, labelOpenInfo);

        HBox logBox = new HBox(8);
        imageView = new ImageView();
        Label labelIconLog = new Label("", imageView);
        labelLog = new Label();

        logBox.getChildren().addAll(labelIconLog, labelLog);

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
            int n = 0;
            boolean isGoodDivide = true;
            while(localCountIp > 1){
                if((localCountIp % 2) > 0){
                    isGoodDivide = false;
                    break;
                }
                localCountIp /= 2;
                n++;
            }
            if(isGoodDivide)
                labelMask.setText(String.valueOf(32-n));
            else {
                labelMask.setText("");
                labelStatus.getSelectionModel().select(2);
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
            int selectIndexStatus = labelStatus.getSelectionModel().getSelectedIndex();
            String networkId =  selectIndexStatus == 0 ? "n" : selectIndexStatus == 1 ? "z" : "";
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

    private void createLeftPanel(TreeItem<Siec6> treeItem, VBox box){
        for (int i = 0; i < treeItem.getChildren().size(); i++) {
            createLeftPanel(treeItem.getChildren().get(i), box);
        }

        if(treeItem.getChildren().size() == 0 && (treeItem.getValue().getStatus() == null || treeItem.getValue().getStatus().isEmpty())) {
            siec6List.add(new Siec6(treeItem.getValue().getAddress(), treeItem.getValue().getMask(), treeItem.getValue().getCountIp(),
                    "", "", "", "", ""));
            box.getChildren().add(new Label(treeItem.getValue().getAddress() + " - " +
                    Siec6.generatedIpSiec(treeItem.getValue().getAddress(), Integer.parseInt(treeItem.getValue().getCountIp()))));
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
                        siec6List.add(new Siec6(treeItem.getValue().getAddress(), "",
                                String.valueOf(Siec6.minus(siec.getAddress(), treeItem.getValue().getAddress())),
                                "", "", "", "", ""));
                        box.getChildren().add(new Label(treeItem.getValue().getAddress() + " - " + siec.getAddress()));
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
                box.getChildren().add(new Label(Siec6.generatedIpSiec(siec.getAddress(), Integer.parseInt(siec.getCountIp())) + " - " +
                        siec2.getAddress()));
                siec6List.add(new Siec6(addr1, "",
                        String.valueOf(Siec6.minus(siec2.getAddress(), addr1)), "", "", "", "", ""));
            }
        }
    }

    private boolean isGood(){
        String error_style = "-fx-border-color: lightcoral;-fx-border-width: 2px;";
        if(labelIp.getText().isEmpty()){
            labelIp.setStyle(error_style);
            labelLog.setText("Ip is empty!");
            return false;
        }
        if(labelCountIp.getText().isEmpty()){
            labelCountIp.setStyle(error_style);
            labelLog.setText("Count ip is empty!");
            return false;
        }

        // Перевірка на правильність
        // IP
        String IPADDRESS_PATTERN =
                "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(labelIp.getText());

        if(matcher.find()){
            String ipAddress = matcher.group();

            Siec6 siec6 = new Siec6(ipAddress, labelMask.getText(), labelCountIp.getText(), "", "", "", "", "");
            boolean isInto = false;
            for (Siec6 siec : siec6List){
                if(siec6.thisIsParentNetwortk(siec)){
                    isInto = true;
                    break;
                }
            }
            if(isInto){
                labelIp.setStyle("");
            }else
            {
                labelIp.setStyle(error_style);
                labelLog.setText("You haven't enough ip address!!");
                return false;
            }
        }else {
            labelIp.setStyle(error_style);
            return false;
        }
        labelCountIp.setStyle("");
        labelMask.setStyle("");
        return true;
    }

    public void show(){
        dialog.show();
    }
}

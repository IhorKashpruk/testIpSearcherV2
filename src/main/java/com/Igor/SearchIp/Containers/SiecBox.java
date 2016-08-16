package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.Siec6;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igor on 07.08.16.
 */
public class SiecBox {
    private Siec6 data;
    private List<SiecBox> listSiecBox;
    private SiecBox residueSiecBox;
    private HBox mainBox;
    private VBox superMainBox;
    private List<Integer> divideNumbers;
    private SiecBox parrent;

    public VBox getMainBox() {
        return superMainBox;
    }

    public SiecBox(Siec6 data, SiecBox parrent) {
        this.parrent = parrent;
        this.data = data;
        listSiecBox = new ArrayList<>();
        residueSiecBox = null;
        mainBox = new HBox();
        divideNumbers = new ArrayList<>();
        superMainBox = new VBox();
        VBox.setVgrow(superMainBox, Priority.ALWAYS);
        HBox.setHgrow(mainBox, Priority.ALWAYS);
        mainBox.setPadding(new Insets(5,5,5,5));
        initialize();
    }

    private void initialize(){

        mainBox.setStyle("-fx-border-style: dotted; -fx-border-color: lime; -fx-border-width: 3px;");
        mainBox.setPadding(new Insets(5,5,5,5));
        // First panel : 2 TextField - ip and count ip
        HBox ipAndCountIpBox = new HBox();
        TextField ipBox = new TextField(data.getAddress());
        TextField countIpBox = new TextField(data.getCountIp());
        ipBox.setEditable(false);
        ipBox.setPrefWidth(120);
        ipBox.setMinWidth(120);
        countIpBox.setEditable(false);
        countIpBox.setPrefWidth(50);
        countIpBox.setMinWidth(50);
        ipAndCountIpBox.getChildren().addAll(ipBox, countIpBox);

        // Labels box
        HBox bottomBox = new HBox();

        // Two panel : Button, TextField - divide
        HBox divideBox = new HBox();
        TextField divideTextBox = new TextField("0");
        divideTextBox.setPrefWidth(50);
        divideTextBox.setMinWidth(50);
        divideTextBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                divideTextBox.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Image image = new Image("Icons/divideOn.png");
        Button divideButton = new Button("", new ImageView(image));
        divideButton.setOnAction(event -> {
            int divideNumber = Integer.parseInt(divideTextBox.getText());
            if ((divideNumber > 0 && (divideNumber & (divideNumber - 1)) != 0) || divideNumber == 0 || divideNumber == 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Number must be power of 2!");
                alert.showAndWait();
                return;
            }

            // Перевірка на кількість
            int countDivideNumbers = 0;
            for(int i = 0; i < divideNumbers.size(); i++){
                countDivideNumbers += divideNumbers.get(i);
            }

            if((countDivideNumbers + divideNumber) > Integer.parseInt(data.getCountIp())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("You haven't enough ip address!");
                alert.showAndWait();
                return;
            }

            divideNumbers.add(divideNumber);
            Label countIpLabel = new Label(String.valueOf(divideNumber));
            countIpLabel.setFont(new Font("Arial", 14));
            countIpLabel.setStyle("-fx-border-color: crimson; -fx-border-style: dashed; -fx-border-width: 2px;");
            countIpLabel.setPrefWidth(35);
            countIpLabel.setMinWidth(35);
            countIpLabel.setPadding(new Insets(0,2,0,2));
            countIpLabel.setOnMouseClicked(event1 -> {
                removeNumberForList(Integer.parseInt(countIpLabel.getText()));
                bottomBox.getChildren().remove(countIpLabel);
            });
            bottomBox.getChildren().add(countIpLabel);
        });

        divideBox.getChildren().addAll(divideTextBox, divideButton);

        HBox topBox = new HBox();
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setPadding(new Insets(0,5,0,5));
        topBox.getChildren().addAll(ipAndCountIpBox, separator, divideBox);


        VBox leftBox = new VBox();
        leftBox.getChildren().addAll(topBox, bottomBox);

        HBox rightBox = new HBox();
        Button divideSiecButton = new Button("", new ImageView(new Image("Icons/plus.png")));
        Button eraseSiecButton = new Button("", new ImageView(new Image("Icons/minus.png")));
        Separator separator1 = new Separator(Orientation.VERTICAL);
        separator1.setPadding(new Insets(0,5,0,5));
        rightBox.getChildren().addAll(separator1, divideSiecButton, eraseSiecButton);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setPadding(new Insets(2,2,2,2));

        // Button divide network
        divideSiecButton.setOnAction(event -> {
            if(divideNumbers.size() == 0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("What to share?");
                alert.showAndWait();
                return;
            }
            // Sorting numbers
            //Collections.sort(divideNumbers);
            leftBox.setDisable(true);
            divideSiecButton.setDisable(true);

            // Clear border color
            mainBox.setStyle("");

            String ipSiec = data.getAddress();
            SiecBox siec = new SiecBox(new Siec6(ipSiec, String.valueOf(generatedMask(divideNumbers.get(0))), String.valueOf(divideNumbers.get(0)),
                    "", "", "", "", ""), this);
            siec.mainBox.setPadding(new Insets(5,5,5,mainBox.getPadding().getLeft()+20));
            listSiecBox.add(siec);
            superMainBox.getChildren().add(siec.getMainBox());
            for(int j = 1; j < divideNumbers.size(); j++){
                ipSiec = generatedIpSiec(ipSiec, divideNumbers.get(j-1));
                SiecBox siecBox = new SiecBox(new Siec6(ipSiec, String.valueOf(generatedMask(divideNumbers.get(j)))
                        , String.valueOf(divideNumbers.get(j)), "", "", "", "", ""), this);
                siecBox.mainBox.setPadding(new Insets(5, 5, 5, mainBox.getPadding().getLeft()+20));
                listSiecBox.add(siecBox);
                superMainBox.getChildren().add(siecBox.getMainBox());
            }
            ipSiec = generatedIpSiec(ipSiec, divideNumbers.get(divideNumbers.size()-1));

            int suma = 0;
            for(int i = 0; i < divideNumbers.size(); i++){
                suma += divideNumbers.get(i);
            }

            if(suma < Integer.parseInt(data.getCountIp())){
                int nIp = Integer.parseInt(data.getCountIp()) - suma;
                int mask = generatedMask(nIp);
                String residueMask = mask == -1 ? "" : String.valueOf(mask);
                residueSiecBox = new SiecBox(new Siec6(ipSiec, residueMask,
                        String.valueOf(nIp), "n", "", "", "", ""), this);
                residueSiecBox.mainBox.setStyle("-fx-border-color: gold; -fx-border-width: 3px;");
                superMainBox.getChildren().add(residueSiecBox.getMainBox());
            }

        });

        // Button erase network
        eraseSiecButton.setOnAction(event -> {
            if(parrent != null){
                removeSiecBox(this);
            }
        });


        mainBox.getChildren().addAll(leftBox, rightBox);
        superMainBox.getChildren().add(mainBox);
    }

    public Siec6 getData() {
        return data;
    }

    private int generatedMask(int countIp){
        int n = 0;
        while(countIp > 1){
            if((countIp % 2) > 0){
                return -1;
            }
            countIp /= 2;
            n++;
        }
        return (32 - n);
    }

    private String generatedIpSiec(String ip, int count){
        String[] strs = ip.split("\\.");

        if(strs.length != 4)
            return null;

        int[] args = new int[4];
        for(int i = 0; i < 4; i++)
            args[i] = Integer.parseInt(strs[i]);

        while(count > 0){
            if(args[3] == 255){
                if(args[2] == 255){
                    if(args[1] == 255){
                        if(args[0] == 255){
                            args[0] = 0;
                        }else
                            args[0]++;
                        args[1]++;
                    }else
                        args[1]++;
                    args[2] = 0;
                }else
                    args[2]++;
                args[3] = 0;
            }else
                args[3]++;
            count--;
        }

        return String.valueOf(args[0]) + "." + String.valueOf(args[1]) + "." + String.valueOf(args[2]) + "." + String.valueOf(args[3]);
    }

    private void removeNumberForList(int number){
        for(int i = 0; i < divideNumbers.size(); i++){
            if(divideNumbers.get(i) == number){
                divideNumbers.remove(i);
                break;
            }
        }
    }

    public void clear(){
        listSiecBox.clear();
        mainBox.getChildren().clear();
        superMainBox.getChildren().clear();
        divideNumbers.clear();
        residueSiecBox = null;
        data = null;
    }

    public static void searchLastNodes(SiecBox siecBox, List<Siec6> siec6List){
        if(siecBox == null)
            return;
        if(siecBox.listSiecBox.size() == 0){
            siec6List.add(siecBox.data);
            return;
        }else {
            for(int i = 0; i < siecBox.listSiecBox.size(); i++){
                searchLastNodes(siecBox.listSiecBox.get(i), siec6List);
            }
        }
        if(siecBox.residueSiecBox != null)
            searchLastNodes(siecBox.residueSiecBox, siec6List);
    }

    public void removeSiecBox(SiecBox siecBox){
        siecBox.listSiecBox.clear();
        siecBox.residueSiecBox = null;
        siecBox.superMainBox.getChildren().clear();
        siecBox.mainBox = null;
        siecBox.parrent.listSiecBox.clear();
        siecBox.parrent.residueSiecBox = null;
        siecBox.parrent.superMainBox.getChildren().clear();
        siecBox.parrent.superMainBox.getChildren().add(siecBox.parrent.mainBox);
        siecBox.parrent.mainBox.setStyle("-fx-border-style: dotted; -fx-border-color: lime; -fx-border-width: 3px;");
        siecBox.parrent.mainBox.setDisable(false);
        siecBox.parrent.mainBox.getChildren().get(0).setDisable(false);
        ((HBox)siecBox.parrent.mainBox.getChildren().get(1)).getChildren().get(1).setDisable(false);
    }
}

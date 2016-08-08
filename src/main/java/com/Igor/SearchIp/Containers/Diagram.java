package com.Igor.SearchIp.Containers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;

/**
 * Created by igor on 04.08.16.
 */
public class Diagram {
    private PieChart pieChart;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();;

    public Diagram(ObservableList<PieChart.Data> pieChartData, PieChart pieChart) {
        this.pieChartData = pieChartData;
        this.pieChart = pieChart;
    }

    public Diagram(PieChart pieChart) {
        this.pieChart = pieChart;
    }

    public ObservableList<PieChart.Data> getPieChartData() {
        return pieChartData;
    }

    public void setPieChartData(ObservableList<PieChart.Data> pieChartData) {
        this.pieChartData.addAll(pieChartData);
    }
    public void Drow(){
        pieChart.setLabelLineLength(10);
        pieChart.setLegendSide(Side.LEFT);
        pieChart.setData(this.pieChartData);
    }

}

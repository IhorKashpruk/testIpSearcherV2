package com.Igor.SearchIp;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by igor on 03.08.16.
 */
public class CSVManager {
    private String path;
    private char delimiter;

    public CSVManager(String path, char delimeter) {
        this.path = path;
        this.delimiter = delimeter;
    }

    public <T extends SiecModel> List<T> readData(Class<T> type) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(path), delimiter);
        ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
        strat.setType(type);
        if(type == Siec6.class) {
            strat.setColumnMapping(Siec6.getCollumnsName());
        }

        CsvToBean csv = new CsvToBean();
        List<T> list = csv.parse(strat, reader);
        reader.close();
        return list;
    }

    public<T extends SiecModel> void writeData(List<T> list, String pathToSave) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(pathToSave), delimiter, '\0');

        for(SiecModel sm : list) {
            writer.writeNext(sm.getColumnsValue());
        }
        writer.close();
    }
}

package com.Igor.SearchIp;

import com.opencsv.bean.CsvBind;

/**
 * Created by igor on 03.08.16.
 */
public class Siec6 extends SiecModel {
    private static final String columns_name[] = new String[]
            {"address", "mask","countIp","status","priority", "client", "type"};

    public Siec6(String address, String mask, String countIp, String status, String priority, String client, String type) {
        this.address = address;
        this.mask = mask;
        this.countIp = countIp;
        this.status = status;
        this.priority = priority;
        this.client = client;
        this.type = type;
    }

    public Siec6() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountIp() {
        return countIp;
    }

    public void setCountIp(String countIp) {
        this.countIp = countIp;
    }

    @CsvBind
    private String address;
    @CsvBind
    private String mask;
    @CsvBind
    private String countIp;
    @CsvBind
    private String status;
    @CsvBind
    private String priority;
    @CsvBind
    private String client;
    @CsvBind
    private String type;

    @Override
    public void setValue(String key, String value) {
        if(key.equals("address")){
            address = value;
        }
        if(key.equals("mask")){
            mask = value;
        }
        if(key.equals("countIp")){
            countIp = value;
        }
        if(key.equals("status")){
            status= value;
        }
        if(key.equals("priority")){
            priority= value;
        }
        if(key.equals("client")){
            client= value;
        }
        if(key.equals("type")){
            type= value;
        }
    }

    @Override
    public String[] getColumnsValue() {
        return new String[]{address, mask, countIp, status, priority, client, type};
    }

    public static String[] getCollumnsName(){return columns_name;}

    @Override
    public String toString() {
        return "Siec{" +
                "address='" + address + '\'' +
                ", mask='" + mask + '\'' +
                ", countIp='" + countIp + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", client='" + client + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public String getValue(String key) {
        if(key.equals("address")){
            return address;
        }
        if(key.equals("mask")){
            return mask;
        }
        if(key.equals("countIp")){
            return countIp;
        }
        if(key.equals("status")){
            return status;
        }
        if(key.equals("priority")){
            return priority;
        }
        if(key.equals("client")){
            return client;
        }
        if(key.equals("type")){
            return type;
        }
        return null;
    }
}

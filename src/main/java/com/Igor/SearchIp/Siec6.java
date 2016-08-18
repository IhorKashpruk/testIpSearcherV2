package com.Igor.SearchIp;

import com.opencsv.bean.CsvBind;
/**
 * Created by igor on 03.08.16.
 */
public class Siec6 extends SiecModel {
    private static final String columns_name[] = new String[]
            {"address", "mask","countIp","status","priority", "client", "type", "date"};

    public Siec6(String address, String mask, String countIp, String status, String priority, String client, String type, String date) {
        this.address = address;
        this.mask = mask;
        this.countIp = countIp;
        this.status = status;
        this.priority = priority;
        this.client = client;
        this.type = type;
        this.date = date;
    }

    public Siec6(String address, String mask, String countIp) {
        this.address = address;
        this.mask = mask;
        this.countIp = countIp;
    }

    public Siec6() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
    @CsvBind
    private String date;

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
        if(key.equals("date")){
            date = value;
        }
    }

    @Override
    public String[] getColumnsValue() {
        return new String[]{address, mask, countIp, status, priority, client, type, date};
    }

    public static String[] getCollumnsName(){return columns_name;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Siec6 siec6 = (Siec6) o;

        if (address != null ? !address.equals(siec6.address) : siec6.address != null) return false;
        if (mask != null ? !mask.equals(siec6.mask) : siec6.mask != null) return false;
        if (countIp != null ? !countIp.equals(siec6.countIp) : siec6.countIp != null) return false;
        if (status != null ? !status.equals(siec6.status) : siec6.status != null) return false;
        if (priority != null ? !priority.equals(siec6.priority) : siec6.priority != null) return false;
        if (client != null ? !client.equals(siec6.client) : siec6.client != null) return false;
        if (type != null ? !type.equals(siec6.type) : siec6.type != null) return false;
        return date != null ? date.equals(siec6.date) : siec6.date == null;

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (mask != null ? mask.hashCode() : 0);
        result = 31 * result + (countIp != null ? countIp.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Siec6{" +
                "address='" + address + '\'' +
                ", mask='" + mask + '\'' +
                ", countIp='" + countIp + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", client='" + client + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
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
        if(key.equals("date")){
            return date;
        }
        return null;
    }
    public boolean thisIsParentNetwortk(Siec6 network){
        if(this.equals(network))
            return false;


        String lastAddressFirst = generatedIpSiec(network.address,Integer.parseInt(network.countIp));
        String lastAddressSecond = generatedIpSiec(address,Integer.parseInt(countIp));

        if((isBigger(address, network.address) || address.equals(network.address)) &&
                (isBigger(lastAddressFirst, lastAddressSecond) || lastAddressFirst.equals(lastAddressSecond))){
            return true;
        }
        return false;
    }

    public int isBigger(Siec6 network){
        if(network == null)
            return 1;
        if(this.mask == null || network.mask == null || this.mask.isEmpty() || network.mask.isEmpty()){
                return Integer.parseInt(network.countIp) - Integer.parseInt(this.countIp);
        }
        int maskN1 = Integer.parseInt(this.mask);
        int maskN2 = Integer.parseInt(network.mask);
        if(maskN1 != maskN2)
            return maskN1-maskN2;
        if(this.address.equals(network.address)){
            if(network.status == null || network.status.equals("")) {
                return 1;
            }
        }
        if(isBigger(this.address, network.address)){
            return 1;
        }else
            return -1;
    }

    public static boolean isBigger(String first, String second){
        String[] strs = first.split("\\.");
        String[] strs2 = second.split("\\.");

        if(strs.length != 4 || strs2.length != 4)
            return false;

        int[] args  = new int[4];
        int[] args2 = new int[4];
        for(int i = 0; i < 4; i++) {
            args[i] = Integer.parseInt(strs[i]);
            args2[i] = Integer.parseInt(strs2[i]);
        }

        for(int i = 0; i < 4; i++){
            if(args[i] > args2[i])
                return true;
            if(args[i] < args2[i])
                return false;
        }
        return false;
    }

    public static int isBigger(Siec6 first, Siec6 second){
        String[] strs = first.getAddress().split("\\.");
        String[] strs2 = second.getAddress().split("\\.");

        if(strs.length != 4 || strs2.length != 4)
            return 0;

        int[] args  = new int[4];
        int[] args2 = new int[4];
        for(int i = 0; i < 4; i++) {
            args[i] = Integer.parseInt(strs[i]);
            args2[i] = Integer.parseInt(strs2[i]);
        }

        for(int i = 0; i < 4; i++){
            if(args[i] > args2[i])
                return 1;
            if(args[i] < args2[i])
                return -1;
        }
        return -1;
    }

    public static String generatedIpSiec(String ip, int count){
        String[] strs = ip.split("\\.");

        if(strs.length != 4)
            return null;

        int[] args = new int[4];
        for(int i = 0; i < 4; i++)
            args[i] = Integer.parseInt(strs[i]);

        while(count > 0){
            int size = 1;
            if(args[3] == 255){
                if(args[2] == 255){
                    if(args[1] == 255){
                        if(args[0] == 255){
                            args[0] = 0;
                        }else
                            args[0] += size;
                        args[1] = 0;
                    }else
                        args[1] += size;
                    args[2] = 0;
                }else
                    args[2] += size;
                args[3] = 0;
            }else {
                size = 255 - args[3];
                size = count-size < 0 ? count : size;
                args[3] += size;
            }
            count -= size;
        }

        return String.valueOf(args[0]) + "." + String.valueOf(args[1]) + "." + String.valueOf(args[2]) + "." + String.valueOf(args[3]);
    }

    public static String minusIp(String address, int count){
        String[] strs = address.split("\\.");

        if(strs.length != 4)
            return null;

        int[] args = new int[4];
        for(int i = 0; i < 4; i++)
            args[i] = Integer.parseInt(strs[i]);

        while(count > 0){
            int size = 1;
            if(args[3] == 0){
                if(args[2] == 0){
                    if(args[1] == 0){
                        if(args[0] == 0){
                            args[0] = 255;
                        }else
                            args[0] -= size;
                        args[1] = 255;
                    }else
                        args[1] -= size;
                    args[2] = 255;
                }else
                    args[2] -= size;
                args[3] = 255;
            }else {
                size = args[3];
                size = count-size < 0 ? count : size;
                args[3] -= size;
            }
            count -= size;
        }

        return String.valueOf(args[0]) + "." + String.valueOf(args[1]) + "." + String.valueOf(args[2]) + "." + String.valueOf(args[3]);
    }

    public static int minus(String firstAddress, String secondAddress){
        if(firstAddress == null || secondAddress == null)
            return -1;

        String[] strs1 = firstAddress.split("\\.");
        String[] strs2 = secondAddress.split("\\.");

        if(strs1.length != 4 || strs2.length != 4)
            return -1;

        int[] args1 = new int[4];
        int[] args2 = new int[4];
        for(int i = 0; i < 4; i++) {
            args1[i] = Integer.parseInt(strs1[i]);
            args2[i] = Integer.parseInt(strs2[i]);
        }



        int returnCount = 0;
        while(args1[0] != args2[0] ||
                args1[1] != args2[1] ||
                args1[2] != args2[2] ||
                args1[3] != args2[3]){

            if(args1[3] == 0){
                if(args1[2] == 0){
                    if(args1[1] == 0){
                        if(args1[0] == 0){
                            args1[0] = 255;
                        }else
                            args1[0]--;
                        args1[1] = 255;
                    }else
                        args1[1]--;
                    args1[2] = 255;
                }else
                    args1[2]--;
                args1[3] = 255;
            }else {
                args1[3]--;
            }
            returnCount++;
        }
        return returnCount;
    }

}

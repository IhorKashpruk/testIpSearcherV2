package com.Igor.SearchIp;

/**
 * Created by igor on 03.08.16.
 */
public abstract class SiecModel {
    public static String[] getCollumnsName(){return new String[]{};}
    public abstract String getValue(String key);
    public abstract void setValue(String key, String value);
    public abstract String[] getColumnsValue();
}

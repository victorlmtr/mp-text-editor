package com.victorlmtr.mptextedit.model;


import java.util.List;

public class HexData {
    private String hex;
    private String data;
    private int originalLength;
    private boolean editable;

    public HexData(String hex, String data, int originalLength, boolean editable) {
        this.hex = hex;
        this.data = data;
        this.originalLength = originalLength;
        this.editable = editable;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getOriginalLength() {
        return originalLength;
    }

    public void setOriginalLength(int originalLength) {
        this.originalLength = originalLength;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}

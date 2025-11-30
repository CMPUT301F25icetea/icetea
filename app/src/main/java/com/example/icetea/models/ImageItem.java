package com.example.icetea.models;

public class ImageItem {
    private String type;
    private String id;
    private String base64;

    public ImageItem() {
        // req
    }
    public ImageItem(String type, String id, String base64) {
        this.type = type;
        this.id = id;
        this.base64 = base64;
    }

    public String getType() {
        return type;
    }
    public String getId() {
        return id;
    }
    public String getBase64() {
        return base64;
    }
    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }
}

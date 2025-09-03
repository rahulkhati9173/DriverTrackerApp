package com.example.drivertracker;

public class PhotoItem {
    public String filename;
    public String path;
    public String thumbnail;

    public PhotoItem() {}

    public PhotoItem(String filename, String path, String thumbnail) {
        this.filename = filename;
        this.path = path;
        this.thumbnail = thumbnail;
    }
}

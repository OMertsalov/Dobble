package com.dobble.cards;

import java.io.Serializable;

public class ImageOnCard implements Serializable {

    private String path;
    private int number;

    public ImageOnCard(String path,int number) {
        this.path = path;
        this.number = number;
    }

    public String getPath() {
        return path;
    }
}

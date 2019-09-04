package com.dobble.cards;

import java.io.Serializable;
import java.util.ArrayList;

public class Card implements Serializable {

    private String path;
    private int number;
    private ArrayList<ImageOnCard> images;

    public Card(ArrayList<ImageOnCard> images,int number,String path) {
        this.path=path;
        this.number = number;
        this.images = images;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<ImageOnCard> getImages() {
        return images;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

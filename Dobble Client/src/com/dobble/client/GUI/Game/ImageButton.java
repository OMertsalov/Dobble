package com.dobble.client.GUI.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageButton extends JButton {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage(String path) {
        BufferedImage img = null;
        try { img = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image scaledImg = img.getScaledInstance(120, 120,Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(scaledImg));
    }

}

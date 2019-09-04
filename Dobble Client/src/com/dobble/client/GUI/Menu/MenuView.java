package com.dobble.client.GUI.Menu;

import com.dobble.client.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuView extends JFrame {

    public static JButton findButton;

    private static MenuView menuView_instance = null;

    public static MenuView Create()
    {
        if (menuView_instance==null)
            menuView_instance=new MenuView();
        return menuView_instance;
    }

    public static MenuView getInstance() {
        return menuView_instance;
    }

    private MenuView() throws HeadlessException {
        super("Menu");
        setResizable(false);
        setBounds(587,220,800,550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();
        container.setLayout(null);

        JLabel image = new JLabel();
        image.setIcon(ScaleImage("tmp/images/Dobble.png",780,450));
        image.setBounds(10,10,780,450);
        image.setBorder(new BevelBorder(BevelBorder.LOWERED));



        findButton = new JButton("Find game");
        findButton.setBounds(10,470,130,35);
        findButton.setFont(new Font("Courier", Font.BOLD,17));
        findButton.setEnabled(false);
        findButton.setBackground(Color.RED);
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client.joinRoom();
            }
        });

        JButton optionsButton = new JButton("Options");
        optionsButton.setBounds(150,470,120,35);
        optionsButton.setFont(new Font("Courier", Font.BOLD,17));
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
                OptionsView.getInstance().setVisible(true);
            }
        });

        container.add(image);
        container.add(findButton);
        container.add(optionsButton);
        setVisible(true);
    }


    public static ImageIcon ScaleImage(String path,int width,int height)
    {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image dimg = img.getScaledInstance(width, height,Image.SCALE_SMOOTH);

        return new ImageIcon(dimg);
    }


    public JButton getFindButton() {
        return findButton;
    }
}



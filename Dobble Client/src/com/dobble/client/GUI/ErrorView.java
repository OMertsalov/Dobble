package com.dobble.client.GUI;

import com.dobble.client.Client;
import com.dobble.client.GUI.Menu.MenuView;
import com.dobble.client.GUI.Menu.PrepareView;
import com.dobble.protocol.DBLgame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ErrorView extends JFrame {

    public static final byte CONNECTION_ERROR = 100;

    private static ErrorView errorView_instance = null;
    private static JLabel textError;

    public static ErrorView Create()
    {
        if (errorView_instance==null)
            errorView_instance=new ErrorView();
        return errorView_instance;
    }

    private ErrorView() throws HeadlessException {
        super("Error");
        setResizable(false);
        setAlwaysOnTop(true);
        setBounds(730,307,500,220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();
        container.setLayout(null);


        JLabel label = new JLabel();
        label.setBounds(10,10,480,160);
        label.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JButton okButton = new JButton("Ok");
        okButton.setBounds(210,130,80,30);
        okButton.setFont(new Font("Courier", Font.BOLD,17));

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errorView_instance.setVisible(false);
                Client.resetViews();
            }
        });

        textError = new JLabel("Connections error.Check internet connection and try again");
        textError.setBounds(20,20,460,130);
        textError.setVerticalAlignment(SwingConstants.TOP);
        textError.setForeground(Color.RED);

        container.add(textError);
        container.add(okButton);
        container.add(label);
    }

    public static void call(byte error) {
        Client.disableAllViews();
        errorView_instance.setVisible(true);
        switch (error){
            case CONNECTION_ERROR:
                textError.setText("Connections error.Check internet connection and try again.");
                MenuView.findButton.setEnabled(false);
                MenuView.findButton.setBackground(Color.RED);
                break;
            case DBLgame.PACKET_LENGTH :
                textError.setText("Sended packet has wrong length.");
                break;
            case DBLgame.PACKAGE_SYNTAX :
                textError.setText("Sended packet has wrong syntax.");
                break;
            case DBLgame.OPTION :
                textError.setText("Sended packet has wrong Option.");
                break;
            case DBLgame.FIELD :
                textError.setText("Sended packet has wrong Field.");
                break;
            case DBLgame.PROTOCOL :
                textError.setText("Wrong Protocol.Server accept only \"DBLprot\" - Protocols.");
                break;
            case DBLgame.PROTOCOL_VERSION :
                textError.setText("Your Protocol Version is Out-of-date.");
                break;
            case DBLgame.CLIENT_VERSION :
                textError.setText("Your Client Version is Out-of-date.");
                break;
            case DBLgame.NICKNAME_CHARACHTERS:
                textError.setText("Your Nickname contains wrong charachters");
                break;
            case DBLgame.NICKNAME_LENGTH:
                textError.setText("Your Nickname length is WRONG");
                break;
            case DBLgame.ROOMS_ARE_FULL :
                textError.setText("Couldn't connect to room.Rooms are full.Try later...");
                break;
            case DBLgame.NOT_READY :
                textError.setText("Kicked from room");
//               if(PrepareView.getInstance().isVisible())
//                   PrepareView.restoreView();
               break;
//            case DBLgame.NOT_STARTED:
//                PrepareView.restoreButton();
//                break;
            }
        }
}

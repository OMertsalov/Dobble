package com.dobble.client.GUI.Menu;

import com.dobble.client.Client;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;

public class OptionsView extends JFrame {

    private static OptionsView optionsView_instance = null;

    public static OptionsView Create()
    {
        if (optionsView_instance==null)
            optionsView_instance=new OptionsView();
        return optionsView_instance;
    }

    public static OptionsView getInstance() {
        return optionsView_instance;
    }

    private OptionsView() throws HeadlessException {
        super("Options");
        setResizable(false);
        setAlwaysOnTop(true);
        setBounds(786,327,400,250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();
        container.setLayout(null);

        JLabel label = new JLabel();
        label.setBounds(10,10,380,195);
        label.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JLabel ipversion = new JLabel("Choose Ip version : ");
        ipversion.setBounds(20,20,210,35);
        ipversion.setFont(new Font("Courier", Font.BOLD,17));

        JRadioButton ipv4 = new JRadioButton("IPv4");
        ipv4.setBounds(220,20,75,35);
        ipv4.setFont(new Font("Courier", Font.BOLD,17));
        ipv4.setSelected(true);




        JRadioButton ipv6 = new JRadioButton("IPv6");
        ipv6.setBounds(295,20,75,35);
        ipv6.setFont(new Font("Courier", Font.BOLD,17));


        ButtonGroup group = new ButtonGroup();
        group.add(ipv4);
        group.add(ipv6);



        JLabel nickname = new JLabel("Your Nickname : ");
        nickname.setBounds(20,65,190,35);
        nickname.setFont(new Font("Courier", Font.BOLD,17));

        JTextField textField = new JTextField();
        textField.setBounds(200,65,170,30);
        textField.setFont(new Font("Courier", Font.BOLD,17));
        textField.setText("player");

        JLabel info = new JLabel("<html>Nickname should be<br/>of length 3-16!!</html>");
        info.setBounds(20,110,190,70);
        info.setFont(new Font("Default", Font.ITALIC,16));
        info.setBorder(new TitledBorder("Note"));
        info.setForeground(Color.RED);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBounds(240,150,130,35);
        confirmButton.setFont(new Font("Courier", Font.BOLD,17));
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(ipv4.isSelected())
                        Client.ipAddress = Inet4Address.getByName("localhost");
                    else
                        Client.ipAddress = Inet6Address.getByName("::1");
                    Client.nickname = textField.getText();
                    Client.restartConnection();
                    setVisible(false);
                    MenuView.getInstance().setEnabled(true);
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
            }
        });


        container.add(info);
        container.add(confirmButton);
        container.add(nickname);
        container.add(textField);
        container.add(ipversion);
        container.add(label);
        container.add(ipv4);
        container.add(ipv6);

    }

}

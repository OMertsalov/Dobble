package com.dobble.client.GUI.Menu;

import com.dobble.client.GUI.Game.Player;
import com.dobble.client.Client;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PrepareView extends JFrame {

    private static Container container;
    private static JLabel roomNbr;
    private static JLabel gameId;
    private static JLabel playersNbr;
    private static List<Player> players;
    private static JPanel playersPanel;
    private static int maxPlayersNbr;
    private static JButton readyButton;


    private static PrepareView prepareView_instance = null;

    public static PrepareView Create()
    {
        if (prepareView_instance==null)
            prepareView_instance=new PrepareView();
        return prepareView_instance;
    }

    public static PrepareView getInstance() {
        return prepareView_instance;
    }

    private PrepareView() throws HeadlessException {
        super("Prepearing");
        setResizable(false);
        setAlwaysOnTop(true);
        setBounds(730,307,500,350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container = getContentPane();
        container.setLayout(null);


        JLabel label = new JLabel();
        label.setBounds(10,10,480,295);
        label.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JLabel text = new JLabel("Waiting for players");
        text.setBounds(135,20,240,35);
        text.setFont(new Font("Courier", Font.BOLD,20));

        JLabel text1 = new JLabel("Room nbr : ");
        text1.setBounds(20,70,130,35);
        text1.setFont(new Font("Courier", Font.ITALIC,18));

        roomNbr = new JLabel("0");
        roomNbr.setBounds(140,70,40,35);
        roomNbr.setFont(new Font("Courier", Font.ITALIC,18));

        JLabel text2 = new JLabel("Game id : ");
        text2.setBounds(20,90,135,35);
        text2.setFont(new Font("Courier", Font.ITALIC,18));

        gameId = new JLabel("0");
        gameId.setBounds(135,90,40,35);
        gameId.setFont(new Font("Courier", Font.ITALIC,18));

        JLabel text3 = new JLabel("Players : ");
        text3.setBounds(20,110,130,35);
        text3.setFont(new Font("Courier", Font.ITALIC,18));

        playersNbr = new JLabel("0/1");
        playersNbr.setBounds(130,110,50,35);
        playersNbr.setFont(new Font("Courier", Font.ITALIC,18));

        JButton leaveButton = new JButton("Leave");
        leaveButton.setBounds(350,100,130,35);
        leaveButton.setFont(new Font("Courier", Font.BOLD,17));
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client.leaveRoom();
                Client.resetViews();
            }
        });


        readyButton = new JButton("Ready");
        readyButton.setBounds(350,60,130,35);
        readyButton.setFont(new Font("Courier", Font.BOLD,17));
        readyButton.setVisible(false);

        readyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readyButton.setEnabled(false);
                readyButton.setBackground(Color.GREEN);
                Client.setReady();
            }
        });

        playersPanel = new JPanel(null);
        //playersPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        playersPanel.setBounds(20,170,460,130);
        container.add(playersPanel);
        container.add(playersNbr);
        container.add(gameId);
        container.add(readyButton);
        container.add(leaveButton);
        container.add(text2);
        container.add(text3);
        container.add(roomNbr);
        container.add(text1);
        container.add(text);
        container.add(label);
    }

    public static void addPlayer(byte[] playerData){
        String data = new String(playerData, Charset.forName("UTF-8"));
        int id = Integer.parseInt(data.substring(0,data.indexOf('\r')));
        String nickname = data.substring(data.indexOf('\r')+1,data.indexOf('\n'));
        Player player = new Player(id,nickname);
        players.add(player);
        add(player);
        //setPlayers();
        setPlayersNbr();
    }

    public static void deletePlayer(int id,JPanel panel){
        Player player = players.stream().filter(p -> id == p.getId()).findAny().orElse(null);
        players.remove(player);
        remove(player,panel);
        setPlayersNbr();
       // setPlayers();
    }

    public static void addPlayers(byte[] playersData){
        players = new ArrayList<>();
        int id = 0;
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<playersData.length;i++){
            char c = (char) playersData[i];
            if(c == '\r') {
                id = Integer.parseInt(sb.toString());
                sb = new StringBuilder();
            } else if (c == '\n'){
                Player player = new Player(id,sb.toString());
                players.add(player);
                add(player);
                sb = new StringBuilder();
            } else sb.append(c);
        }
        //setPlayers();
        setPlayersNbr();
    }

    public static void restoreView(){
        PrepareView.getInstance().setVisible(false);
        prepareView_instance=null;
        Create();
        MenuView.getInstance().setEnabled(true);
    }
    public static void restoreButton(){
        readyButton.setBackground(Color.RED);
        readyButton.setVisible(false);
        readyButton.setEnabled(true);
    }

    private static void add(Player player){
        player.setBounds(0,(players.size()-1)*30,460,30);
        player.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        playersPanel.add(player);
        playersPanel.repaint();
    }

    private static void remove(Player player,JPanel panel){
        panel.remove(player);
        for(Component c : panel.getComponents())
            if(player.getLocation().y<c.getLocation().y)
                c.setLocation(0, c.getLocation().y-30);
        panel.repaint();
    }



    public static void setRoomNbr(int nbr) {
        roomNbr.setText(String.valueOf(nbr));
    }

    public static void setGameId(int id) {
        gameId.setText(String.valueOf(id));
    }

    public static void setMaxPlayersNbr(int nbr){
        maxPlayersNbr = nbr;
    }

    private static void setPlayersNbr() {
        playersNbr.setText(players.size()+"/"+maxPlayersNbr);
    }

    public static JPanel getPlayersPanel() {
        return playersPanel;
    }

    public static void askIfReady() {
        readyButton.setVisible(true);
        readyButton.setBackground(Color.RED);
    }

    public static List<Player> getPlayers() {
        return players;
    }
}

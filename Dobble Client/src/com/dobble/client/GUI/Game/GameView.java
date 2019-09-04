package com.dobble.client.GUI.Game;

import com.dobble.cards.ImageOnCard;
import com.dobble.client.GUI.Menu.MenuView;
import com.dobble.client.GUI.Menu.PrepareView;
import com.dobble.client.Client;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameView extends JFrame {

    private static JLabel serverCard;
    private static JPanel serverPanel;
    private static JPanel playersPanel;
    private static List<ImageButton> clientCard;
    public static int answer;

    private static GameView gameView_instance = null;

    public static GameView Create()
    {
        if (gameView_instance==null)
            gameView_instance=new GameView();
        return gameView_instance;
    }

    public static GameView getInstance() {
        return gameView_instance;
    }


    private GameView() throws HeadlessException {
        super("Client");
        setResizable(false);
        setBounds(610,130,710,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();
        container.setLayout(null);

        serverPanel = new JPanel();
        serverPanel.setBounds(10,10,350,550);
        serverPanel.setBorder(new TitledBorder("Server card"));

        serverCard = new JLabel();
        serverCard.setBounds(40,45,240,480);
        serverPanel.add(serverCard);
        //serverCard.setVerticalAlignment(JLabel.CENTER);
        //serverCard.setHorizontalAlignment(JLabel.CENTER);

        JLabel label = new JLabel();
        label.setBounds(370,10,330,550);
        label.setBorder(new TitledBorder("Your card"));

//        JLabel label1 = new JLabel();
//        label1.setBounds(11,570,685,180);
//        label1.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JButton leaveButton = new JButton("Leave");
        leaveButton.setBounds(555,705,130,35);
        leaveButton.setFont(new Font("Courier", Font.BOLD,17));
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client.leaveRoom();
                Client.resetViews();
            }
        });

        clientCard = new ArrayList<>();
        int x = 410, y = 55;
        for(int i=0;i<8;i++,y += 120)
        {
            if(i==4){ x += 120; y = 55;}
            ImageButton imageButton = new ImageButton();
            imageButton.setBounds(x,y,120,120);
            int finalI = i;
            imageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    answer = finalI;
                    Client.sendAnswer();
                }
            });
            imageButton.setVisible(false);

            clientCard.add(imageButton);
            container.add(imageButton);
        }

        playersPanel = new JPanel(null);
        playersPanel.setBounds(11,600,400,140);
        //playersPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));


        container.add(serverPanel);
        container.add(leaveButton);
//        container.add(label1);
        container.add(label);
        container.add(playersPanel);
        //container.add(serverCard);
    }

    public static void start(int amountOfCards) {
        PrepareView.getInstance().setVisible(false);
        MenuView.getInstance().setVisible(false);
        int y=0;
        for(Player player : PrepareView.getPlayers()){
            player.setAmountOfCards(amountOfCards);
            add(player,y);
            y+=29;
        }
        gameView_instance.setVisible(true);
    }

    private static void add(Player player,int y){
        player.setBounds(0,y,399,30);
        player.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        player.showCardsSum();
        playersPanel.add(player);
        playersPanel.repaint();
    }

    public static JPanel getPlayersPanel() {
        return playersPanel;
    }

    public static void restoreView(){
        GameView.getInstance().setVisible(false);
        gameView_instance=null;
        Create();
    }

    public static void setClientCard(ArrayList<ImageOnCard> images){
        for(int i=0;i<images.size();i++)
        {
            clientCard.get(i).setId(i);
            String imagePath = images.get(i).getPath();
            clientCard.get(i).setImage(imagePath);
            clientCard.get(i).setVisible(true);
        }
        if(images.size()==7) clientCard.get(7).setVisible(false);
    }

    public static void setServerCard(String path)
    {
        ImageIcon image = (MenuView.ScaleImage(path,240,480));
        serverCard.setIcon(image);
        serverPanel.repaint();
    }

    public static void decreaseCardsSum(int playerId) {
        System.out.println(playerId);
        PrepareView.getPlayers().stream()
                .filter(p -> playerId == p.getId())
                .findAny()
                .orElse(null)
                .decreaseAmountOfCards();
        playersPanel.repaint();
    }
}

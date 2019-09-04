package com.dobble.client.GUI.Game;

import javax.swing.*;
import java.awt.*;

public class Player extends JPanel{

    private JLabel id;
    private int amountOfCards;
    private JLabel nickname;
    private JLabel ready;
    private JLabel cardsSum;

    public Player(int id, String nickname) {
        setLayout(null);
        this.id = new JLabel(String.valueOf(id));
        this.id.setBounds(10,0,25,30);
        add(this.id);
        this.nickname = new JLabel(nickname);
        this.nickname.setBounds(100,0,200,30);
        add(this.nickname);
        ready = new JLabel("No");
        ready.setBounds(430,0,200,30);
        add(ready);
    }


    public void showCardsSum(){
//        id.setBounds(15,0,25,30);
        id.setLocation(15,0);
        nickname.setLocation(60,0);
//        nickname.setBounds(60,0,200,30);
        remove(ready);
        cardsSum = new JLabel(String.valueOf(amountOfCards));
        cardsSum.setBounds(310,0,25,30);
        add(cardsSum);
    }

    public int getId() {
        return Integer.parseInt(id.getText());
    }

    public void setId(JLabel id) {
        this.id = id;
    }

    public int getAmountOfCards() {
        return amountOfCards;
    }

    public void setAmountOfCards(int amountOfCards) {
        this.amountOfCards = amountOfCards;
    }

    public void decreaseAmountOfCards(){
        amountOfCards--;
        cardsSum.setText(String.valueOf(amountOfCards));
    }

    public String getNickname() {
        return nickname.getText();
    }

    public void setNickname(JLabel nickname) {
        this.nickname = nickname;
    }

    public JLabel getReady() {
        return ready;
    }

    public void setReady(JLabel ready) {
        this.ready = ready;
    }
}

package com.dobble.server.Client;

import com.dobble.server.Room;
import com.dobble.server.Client.Messaging.MsgReciever;
import com.dobble.server.Client.Messaging.MsgSender;
import com.dobble.cards.Card;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;


public class Client {

    private Socket  socket;

    private MsgReciever msgReciever;
    private MsgSender msgSender;

    private String nickname;
    private Room room;
    private int id;

    private int cardSum;
    private Card card;


    private boolean ready;
    private boolean finishedGame;
    private boolean handshaked;

    //Constructor
    public Client(Socket socket) {
        this.socket = socket;
    }

    public void startCommunication(){
        msgReciever = new MsgReciever(this,socket);
        msgReciever.setName("MsgReciever "+id);
        msgReciever.start();
        msgSender = new MsgSender(this,socket);
    }

    public void stopMessaging(){
        try {
            msgSender.stopSend();
            msgReciever.stopRecieve();
            if(!socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreData() {
        nickname=null;
        room=null;
        card=null;
        ready=false;
    }


    public void setNickname(String Nickname) {
        this.nickname = Nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCard(Card Card) {
        this.card = Card;
    }

    public Card getCard() {
        return card;
    }

    public void setRoom(Room Room) { this.room = Room; }

    public Room getRoom() {
        return room;
    }

    public int getCardSum() {
        return cardSum;
    }

    public void setCardsSum(int cardSum) {
        this.cardSum = cardSum;
    }

    public void decreaseCardSum() {
        cardSum--;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() { return ready; }

    public MsgSender getMsgSender() { return msgSender; }

    public Socket getSocket() {
        return socket;
    }

    public boolean isHandshaked() {
        return handshaked;
    }

    public void setHandshaked(boolean handshaked) {
        this.handshaked = handshaked;
    }
}

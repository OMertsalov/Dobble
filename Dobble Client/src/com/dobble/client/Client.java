package com.dobble.client;

import com.dobble.client.GUI.ErrorView;
import com.dobble.client.GUI.Game.GameView;
import com.dobble.client.GUI.Menu.MenuView;
import com.dobble.client.GUI.Menu.OptionsView;
import com.dobble.client.GUI.Menu.PrepareView;
import com.dobble.client.Messaging.MsgReciever;
import com.dobble.client.Messaging.MsgSender;
import com.dobble.protocol.DBLgame;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public  static Socket socket;
    private static MsgSender msgSender;
    private static MsgReciever msgReciever;
    public static InetAddress ipAddress;

    public static final String  clientVersion = "2.0";
    public static final DBLgame protocol = DBLgame.Create();
    public static String nickname = "player";


    private static Client client_instance = null;
    public static boolean connected;

    public static Client Create()
    {
        if (client_instance ==null)
            client_instance =new Client();
        return client_instance;
    }

    private Client() {

        MenuView.Create();
        OptionsView.Create();
        PrepareView.Create();
        ErrorView.Create();
        GameView.Create();

        try {
            ipAddress = Inet4Address.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        connect();
    }

    public static void resetViews(){
        GameView.restoreView();
        PrepareView.restoreView();
        MenuView.getInstance().setVisible(true);
        MenuView.getInstance().setEnabled(true);
        GameView.getInstance().setEnabled(true);
        PrepareView.getInstance().setEnabled(true);
        OptionsView.getInstance().setEnabled(true);
    }

    public static void disableAllViews(){
        MenuView.getInstance().setEnabled(false);
        GameView.getInstance().setEnabled(false);
        PrepareView.getInstance().setEnabled(false);
        OptionsView.getInstance().setEnabled(false);
    }

    private static void connect()
    {
        try {
            socket = new Socket(ipAddress, 8080);
            msgReciever = new MsgReciever();
            msgReciever.start();
            msgSender = new MsgSender();
            msgSender.writeData(MsgSender.HANDSHAKE);
            connected=true;
        } catch (IOException e) {
            ErrorView.call(ErrorView.CONNECTION_ERROR);
        }
    }

    public static void restartConnection()
    {
        if(msgReciever!=null && socket!=null)
            dissconnect();
        connect();
    }

    public static void leaveRoom(){
        msgSender.writeData(MsgSender.LEAVE_ROOM);
    }

    public static void dissconnect() {
        if(connected) {
            try {
                msgReciever.stopRecieve();
                msgSender.stopSend();
                socket.close();
                connected=false;
                ErrorView.call(ErrorView.CONNECTION_ERROR);
            } catch (IOException e) {

            }
        }
    }

    public static void joinRoom() {
        msgSender.writeData(MsgSender.JOIN_ROOM);
    }

    public static void setReady() {
        msgSender.writeData(MsgSender.READY);
    }


    public static void sendAnswer(){
        msgSender.writeData(MsgSender.ANSWER);
    }
}

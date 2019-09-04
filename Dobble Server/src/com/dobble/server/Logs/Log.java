package com.dobble.server.Logs;

import com.dobble.server.Client.Client;
import com.dobble.server.Room;
import com.dobble.server.Server;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    private static final int ID_CLIENT_ACTION = 10;
    public static final int CLIENT_CONNECTED = 11;
    public static final int CLIENT_DISSCONNECTED = 12;
    public static final int PLAYER_JOIN_ROOM = 13;
    public static final int PLAYER_LEFT_ROOM = 14;


    private static final int ID_ROOM_ACTION = 20;
    public static final int ROOM_STAGE_CHANGED = 21;


    private static final int ID_MESSAGE_ACTION = 30;
    public static final int CLIENT_SEND_PACKET = 31;
    public static final int SERVER_SEND_PACKET = 32;




    private Logger logger;

    public Log (String filename){
        try {
            File file = new File(filename);
            if(!file.exists())
                file.createNewFile();
            FileHandler fileHandler = new FileHandler(filename, true);
            logger = Logger.getLogger("MainProgram");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            getLogger().setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Logger getLogger() {
        return logger;
    }

    public void write(int header, Client client)
    {
        String info = null;
        switch (header)
        {
            case CLIENT_CONNECTED:
                info = "CLIENT CONNECTED"+
                        "\n  ip : "+client.getSocket().getInetAddress()+
                        "\n Player id : "+client.getId()+
                        "\n Number of connected to server clients : "+
                        Server.clientList.size()+'/'+Server.maxClientsNbr;
                break;
            case CLIENT_DISSCONNECTED:
                info = "CLIENT DISSCONNECTED"+
                        "\n  ip : "+client.getSocket().getInetAddress()+
                        "\n Player id : "+client.getId()+
                        "\n Number of connected to server clients : "+
                        Server.clientList.size()+'/'+Server.maxClientsNbr;
                break;
            case PLAYER_JOIN_ROOM:
                info = "PLAYER JOINED ROOM"+
                        "\n Room number : "+client.getRoom().getNbr()+
                        "\n Player ip : "+client.getSocket().getInetAddress()+
                        "\n Player id : "+client.getId()+
                        "\n Player nickname : "+client.getNickname()+
                        "\n Players in room : "+client.getRoom().getPlayerList().size()+
                        '/' + client.getRoom().getPlayersLimit();
                break;
            case PLAYER_LEFT_ROOM:
                info = "PLAYER LEFT ROOM"+
                        "\n Room number : "+client.getRoom().getNbr()+
                        "\n Player ip : "+client.getSocket().getInetAddress()+
                        "\n Player id : "+client.getId()+
                        "\n Player nickname : "+client.getNickname()+
                        "\n Players in room : "+client.getRoom().getPlayerList().size()+
                        '/' + client.getRoom().getPlayersLimit();
                break;
        }
        this.getLogger().info("\nLogID : "+ID_CLIENT_ACTION+'\n'+info
                +"\n________________________________________________________________\n");
    }

    public void write(int header, Room room)
    {
        String info = null;
        switch (header)
        {
            case ROOM_STAGE_CHANGED:
                info = "ROOM_STAGE_CHANGED"+
                        "\n Room number : "+room.getNbr()+
                        "\n GameID : "+room.getGameId()+
                        "\n Stage number : "+ room.getStage();
                break;
        }
        this.getLogger().info("\nLogID : "+ID_ROOM_ACTION+'\n'+info
                +"\n________________________________________________________________\n");
    }

    public void write(int header,Client client,byte... message)
    {
        String info = null;
        switch (header)
        {
            case CLIENT_SEND_PACKET:
                info = "CLIENT_SEND_PACKET"+
                        "\n Player ip : "+client.getSocket().getInetAddress()+
                        "\n Player id : "+client.getId()+
                        "\n Packet length : "+message.length+
                        "\n Packet :\n"
                        + Arrays.toString(message);
                break;
            case SERVER_SEND_PACKET:
                info = "SERVER_SEND_PACKET"+
                        "\n Player ip : "+client.getSocket().getInetAddress()+
                        "\n Player id : "+client.getId()+
                        "\n Packet length : "+message.length+
                        "\n Packet :\n"
                        + Arrays.toString(message);
                break;
        }
        this.getLogger().info("\nLogID : "+ID_MESSAGE_ACTION+'\n'+info
                +"\n________________________________________________________________\n");
    }
}

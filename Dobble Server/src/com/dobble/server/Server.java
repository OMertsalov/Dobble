package com.dobble.server;

import com.dobble.server.Logs.Log;
import com.dobble.server.Client.Client;
import com.dobble.protocol.DBLgame;


import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class Server {

    public static final DBLgame protocol = DBLgame.Create();
    public static final String REQUIRED_CLIENT_VER="2.0";

    private static final int roomsAmount = 4;
    public static final int maxClientsNbr = 4;

    public static final List<Client> clientList = new ArrayList<>();
    public static final List<Room> roomList = new ArrayList<>();

    public static final Log serverLogger = new Log("ServerLogs.txt");
    private static Server server_instance = null;
    private static int id=1;

    static void Create()
    {
        if (server_instance==null)
            server_instance=new Server();
    }

    //Constructor
    private Server() {
        for(int i=0;i<roomsAmount;i++)
            roomList.add(new Room(i));
        listen();
    }

    //Start Thread's ,to listen for connections on IPV4/IPV6 Socket's
    private void listen(){
        try {
            InetAddress IPV4_address = Inet4Address.getByName("localhost");
            ServerSocket IPV4_SERV_SOCKET = new ServerSocket(8080
                    ,maxClientsNbr,IPV4_address);
            InetAddress IPV6_address = Inet6Address.getByName("::1");
            ServerSocket IPV6_SERV_SOCKET = new ServerSocket(8080
                    ,maxClientsNbr,IPV6_address);
        new Thread(new Listener(IPV4_SERV_SOCKET)).start();
        new Thread(new Listener(IPV6_SERV_SOCKET)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void clientListAdd(Client client) {
        synchronized (clientList){
            client.setId(id);
            id++;
            clientList.add(client);
            serverLogger.write(Log.CLIENT_CONNECTED,client); //Adding logs
            client.startCommunication();
        }
    }

    public static void clientListRemove(Client client) {
        synchronized (clientList){
            client.stopMessaging();
            if(client.getRoom()!=null)
                client.getRoom().removePlayer(client);
            clientList.remove(client);
            serverLogger.write(Log.CLIENT_DISSCONNECTED,client); //Adding logs
        }
    }
}

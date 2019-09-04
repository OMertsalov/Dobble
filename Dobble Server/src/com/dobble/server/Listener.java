package com.dobble.server;

import com.dobble.server.Client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable{

    private ServerSocket    serverSocket;

    //Constructor
    public Listener(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted())
        {
            try {
                Socket clientSocket = serverSocket.accept();
                if(Server.clientList.size() < Server.maxClientsNbr)
                    Server.clientListAdd(new Client(clientSocket));
                else clientSocket.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

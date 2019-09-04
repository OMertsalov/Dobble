package com.dobble.server.Client.Messaging;


import com.dobble.server.Client.Client;
import com.dobble.server.Logs.Log;
import com.dobble.server.Server;
import com.dobble.protocol.DBLgame;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class MsgSender {
    private Client client;
    private DataOutputStream out;

    public MsgSender(Client client, Socket socket) {
        this.client = client;
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeData(byte option,byte field,String value){
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            DataOutputStream dataWriter = new DataOutputStream(data);
            dataWriter.writeByte(option);
            dataWriter.writeByte(field);
            byte code;
            switch (field) {
                case DBLgame.JOIN:
                    dataWriter.writeByte((byte)client
                            .getRoom().getNbr());
                    dataWriter.writeByte((byte)client
                            .getRoom().getGameId());
                    dataWriter.writeByte((byte)client.getRoom().getPlayersLimit());
                    byte []players = value.getBytes(Charset.forName("UTF-8"));
                    dataWriter.write(players,0,players.length);
                    break;
                case DBLgame.CLIENT_CARD :
                    ObjectOutputStream os = new ObjectOutputStream(dataWriter);
                    os.writeObject(client.getCard());
                    os.close();
                    break;
                case DBLgame.SERVER_CARD :
                    File serverCardImage = new File(value);
                    FileInputStream image = new FileInputStream(serverCardImage);
                    byte [] imageData = new byte[(int) serverCardImage.length()];
                    image.read(imageData,0,imageData.length);
                    image.close();
                    dataWriter.write(imageData,0,imageData.length);
                    break;
                case DBLgame.NEW_PLAYER:
                case DBLgame.END:
                    byte[] array = value.getBytes(Charset.forName("UTF-8"));
                    dataWriter.write(array,0,array.length);
                    break;
                default:
                    code = Byte.parseByte(value);
                    if(code!=0)
                    dataWriter.writeByte(code);
            }
            dataWriter.flush();
            createPacket(data);
            data.close();
            dataWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPacket(ByteArrayOutputStream data){
            try {
                ByteArrayOutputStream packet = new ByteArrayOutputStream();
                DataOutputStream packetWriter = new DataOutputStream(packet);
                packetWriter.write(new byte[]{0x64,0x6f,0x62});
                packetWriter.writeInt(data.size()+10);
                packetWriter.write(data.toByteArray(),0,data.size());
                packetWriter.write(new byte[]{0x62,0x6c,0x65});
                packetWriter.flush();
                sendPacket(packet.toByteArray());
                packet.close();
                packetWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private void sendPacket(byte[] packet) {
        try {
            out.write(packet,0,packet.length);
            out.flush();
            Server.serverLogger.write(Log.SERVER_SEND_PACKET,client,packet);
        } catch (IOException e) {
            Server.clientListRemove(client);
        }
    }

    public void stopSend() throws IOException { out.close(); }

}

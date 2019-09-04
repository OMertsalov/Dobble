package com.dobble.server.Client.Messaging;

import com.dobble.cards.ImageOnCard;
import com.dobble.server.Logs.Log;
import com.dobble.server.Client.Client;
import com.dobble.server.Room;
import com.dobble.server.Server;
import com.dobble.protocol.DBLgame;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;


public class MsgReciever extends Thread{

    private Client client;
    private DataInputStream in;

    public MsgReciever(Client client,Socket socket) {
        this.client = client;
        try {
            in  = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            byte []packet = recievePacket();
            if(packet!=null && isDBLprotData(packet)){
                Server.serverLogger.write(Log.CLIENT_SEND_PACKET,client,packet);
                if(client.isHandshaked()) readPacket(packet);
                else if(packet[7]==DBLgame.HANDSHAKE && packet.length == 15)
                    readHandshakePacket(packet[8],packet[9],packet[10],packet[11]);
                else client.getMsgSender().writeData(DBLgame.PACKET_DATA
                            ,DBLgame.ERROR
                            ,String.valueOf(DBLgame.PACKAGE_SYNTAX));
            }
        }
    }

    private byte[] recievePacket(){
        try {
            byte first = in.readByte();
            byte[] p = new byte[6];
            in.read(p, 0, 6);
            int packetLength = byteToInt(new byte[]{p[5], p[4], p[3], p[2]});
            if (packetLength < DBLgame.MAX_PACKET_LENGTH && packetLength > 7) {
                ByteArrayOutputStream packet = new ByteArrayOutputStream();
                packet.write(first);
                packet.write(p, 0, 6);
                p = new byte[packetLength - 7];
                in.read(p, 0, p.length);
                packet.write(p, 0, p.length);
                return packet.toByteArray();
            }
            in.skip(in.available());
            client.getMsgSender().writeData(DBLgame.PACKET_DATA
                  ,DBLgame.ERROR
                  ,String.valueOf(DBLgame.PACKET_LENGTH));
        } catch (IOException e) {
            Server.clientListRemove(client);
        }
        return null;
    }

    private int byteToInt(byte[] b)
    {
        int value = 0;
        value += (b[3] & 0x000000FF) << 24;
        value += (b[2] & 0x000000FF) << 16;
        value += (b[1] & 0x000000FF) << 8;
        value += (b[0] & 0x000000FF);
        return value;
    }

    private void readPacket(byte[] packet) {
        byte option=packet[7];
            switch (option) {
                case DBLgame.ROOM:
                    readRoomPacket(packet);
                    break;
                case DBLgame.RESPONSE :
                    readResponsePacket(packet[8]);
                    break;
                case DBLgame.GAME :
                    readGamePacket(packet[8],packet[9]);
                    break;
                default:
                    client.getMsgSender().writeData(DBLgame.PACKET_DATA
                            ,DBLgame.ERROR
                            ,String.valueOf(DBLgame.OPTION));
            }

    }

    private boolean isDBLprotData(byte[] m) {
        byte[] packet = {m[0],m[1],m[2],m[m.length-3],m[m.length-2],m[m.length-1]};
        if(new String(packet).equals("dobble"))
            return true;
        client.getMsgSender().writeData(DBLgame.PACKET_DATA
                ,DBLgame.ERROR
                ,String.valueOf(DBLgame.PACKAGE_SYNTAX));
        return false;
    }

    private void readHandshakePacket(byte field,byte protocol,byte protVer,byte clntVer) {
        if(field == DBLgame.CONNECT){
            if (protocolIsOk(protocol,protVer) && clientVerIsOk(clntVer)){
                client.getMsgSender().writeData(DBLgame.HANDSHAKE
                        ,field,
                        String.valueOf(DBLgame.SUCCESS));
                client.setHandshaked(true);
            }
        } else
            client.getMsgSender().writeData(DBLgame.PACKET_DATA
                    ,DBLgame.ERROR,String.valueOf(DBLgame.FIELD));
    }

    private boolean protocolIsOk(byte clientP,byte clientPVer) {
        if(Server.protocol.getID() == clientP)
                return checkVer(Server.protocol.getVersion(),clientPVer);
        client.getMsgSender().writeData(DBLgame.HANDSHAKE
                ,DBLgame.ERROR
                ,String.valueOf(DBLgame.PROTOCOL));
        return false;
    }

    private boolean checkVer(String serverPVer, byte clVersion) {
        String clientPVer = byteToString(clVersion);
        if( clientPVer.equals(serverPVer) ) return true;
        client.getMsgSender().writeData(DBLgame.HANDSHAKE
                ,DBLgame.ERROR
                ,String.valueOf(DBLgame.PROTOCOL_VERSION));
        return false;
    }

    private String byteToString(byte b) {
        String binaryReprezent = String.format("%8s"
                ,Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
        int last4bits = Integer.parseInt(binaryReprezent.substring(0,4),2);
        int first4bits = Integer.parseInt(binaryReprezent.substring(4),2);
        return String.valueOf(last4bits) + '.' + first4bits;
    }

    private boolean clientVerIsOk(byte version) {
        String clVersion = byteToString(version);
        String requiredVersion = Server.REQUIRED_CLIENT_VER;
        if(clVersion.equals(requiredVersion)) return true;
        client.getMsgSender().writeData(DBLgame.HANDSHAKE
                ,DBLgame.ERROR
                ,String.valueOf(DBLgame.CLIENT_VERSION));
        return false;
    }


    private void readRoomPacket(byte[] packet) {
        byte field = packet[8];
        if (field == DBLgame.JOIN && client.getRoom()==null) {
            if(nicknameOk(packet))
                joinRoom();
        } else if(field == DBLgame.LEAVE)
            client.getRoom().removePlayer(client);

        else
            client.getMsgSender().writeData(DBLgame.PACKET_DATA
                    ,DBLgame.ERROR,String.valueOf(DBLgame.FIELD));

    }

    private boolean nicknameOk(byte[] p) {
        if(!nickLengthOk(p[9])) return false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(int i=10;i<10+p[9];i++){
            if(p[i]<0x20 || p[i]>0x7e ) {
                client.getMsgSender().writeData(DBLgame.ROOM
                        ,DBLgame.ERROR
                        ,String.valueOf(DBLgame.NICKNAME_CHARACHTERS));
                return false;
            }
            baos.write(p[i]);
        }
        client.setNickname(new String(baos.toByteArray()));
        return true;
    }

    private boolean nickLengthOk(byte nickLength){
        if(nickLength<3 || nickLength>16){
            client.getMsgSender().writeData(DBLgame.ROOM
                    ,DBLgame.ERROR
                    ,String.valueOf(DBLgame.NICKNAME_LENGTH));
            return false;
        }
        return true;
    }

    private void joinRoom() {
        for (Room room : Server.roomList) {
                room.addPlayer(client);
            if (client.getRoom() != null) return;
        }
        client.getMsgSender().writeData(DBLgame.ROOM
                , DBLgame.ERROR
                , String.valueOf(DBLgame.ROOMS_ARE_FULL));
    }


    private void readResponsePacket(byte field) {
        if(field==DBLgame.READY){
            client.setReady(true);
        } else
            client.getMsgSender().writeData(DBLgame.PACKET_DATA
                    ,DBLgame.ERROR,String.valueOf(DBLgame.FIELD));
    }

    private void readGamePacket(byte field,byte answer) {
        if(field == DBLgame.ANSWER){
            if(client.getCardSum() > 0 && client.getCard() != null){
                ImageOnCard clientAnswer = client.getCard().getImages().get(answer);
                client.getRoom().checkCard(client,clientAnswer);
            }
        } else
            client.getMsgSender().writeData(DBLgame.PACKET_DATA
                    ,DBLgame.ERROR,String.valueOf(DBLgame.FIELD));
    }

    public void stopRecieve() throws IOException {
        interrupt();
        in.close();
    }


}

package com.dobble.client.Messaging;

import com.dobble.client.GUI.ErrorView;
import com.dobble.client.GUI.Game.GameView;
import com.dobble.client.Client;
import com.dobble.protocol.DBLgame;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class MsgSender {

    public static final int HANDSHAKE = 1;
    public static final int JOIN_ROOM = 2;
    public static final int LEAVE_ROOM = 3;
    public static final int READY = 4;
    public static final int ANSWER = 5;

    private DataOutputStream out;

    public MsgSender() {
        try {
            out = new DataOutputStream( Client.socket.getOutputStream());
        } catch (IOException e) {

        }
    }

    public void writeData(int dataType){
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            DataOutputStream dataWriter = new DataOutputStream(data);
            switch (dataType) {
                case HANDSHAKE:
                    dataWriter.writeByte(DBLgame.HANDSHAKE);
                    dataWriter.writeByte(DBLgame.CONNECT);
                    dataWriter.writeByte(Client.protocol.getID());
                    byte protocolVer = stringToByte(Client.protocol.getVersion());
                    dataWriter.writeByte(protocolVer);
                    byte clientVer = stringToByte(Client.clientVersion);
                    dataWriter.writeByte(clientVer);
                    break;
                case JOIN_ROOM:
                    dataWriter.writeByte(DBLgame.ROOM);
                    dataWriter.writeByte(DBLgame.JOIN);
                    byte[] nickname = Client.nickname.getBytes(Charset.forName("UTF-8"));
                    dataWriter.writeByte((byte) nickname.length);
                    dataWriter.write(nickname, 0, nickname.length);
                    break;
                case LEAVE_ROOM:
                    dataWriter.writeByte(DBLgame.ROOM);
                    dataWriter.writeByte(DBLgame.LEAVE);
                    break;
                case READY:
                    dataWriter.writeByte(DBLgame.RESPONSE);
                    dataWriter.writeByte(DBLgame.READY);
                    break;
                case ANSWER:
                    dataWriter.writeByte(DBLgame.GAME);
                    dataWriter.writeByte(DBLgame.ANSWER);
                    dataWriter.writeByte(GameView.answer);
                    break;
            }
            dataWriter.flush();
            createPacket(data);
            data.close();
            dataWriter.close();
        } catch (IOException e) {
        e.printStackTrace();
        }
    }

    private byte stringToByte(String version) {
        int firstValue = Integer.parseInt(version.substring(0,version.indexOf('.')));
        int lastValue = Integer.parseInt(version.substring(version.indexOf('.')+1));
        String hex = Integer.toHexString(firstValue) + Integer.toHexString(lastValue);
        return (byte) Long.parseLong(hex, 16);
    }

    private void createPacket(ByteArrayOutputStream data) throws IOException {
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
    }

    private void sendPacket(byte[] packet) {
        try {
            out.write(packet,0,packet.length);
            out.flush();
        } catch (IOException e) {
            Client.dissconnect();
        }
    }
    public void stopSend() throws IOException { out.close(); }


}

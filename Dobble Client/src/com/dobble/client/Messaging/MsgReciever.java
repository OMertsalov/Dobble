package com.dobble.client.Messaging;

import com.dobble.client.GUI.Menu.MenuView;
import com.dobble.client.GUI.Menu.PrepareView;
import com.dobble.client.Client;
import com.dobble.protocol.DBLgame;
import com.dobble.cards.Card;
import com.dobble.client.GUI.ErrorView;
import com.dobble.client.GUI.Game.GameView;


import java.awt.*;
import java.io.*;
import java.util.Arrays;

public class MsgReciever extends Thread{

    private DataInputStream in;

    public MsgReciever() {
        try {
            in = new DataInputStream(Client.socket.getInputStream());
        } catch (IOException e) {

        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            byte[] packet = recievePacket();
            if (packet != null) {
               checkPacket(packet);
            }
        }
    }



    private byte[] recievePacket(){
            try {
                byte firstbyte = in.readByte();
                byte[] b = new byte[6];
                in.read(b, 0, 6);
                int length = byteToInt(new byte[]{b[5], b[4], b[3], b[2]});
                if (length < DBLgame.MAX_PACKET_LENGTH && length > 7) {
                    ByteArrayOutputStream packet = new ByteArrayOutputStream();
                    packet.write(firstbyte);
                    packet.write(b, 0, 6);
                    b = new byte[length - 7];
                    in.readFully(b, 0, b.length);
                    packet.write(b, 0, b.length);
                    return packet.toByteArray();
                }
                in.skip(in.available());
            } catch (IOException e) {
                Client.dissconnect();
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

    private void checkPacket(byte[] packet) {
        byte option=packet[7];
            switch (option) {
                case  DBLgame.PACKET_DATA:
                    checkPacketData(packet[8],packet[9]);
                    break;
                case DBLgame.HANDSHAKE:
                    checkHandshake(packet[8],packet[9]);
                    break;
                case DBLgame.ROOM:
                    checkJoining(packet);
                    break;
                case DBLgame.REQUEST:
                    checkRequest(packet[8]);
                    break;
                case DBLgame.GAME:
                    checkGame(packet);
            }
    }

    private void checkGame(byte[] packet) {
        byte field = packet[8];
        switch (field) {
            case DBLgame.START:
                GameView.start((packet[9] & 0xFF));
                break;
            case DBLgame.NOT_STARTED:
                PrepareView.restoreButton();
                break;
            case DBLgame.CLIENT_CARD:
                byte []cardData = Arrays.copyOfRange(packet,9,(packet.length-3));
                ByteArrayInputStream bis = new ByteArrayInputStream(cardData);
                try {
                    ObjectInputStream is = new ObjectInputStream(bis);
                    Card card = (Card)is.readObject();
                    GameView.setClientCard(card.getImages());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case DBLgame.SERVER_CARD:
                byte []cardImage = Arrays.copyOfRange(packet,9,(packet.length-3));
                try {
                    String imagePath = "/home/honor/IdeaProjects/Dobble Client/tmp/images/image.png";
                    FileOutputStream fos = new FileOutputStream(imagePath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write(cardImage, 0 , cardImage.length);
                    bos.flush();
                    GameView.setServerCard(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case DBLgame.ANSWERED:
                GameView.decreaseCardsSum((packet[9] & 0xFF));
                break;
            case DBLgame.END:
                Client.resetViews();
                System.out.println(new String(Arrays.copyOfRange(packet,9,packet.length-3)));
                break;
//            case DBLgame.ERROR:
//                ErrorView.call(DBLgame.NOT_STARTED);
//                break;
        }
    }

    private void checkRequest(byte field) {
        if(field==DBLgame.READY)
            PrepareView.askIfReady();
        else if(field==DBLgame.ERROR)
            ErrorView.call(DBLgame.NOT_READY);

    }

    private void checkPacketData(byte field,byte code) {
        if(field==DBLgame.ERROR)
            ErrorView.call(code);
    }

    private void checkHandshake(byte field,byte code) {
        if ( field == DBLgame.CONNECT)
            if(code == DBLgame.SUCCESS) {
                MenuView.findButton.setEnabled(true);
                MenuView.findButton.setBackground(Color.green);}
        else if(field==DBLgame.ERROR)
            ErrorView.call(code);
    }

    private void checkJoining(byte[] p) {
        byte field = p[8];
        switch (field){
            case DBLgame.JOIN:
                MenuView.getInstance().setEnabled(false);
                PrepareView.getInstance().setVisible(true);
                PrepareView.setRoomNbr((p[9] & 0xFF));
                PrepareView.setGameId((p[10] & 0xFF));
                PrepareView.setMaxPlayersNbr((p[11] & 0xFF));
                PrepareView.addPlayers(Arrays.copyOfRange(p,12,p.length-3));
                break;
            case DBLgame.NEW_PLAYER:
                if(PrepareView.getInstance().isVisible())
                PrepareView.addPlayer(Arrays.copyOfRange(p,9,p.length-3));
                break;
            case DBLgame.PLAYER_LEAVE:
                if(PrepareView.getInstance().isVisible())
                    PrepareView.deletePlayer((p[9] & 0xFF),PrepareView.getPlayersPanel());
                else if(GameView.getInstance().isVisible())
                    PrepareView.deletePlayer((p[9] & 0xFF),GameView.getPlayersPanel());
                break;
            case DBLgame.ERROR:
                ErrorView.call(p[9]);
                break;
        }
    }


    public void stopRecieve() throws IOException {
        interrupt();
        in.close();
    }

}

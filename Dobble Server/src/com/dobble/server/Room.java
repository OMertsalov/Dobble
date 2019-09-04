package com.dobble.server;

import com.dobble.cards.*;
import com.dobble.server.Client.Client;
import com.dobble.protocol.DBLgame;
import com.dobble.server.Logs.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class Room {

    private final List<Client> playerList;
    private final int playersLimit=4;
    private final int nbr;
    private int gameId;
    private Game game;
    private int stage;

    //Constructor
    public Room(int nbr) {
        this.nbr = nbr;
        playerList = new ArrayList<>();
    }

    public void addPlayer(Client player){
        if(playerList.size() < playersLimit && stage==0)
            synchronized (playerList){
                player.setRoom(this);
                Server.serverLogger.write(Log.PLAYER_JOIN_ROOM,player); //Adding logs
                player.getMsgSender().writeData(DBLgame.ROOM
                        ,DBLgame.JOIN,getPlayersData());
                playerList.add(player);
                sendToAll(DBLgame.ROOM
                        ,DBLgame.NEW_PLAYER
                        ,getPlayerData(player));
                if (playerList.size() == playersLimit)
                    startGameThread();
            }
    }

    private void sendToAll(byte option, byte field, String value) {
        synchronized (playerList){
            for(Client player : playerList){
                if (player.getRoom() != null && player.getRoom().equals(this)){
                    if(field==DBLgame.START)
                        player.setCardsSum(Integer.valueOf(value));
                    else if(field==DBLgame.NOT_STARTED)
                        player.setReady(false);
                    else if(field==DBLgame.END)
                        player.restoreData();
                player.getMsgSender().writeData(option, field, value);
                }
            }
        }
    }

    public String getPlayersData() {
        synchronized (playerList) {
            String playersData = "";
            for (Client player : playerList)
                playersData += getPlayerData(player);
            return playersData;
        }
    }

    private String getPlayerData(Client player) {
        return player.getId()+"\r"+player.getNickname()+"\n";
    }

    public void removePlayer(Client player){
        if(playerList.contains(player))
        synchronized (playerList){
            playerList.remove(player);
            Server.serverLogger.write(Log.PLAYER_LEFT_ROOM,player); //Adding logs
            player.restoreData();
            sendToAll(DBLgame.ROOM
                    ,DBLgame.PLAYER_LEAVE
                    ,String.valueOf(player.getId()));
            if(stage==2 && playerList.size()==1){
                game.interrupt();
            }
        }
    }

    private void startGameThread() {
        game = new Game();
        game.setName("Room"+nbr+"-Game"+gameId);
        game.start();
    }

    private class Game extends Thread {

        private ArrayList<Card> deckOfCards;
        private int tableCard;
        private Integer cardsCounter;
        private int rank = 0;

        @Override
        public void run() {
            setStage(1);
            sendToAll(DBLgame.REQUEST
                    , DBLgame.READY
                    ,"0");
            timer(10);
            checkResponse();
            if(playersReady()){
                setStage(2);
                gameId++;
                shuffleCards();
                startGame();
                sendToAll(DBLgame.GAME
                        ,DBLgame.END
                        ,getPlayersData());
                playerList.clear();
            }
            else
                sendToAll(DBLgame.GAME
                        ,DBLgame.NOT_STARTED
                        ,"0");
            setStage(0);
        }


        private void checkResponse() {
            synchronized (playerList){
                Iterator i  = playerList.iterator();
                while (i.hasNext()){
                    Client player = (Client)i.next();
                    if (player.getRoom() != null
                            && player.getRoom().equals(Room.this)
                            && !player.isReady()){
                        player.getMsgSender().writeData(DBLgame.REQUEST
                                ,DBLgame.ERROR
                                ,String.valueOf(DBLgame.NOT_READY));
                        i.remove();
                        Server.serverLogger.write(Log.PLAYER_LEFT_ROOM,player); //Adding logs
                        player.restoreData();
                        sendToAll(DBLgame.ROOM
                                ,DBLgame.PLAYER_LEAVE
                                ,String.valueOf(player.getId()));
                    }
                }
            }
        }

        private boolean playersReady() {return playerList.size()==playersLimit;}

        private void shuffleCards() {
            deckOfCards = new DeckOfCards().getCards();
            tableCard = 0;
            cardsCounter = 1;
            int playerCardSum = (deckOfCards.size() - 1)/playerList.size();
            sendToAll(DBLgame.GAME
                    ,DBLgame.START
                    ,String.valueOf(playerCardSum));
        }

        private void startGame(){
            stage=2;
            synchronized (game) {
                try{
                    while (rank<playerList.size()-1) {
                        dealCards();
                        game.wait();
                    }
                } catch (InterruptedException e) {
                   // e.printStackTrace();
                }
            }
        }

        private void dealCards() {
            synchronized (playerList) {
                for (Client player : playerList) {
                    if (player.getRoom() != null
                            && player.getRoom().equals(Room.this)
                            && player.getCard() == null) {
                        player.setCard(deckOfCards.get(cardsCounter));
                        cardsCounter++;
                        player.getMsgSender().writeData(DBLgame.GAME
                                ,DBLgame.CLIENT_CARD,"");
                    }
                    player.getMsgSender().writeData(DBLgame.GAME
                            ,DBLgame.SERVER_CARD
                            ,deckOfCards.get(tableCard).getPath());
                }
            }
        }

        private void timer(int sec)
        {
            try {
                Thread.sleep(sec*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void acceptAnswer(Client player) {
            synchronized (game) {
                if(game.getState().equals(State.WAITING)) {
                    player.decreaseCardSum();
                    tableCard = player.getCard().getNumber();
                    player.setCard(null);
                    if (player.getCardSum() == 0) {
                        setRank(player);
                        rank++;
                    }
                    sendToAll(DBLgame.GAME
                            , DBLgame.ANSWERED
                            , String.valueOf(player.getId()));
                    game.notify();
                }
            }
        }

        private void setRank(Client player) {
            synchronized (playerList){
                if(player.getRoom() != null && player.getRoom().equals(Room.this))
                Collections.swap(playerList,rank,playerList.indexOf(player));
            }
        }
    }


    public int getStage() {
        return stage;
    }

    private void setStage(int stage) {
        this.stage = stage;
        Server.serverLogger.write(Log.ROOM_STAGE_CHANGED,this);
    }

    public void checkCard(Client player, ImageOnCard playerImage){
        for (ImageOnCard tableImage : game.deckOfCards.get(game.tableCard).getImages())
            if (playerImage.equals(tableImage))
                game.acceptAnswer(player);
    }

    public List<Client> getPlayerList() {
        return playerList;
    }

    public int getPlayersLimit() {
        return playersLimit;
    }

    public int getGameId() {
        return gameId;
    }

    public int getNbr() {
        return nbr;
    }
}

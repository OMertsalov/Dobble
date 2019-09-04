package com.dobble.protocol;

public class DBLgame extends DBLprot{

    //OPTION
    public static final byte ROOM = 0x40;;
    public static final byte GAME = 0x50;
    //FIELD
    public static final byte JOIN = 0x41;
    public static final byte LEAVE = 0x42;
    public static final byte NEW_PLAYER = 0x43;
    public static final byte PLAYER_LEAVE = 0x44;
    public static final byte READY = 0x32;
    public static final byte START = 0x52;
    public static final byte CLIENT_CARD = 0x53;
    public static final byte SERVER_CARD = 0x54 ;
    public static final byte ANSWER = 0x55;
    public static final byte ANSWERED = 0x56;
    public static final byte END = 0x57;
    //CODE
    public static final byte NOT_STARTED = 0x51;
    public static final byte NOT_READY = 0x36;
    public static final byte NICKNAME_LENGTH = 0x46;
    public static final byte NICKNAME_CHARACHTERS = 0x47;
    public static final byte ROOMS_ARE_FULL = 0x48;

    private static DBLgame DBLgame_instance = null;

    private DBLgame(){
        super("DBLprot","1.0",1);
    }

    public static DBLgame Create()
    {
        if (DBLgame_instance==null)
            DBLgame_instance=new DBLgame();
        return DBLgame_instance;
    }

}

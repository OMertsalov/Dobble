package com.dobble.protocol;

public abstract class DBLprot {

    public static final int MAX_PACKET_LENGTH = 169984;

    //OPTION
    public static final byte PACKET_DATA = 0x10;
    public static final byte HANDSHAKE = 0x20;
    public static final byte REQUEST =  0x30;
    public static final byte RESPONSE = 0x31;
    //FIELD
    public static final byte ERROR = 0x5;
    public static final byte CONNECT = 0x21;
    //CODE
    public static final byte PACKET_LENGTH = 0x16;
    public static final byte PACKAGE_SYNTAX = 0x17;
    public static final byte OPTION = 0x18;
    public static final byte FIELD = 0x19;
    public static final byte SUCCESS = 0x26;
    public static final byte PROTOCOL = 0x27;
    public static final byte PROTOCOL_VERSION = 0x28;
    public static final byte CLIENT_VERSION = 0x29;


    private String name;
    private String current_version;
    private int id;

    public DBLprot(String name, String current_version, int id) {
        this.name = name;
        this.current_version = current_version;
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return current_version;
    }
}

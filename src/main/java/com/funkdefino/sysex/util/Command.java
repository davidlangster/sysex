package com.funkdefino.sysex.util;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public enum Command {

    Read (0x01),
    Write(0x02),
    Query(0x03),
    Error(0x04);

    private byte data;

    Command(int data) {
        this.data = (byte)data;
    }

    public static Command get(byte b) {

        Command command = null;
        for(Command c : values()) {
            if(c.data == b) {
                command = c;
                break;
            }
        }

        return command;

    }   // get()

    public byte get() {
        return data;
    }

}   // enum Command

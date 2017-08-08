package com.funkdefino.sysex;

import com.funkdefino.common.util.reflect.Loader;
import com.funkdefino.sysex.handlers.*;
import com.funkdefino.sysex.util.*;
import javax.sound.midi.*;
import java.util.concurrent.*;
import java.util.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class Processor implements IUtility {

    //** ------------------------------------------------------------- Constants

    private final static int SYSEX_ID[] = {0x00,0x21,0x1B}; // Manufacturer's ID

    //** ------------------------------------------------- Static initialisation

    private static Map<Command, Integer> partlen = new EnumMap<Command, Integer>(Command.class);
    private final static Map<Command,Class> handlers =
                 new EnumMap<Command,Class>(Command.class);

    static {
        handlers.put(Command.Read,  ReadHandler.class );
        handlers.put(Command.Write, WriteHandler.class);
        handlers.put(Command.Query, QueryHandler.class);

        partlen.put (Command.Read,  8);
        partlen.put (Command.Write, 9);
        partlen.put (Command.Query, 6);
    }

    //** ------------------------------------------------------------------ Data

    private final Transmitter transmitter;
    private final Receiver receiver;
    private final Configuration config;
    private final FileInfo fileInfo;

    private ScheduledExecutorService exec;
    private IHandler handler;
    private Watcher watcher;

    //** ---------------------------------------------------------- Construction

    /**
     * Ctor.
     * @param transmitter a transmitter.
     * @param receiver a receiver.
     * @param fileInfo a file utility.
     * @param config a configuration value object.
     */
    public Processor(Transmitter transmitter, Receiver receiver,
                     FileInfo fileInfo, Configuration config) {

        transmitter.setReceiver(new MidiInputReceiver(this));
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.fileInfo = fileInfo;
        this.config = config;

        this.watcher = new Watcher(this, config.getTimeout());
        exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(this.watcher, 1, 1, TimeUnit.SECONDS);

    }   // Processor()

    //** ------------------------------------------------------------ Operations

    /**
     * Process incoming SYSEX commands.
     * @param data SYSEX data.
     * @throws Exception on error.
     */
    public synchronized void execute(byte[] data) throws Exception {

        try {
            if(config.isDebug()) Convert.dump(data);
            if((handler = selectHandler(data, handler)) != null) {

                List<byte[]> ls;
                if((ls  = handler.execute(data)) != null) {
                    handler = null;
                    for(byte[] buf : ls) {
                        if(config.isDebug()) Convert.dump(buf);
                        SysexMessage msg = new SysexMessage(SysexMessage.SYSTEM_EXCLUSIVE, buf, buf.length);
                        receiver.send(msg, -1);
                        long delay = config.getDelay();
                        if(delay > 0) {
                           Thread.sleep(delay);
                        }
                    }
                }
            }
        }
        catch(Exception excp) {
            System.out.println(excp.toString());
            byte[] buf = error();
            SysexMessage msg = new SysexMessage(SysexMessage.SYSTEM_EXCLUSIVE, buf, buf.length);
            receiver.send(msg, -1);
            handler = null;
        }

        watcher.update(handler != null);

    }   // execute()

    /**
     * Releases all resources.
     */
    public void close() {
        if(transmitter != null) transmitter.close();
        if(receiver != null) receiver.close();
        if(exec != null) exec.shutdown();
    }

    //** --------------------------------------- Operations (IUtility interface)

    public Configuration getConfiguration() {return config;}
    public FileInfo getFileInfo() {return fileInfo;}

    //** ------------------------------------------------------------ Operations

    protected synchronized void onTimeout() {
        System.out.println("Command timeout");
        handler = null;
    }

    //** -------------------------------------------------------- Implementation

    /**
     * Selects a handler based on the command byte.
     * @param data SYSEX data.
     * @return the handler (or null).
     */
    private IHandler selectHandler(byte[] data, IHandler handler) {

        if(handler == null) {
            if(validateSysexId(data)) {
                Class clz = handlers.get(Command.get(data[IConstants.IDX_CMND]));
                if(clz != null) {
                    Loader.ArgumentList argList = new Loader.ArgumentList(IUtility.class, this);
                    handler = (IHandler)Loader.getTarget(clz.getName(), argList);
                }
            }
        }

        return handler;

    }   // selectHandler()

    /**
     * Checks for the presence of a manufacturer's MIDI identifier
     * @param data SYSEX data.
     * @return true if present; otherwise false.
     */
    private static boolean validateSysexId(byte[] data) {

        boolean valid = false;
        if(data.length > IConstants.IDX_CMND) {
            Command cmnd = Command.get(data[IConstants.IDX_CMND]);
            if(cmnd != null) {
                if(data.length == partlen.get(cmnd)) {
                    for(int i = 0; i < SYSEX_ID.length; i++) {
                        if(!(valid = (data[IConstants.IDX_SYSEX_ID + i] == SYSEX_ID[i]))) {
                            break;
                        }
                    }
                }
            }
        }

        return valid;

    }   // validateSysexId()

    /**
     * Creates a SYSEX error response.
     * @return the part.
     */
    private static byte[] error() {
        return new byte[]{(byte)IConstants.SYSEX_START,
                          Command.Error.get(),
                          (byte)IConstants.SYSEX_STATUS};
    }

    //** ---------------------------------------------------------------- Nested

    private final static class MidiInputReceiver implements Receiver {

        private Processor proc;
        public  MidiInputReceiver(Processor proc) {
            this.proc = proc;
        }

        public void send(MidiMessage msg, long timeStamp) {
            if(msg instanceof SysexMessage) {
                try {proc.execute(((SysexMessage)msg).getData());}
                catch(Exception excp) {
                    excp.printStackTrace();
                }
            }
        }

        public void close()
        {
        }

    }   // class MidiInputReceiver

    //** ---------------------------------------------------------------- Nested

    private final static class Watcher implements Runnable {

        private Processor processor;
        private boolean initiated;
        private long timestamp;
        private long timeout;

        private Watcher(Processor processor, long timeout) {
            this.processor = processor;
            this.timeout = timeout;
            initiated = false;
        }

        public synchronized void update(boolean initiated) {
            timestamp = System.currentTimeMillis();
            this.initiated = initiated;
        }

        public synchronized void run() {
            if(initiated) {
                if(System.currentTimeMillis() - timestamp > timeout) {
                    processor.onTimeout();
                    initiated = false;
                }
            }
        }

    }   // class Watcher

}   // class Processor

package com.funkdefino.sysex;

import com.funkdefino.common.util.xml.XmlDocument;
import com.funkdefino.sysex.midi.MidiCommon;
import com.funkdefino.sysex.util.Configuration;
import com.funkdefino.sysex.util.FileInfo;
import javax.sound.midi.MidiDevice;
import java.io.File;

/**
 * <p>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class Server {

    //** ----------------------------------------------------------- Application

    /**
     * Application entry point.
     * @param args command-line arguments.
     */
    public static void main(String[] args) {

        if(args.length != 1) {
            System.out.println("Usage : Server [configuration]");
            return;
        }

        try {
            XmlDocument doc = new XmlDocument(new File(args[0]), false);
            Configuration config = new Configuration(doc.getRootElement());
            new Server(config);
        }
        catch(Exception excp) {
            excp.printStackTrace();
        }

    }   // main()

    //** ---------------------------------------------------------- Construction

    /**
    * Ctor.
    * @param config a configuration value object.
    * @throws Exception on error.
    */
    private Server(Configuration config) throws Exception {

        System.out.println("**********************************");
        System.out.println("Lemur System Exclusive Server ****");
        System.out.println("David Lang \u00A9 2014             ****");
        System.out.println("**********************************");

        MidiDevice devin  = MidiCommon.getDevice(config.getInput(),  MidiCommon.Type.Input );
        MidiDevice devout = MidiCommon.getDevice(config.getOutput(), MidiCommon.Type.Output);

        validate(devin, config.getInput());
        validate(devout, config.getOutput());

        FileInfo fileInfo = new FileInfo(config);

        Processor processor =
            new Processor(devin.getTransmitter(),
                            devout.getReceiver(),
                               fileInfo,
                                 config);
        devin.open ();
        devout.open();

        synchronized(this) {
            wait();
        }

        processor.close();

        devin.close();
        devout.close();

    }   // Server

    /**
     * Validates a device.
     * @param device the device.
     * @param name the device name.
     * @throws Exception  if null.
     */
    private static void validate(MidiDevice device, String name) throws Exception {
        if(device == null) throw new Exception(String.format("Unable to open device '%s'", name));
        System.out.println(String.format("Opened : %s", name));
    }

}   // class Server

package com.funkdefino.sysex.util;

import com.differitas.common.util.xml.*;
import com.differitas.common.util.*;
import java.util.*;

/**
 * <p>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class Configuration {

    //** ------------------------------------------------------------- Constants

    private final static String ElmntBase    = "Base";
    private final static String ElmntInput   = "Input";
    private final static String ElmntOutput  = "Output";
    private final static String ElmntTimeout = "Timeout";
    private final static String ElmntDelay   = "Delay";
    private final static String ElmntDebug   = "Debug";
    private final static String ElmntSeqncs  = "Sequences";
    private final static String AttrbName    = "name";
    private final static String AttrbId      = "id";

    //** ------------------------------------------------------------------ Data

    private String  base;     // Base directory
    private String  input;    // MIDI input port
    private String  output;   // MIDI output port
    private long    timeout;  // SYSEX part timeout
    private long    delay;    // SYSEX part transmit delay
    private boolean debug;    // Diagnostic debug

    private Map<Integer,String> sequences = new HashMap<Integer,String>();

    //** ---------------------------------------------------------- Construction

    /**
     * Defaul ctor.
     */
    public Configuration()
    {
    }

    /**
     * Ctor.
     * @param config a configuration element.
     * @throws UtilException on error.
     */
    public Configuration(XmlElement config) throws UtilException {
        initialise(config);
    }

    //** ------------------------------------------------------------ Operations

    public void   setBase(String base)     {this.base = base;}
    public void   setInput(String input)   {this.input = input;}
    public void   setOutput(String output) {this.output = output;}
    public void   setTimeout(long timeout) {this.timeout = timeout;}
    public void   setDelay (long delay)    {this.delay = delay;}

    public void   addSequence(int id, String name) {
        sequences.put(id,name);
    }

    //** ------------------------------------------------------------ Operations

    public String  getBase()      {return base;   }
    public String  getInput()     {return input;  }
    public String  getOutput()    {return output; }
    public long    getTimeout()   {return timeout;}
    public long    getDelay()     {return delay;  }
    public boolean isDebug()      {return debug;  }

    public Map<Integer,String> getSequences() {
        return sequences;
    }

    //** -------------------------------------------------------- Implementation

    /**
     * Performs startup initialisation.
     * @param config a configuration element.
     * @throws UtilException on error.
     */
    private void initialise(XmlElement config) throws UtilException {

        base   = XmlValidate.getContent(config, ElmntBase  );
        input  = XmlValidate.getContent(config, ElmntInput );
        output = XmlValidate.getContent(config, ElmntOutput);

        String sTimeout = XmlValidate.getContent(config, ElmntTimeout, "3000" );
        String sDelay   = XmlValidate.getContent(config, ElmntDelay,   "4"    );
        String sDebug   = XmlValidate.getContent(config, ElmntDebug,   "false");

        timeout = Long.parseLong(sTimeout);
        delay   = Long.parseLong(sDelay);
        debug   = Boolean.parseBoolean(sDebug);

        XmlElement sqncs = XmlValidate.getElement(config, ElmntSeqncs);
        Iterator<XmlElement> ii = sqncs.getChildren().iterator();
        for(XmlElement sqnc : sqncs.getChildren()) {
            String name = XmlValidate.getAttribute(sqnc, AttrbName);
            String id = XmlValidate.getAttribute(sqnc, AttrbId);
            sequences.put(Integer.parseInt(id), name);
        }

    }   // initialise()

}   // class Configuration

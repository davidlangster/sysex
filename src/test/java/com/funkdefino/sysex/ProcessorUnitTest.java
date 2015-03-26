package com.funkdefino.sysex;

import com.differitas.common.unittest.CTestCase;
import com.differitas.common.util.xml.XmlDocument;
import com.funkdefino.sysex.midi.MidiCommon;
import com.funkdefino.sysex.util.Configuration;
import com.funkdefino.sysex.util.FileInfo;
import junit.framework.Test;
import javax.sound.midi.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class ProcessorUnitTest extends CTestCase {

    //** ---------------------------------------------------------- Construction

    public ProcessorUnitTest(String sMethod) {
        super(sMethod);
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the suite of cases for testing.
     * @return the test suite.
     */
    public static Test suite() {
        return CTestCase.suite(ProcessorUnitTest.class,
                              "UnitTest.xml",
                              "test#2");
    }

    //** ----------------------------------------------------------------- Tests

    public void test01() throws Exception {

        MidiDevice input  = MidiCommon.getDevice("SysexA", MidiCommon.Type.Input );
        MidiDevice output = MidiCommon.getDevice("SysexB", MidiCommon.Type.Output);
        System.out.println(input.getDeviceInfo().getName ());
        System.out.println(output.getDeviceInfo().getName());

        XmlDocument doc = XmlDocument.fromResource(getClass(), "Sysex.xml");
        Configuration config = new Configuration(doc.getRootElement());
        FileInfo fileInfo = new FileInfo(config);

        Processor processor =
            new Processor(input.getTransmitter(),
                            output.getReceiver(),
                              fileInfo,
                                config);

        input.open ();
        output.open();

        synchronized(this) {
            System.out.println("Waiting");
            wait();
        }

        processor.close();

        input.close();
        output.close();
     }

} // class ProcessorUnitTest

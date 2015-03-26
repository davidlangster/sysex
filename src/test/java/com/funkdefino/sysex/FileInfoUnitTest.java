package com.funkdefino.sysex;

import com.differitas.common.unittest.CTestCase;
import com.differitas.common.util.xml.XmlDocument;
import com.funkdefino.sysex.util.Configuration;
import com.funkdefino.sysex.util.FileInfo;
import junit.framework.Test;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class FileInfoUnitTest extends CTestCase {

    //** ---------------------------------------------------------- Construction

    public FileInfoUnitTest(String sMethod) {
        super(sMethod);
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the suite of cases for testing.
     * @return the test suite.
     */
    public static Test suite() {
        return CTestCase.suite(FileInfoUnitTest.class,
                              "UnitTest.xml",
                              "test#3");
    }

    //** ----------------------------------------------------------------- Tests

    public void test01() throws Exception {

        XmlDocument doc = XmlDocument.fromResource(getClass(), "Sysex.xml");
        FileInfo fi = new FileInfo(new Configuration(doc.getRootElement()));
        String s = fi.getFilename("FallingAway", FileInfo.Type.Next);
        System.out.println(s);
    }

} // class FileInfoUnitTest

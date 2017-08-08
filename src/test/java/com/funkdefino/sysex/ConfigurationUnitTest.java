package com.funkdefino.sysex;

import com.funkdefino.common.unittest.CTestCase;
import com.funkdefino.common.util.xml.XmlDocument;
import com.funkdefino.sysex.util.Configuration;
import junit.framework.Test;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class ConfigurationUnitTest extends CTestCase {

    //** ---------------------------------------------------------- Construction

    public ConfigurationUnitTest(String sMethod) {
        super(sMethod);
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the suite of cases for testing.
     * @return the test suite.
     */
    public static Test suite() {
        return CTestCase.suite(ConfigurationUnitTest.class,
                              "UnitTest.xml",
                              "test#4");
    }

    //** ----------------------------------------------------------------- Tests

    public void test01() throws Exception {
        XmlDocument doc = XmlDocument.fromResource(getClass(), "Sysex.xml");
        new Configuration(doc.getRootElement());
    }

} // class ConfigurationUnitTest

package com.funkdefino.sysex;

import com.funkdefino.common.unittest.CTestCase;
import com.funkdefino.sysex.util.Convert;
import junit.framework.Assert;
import junit.framework.Test;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class ConvertUnitTest extends CTestCase {

    //** ---------------------------------------------------------- Construction

    public ConvertUnitTest(String sMethod) {
        super(sMethod);
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the suite of cases for testing.
     * @return the test suite.
     */
    public static Test suite() {
        return CTestCase.suite(ConvertUnitTest.class,
                              "UnitTest.xml",
                              "test#3");
    }

    //** ----------------------------------------------------------------- Tests

    public void test01() throws Exception {
        Convert.dump("FallingAway".getBytes());
    }

    public void test02() throws Exception {

        int step = 0x80;
        byte[] arr = new byte[2];
        arr[0] = (byte)(step >> 0x07);
        arr[1] = (byte)(step &  0x7f);
        int step_ = (arr[0] << 0x07) | arr [1];
        Assert.assertEquals(step, step_);

    }

} // class ConvertUnitTest

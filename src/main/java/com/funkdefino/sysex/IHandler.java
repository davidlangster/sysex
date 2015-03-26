package com.funkdefino.sysex;

import java.util.List;

/**
 * <p>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public interface IHandler {
    public List<byte[]> execute(byte[] data) throws Exception;
}

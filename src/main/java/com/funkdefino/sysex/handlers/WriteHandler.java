package com.funkdefino.sysex.handlers;

import com.differitas.common.io.IOUtil;
import com.funkdefino.sysex.IUtility;
import com.funkdefino.sysex.util.Command;
import com.funkdefino.sysex.util.IConstants;
import java.io.ByteArrayOutputStream;
import java.nio.file.*;
import java.util.*;

/**
 * <p>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class WriteHandler extends BaseHandler {

    //** ------------------------------------------------------------------ Data

    private ByteArrayOutputStream baos;
    private int parts;
    private int seqId;
    private int seqSubNum;

    //** ---------------------------------------------------------- Construction

    /**
     * Ctor.
     * @param utility a utility implementation.
     */
    public WriteHandler(IUtility utility) {
        super(utility);
        baos = new ByteArrayOutputStream();
        baos.write(IConstants.SYSEX_START);
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Concatenates a number of SYSEX sequence parts into a single SYSEX sequence.
     * @param data SYSEX part data.
     * @return a response message when all parts have been concatenated & written to file.
     * @throws Exception on error.
     */
    public List<byte[]> execute(byte[] data) throws Exception {

        List<byte[]> ls = null;

        try {
            // First part contains number of subsequent parts
            // and sequence identifiers.
            if(getPartNo(data) == 0) {
                parts = data[IConstants.IDX_PARTS];
                seqId = data[IConstants.IDX_SEQ_ID];
                seqSubNum = data[IConstants.IDX_SEQ_SUBNUM];
            }
            else {
                // Add all remaining parts to the buffer
                if(addPart(data, baos, parts)) {  // Last part?
                    baos.write(IConstants.SYSEX_STATUS);
                    String filename = getFilename(seqId, seqSubNum);
                    System.out.println(String.format("Writing : %s", filename));
                    Files.write(Paths.get(filename), baos.toByteArray());
                    IOUtil.close(baos);

                    // Create a response message
                    ls = new ArrayList<byte[]>();
                    ls.add(response());
                }
            }
        }
        catch(Exception excp) {
            IOUtil.close(baos);
            throw excp;
        }

        return ls;

    }   // execute()

    //** -------------------------------------------------------- Implementation

    /**
     * Adds a SYSEX part to the sequence buffer.
     * @param data SYSEX part data.
     * @param baos the buffer.
     * @param parts maximum number of parts.
     * @return true if this is the last part; otherwise false.
     */
    private static boolean addPart(byte[] data, ByteArrayOutputStream baos,
                                   int parts) {

        int len = data.length - 1;     // Exclude F7
        baos.write((len + 2) >> 0x07); // Part length stored in 2 bytes
        baos.write((len + 2)  & 0x7f);
        baos.write(data, 0, len);      // Part
        return getPartNo(data) == parts;

    }   // addPart()

    /**
     * Creates a SYSEX response.
     * @return the part.
     */
    private static byte[] response() {
        return new byte[]{(byte)IConstants.SYSEX_START,
                          Command.Write.get(),
                          (byte)IConstants.SYSEX_STATUS};
    }

}   // class WriteHandler

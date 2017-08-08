package com.funkdefino.sysex.handlers;

import com.funkdefino.common.io.IOUtil;
import com.funkdefino.sysex.IUtility;
import com.funkdefino.sysex.util.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class QueryHandler extends BaseHandler {

    //** ---------------------------------------------------------- Construction

    /**
     * Ctor.
     * @param utility a utility implementation.
     */
    public QueryHandler(IUtility utility) {
        super(utility);
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns a list of sequence names and associated identifiers.
     * @param data unused.
     * @return a list of SYSEX part(s).
     * @throws IOException on error.
     */
    public List<byte[]> execute(byte[] data) throws IOException {

        List<byte[]> ls = new ArrayList<byte[]>();
        ls.add(query());
        return ls;

    }   // execute()

    //** -------------------------------------------------------- Implementation

    /**
     * Creates a single SYSEX part.
     * @return the part.
     * @throws IOException on error.
     */
    private byte[] query() throws IOException {

        ByteArrayOutputStream baos = null;
        byte[] buf = null;

        try {
            baos = new ByteArrayOutputStream();
            baos.write(IConstants.SYSEX_START);
            baos.write(Command.Query.get());
            writeSequences(baos);
            baos.write(IConstants.SYSEX_STATUS);
            buf = baos.toByteArray();
        }
        finally {
            IOUtil.close(baos);
        }

        return buf;

    }   // query()

    /**
     * Writes sequence data (id & name) to the output stream.
     * @param baos the stream.
     * @throws IOException on error.
     */
    private void writeSequences(ByteArrayOutputStream baos) throws IOException {

        Map<Integer, String> sequences = getUtility().getConfiguration().getSequences();
        baos.write(sequences.size());
        for(Map.Entry<Integer,String> entry : sequences.entrySet()) {
            int id = entry.getKey();
            String name = entry.getValue();
            baos.write(id);
            baos.write(name.length());
            baos.write(name.getBytes());
        }
    }

}   // class QueryHandler

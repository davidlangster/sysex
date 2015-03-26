package com.funkdefino.sysex.handlers;

import com.differitas.common.io.IOUtil;
import com.funkdefino.sysex.IUtility;
import com.funkdefino.sysex.util.*;
import java.io.ByteArrayOutputStream;
import java.nio.file.*;
import java.util.*;

/**
 * <p>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class ReadHandler extends BaseHandler {

    //** ---------------------------------------------------------- Construction

    /**
     * Ctor.
     * @param utility a utility implementation.
     */
    public ReadHandler(IUtility utility) {
        super(utility);
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Splits a file sequence into a number of indvidual SYSEX parts.
     * @param data unused.
     * @return a list of SYSEX parts.
     * @throws Exception on error.
     */
    public List<byte[]> execute(byte[] data) throws Exception {

        int seqId = data[IConstants.IDX_SEQ_ID];
        int seqSubNum = data[IConstants.IDX_SEQ_SUBNUM];

        List<byte[]> ls = new ArrayList<byte[]>();
        String filename = getFilename(seqId, seqSubNum);
        System.out.println(String.format("Reading : %s", filename));
        byte[] sequence = Files.readAllBytes(Paths.get(filename));
        sequence = Arrays.copyOfRange(sequence,1,sequence.length);
        int index = 0;
        byte[] buf;

        while((buf = split(sequence, index)) != null) {
            index += (buf.length-2);
            ls.add(buf);
        }

        ls.add(end(ls.size()+1, seqId, seqSubNum));

        return ls;

    }   // execute()

    //** -------------------------------------------------------- Implementation

    /**
     * Creates a single SYSEX part.
     * @param sequence the file sequence.
     * @param index the start index.
     * @return the part.
     */
    private static byte[] split(byte[] sequence, int index) {

        ByteArrayOutputStream baos;
        byte[] buf = null;

        if(sequence[index] != (byte)IConstants.SYSEX_STATUS) {
            // Part length stored in 2 bytes
            int len = (sequence[index] << 0x07 | sequence[++index]);
            baos = new ByteArrayOutputStream( );
            baos.write(IConstants.SYSEX_START );
            baos.write(Command.Read.get());
            baos.write(sequence, ++index,--len);
            baos.write(IConstants.SYSEX_STATUS);
            buf = baos.toByteArray();
            IOUtil.close(baos);
        }

        return buf;

    }   // split()

    /**
     * Creates an end-of-sequence SYSEX part.
     * @param part the part number.
     * @param seqId the sequence indentifier.
     * @param seqSubNum the sequence sub number.
     * @return the part.
     */
    private static byte[] end(int part, int seqId, int seqSubNum) {
        return new byte[]{(byte)IConstants.SYSEX_START,
                          Command.Read.get(),
                          (byte)part,
                          (byte)seqId,
                          (byte)seqSubNum,
                          (byte)IConstants.SYSEX_STATUS};
    }

}   // class ReadHandler

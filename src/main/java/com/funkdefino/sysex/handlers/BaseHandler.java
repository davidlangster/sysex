package com.funkdefino.sysex.handlers;

import com.funkdefino.sysex.*;
import com.funkdefino.sysex.util.IConstants;
import java.io.FileNotFoundException;

/**
 * <p>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public abstract class BaseHandler implements IHandler {

    //** ------------------------------------------------------------------ Data

    private final IUtility utility;

    //** ---------------------------------------------------------- Construction

    /**
     * Ctor.
     * @param utility a utility implementation.
     */
    protected BaseHandler(IUtility utility) {
        this.utility = utility;
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the utility impelementation.
     * @return the utility.
     */
    protected final IUtility getUtility() {
        return utility;
    }

    /**
     * Formats a sequence filename.
     * @param seqId the sequence identifier
     * @param seqSubNum the sequence sub number.
     * @return the filename.
     * @throws FileNotFoundException on error.
     */
    protected final String getFilename(int seqId, int seqSubNum) throws FileNotFoundException {
        return utility.getFileInfo().getFilename(seqId, seqSubNum);
    }

    /**
     * Returns the number of the incoming SYSEX sequence part.
     * @param data SYSEX part data.
     * @return the part nymber.
     */
    protected static int getPartNo(byte[] data) {
        return data[IConstants.IDX_PART_NO];
    }

}   // class BaseHandler

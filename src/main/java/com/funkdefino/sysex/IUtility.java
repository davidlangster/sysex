package com.funkdefino.sysex;

import com.funkdefino.sysex.util.Configuration;
import com.funkdefino.sysex.util.FileInfo;

/**
 * <p>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public interface IUtility {
    public Configuration getConfiguration();
    public FileInfo getFileInfo();
}

package com.funkdefino.sysex.util;

import java.util.*;
import java.io.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class FileInfo {

    //** ------------------------------------------------------------- Constants

    private final static String MissingSequence = "No sequence registered for '%s'";
    private final static String EXTENSION = ".syx";
    private final static int ROLLOVER = 100;

    //** ------------------------------------------------------ Enumerated types

    public enum Type {
        Current,
        Next
    }

    //** ------------------------------------------------------------------ Data

    private Configuration config;

    //** ---------------------------------------------------------- Construction

    /**
     * Ctor.
     * @param config a configuration value object.
     */
    public FileInfo(Configuration config) {
        this.config = config;
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns a filename based on sequence identifier and sub number.
     * @param seqId the sequence identifier.
     * @param seqSubNum the sequence sub number.
     * @return the filename.
     * @throws FileNotFoundException on error.
     */
    public String getFilename(int seqId, int seqSubNum)
                  throws FileNotFoundException {

        String sequence = config.getSequences().get(seqId);
        if(sequence == null) {
            throw new FileNotFoundException(String.format(MissingSequence, seqId));
        }

        return String.format("%s/%s.%02d%s", config.getBase(), sequence, seqSubNum, EXTENSION);

    }   // getFilename()

    /**
      * Returns a (rollover) filename, based on prefix and type.
      * @param pfx the filename prefix.
      * @param type the filename type (current or next).
      * @return the filename.
      * @throws FileNotFoundException on error.
      */
    public String getFilename(String pfx, Type type)
                  throws FileNotFoundException {

        int sequence = getSequence(pfx,type);
        if(sequence == -1) {
            throw new FileNotFoundException("Sequence error");
        }

        return String.format("%s/%s.%02d%s", config.getBase(), pfx, sequence, EXTENSION);

    } // getFilename()

    //** --------------------------------------------------------- Implemenation

    /**
     * Gets a sequence number.
     * @param pfx the filename prefix.
     * @param type the sequence type (current or next).
     * @return the sequence number (or -1).
     */
    private int getSequence(String pfx, Type type) {

        File f = new File(config.getBase());
        int sequence = -1;

        if(f.isDirectory()) {

            // List & sort, newest first
            File[] files = listFiles(f, pfx);
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f2.lastModified()).compareTo(
                           f1.lastModified());
                }
            });

            if(files.length == 0) sequence = 1;
            else {
                if((sequence = getSequence(files[0])) != -1) {
                    // Conditionally increment the sequence number, with rollover.
                    if(type == Type.Next) {
                        if(sequence == ROLLOVER)
                            sequence = 0;
                        ++sequence;
                    }
                }
            }
        }

        return sequence;

    } // getSequence()

    /**
     * Lists all files in a directory with matching prefix.
     * @param directory the directory.
     * @param pfx the filename prefix.
     * @return a list of files.
     */
    private static File[] listFiles(File directory, String pfx) {

        List<File> ls = new ArrayList<File>();

        File[] files = directory.listFiles();
        if(files != null) {
            for(File file : files) {
                String name = file.getName();
                if(name.endsWith(EXTENSION)) {
                    name = name.substring(0, name.length() - EXTENSION.length());
                    if(name.startsWith(pfx)) {
                        ls.add(file);
                    }
                }
            }
        }

        return ls.toArray(new File[ls.size()]);

    }   // listFiles()

    /**
     * Calculates the file's sequence number.
     * @param file the file.
     * @return the sequence number (or -1).
     */
    private static int getSequence(File file) {

        int nSequence = -1;

        String name = file.getName();
        if(name.endsWith(EXTENSION)) {
            name = name.substring(0, name.length() - EXTENSION.length());
            int nIndex = name.lastIndexOf('.');
            if(nIndex != -1) {
                String sequence = name.substring(++nIndex, name.length());
                nSequence = Integer.parseInt(sequence);
            }
        }

        return nSequence;

    } // getSequence()

} // class FileInfo

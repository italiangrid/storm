package it.grid.storm.namespace;

import java.io.*;

/**
 * This class represents an Exception throws if TDirOptionData  is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidDescendantsFileRequestException
    extends Exception {

    private boolean isNotDirectory = false;

    public InvalidDescendantsFileRequestException(File fh) {
        isNotDirectory = fh.isDirectory();
    }

    public String toString() {
        return ("Path Specified is NOT a  directory = " + isNotDirectory);
    }
}

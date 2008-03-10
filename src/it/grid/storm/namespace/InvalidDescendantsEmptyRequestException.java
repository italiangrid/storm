package it.grid.storm.namespace;

import java.io.*;
import java.util.*;

/**
 * This class represents an Exception throws if TDirOptionData  is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidDescendantsEmptyRequestException
    extends Exception {

    private boolean isEmptyDir = false;

    public InvalidDescendantsEmptyRequestException(File fh, Collection pathlist) {
        isEmptyDir = ( ( (ArrayList) pathlist).size() == 0);
    }

    public String toString() {
        return ("Path Specified is EMPTY Directory = " + isEmptyDir);
    }
}

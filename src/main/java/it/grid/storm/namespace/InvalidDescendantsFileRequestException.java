/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

import java.io.*;

/**
 * This class represents an Exception throws if TDirOptionData is not well formed. *
 *
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */
public class InvalidDescendantsFileRequestException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private boolean isNotDirectory = false;

  public InvalidDescendantsFileRequestException(File fh) {

    isNotDirectory = fh.isDirectory();
  }

  public String toString() {

    return ("Path Specified is NOT a  directory = " + isNotDirectory);
  }
}

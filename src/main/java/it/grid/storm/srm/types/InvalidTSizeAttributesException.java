/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

public class InvalidTSizeAttributesException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private long size;

    public InvalidTSizeAttributesException(long size) {
      this.size = size;
    }

    public String toString() {

        return "Invalid TFileSize Attributes: " + size;
    }
}

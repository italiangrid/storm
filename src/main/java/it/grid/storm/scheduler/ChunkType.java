/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.scheduler;

/**
 * Title:
 *
 * <p>Description:
 *
 * <p>Copyright: Copyright (c) 2005-2007
 *
 * <p>Company: Project 'Grid.it' for INFN-CNAF, Bologna, Italy
 *
 * @author Zappi Riccardo <mailto://riccardo.zappi@cnaf.infn.it>
 * @version 1.1
 */
public class ChunkType {

  private String chunkTypeDesc = "undef";
  private String chunkName = "undef";
  public int chunkType = -1;

  public static final ChunkType GENERIC = new ChunkType(0, "generic", "Generic Chunk Type");
  public static final ChunkType PREPARE_TO_GET = new ChunkType(1, "PTG", "srmPrepareToGet Type");
  public static final ChunkType PREPARE_TO_PUT = new ChunkType(2, "PTP", "srmPrepareToPut Type");
  public static final ChunkType COPY = new ChunkType(3, "COPY", "srmCopy Type");
  public static final ChunkType BOL = new ChunkType(4, "BOL", "srmBringOnLine Type");

  private ChunkType(int type, String chunkName, String description) {

    this.chunkTypeDesc = description;
    this.chunkName = chunkName;
    this.chunkType = type;
  }

  public int getIndex() {

    return this.chunkType;
  }

  public String toString() {

    return chunkTypeDesc;
  }

  public int hashCode() {

    return this.chunkType;
  }

  public boolean equals(Object obj) {

    boolean result = false;
    if (obj instanceof ChunkType) {
      ChunkType other = (ChunkType) obj;
      if (other.chunkType == this.chunkType) {
        result = true;
      }
    }
    return result;
  }
}

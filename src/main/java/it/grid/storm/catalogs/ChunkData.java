/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

public interface ChunkData extends RequestData {

  /** Method that returns the primary key in persistence, associated with This Chunk. */
  public long getIdentifier();
}

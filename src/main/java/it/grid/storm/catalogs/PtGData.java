/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSizeInBytes;

public interface PtGData extends FileTransferData {

  /** Method that returns the requested pin life time for this chunk of the srm request. */
  public TLifeTimeInSeconds getPinLifeTime();

  /** Method that returns the dirOption specified in the srm request. */
  public TDirOption getDirOption();

  /** Method that returns the file size for this chunk of the srm request. */
  public TSizeInBytes getFileSize();

  /**
   * Method used to set the size of the file corresponding to the requested SURL. If the supplied
   * TSizeInByte is null, then nothing gets set!
   */
  public void setFileSize(TSizeInBytes size);

  /**
   * Method that sets the status of this request to SRM_FILE_PINNED; it needs the explanation String
   * which describes the situation in greater detail; if a null is passed, then an empty String is
   * used as explanation.
   */
  public void changeStatusSRM_FILE_PINNED(String explanation);
}

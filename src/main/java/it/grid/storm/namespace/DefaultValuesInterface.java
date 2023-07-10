/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

import it.grid.storm.srm.types.*;

public interface DefaultValuesInterface {

  public TLifeTimeInSeconds getDefaultSpaceLifetime();

  public TSpaceType getDefaultSpaceType();

  public TSizeInBytes getDefaultGuaranteedSpaceSize();

  public TSizeInBytes getDefaultTotalSpaceSize();

  public TLifeTimeInSeconds getDefaultFileLifeTime();

  public TFileStorageType getDefaultFileType();

  public final long DEFAULT_SPACE_LT = 2147483647L;
  public final String DEFAULT_SPACE_TYPE = "permament";
  public final long DEFAULT_SPACE_GUAR_SIZE = 2147483647L;
  public final long DEFAULT_SPACE_TOT_SIZE = 2147483647L;
  public final long DEFAULT_FILE_LT = 2147483647L;
  public final String DEFAULT_FILE_TYPE = "permament";
}

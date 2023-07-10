/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.ArrayOfSURLs;

public interface AbortFilesInputData extends AbortInputData {

  public ArrayOfSURLs getArrayOfSURLs();
}

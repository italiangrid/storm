/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the general Abort Input Data associated with the SRM request Abort
 *
 * @author Magnoni Luca
 * @author CNAF -INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;

public class AnonymousAbortFilesInputData extends AnonymousAbortGeneralInputData
    implements AbortFilesInputData {

  private final ArrayOfSURLs arrayOfSURLs;

  public AnonymousAbortFilesInputData(TRequestToken reqToken, ArrayOfSURLs surlArray)
      throws IllegalArgumentException {

    super(reqToken, AbortType.ABORT_REQUEST);
    if (surlArray == null) {
      throw new IllegalArgumentException(
          "Unable to create the object, invalid arguments: surlArray=" + surlArray);
    }
    this.arrayOfSURLs = surlArray;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * it.grid.storm.synchcall.data.datatransfer.AbortFilesInputData#getArrayOfSURLs
   * ()
   */
  @Override
  public ArrayOfSURLs getArrayOfSURLs() {

    return arrayOfSURLs;
  }
}

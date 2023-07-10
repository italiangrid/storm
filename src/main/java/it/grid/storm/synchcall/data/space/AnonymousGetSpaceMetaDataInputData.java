/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousGetSpaceMetaDataInputData extends AbstractInputData
    implements GetSpaceMetaDataInputData {

  private final ArrayOfTSpaceToken spaceTokenArray;

  public AnonymousGetSpaceMetaDataInputData(ArrayOfTSpaceToken tokenArray)
      throws IllegalArgumentException {

    if (tokenArray == null) {
      throw new IllegalArgumentException(
          "Unable to create the object, invalid arguments: tokenArray=" + tokenArray);
    }
    this.spaceTokenArray = tokenArray;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * it.grid.storm.synchcall.data.space.GetSpaceMetaDataInputData#getTokenArray
   * ()
   */
  @Override
  public ArrayOfTSpaceToken getSpaceTokenArray() {

    return spaceTokenArray;
  }
}

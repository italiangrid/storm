/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents a TTSpace Token
 *
 * @author EGRID ICTP Trieste / CNAF Bologna
 * @date March 23rd, 2005
 * @version 2.0
 */
package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArrayOfTMetaDataSpace implements Serializable {

  public static String PNAME_ARRAYOFSPACEDETAILS = "arrayOfSpaceDetails";

  ArrayList<TMetaDataSpace> metaDataList;

  public ArrayOfTMetaDataSpace() {

    metaDataList = new ArrayList<TMetaDataSpace>();
  }

  public TMetaDataSpace[] getArray() {

    return (TMetaDataSpace[]) metaDataList.toArray(new TMetaDataSpace[metaDataList.size()]);
  }

  public TMetaDataSpace getTMetaDataSpace(int i) {

    return (TMetaDataSpace) metaDataList.get(i);
  }

  public void setTMetaDataSpace(int index, TMetaDataSpace data) {

    metaDataList.set(index, data);
  }

  public void addTMetaDataSpace(TMetaDataSpace data) {

    metaDataList.add(data);
  }

  public int size() {

    return metaDataList.size();
  }

  public void encode(Map outputParam, String fieldName) {

    ArrayList metaDataSpaceList = new ArrayList();
    int arraySize = this.size();

    for (int i = 0; i < arraySize; i++) {
      Map metaDataSpace = new HashMap();
      TMetaDataSpace metaDataElement = this.getTMetaDataSpace(i);
      metaDataElement.encode(metaDataSpace);

      metaDataSpaceList.add(metaDataSpace);
    }

    outputParam.put(fieldName, metaDataSpaceList);
  }
}

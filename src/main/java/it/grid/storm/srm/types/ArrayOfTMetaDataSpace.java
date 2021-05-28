/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ArrayOfTMetaDataSpace implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

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

  public void encode(Map<String, Object> outputParam, String fieldName) {

    List<Map<String, Object>> metaDataSpaceList = Lists.newArrayList();
    int arraySize = this.size();

    for (int i = 0; i < arraySize; i++) {
      Map<String, Object> metaDataSpace = Maps.newHashMap();
      TMetaDataSpace metaDataElement = this.getTMetaDataSpace(i);
      metaDataElement.encode(metaDataSpace);

      metaDataSpaceList.add(metaDataSpace);
    }

    outputParam.put(fieldName, metaDataSpaceList);
  }
}

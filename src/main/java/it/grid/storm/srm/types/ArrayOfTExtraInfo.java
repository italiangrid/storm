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
 * This class represents a TExtraInfoArray
 * 
 * @author EGRID ICTP Trieste / CNAF Bologna
 * @date July, 2006
 * @version 2.0
 */

package it.grid.storm.srm.types;

import java.io.*;
import java.util.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ArrayOfTExtraInfo implements Serializable {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  public static String PNAME_STORAGESYSTEMINFO = "storageSystemInfo";

  private ArrayList<TExtraInfo> extraInfoList;

  /**
   * Constructor that requires a String. If it is null, then an
   * InvalidArrayOfTExtraInfoAttributeException is thrown.
   */
  public ArrayOfTExtraInfo(TExtraInfo[] infoArray)
      throws InvalidArrayOfTExtraInfoAttributeException {

    if (infoArray == null) {
      throw new InvalidArrayOfTExtraInfoAttributeException(infoArray);
    }
  }

  public ArrayOfTExtraInfo() {

    extraInfoList = new ArrayList<TExtraInfo>();
  }

  public Object[] getArray() {

    return extraInfoList.toArray();
  }

  public TExtraInfo getTSpaceToken(int i) {

    return extraInfoList.get(i);
  }

  public void setTExtraInfo(int index, TExtraInfo info) {

    extraInfoList.set(index, info);
  }

  public void addTExtraInfo(TExtraInfo info) {

    extraInfoList.add(info);
  }

  public int size() {

    return extraInfoList.size();
  }

  /**
   * Fills this class using the values found in a structure inside a HashMap. The HashMap may
   * contain different structures inside, all are identified by a name. Used for communication with
   * the FE.
   * 
   * @param inputParam HashMap to read.
   * @param fieldName Name that identifies the ArrayOfTExtraInfo structure in the HashMap.
   * @return A new ArrayOfTExtraInfo instance.
   */
  @SuppressWarnings("unchecked")
  public static ArrayOfTExtraInfo decode(Map<String, Object> inputParam, String fieldName)
      throws InvalidArrayOfTExtraInfoAttributeException {

    List<Object> list = null;
    try {
      list = Arrays.asList((Object[]) inputParam.get(fieldName));
    } catch (NullPointerException e) {
      // log.warn("Empty SURL array found!");
    }

    if (list == null) {
      throw new InvalidArrayOfTExtraInfoAttributeException(null);
    }

    ArrayOfTExtraInfo extraInfoArray = new ArrayOfTExtraInfo();

    for (int i = 0; i < list.size(); i++) {
      Map<String, Object> extraInfo;

      extraInfo = (Map<String, Object>) list.get(i);
      try {
        extraInfoArray.addTExtraInfo(TExtraInfo.decode(extraInfo));
      } catch (InvalidTExtraInfoAttributeException e) {
        throw new InvalidArrayOfTExtraInfoAttributeException(null);
      }
    }
    return extraInfoArray;
  }

  public void encode(Map<String, Object> outputParam, String name) {

    List<Map<String, Object>> vector = Lists.newArrayList();

    for (TExtraInfo extraInfo : extraInfoList) {
      Map<String, Object> extraInfoMap = Maps.newHashMap();
      extraInfo.encode(extraInfoMap);
      vector.add(extraInfoMap);
    }
    outputParam.put(name, vector);
  }

  public String toString() {

    StringBuilder sb = new StringBuilder();
    if (extraInfoList != null) {
      sb.append("[");
      for (Iterator<TExtraInfo> it = extraInfoList.iterator(); it.hasNext();) {
        TExtraInfo element = (TExtraInfo) it.next();
        sb.append(element.toString());
      }
      sb.append("]");
    } else {
      sb.append("EMPTY LIST");
    }
    return sb.toString();
  }

}

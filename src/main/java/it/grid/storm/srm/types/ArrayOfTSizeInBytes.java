/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the ArrayOfTSizeInBytes SRM type.
 * 
 * @author Alberto Forti
 * @author CNAF - INFN Bologna
 * @date Luglio, 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class ArrayOfTSizeInBytes implements Serializable {

  private static final long serialVersionUID = -1987674620390240434L;

  public static final String PNAME_arrayOfExpectedFileSizes = "arrayOfExpectedFileSizes";

  private List<TSizeInBytes> sizeInBytesList;

  public ArrayOfTSizeInBytes() {

    sizeInBytesList = Lists.newArrayList();
  }

  public static ArrayOfTSizeInBytes decode(Map<String, Object> inputParam, String fieldName) {

    List<Object> inputList = null;
    try {
      inputList = Arrays.asList((Object[]) inputParam.get(fieldName));
    } catch (NullPointerException e) {
      // log.warn("Empty SURL array found!");
    }

    if (inputList == null)
      return null;

    ArrayOfTSizeInBytes list = new ArrayOfTSizeInBytes();
    for (int i = 0; i < inputList.size(); i++) {
      TSizeInBytes size = null;
      String strLong = (String) inputList.get(i);
      try {
        size = TSizeInBytes.make(Long.parseLong(strLong));
      } catch (InvalidTSizeAttributesException e) {
        return null;
      }
      list.addTSizeInBytes(size);
    }
    return list;
  }

  public Object[] getArray() {

    return sizeInBytesList.toArray();
  }

  public TSizeInBytes getTSizeInBytes(int i) {

    return sizeInBytesList.get(i);
  }

  public void setTSizeInBytes(int index, TSizeInBytes size) {

    sizeInBytesList.set(index, size);
  }

  public void addTSizeInBytes(TSizeInBytes size) {

    sizeInBytesList.add(size);
  }

  public int size() {

    return sizeInBytesList.size();
  }
}

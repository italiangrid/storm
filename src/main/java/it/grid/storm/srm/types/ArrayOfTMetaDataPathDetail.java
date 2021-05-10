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
 * @date March 23rd, 2005
 * @version 2.0
 */

package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class ArrayOfTMetaDataPathDetail implements Serializable {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  public static String PNAME_DETAILS = "details";
  public static String PNAME_ARRAYOFSUBPATHS = "arrayOfSubPaths";
  List<TMetaDataPathDetail> metaDataList;

  public ArrayOfTMetaDataPathDetail() {

    metaDataList = Lists.newArrayList();
  }

  public Object[] getArray() {

    return metaDataList.toArray();
  }

  public TMetaDataPathDetail getTMetaDataPathDetail(int i) {

    return metaDataList.get(i);
  }

  public void setTMetaDataPathDetail(int index, TMetaDataPathDetail elem) {

    metaDataList.set(index, elem);
  }

  public void addTMetaDataPathDetail(TMetaDataPathDetail elem) {

    metaDataList.add(elem);
  }

  public int size() {

    return metaDataList.size();
  }

  /**
   * Encode method, used to create a structured parameter representing this object, for FE
   * communication.
   * 
   * @param outputParam structured Parameter that must be filled whit ArrayOfTMetaDataPath
   *        information.
   * @param name name of the parameter
   */
  public void encode(Map<String, Object> outputParam, String name) {

    List<TMetaDataPathDetail> list = Lists.newArrayList();
    for (int i = 0; i < metaDataList.size(); i++) {
      ((TMetaDataPathDetail) metaDataList.get(i)).encode(list);
    }
    outputParam.put(name, list);
  }

  public String toString() {

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < metaDataList.size(); i++) {
      sb.append("MetaData[" + i + "]:\n");
      sb.append(((TMetaDataPathDetail) metaDataList.get(i)).toString());
    }
    return sb.toString();
  }

}

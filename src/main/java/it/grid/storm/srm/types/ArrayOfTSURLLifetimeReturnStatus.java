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
 * This class represents an ArrayOfTSURLLifetimeReturnStatus.
 * 
 * @author Alberto Forti
 * @author CNAF Bologna
 * @date Dec 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class ArrayOfTSURLLifetimeReturnStatus {

  public static String PNAME_ARRAYOFFILESTATUSES = "arrayOfFileStatuses";

  List<TSURLLifetimeReturnStatus> array;

  /**
   * Constructs an empty ArrayOfTSURLLifetimeReturnStatus.
   */
  public ArrayOfTSURLLifetimeReturnStatus() {

    array = Lists.newArrayList();
  }

  /**
   * Get the array list.
   * 
   * @return ArrayList
   */
  public List<TSURLLifetimeReturnStatus> getArray() {

    return array;
  }

  /**
   * Add an element to the array.
   * 
   * @param item TSURLLifetimeReturnStatus
   */
  public void addTSurlReturnStatus(TSURLLifetimeReturnStatus item) {

    array.add(item);
  }

  /**
   * Returns the size of the array.
   * 
   * @return int
   */
  public int size() {

    return array.size();
  }

  /**
   * Encodes the array to a HashMap structure.
   * 
   * @param outputParam HashMap
   * @param name String
   */
  public void encode(Map<String, Object> outputParam, String name) {

    List<Map<String, Object>> list = Lists.newArrayList();
    for (int i = 0; i < array.size(); i++) {
      ((TSURLLifetimeReturnStatus) array.get(i)).encode(list);
    }
    outputParam.put(name, list);
  }
}

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

package it.grid.storm.srm.types;

import java.util.Map;

/**
 * This class represents the TCheckSumValue of a Permission Area managed by Srm.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */

/**
 * Class that represent CheckSum for file.
 */
public class TCheckSumValue {

  private String value = null;

  public static String PNAME_CHECKSUMVALUE = "checkSumValue";

  public TCheckSumValue(String value) {

    this.value = value;
  }

  public String toString() {

    return value;
  }

  public String getValue() {

    return value;
  }

  public void encode(Map<String, Object> param, String name) {

    param.put(name, this.toString());
  }
}

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

/**
 * This class represents the ReqType of an SRM request. It is a simple application of the TypeSafe
 * Enum Pattern.
 * 
 * @author EGRID ICTP Trieste / CNAF Bologna
 * @date March 18th, 2005
 * @version 3.0
 */
public enum TRequestType {

  PREPARE_TO_GET("PrepareToGet"), PREPARE_TO_PUT("PrepareToPut"), COPY("Copy"), BRING_ON_LINE(
      "BringOnLine"), EMPTY("Empty");

  private final String value;

  private TRequestType(String value) {

    this.value = value;
  }

  public String getValue() {

    return value;
  }

  public boolean isEmpty() {

    return this.equals(EMPTY);
  }

  public String toString() {

    return value;
  }
}

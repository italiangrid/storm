/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * This class represents the TSpaceType of a Space Area managed by Srm.
 *
 * @author  Magnoni Luca
 * @author  CNAF - INFN  Bologna
 * @date    Avril, 2005
 * @version 1.0
i */
package it.grid.storm.srm.types;


import java.io.Serializable;


public class TSpaceType implements Serializable {

  private String type = null;

  public static final TSpaceType VOLATILE = new TSpaceType("Volatile");
  public static final TSpaceType PERMANENT = new TSpaceType("Permanent");
  public static final TSpaceType DURABLE = new TSpaceType("Durable");
  public static final TSpaceType VOSPACE = new TSpaceType("VOSpace");
  public static final TSpaceType EMPTY = new TSpaceType("Empty");

   
  private TSpaceType(String type) {
    this.type = type;
  } 
  
  public boolean isEmpty() {
    if(this == this.EMPTY)
        return true;
    else 
        return false;
  }

  public String toString() {
    return type;
  }


  public String getValue() {
    return type;
  }


  public static TSpaceType getTSpaceType(String type) {

    if (type == null) return EMPTY;

    if (type.toLowerCase().replaceAll(" ", "").equals(VOLATILE.getValue().toLowerCase())) {
      return VOLATILE;
    }
    if (type.toLowerCase().replaceAll(" ", "").equals(PERMANENT.getValue().toLowerCase())) {
      return PERMANENT;
    }
    if (type.toLowerCase().replaceAll(" ", "").equals(DURABLE.getValue().toLowerCase())) {
      return DURABLE;
    }
    if (type.toLowerCase().replaceAll(" ", "").equals(VOSPACE.getValue().toLowerCase())) {
        return VOSPACE;
      }
    else {
      return EMPTY;
    }
  }
}

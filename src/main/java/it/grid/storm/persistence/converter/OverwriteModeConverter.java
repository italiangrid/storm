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

package it.grid.storm.persistence.converter;

import static it.grid.storm.srm.types.TOverwriteMode.ALWAYS;
import static it.grid.storm.srm.types.TOverwriteMode.NEVER;
import static it.grid.storm.srm.types.TOverwriteMode.WHENFILESAREDIFFERENT;

import java.util.Map;

import com.google.common.collect.Maps;

import it.grid.storm.config.model.v2.OverwriteMode;
import it.grid.storm.srm.types.TOverwriteMode;

/**
 * Package private auxiliary class used to convert between DB and StoRM object model representation
 * of TOverwriteMode.
 * 
 * @author: EGRID ICTP
 * @version: 2.0
 * @date: June 2005
 */
public class OverwriteModeConverter {

  private static Map<TOverwriteMode, OverwriteMode> STORMtoDB = Maps.newHashMap();

  static {

    STORMtoDB.put(NEVER, OverwriteMode.N);
    STORMtoDB.put(ALWAYS, OverwriteMode.A);
    STORMtoDB.put(WHENFILESAREDIFFERENT, OverwriteMode.D);
  }

  public static OverwriteMode toDB(TOverwriteMode om) {

    if (STORMtoDB.containsKey(om)) {
      return STORMtoDB.get(om);
    }
    return OverwriteMode.N;
  }

  public static TOverwriteMode toSTORM(String s) {

    OverwriteMode om = OverwriteMode.valueOf(s.trim().toUpperCase());
    return toSTORM(om);
  }

  public static TOverwriteMode toSTORM(OverwriteMode om) {

    switch (om) {
      case N:
        return TOverwriteMode.NEVER;
      case A:
        return TOverwriteMode.ALWAYS;
      case D:
        return TOverwriteMode.WHENFILESAREDIFFERENT;
      default:
        return TOverwriteMode.EMPTY;
    }
  }

}

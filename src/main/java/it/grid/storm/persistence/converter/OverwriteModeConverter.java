/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.converter;

import static it.grid.storm.srm.types.TOverwriteMode.ALWAYS;
import static it.grid.storm.srm.types.TOverwriteMode.NEVER;
import static it.grid.storm.srm.types.TOverwriteMode.WHENFILESAREDIFFERENT;

import java.util.Map;

import com.google.common.collect.Maps;

import it.grid.storm.srm.types.TOverwriteMode;

/**
 * Package private auxiliary class used to convert between DB and StoRM object model representation
 * of TOverwriteMode.
 */
public class OverwriteModeConverter {

  private static Map<String, TOverwriteMode> DBtoSTORM = Maps.newHashMap();
  private static Map<TOverwriteMode, String> STORMtoDB = Maps.newHashMap();

  static {

    STORMtoDB.put(NEVER, "N");
    STORMtoDB.put(ALWAYS, "A");
    STORMtoDB.put(WHENFILESAREDIFFERENT, "D");

    DBtoSTORM.put("N", NEVER);
    DBtoSTORM.put("A", ALWAYS);
    DBtoSTORM.put("D", WHENFILESAREDIFFERENT);
  }

  public static String toDB(TOverwriteMode om) {

    if (STORMtoDB.containsKey(om)) {
      return STORMtoDB.get(om);
    }
    return "N";
  }

  public static TOverwriteMode toSTORM(String s) {

    if (DBtoSTORM.containsKey(s)) {
      return DBtoSTORM.get(s);
    }
    return TOverwriteMode.EMPTY;
  }

}

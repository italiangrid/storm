/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.TFileStorageType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Package private auxiliary class used to convert between DB raw data and StoRM object model
 * representation of TFileStorageType.
 *
 * @author: EGRID ICTP
 * @version: 2.0
 * @date: June 2005
 */
class FileStorageTypeConverter {

  private Map<String, TFileStorageType> DBtoSTORM = new HashMap<String, TFileStorageType>();
  private Map<TFileStorageType, String> STORMtoDB = new HashMap<TFileStorageType, String>();

  private static FileStorageTypeConverter c = new FileStorageTypeConverter();

  /**
   * Private constructor that fills in the conversion tables;
   *
   * <p>V - VOLATILE P - PERMANENT D - DURABLE
   */
  private FileStorageTypeConverter() {

    DBtoSTORM.put("V", TFileStorageType.VOLATILE);
    DBtoSTORM.put("P", TFileStorageType.PERMANENT);
    DBtoSTORM.put("D", TFileStorageType.DURABLE);
    String aux;
    for (Iterator<String> i = DBtoSTORM.keySet().iterator(); i.hasNext(); ) {
      aux = i.next();
      STORMtoDB.put(DBtoSTORM.get(aux), aux);
    }
  }

  /** Method that returns the only instance of FileStorageTypeConverter. */
  public static FileStorageTypeConverter getInstance() {

    return c;
  }

  /**
   * Method that returns the String used in the DB to represent the given TFileStorageType. The
   * empty String "" is returned if no match is found.
   */
  public String toDB(TFileStorageType fst) {

    String aux = (String) STORMtoDB.get(fst);
    if (aux == null) return "";
    return aux;
  }

  /**
   * Method that returns the TFileStorageType used by StoRM to represent the supplied String
   * representation in the DB. A configured default TFileStorageType is returned in case no
   * corresponding StoRM type is found. TFileStorageType.EMPTY is returned if there are
   * configuration errors.
   */
  public TFileStorageType toSTORM(String s) {

    TFileStorageType aux = DBtoSTORM.get(s);
    if (aux == null)
      // This case is that the String s is different from V,P or D.
      aux = DBtoSTORM.get(Configuration.getInstance().getDefaultFileStorageType());
    if (aux == null)
      // This case should never happen, but in case we prefer ponder PERMANENT.
      return TFileStorageType.EMPTY;
    else return aux;
  }

  public String toString() {

    return "FileStorageTypeConverter.\nDBtoSTORM map:" + DBtoSTORM + "\nSTORMtoDB map:" + STORMtoDB;
  }
}

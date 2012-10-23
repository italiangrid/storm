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

package it.grid.storm.catalogs;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.config.Configuration;

/**
 * Package private auxiliary class used to convert between DB and StoRM object model
 * representation of TOverwriteMode.
 *
 * @author:  EGRID ICTP
 * @version: 2.0
 * @date:    June 2005
 */
public class OverwriteModeConverter {

    private Map DBtoSTORM = new HashMap();
    private Map STORMtoDB = new HashMap();

    private static OverwriteModeConverter c = new OverwriteModeConverter();

    /**
     * Private constructor that fills in the conversion table; in particular, DB uses String
     * values to represent TOverwriteMode:
     *
     * N NEVER
     * A ALWAYS
     * D WHENFILESAREDIFFERENT
     */
    private OverwriteModeConverter() {
        DBtoSTORM.put("N",TOverwriteMode.NEVER);
        DBtoSTORM.put("A",TOverwriteMode.ALWAYS);
        DBtoSTORM.put("D",TOverwriteMode.WHENFILESAREDIFFERENT);
        Object aux;
        for (Iterator i = DBtoSTORM.keySet().iterator(); i.hasNext(); ) {
            aux = i.next();
            STORMtoDB.put(DBtoSTORM.get(aux),aux);
        }
    }

    /**
     * Method that returns the only instance of OverwriteModeConverter.
     */
    public static OverwriteModeConverter getInstance() {
        return c;
    }

    /**
     * Method that returns the int used by DPM to represent the
     * given TOverwriteMode. "" is returned if no match is found.
     */
    public String toDB(TOverwriteMode om) {
        String aux = (String) STORMtoDB.get(om);
        if (aux==null) return "";
        return aux;
    }

    /**
     * Method that returns the TOverwriteMode used by StoRM to represent
     * the supplied String representation of DPM.  A configured default
     * TOverwriteMode is returned in case no corresponding StoRM type is found.
     * TOverwriteMode.EMPTY is returned if there are configuration errors.
     */
    public TOverwriteMode toSTORM(String s) {
        TOverwriteMode aux = (TOverwriteMode) DBtoSTORM.get(s);
        if (aux==null) aux = (TOverwriteMode) DBtoSTORM.get( Configuration.getInstance().getDefaultOverwriteMode() );
        if (aux==null) return TOverwriteMode.EMPTY; else return aux;
    }

    public String toString() {
        return "OverWriteModeConverter.\nDBtoSTORM map:"+DBtoSTORM+"\nSTORMtoDB map:"+STORMtoDB;
    }

}

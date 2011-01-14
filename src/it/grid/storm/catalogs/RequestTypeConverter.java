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
import it.grid.storm.srm.types.TRequestType;

/**
 * Package private auxiliary class used to convert between DB and StoRM object
 * model representation of the request type.
 */
class RequestTypeConverter {

    private Map DBtoSTORM = new HashMap();
    private Map STORMtoDB = new HashMap();

    private static RequestTypeConverter c = new RequestTypeConverter();

    private RequestTypeConverter() {
        DBtoSTORM.put("PTG",TRequestType.PREPARE_TO_GET);
        DBtoSTORM.put("PTP",TRequestType.PREPARE_TO_PUT);
        DBtoSTORM.put("COP",TRequestType.COPY);
        DBtoSTORM.put("BOL",TRequestType.BRING_ON_LINE);
        Object aux;
        for (Iterator i = DBtoSTORM.keySet().iterator(); i.hasNext(); ) {
            aux = (String) i.next();
            STORMtoDB.put(DBtoSTORM.get(aux),aux);
        }
    }

    /**
     * Method that returns the only instance of RequestTypeConverter.
     */
    public static RequestTypeConverter getInstance() {
        return c;
    }

    /**
     * Method that returns the String used by DB to represent the
     * given TRequestType. An empty String is returned if no match
     * is found.
     */
    public String toDB(TRequestType rt) {
        String aux = (String) STORMtoDB.get(rt);
        if (aux==null) return "";
        return aux;
    }

    /**
     * Method that returns the TRequestType used by StoRM to represent
     * the supplied String representation of DB. TRequestType.EMPTY is
     * returned if no StoRM type is found.
     */
    public TRequestType toSTORM(String s) {
        TRequestType aux = (TRequestType) DBtoSTORM.get(s);
        if (aux==null) return TRequestType.EMPTY;
        return aux;
    }
}

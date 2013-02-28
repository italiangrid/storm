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

package it.grid.storm.persistence.util.db;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class InsertBuilder extends SQLBuilder {
    private String table;
    private Map<String, Object> columnsAndData = new HashMap<String, Object>();

    public void setTable(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }

    public String getCommand() {
        return "INSERT INTO ";
    }

    public String getCriteria() {
        return "";
    }

    public String getWhat() {
        StringBuffer columns = new StringBuffer();
        StringBuffer values = new StringBuffer();
        StringBuffer what = new StringBuffer();

        String columnName = null;
        Iterator<String> iter = columnsAndData.keySet().iterator();
        while (iter.hasNext()) {
            columnName = iter.next();
            columns.append(columnName);
            values.append(columnsAndData.get(columnName));
            if (iter.hasNext()) {
                columns.append(',');
                values.append(',');
            }
        }

        what.append(" (");
        what.append(columns);
        what.append(") VALUES (");
        what.append(values);
        what.append(") ");
        return what.toString();

    }

    public void addColumnAndData(String columnName, Object value) {
        if (value != null) {
            columnsAndData.put(columnName, value);
        }
    }
}

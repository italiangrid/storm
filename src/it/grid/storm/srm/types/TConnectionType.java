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
 * This class represents the TTransferParameters SRM type.
 * @author  Alberto Forti
 * @author  Cnaf -INFN Bologna
 * @date July, 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.Hashtable;
import java.util.Map;

public class TConnectionType {
    public static String PNAME_connectionType = "connectionType";
    private String connectionType = null;
    
    public static final TConnectionType
        WAN = new TConnectionType("WAN"),
        LAN = new TConnectionType("LAN"),
        EMPTY = new TConnectionType("EMPTY");

    private TConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
    
    public final static TConnectionType getTConnectionType(int idx) {
        switch (idx) {
        case 0: return WAN;
        case 1: return LAN;
        default: return EMPTY;
        }
    }
    
    public final static TConnectionType decode(Map inputParam, String fieldName)
    {
        Integer val;
        
        val = (Integer) inputParam.get(fieldName);
        if (val == null) return EMPTY;
        
        return TConnectionType.getTConnectionType(val.intValue());
    }
    
    public int toInt(TConnectionType conType) {
        if (conType.equals(WAN)) return 0;
        if (conType.equals(LAN)) return 1;
        return 2;
    }
    
    public String toString()
    {
        return connectionType;
    }
    
    public String getValue()
    {
        return connectionType;
    }
}

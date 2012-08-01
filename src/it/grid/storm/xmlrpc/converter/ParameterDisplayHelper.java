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

package it.grid.storm.xmlrpc.converter;

import it.grid.storm.srm.types.ArrayOfSURLs;
import java.util.Map;

public class ParameterDisplayHelper {

    private static final String sepBegin="(";
    private static final String sepEnd=")";  
    private static final String arrow="->";

    public static String display(Map<?, ?> map)
    {
        StringBuilder sb = new StringBuilder("[");
        for (Object mapKey : map.keySet())
        {
            String mapKeyStr = mapKey.toString();
            sb.append(sepBegin).append(mapKeyStr);
            if ((mapKeyStr.equals("userFQANS")) || (mapKeyStr.equals(ArrayOfSURLs.ARRAYOFSURLS)))
            {
                sb.append(arrow).append("[");
                Object[] mapKeyValues = (Object[]) map.get(mapKey);
                for (int i = 0; i < mapKeyValues.length - 1; i++)
                {
                    sb.append(mapKeyValues[i].toString()).append(",");
                }
                sb.append(mapKeyValues[mapKeyValues.length - 1]).append("]");

            }
            else
            {
                String mapKeyValue = "'" + (map.get(mapKey)).toString() + "'";
                sb.append(arrow).append(mapKeyValue).append("]");
            }
        }

        return sb.append("]").toString();
	}
	
}

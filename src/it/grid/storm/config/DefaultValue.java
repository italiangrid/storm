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

package it.grid.storm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultValue {

    private static final Logger log = LoggerFactory.getLogger(DefaultValue.class);

    private DefaultValue() {
    }

    /**
     * Retrieve default Space Type for anonymous user
     */
    public static String getAnonymous_SpaceType() {
        //return Config.getStringParameter("anyVO.space.type");
        return "volatile";
    }

    /**
     * Retrieve default Space Type for named VO
     */
    public static String getNamedVO_SpaceType(String voname) {
        String result = null;
        //result = Config.getStringParameter(voname+".space.type");
        if (result==null)
        {
            log.info("Searching for ANONYMOUS <SPACE-TYPE> parameter value..");
            result = getAnonymous_SpaceType();
        }
        return result;
    }

    /**
     * Retrieve default Total Space Size for anonymous user
     */
    public static long getAnonymous_TotalSpaceSize() {
        return 104857600; //100 Mb
        //return Config.getLongParameter("anyVO.space.totalsize");
    }

    /**
     * Retrieve default Total Space Size for named VO
     */
    public static long getNamedVO_TotalSpaceSize(String voname) {
        long result = -1;
        // result = Config.getLongParameter(voname + ".space.totalsize");
        if (result == -1) {
            log.info("Searching for ANONYMOUS <TOTAL-SPACE-SIZE> parameter value..");
            result = getAnonymous_TotalSpaceSize();
        }
        return result;
    }

    /**
     * Retrieve default Total Space Size for anonymous user
     */
    public static long getAnonymous_GuaranteedSpaceSize() {
        return 10485760; //10 Mb
        //return Config.getLongParameter("anyVO.space.guarsize");
    }

    /**
     * Retrieve default Total Space Size for named VO
     */
    public static long getNamedVO_GuaranteedSpaceSize(String voname) {
        long result = -1;
        // result = Config.getLongParameter(voname + ".space.guarsize");
        if (result == -1) {
            log.info("Searching for ANONYMOUS <GUARANTEED-SPACE-SIZE> parameter value..");
            result = getAnonymous_GuaranteedSpaceSize();
        }
        return result;
    }

    /**
     * Retrieve default Total Space Life Time for anonymous user
     */
    public static long getAnonymous_SpaceLifeTime() {
        return 86400; //24h
        //return Config.getLongParameter("anyVO.space.lifetime");
    }

    /**
     * Retrieve default Space Life Time for named VO
     */
    public static long getNamedVO_SpaceLifeTime(String voname) {
        long result = -1;
        // result = Config.getLongParameter(voname + ".space.lifetime");
        if (result == -1) {
            log.info("Searching for ANONYMOUS <SPACE-LIFETIME> parameter value..");
            result = getAnonymous_SpaceLifeTime();
        }
        return result;
    }


}

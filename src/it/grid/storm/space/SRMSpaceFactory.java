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

package it.grid.storm.space;

import it.grid.storm.griduser.GridUserInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 30, 2008
 * 
 */

public class SRMSpaceFactory {

    private static final Logger log = LoggerFactory.getLogger(SRMSpaceFactory.class);



    public static SRMSpace get() {
        return null;
    }

    /**
     * @return
     */
    public static SRMSpace createDynamic(GridUserInterface user) {

        return null;


    }

    public static SRMSpace createStatic() {
        return null;
    }

}

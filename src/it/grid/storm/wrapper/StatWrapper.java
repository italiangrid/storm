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

package it.grid.storm.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class represent wrapper for native get acl libraryy.
 * Long containing size if success
 * null
 *
 */
public class StatWrapper {
    /**
     * Logger.
     * This Logger it's used to log information.
     */
    private static final Logger log = LoggerFactory.getLogger(StatWrapper.class);

    native long statfs(String rootDir);
    static  {
        //DEBUG
        //log.debug("<StatWrapper>## Load Library ##");
        String libraryPath = System.getProperty("java.library.path");
        log.debug("<StatWrapper> JAVA.LIBRARY.PATH = "+libraryPath);

        try {
            System.loadLibrary("statnativelib");
        } catch (UnsatisfiedLinkError e) {
            log.error("Get ACL native library failed to load!", e);
            System.exit(1);
        }
    }

    native long stat(String file);

    static 	{
        try {
            System.loadLibrary("statnativelib");
        } catch (UnsatisfiedLinkError e) {
            log.error("Get ACL native library failed to load!", e);
            System.exit(1);
        }
    }


}

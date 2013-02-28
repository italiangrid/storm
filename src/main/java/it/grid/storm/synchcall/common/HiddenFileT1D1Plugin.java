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

package it.grid.storm.synchcall.common;

import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.naming.NamespaceUtil;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiddenFileT1D1Plugin implements T1D1PluginInterface {

    private static final Logger log = LoggerFactory.getLogger(HiddenFileT1D1Plugin.class);

    public int startMigration(StoRI stori, String prefix) {
        if(stori!=null) {
            log.debug("HiddenFileT1D1PLugin: SURL filename"+stori.getFilename());
            String localPathWithoutFilename =  NamespaceUtil.consumeFileName(stori.getLocalFile().getPath());
            log.debug("HiddenFileT1D1PLugin: local path without filename "+ localPathWithoutFilename);
            String hiddenFileName = localPathWithoutFilename + "."+prefix+stori.getFilename();
            log.debug("HiddenFileT1D1Plugin: hidden file name "+hiddenFileName);

            File hiddenFile =   new File(hiddenFileName);
            try {
                hiddenFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch blo
                log.debug("HiddenFileT1D1Plugin: Error creating file "+e);
                return 1;
            }

            return 0;

        } else {
            return 1;
        }
    }

}

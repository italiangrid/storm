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

package it.grid.storm.authz.sa.conf;

import it.grid.storm.authz.AuthzDirector;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.slf4j.Logger;

public class FileAuthzDBListener implements ConfigurationListener {

    private final Logger log = AuthzDirector.getLogger();
    private String authzFileName;

    public FileAuthzDBListener(String authzFileName) {
        this.authzFileName = authzFileName;
    }

    /**
     * configurationChanged
     *
     * @param configurationEvent ConfigurationEvent
     */
    public void configurationChanged(ConfigurationEvent configurationEvent) {
        if (!configurationEvent.isBeforeUpdate())
        {
            // only display events after the modification was done
            log.debug("Authz DB File "+this.authzFileName+" is changed!");
            log.debug("  - Type = " + configurationEvent.getType());
            if (configurationEvent.getPropertyName() != null)
            {
                log.debug("Property name = " + configurationEvent.getPropertyName());
            }
            if (configurationEvent.getPropertyValue() != null)
            {
                log.debug("Property value = " + configurationEvent.getPropertyValue());
            }
        }
    }

}

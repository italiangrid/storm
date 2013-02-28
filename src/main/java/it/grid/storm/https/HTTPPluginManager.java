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
package it.grid.storm.https;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 *
 */
public class HTTPPluginManager
{

    private static HTTPSPluginInterface httpPluginInstance = null;

    private static boolean initialized = false;
    
    private static final Object lock = new Object();
    
    private static Logger log = LoggerFactory.getLogger(HTTPPluginManager.class);
    
    /**
     * @param httpPlugin
     */
    public static void init(HTTPSPluginInterface httpPlugin)
    {
        if(!initialized)
        {
            HTTPPluginManager.httpPluginInstance = httpPlugin;
            synchronized(lock)
            {
                log.debug("Notifying the eventual waiters...");
                initialized = true;
                lock.notifyAll();
            }
        }
    }
    
    /**
     * @return
     * @throws IllegalStateException
     */
    public static HTTPSPluginInterface getHTTPSPluginInstance() throws IllegalStateException
    {
        synchronized (lock)
        {
            if (!initialized)
            {
                try
                {
                    log.debug("Waiting for the initialization...");
                    lock.wait();
                    log.debug("Initialization signaled!");
                }
                catch (InterruptedException e)
                {
                    log.warn("Unexpected InterruptedException :" + e.getMessage());
                }
            }
            if (!initialized)
            {
                log.warn("Unexpected state of not initialized HTTPPluginManager after signal barrier");
                throw new IllegalStateException("Not initialized! Call init(HTTPSPluginFactory) first!");
            }
        }
        return httpPluginInstance;
    }
}

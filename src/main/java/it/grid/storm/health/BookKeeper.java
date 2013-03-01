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

package it.grid.storm.health;

import java.util.ArrayList;

import org.slf4j.Logger;

public abstract class BookKeeper {

    protected Logger bookKeepingLog = HealthDirector.getBookKeepingLogger();
    protected Logger performanceLog = HealthDirector.getPerformanceLogger();

    protected ArrayList<LogEvent> logbook = new ArrayList<LogEvent>();

    /**
     *
     * @param logEvent LogEvent
     */
    public abstract void addLogEvent(LogEvent logEvent);



    /**
     * Removes all event's in logbook field
     */
    public synchronized void cleanLogBook()
    {
            logbook.clear();
    }
    
//////////////////////////////////////////

    /**
     *
     * @param msg String
     */
    protected void logDebug(String msg) {
        if ((HealthDirector.isBookKeepingConfigured())&&(HealthDirector.isBookKeepingEnabled())) {
            bookKeepingLog.debug("BK: " + msg);
        }
        /**
        if ((HealthDirector.isPerformanceMonitorConfigured())&&(HealthDirector.isPerformanceMonitorEnabled())) {
            performanceLog.debug("P: " + msg);
        }
         **/
    }

    /**
     *
     * @param msg String
     */
    protected void logInfo(String msg) {
        if ((HealthDirector.isBookKeepingConfigured())&&(HealthDirector.isBookKeepingEnabled())) {
            bookKeepingLog.info(msg);
        }
        /**
        if ((HealthDirector.isPerformanceMonitorConfigured())&&(HealthDirector.isPerformanceMonitorEnabled())) {
            performanceLog.info(msg);
        }
         **/

    }


}
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
 * 
 */
package it.grid.storm.namespace.util.userinfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class LocalGroups {

    private static final Logger log = LoggerFactory.getLogger(LocalGroups.class);
    private Set<String> groups = Collections.synchronizedSet(new HashSet<String>()); 
    private ConcurrentHashMap<String, Integer> groupId = new ConcurrentHashMap<String, Integer>();
    private ConcurrentHashMap<Integer, String> groupName = new ConcurrentHashMap<Integer, String>();
    private static final LocalGroups instanceLinux = new LocalGroups();
    private long parsingInstant = 0;
    private static final long minimumLifetime = 1000 * 60 * 10; //10 minutes;

    private LocalGroups() {
        init();
    }

    public static void refresh() {
        LocalGroups thiS = getInstance();
        thiS.init();  
    }

    private synchronized void init() {
        groups.clear();
        groupId.clear();
        groupName.clear();
        
        try {
            //Parsing all the database and cache it
            groupId.putAll(UserInfoExecutor.digestGroupDatabase());
            groups.addAll(groupId.keySet());  
            for (String gName : groups) {
                groupName.put(groupId.get(gName), gName);
            }
            parsingInstant = System.currentTimeMillis();
        } catch (UserInfoException e) {
            log.error("Unable to digest the local group database: " + e);
        }
    }

    private static synchronized LocalGroups getInstance() {
        if (instanceLinux.parsedAge() > LocalGroups.minimumLifetime) {
            instanceLinux.init(); // Re-digest the local group database
            instanceLinux.parsingInstant = System.currentTimeMillis();
        }
        return instanceLinux;
    }

    private long parsedAge() {
        return System.currentTimeMillis() - parsingInstant;
    }

    
    /******************
     * PUBLIC METHODS *
    *******************/
    
    
    /**
     * 
     */
    public static boolean isGroupDefined(String groupName) {
        boolean result = false;
        LocalGroups gr = getInstance();
        result = gr.groups.contains(groupName);
        if (!result) {
            // Try a refresh
            refresh();
            result = gr.groups.contains(groupName);
        }
        return result;
    }

    
    public static int getGroupId(String groupName) {
        int result = -1;
        if (isGroupDefined(groupName)) {
            LocalGroups gr = getInstance();
            result = gr.groupId.get(groupName).intValue();
        } 
        return result;
    }

 
    public static String getGroupName(int groupId) {
        String result = "unknown";
        LocalGroups gr = getInstance();
        if (gr.groupName.containsKey(Integer.valueOf(groupId))) {
            result = gr.groupName.get(Integer.valueOf(groupId));
        } else {
            // Try a refresh
            refresh();
            result = gr.groupName.get(Integer.valueOf(groupId));
            if (result==null) {
                log.warn("Unable to find a group with GID='"+groupId+"'");
            }
        }
        return result;
    }

    
    public static Map<String,Integer> getGroupDB() {
        LocalGroups gr = getInstance();
        return gr.groupId;
    }
    
}

/**
 * 
 */
package it.grid.storm.namespace.util.userinfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class LocalGroups {

    private static final Logger log = LoggerFactory.getLogger(LocalGroups.class);
    private HashSet<String> groups = null;
    private HashMap<String, Integer> groupId = null;
    private HashMap<Integer, String> groupName = null;
    private static final LocalGroups instanceLinux = new LocalGroups();
    private long parsingInstant = 0;
    private static final long minimumLifetime = 1000 * 60; //1 minute;

    private LocalGroups() {
        init();
    }

    public static void refresh() {
        LocalGroups thiS = getInstance();
        thiS.init();  
    }

    private void init() {
        groups = new HashSet<String>();
        groupId = new HashMap<String, Integer>();
        groupName = new HashMap<Integer, String>();
        
        try {
            //Parsing all the database and cache it
            groupId = UserInfoExecutor.digestGroupDatabase();
            groups.addAll(groupId.keySet());  
            for (String gName : groups) {
                groupName.put(groupId.get(gName), gName);
            }
            
            parsingInstant = System.currentTimeMillis();
        } catch (UserInfoException e) {
            log.error("Unable to digest the local group database: " + e);
        }
    }

    private static LocalGroups getInstance() {
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

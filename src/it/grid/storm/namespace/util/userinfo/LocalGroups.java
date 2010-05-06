/**
 * 
 */
package it.grid.storm.namespace.util.userinfo;

import it.grid.storm.config.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class LocalGroups {

    private static final String etcGroupLinuxCommand = "getent";

    private static final Logger log = LoggerFactory.getLogger(LocalGroups.class);
    private HashSet<String> groups = null;
    private HashMap<String, Integer> groupId = null;
    private HashMap<Integer, String> groupName = null;
    private static final LocalGroups instanceLinux = new LocalGroups();
    private long parsingInstant = 0;
    private final long minimumLifetime = 1000 * 60; //1 minute;

    private LocalGroups() {
        init(etcGroupLinuxCommand);
    }

    private LocalGroups(String filename) {
        init(filename);
    }

    public static void refresh() {
        LocalGroups thiS = getInstance();
        if (thiS.parsedAge() > thiS.minimumLifetime) {
            thiS.init(etcGroupLinuxCommand); // Re-parse the /etc/group file
            thiS.parsingInstant = System.currentTimeMillis();
        }
    }

    private void init(String filename) {
        groups = new HashSet<String>();
        groupId = new HashMap<String, Integer>();
        groupName = new HashMap<Integer, String>();
        try {
            BufferedReader in = new BufferedReader(new FileReader("/etc/group"));
            String str;
            while ((str = in.readLine()) != null) {
                // Parsing the line
                String patternStr = ":";
                String[] fields = str.split(patternStr);
                String groupNameStr = fields[0];
                groups.add(groupNameStr);
                int gid = 33333; //Default group name
                try {
                    gid = Integer.parseInt(fields[2]);
                } catch (NumberFormatException nfe) {
                    log.error("Unable to parse the GID '" + fields[2]
                            + "' and convert it to an Integer. Use the default group 33333");
                    gid = 33333;
                }
                Integer gId = Integer.valueOf(gid);
                groupId.put(groupNameStr, gId);
                groupName.put(gId, groupNameStr);
            }
            in.close();
            parsingInstant = System.currentTimeMillis();
        } catch (IOException e) {
            log.error("Unable to read the '" + filename + "' file." + e);
        }
    }

    private static LocalGroups getInstance() {
        return instanceLinux;
    }

    public long parsedAge() {
        return System.currentTimeMillis() - parsingInstant;
    }

    
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
        int result = 33333;
        if (isGroupDefined(groupName)) {
            LocalGroups gr = getInstance();
            result = gr.groupId.get(groupName);
        } else {
            log.error("Unable to retrieve groupId of groupName '" + groupName + "'");
        }
        return result;
    }

 
    public static String getGroupName(int groupId) {
        String result = "unknown";
        LocalGroups gr = getInstance();
        Integer gID = Integer.valueOf(groupId);
        if (gr.groupId.containsValue(gID)) {
            result = gr.groupName.get(gID);
        }
        return result;
    }

}

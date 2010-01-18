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
 * @author ritz
 */
public class EtcGroupReader {

    private static final String etcGroupLinuxFN = "/etc/group";
    private static final String etcGroupTest = "etc-group";

    private static final Logger log = LoggerFactory.getLogger(EtcGroupReader.class);
    private HashSet<String> groups = null;
    private HashMap<String, Integer> groupId = null;
    private HashMap<Integer, String> groupName = null;
    private static final EtcGroupReader instanceLinux = new EtcGroupReader();
    private EtcGroupReader instanceTest;

    private EtcGroupReader() {
        init(etcGroupLinuxFN);
    }

    private EtcGroupReader(String filename) {
        init(filename);
    }

    public static void refresh() {
        EtcGroupReader thiS = getInstance(false);
        thiS.init(etcGroupLinuxFN);
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
                int gid = Integer.parseInt(fields[2]);
                Integer gId = new Integer(gid);
                groupId.put(groupNameStr, gId);
                groupName.put(gId, groupNameStr);
            }
            in.close();
        } catch (IOException e) {
            log.error("Unable to read the '" + filename + "' file." + e);
        }
    }

    private static EtcGroupReader getInstance(boolean test) {
        if (test) {
            String configurationDir = Configuration.getInstance().configurationDir();
            String etcGroupFN = configurationDir + File.pathSeparator + etcGroupTest;
            log.debug("TEST etc-group filename = " + etcGroupFN);
            return new EtcGroupReader(etcGroupFN);
        }
        return instanceLinux;
    }

    public static boolean isGroupDefined(String groupName) {
        boolean result = false;
        EtcGroupReader gr = getInstance(false);
        result = gr.groups.contains(groupName);
        if (!result) {
            // Try a refresh
            gr.init(etcGroupLinuxFN);
            result = gr.groups.contains(groupName);
        }
        return result;
    }

    public static boolean isGroupDefined(String groupName, boolean test) {
        boolean result = false;
        EtcGroupReader gr = getInstance(test);
        result = gr.groups.contains(groupName);
        if (!result) {
            // Try a refresh
            gr.init(etcGroupTest);
            result = gr.groups.contains(groupName);
        }
        return result;
    }

    public static int getGroupId(String groupName) {
        int result = 33333;
        if (isGroupDefined(groupName)) {
            EtcGroupReader gr = getInstance(false);
            result = gr.groupId.get(groupName);
        } else {
            log.error("Unable to retrieve groupId of groupName '" + groupName + "'");
        }
        return result;
    }

    public static int getGroupId(String groupName, boolean test) {
        int result = 33333;
        if (isGroupDefined(groupName)) {
            EtcGroupReader gr = getInstance(test);
            result = gr.groupId.get(groupName);
        } else {
            log.error("Unable to retrieve groupId of groupName '" + groupName + "'");
        }
        return result;
    }

    public static String getGroupName(int groupId) {
        String result = "unknown";
        EtcGroupReader gr = getInstance(false);
        Integer gID = new Integer(groupId);
        if (gr.groupId.containsValue(gID)) {
            result = gr.groupName.get(gID);
        }
        return result;
    }

    public static String getGroupName(int groupId, boolean test) {
        String result = "unknown";
        EtcGroupReader gr = getInstance(test);
        Integer gID = new Integer(groupId);
        if (gr.groupId.containsValue(gID)) {
            result = gr.groupName.get(gID);
        }
        return result;
    }

}

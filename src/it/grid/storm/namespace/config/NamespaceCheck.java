package it.grid.storm.namespace.config;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.MappingRule;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.slf4j.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class NamespaceCheck {

    private final Logger log = NamespaceDirector.getLogger();
    private final Hashtable<String, VirtualFSInterface> vfss;
    private final Hashtable<String, MappingRule> maprules;
    private final Hashtable<String, ApproachableRule> apprules;

    public NamespaceCheck(Hashtable<String, VirtualFSInterface> vfss, Hashtable<String, MappingRule> maprules,
            Hashtable<String, ApproachableRule> apprules) {
        this.vfss = vfss;
        this.maprules = maprules;
        this.apprules = apprules;
    }

    public boolean check() {
        boolean result = true;
        result = checkVFS() && checkMapRules() && checkAppRules();
        return result;
    }

    /**
     * Check if the root of the VFS exists.
     * @todo: this method don't check if the root is accessible by storm user.
     *
     * @return true if "filesystems" element (list of VFS) is valid
     *         false otherwise
     */
    private boolean checkVFS() {
        boolean result = true;
        if (vfss == null) {
            return false;
        } else {
            ArrayList<VirtualFSInterface> rules = new ArrayList<VirtualFSInterface>(vfss.values());
            Iterator<VirtualFSInterface> scan = rules.iterator();

            while (scan.hasNext()) {
                VirtualFSInterface vfs = scan.next();
                try {
                    String aliasName = vfs.getAliasName();
                    log.debug("VFS named '" + aliasName + "' found.");
                    String root = vfs.getRootPath();
                    File file = new File(root);
                    boolean exists = file.exists();
                    if (!exists) {
                        log.error("ERROR in NAMESPACE: The VFS '" + aliasName + "' does not have a valid root :'"
                                + root + "'");
                        result = false;
                    }
                } catch (NamespaceException ex) {
                    log.error("Error while checking VFS.", ex);
                }
            }
        }
        return result;
    }

    private boolean checkMapRules() {
        boolean result = true;
        if (maprules == null) {
            return false;
        } else {
            int nrOfMappingRules = maprules.size();
            log.debug("Number of Mapping rules = " + nrOfMappingRules);
            ArrayList<MappingRule> rules = new ArrayList<MappingRule>(maprules.values());
            Iterator<MappingRule> scan = rules.iterator();
            MappingRule rule;
            String mappedVFS;
            boolean check = false;
            while (scan.hasNext()) {
                rule = scan.next();
                mappedVFS = rule.getMappedFS();
                //log.debug("Map rule "+rule.getRuleName()+" maps to VFS named = '"+mappedVFS+"'");
                check = vfss.containsKey(mappedVFS);
                if (!check) {
                    log.error("ERROR in NAMESPACE - MAP RULE '" + rule.getRuleName() + "' point a UNKNOWN VFS '"
                            + mappedVFS + "'!");
                    result = false;
                }
            }
        }
        return result;

    }

    private boolean checkAppRules() {
        boolean result = true;
        if (apprules == null) {
            return false;
        } else {
            int nrOfApproachableRules = apprules.size();
            log.debug("Number of Approachable rules = " + nrOfApproachableRules);
            ArrayList<ApproachableRule> rules = new ArrayList<ApproachableRule>(apprules.values());
            Iterator<ApproachableRule> scan = rules.iterator();
            boolean check = false;
            while (scan.hasNext()) {
                ApproachableRule rule = scan.next();
                ArrayList<String> approachVFSs = new ArrayList<String>(rule.getApproachableVFS());
                for (String aVfs : approachVFSs) {
                    check = vfss.containsKey(aVfs);
                    if (!check) {
                        log.error("ERROR in NAMESPACE - APP RULE '" + rule.getRuleName() + "' point a UNKNOWN VFS '"
                                + aVfs + "'!");
                        result = false;
                    }
                }
            }
        }
        return result;
    }
}

package it.grid.storm.namespace.config;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

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

    private Logger log = NamespaceDirector.getLogger();
    private Hashtable vfss;
    private Hashtable maprules;
    private Hashtable apprules;

    public NamespaceCheck(Hashtable vfss, Hashtable maprules, Hashtable apprules) {
        this.vfss = vfss;
        this.maprules = maprules;
        this.apprules = apprules;
    }

    public boolean check() {
        boolean result = true;
        result = checkVFS() && checkMaprules();
        return result;
    }

    private boolean checkVFS() {
        boolean result = true;
        if (vfss == null) {
            return false;
        }
        else {
            int nrOfVFS = vfss.size();
            //log.debug("Number of VFS = " + nrOfVFS);
            List rules = new ArrayList(vfss.values());
            Iterator scan = rules.iterator();
            VirtualFSInterface vfs;

            while (scan.hasNext()) {
                vfs = (VirtualFSInterface) scan.next();
                try {
                    String aliasName = vfs.getAliasName();
                    //log.debug("VFS named '" + aliasName + "' found.");
                }
                catch (NamespaceException ex) {
                    log.error("Error while checking VFS.", ex);
                }
            }

        }
        return result;
    }

    private boolean checkMaprules() {
        boolean result = true;
        if (maprules == null) {
            return false;
        }
        else {
            int nrOfMappingRules = maprules.size();
            log.debug("Number of Mapping rules = " + nrOfMappingRules);
            List rules = new ArrayList(maprules.values());
            Iterator scan = rules.iterator();
            MappingRule rule;
            String mappedVFS;
            boolean check = false;
            while (scan.hasNext()) {
                rule = (MappingRule) scan.next();
                mappedVFS = rule.getMappedFS();
                //log.debug("Map rule "+rule.getRuleName()+" maps to VFS named = '"+mappedVFS+"'");
                check = vfss.containsKey(mappedVFS);
                if (!check) {
                    log.debug("VFS named '" + mappedVFS + "' DOES NOT EXISTS!");
                    result = false;
                }
            }
        }

        return result;

    }

}

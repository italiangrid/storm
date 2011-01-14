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

package it.grid.storm.namespace.config;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.ACLEntry;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.DefaultACL;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.util.userinfo.LocalGroups;

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
        boolean totalResult = true;
        boolean vfsCheck = checkVFS();
        boolean mapRulesCheck = checkMapRules();
        boolean appRules = checkAppRules();
        boolean checkGroups = checkGroups(vfsCheck);
        totalResult =  vfsCheck && mapRulesCheck && appRules;
        return totalResult;
    }

    
    
    private boolean checkGroups(boolean vfsCheckResult) {
        log.info("Namespace check. Checking of the existence of the needed Local group ...");
        boolean result = true;
        if (!vfsCheckResult){
            log.warn("Skip the check of the needed Local Group, because check of VFSs failed."); 
        } else {
             
             ArrayList<VirtualFSInterface> vf = new ArrayList<VirtualFSInterface>(vfss.values());
             for (VirtualFSInterface vfs : vf) {
                try {
                    if (vfs.getStorageClassType().isTapeEnabled()) {
                       //Checking the existence of groups for the buffers
                       String groupRead = Configuration.getInstance().getGroupTapeReadBuffer(); 
                       if (!LocalGroups.isGroupDefined(groupRead)) {
                           log.warn("!!!!! Local Group for READ BUFFER ('"+groupRead+"') is not defined!");
                           result = false;
                       }
                       String groupWrite = Configuration.getInstance().getGroupTapeWriteBuffer();
                       if (!LocalGroups.isGroupDefined(groupWrite)) {
                           log.warn("!!!!! Local Group for WRITE BUFFER ('"+groupWrite+"') is not defined!");
                       }
                    }
                    // Check the presence of Default ACL
                    CapabilityInterface cap = vfs.getCapabilities();
                    if (cap!=null) {
                        DefaultACL defACL = cap.getDefaultACL();
                        if (defACL!=null) {
                            ArrayList<ACLEntry> acl = new ArrayList<ACLEntry>(defACL.getACL());
                            if (!acl.isEmpty()) {
                                for (ACLEntry aclEntry : acl) {
                                    if (!LocalGroups.isGroupDefined(aclEntry.getGroupName())) {
                                        log.warn("!!!!! Local Group for ACL ('"+aclEntry+"') is not defined!");
                                        result = false;
                                    }
                                }
                            }
                        }  
                    }
                    
                    
                } catch (NamespaceException e) {
                    log.error("Error while checking VFS.", e);
                    result = false;   
                }    
            }
        }
        if (result) {
            log.info("All local groups are defined. ");    
        } else {
            log.warn("Please check the local group needed to StoRM");
        }
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
        log.info("Namespace checking VFSs ..");
        boolean result = true;
        if (vfss == null) {
            log.error("Anyone VFS is defined in namespace!");
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
                    result = false;
                }
            }
        }
        if (result) {
            log.info(" VFSs are well-defined.");    
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

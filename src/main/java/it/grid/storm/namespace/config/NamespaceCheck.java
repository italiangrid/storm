/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.namespace.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.model.ACLEntry;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.Capability;
import it.grid.storm.namespace.model.DefaultACL;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.namespace.util.userinfo.LocalGroups;

public class NamespaceCheck {

	private final Logger log = NamespaceDirector.getLogger();
	private final Map<String, VirtualFS> vfss;
	private final Map<String, MappingRule> maprules;
	private final Map<String, ApproachableRule> apprules;

	public NamespaceCheck(Map<String, VirtualFS> vfss,
		Map<String, MappingRule> maprules,
		Map<String, ApproachableRule> apprules) {

		this.vfss = vfss;
		this.maprules = maprules;
		this.apprules = apprules;
	}

	public boolean check() {

		boolean vfsCheck = checkVFS();
		boolean mapRulesCheck = checkMapRules();
		boolean appRules = checkAppRules();
		checkGroups(vfsCheck);
		return vfsCheck && mapRulesCheck && appRules;
	}

	private boolean checkGroups(boolean vfsCheckResult) {

		log
			.info("Namespace check. Checking of the existence of the needed Local group ...");
		boolean result = true;
		if (!vfsCheckResult) {
			log
				.warn("Skip the check of the needed Local Group, because check of VFSs failed.");
		} else {

			List<VirtualFS> vf = new ArrayList<>(vfss.values());
			for (VirtualFS vfs : vf) {
				
				// Check the presence of Default ACL
				Capability cap = vfs.getCapabilities();
				if (cap != null) {
					DefaultACL defACL = cap.getDefaultACL();
					if (defACL != null) {
						List<ACLEntry> acl = new ArrayList<>(defACL.getACL());
						if (!acl.isEmpty()) {
							for (ACLEntry aclEntry : acl) {
								if (!LocalGroups.getInstance().isGroupDefined(
									aclEntry.getGroupName())) {
									log.warn("!!!!! Local Group for ACL ('{}') is not defined!", aclEntry);
									result = false;
								}
							}
						}
					}
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
	 * 
	 * @todo: this method don't check if the root is accessible by storm user.
	 * 
	 * @return true if "filesystems" element (list of VFS) is valid false
	 *         otherwise
	 */
	private boolean checkVFS() {

		log.info("Namespace checking VFSs ..");
		boolean result = true;
		if (vfss == null) {
			log.error("Anyone VFS is defined in namespace!");
			return false;
		} else {
			List<VirtualFS> rules = new ArrayList<>(vfss.values());
			Iterator<VirtualFS> scan = rules.iterator();

			while (scan.hasNext()) {
				VirtualFS vfs = scan.next();

					String aliasName = vfs.getAliasName();
					log.debug("VFS named '{}' found.", aliasName);
					String root = vfs.getRootPath();
					File file = new File(root);
					boolean exists = file.exists();
					if (!exists) {
						log.error("ERROR in NAMESPACE: The VFS '{}' does not have a valid root :'{}'", aliasName, root);
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
			log.debug("Number of Mapping rules = {}", nrOfMappingRules);
			List<MappingRule> rules = new ArrayList<>(maprules.values());
			Iterator<MappingRule> scan = rules.iterator();
			MappingRule rule;
			String mappedVFS;
			boolean check = false;
			while (scan.hasNext()) {
				rule = scan.next();
				mappedVFS = rule.getMappedFS().getAliasName();
				check = vfss.containsKey(mappedVFS);
				if (!check) {
					log.error("ERROR in NAMESPACE - MAP RULE '{}' point a UNKNOWN VFS '{}'!", rule.getRuleName(), mappedVFS);
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
			log.debug("Number of Approachable rules = {}", nrOfApproachableRules);
			List<ApproachableRule> rules = new ArrayList<>(apprules.values());
			Iterator<ApproachableRule> scan = rules.iterator();
			boolean check = false;
			while (scan.hasNext()) {
				ApproachableRule rule = scan.next();
				List<VirtualFS> approachVFSs = Lists.newArrayList(rule.getApproachableVFS());
				for (VirtualFS aVfs : approachVFSs) {
					check = vfss.containsKey(aVfs.getAliasName());
					if (!check) {
						log.error("ERROR in NAMESPACE - APP RULE '{}' point a UNKNOWN VFS '{}'!", rule.getRuleName(), aVfs);
						result = false;
					}
				}
			}
		}
		return result;
	}
}

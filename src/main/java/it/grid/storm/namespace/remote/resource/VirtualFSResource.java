package it.grid.storm.namespace.remote.resource;

/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.remote.Constants;
import it.grid.storm.namespace.remote.Constants.HttpPerms;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

/**
 * @author Michele Dibenedetto
 */
@Path("/" + Constants.RESOURCE + "/" + Constants.VERSION)
public class VirtualFSResource {

	private static final Logger log = LoggerFactory
		.getLogger(VirtualFSResource.class);

	class SAInfo {

		String name;
		String token;
		String voname;
		String root;
		String storageclass;
		List<String> stfnRoot;
		String retentionPolicy;
		String accessLatency;
		List<String> protocols;
		HttpPerms anonymous;
		long availableNearlineSpace;
		List<String> approachableRules;
	}

	/**
	 * @return
	 * @throws WebApplicationException
	 */
	@GET
	@Path("/" + Constants.LIST_ALL_VFS)
	@Produces("application/json")
	public String listVFS() throws WebApplicationException {

		log.info("Serving VFS resource listing");
		Collection<VirtualFSInterface> vfsCollection = null;
		try {
			vfsCollection = NamespaceDirector.getNamespace().getAllDefinedVFS();
		} catch (NamespaceException e) {
			log
				.error("Unable to retrieve virtual file system list. NamespaceException : "
					+ e.getMessage());
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
			responseBuilder.entity("Unable to retrieve virtual file systems");
			throw new WebApplicationException(responseBuilder.build());
		}
		Map<String, SAInfo> output = new HashMap<String, SAInfo>();
		for (VirtualFSInterface vfs : vfsCollection) {

			SAInfo sa = new SAInfo();
			sa.name = vfs.getAliasName();
			sa.token = vfs.getSpaceTokenDescription();
			try {
				sa.voname = vfs.getApproachableRules().get(0).getSubjectRules()
					.getVONameMatchingRule().getVOName();
			} catch (NamespaceException e) {
				log.error(e.getMessage());
			}
			sa.root = vfs.getRootPath();
			sa.stfnRoot = new ArrayList<String>();
			try {
				for (MappingRule rule : vfs.getMappingRules()) {
					sa.stfnRoot.add(rule.getStFNRoot());
				}
			} catch (NamespaceException e1) {
				log.error(e1.getMessage());
			}
			sa.protocols = new ArrayList<String>();
			Iterator<Protocol> protocolsIterator = vfs.getCapabilities()
				.getAllManagedProtocols().iterator();
			while (protocolsIterator.hasNext()) {
				sa.protocols.add(protocolsIterator.next().getSchema());
			}
			if (vfs.isHttpWorldReadable()) {
				if (vfs.isApproachableByAnonymous()) {
					sa.anonymous = HttpPerms.READWRITE;
				} else {
					sa.anonymous = HttpPerms.READ;
				}
			} else {
				sa.anonymous = HttpPerms.NOREAD;
			}
			sa.storageclass = vfs.getStorageClassType().getStorageClassTypeString();
			sa.retentionPolicy = vfs.getProperties().getRetentionPolicy()
				.getRetentionPolicyName();
			sa.accessLatency = vfs.getProperties().getAccessLatency()
				.getAccessLatencyName();
			try {
				sa.availableNearlineSpace = vfs.getAvailableNearlineSpace().value();
			} catch (NamespaceException e) {
				log.error(e.getMessage());
			}
			sa.approachableRules = new ArrayList<String>();
			try {
				for (ApproachableRule rule : vfs.getApproachableRules()) {
					if (rule.getSubjectRules().getDNMatchingRule().isMatchAll()
						&& rule.getSubjectRules().getVONameMatchingRule().isMatchAll()) {
						continue;
					}
					if (!rule.getSubjectRules().getDNMatchingRule().isMatchAll()) {
						sa.approachableRules.add(rule.getSubjectRules().getDNMatchingRule()
							.toGlue2String());
					}
					if (!rule.getSubjectRules().getVONameMatchingRule().isMatchAll()) {
						sa.approachableRules.add("vo:"
							+ rule.getSubjectRules().getVONameMatchingRule().getVOName());
					}
				}
			} catch (NamespaceException e) {
				log.error(e.getMessage());
			}
			if (sa.approachableRules.size() == 0) {
				sa.approachableRules.add("'ALL'");
			}
			output.put(vfs.getAliasName(), sa);
		}
		Gson gson = new Gson();
		return gson.toJson(output);
	}

}

package it.grid.storm.authz.remote.resource;

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

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.path.model.PathOperation;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.remote.Constants;
import it.grid.storm.catalogs.OverwriteModeConverter;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.StFN;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.srm.types.TOverwriteMode;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

class PermissionEvaluator {

	private static final Logger log = LoggerFactory
		.getLogger(PermissionEvaluator.class);

	public static Boolean isOverwriteAllowed() {

		return OverwriteModeConverter.getInstance()
			.toSTORM(Configuration.getInstance().getDefaultOverwriteMode())
			.equals(TOverwriteMode.ALWAYS);
	}

	static Boolean evaluateVomsGridUserPermission(String DNDecoded,
		String FQANSDecoded, String filePathDecoded, PathOperation operation) {

		String[] FQANSArray = parseFQANS(FQANSDecoded);
		GridUserInterface gu = buildGridUser(DNDecoded, FQANSArray);

		VirtualFSInterface fileVFS;
		try {
			fileVFS = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(
				filePathDecoded);
		} catch (NamespaceException e) {
			log.error("Unable to determine a VFS that maps the requested file "
				+ "path '{}'. NamespaceException: {}", filePathDecoded, e.getMessage());
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.NOT_FOUND);
			responseBuilder
				.entity("Unable to determine file path\'s associated virtual file system");
			throw new WebApplicationException(responseBuilder.build());
		}
		if (!fileVFS.getCapabilities().getAllManagedProtocols()
			.contains(Protocol.HTTPS)) {
			log.debug("User '{}' is not authorized to access the requeste file '{}' via "
				+ "HTTPS", gu, filePathDecoded);
			return new Boolean(false);
		}
		if (!fileVFS.isApproachableByUser(gu)) {
			log.debug("User '{}' is not authorized to approach the requested "
				+ "Storage Area '{}' via HTTPS", gu, fileVFS.getAliasName());
			return new Boolean(false);
		}
		StFN fileStFN = buildStFN(filePathDecoded, fileVFS);
		AuthzDecision decision = AuthzDirector.getPathAuthz().authorize(gu,
			operation, fileStFN);
		log.info("Authorization decision for user '{}{}' requesting {} on {} is "
			+ "[{}]", DNDecoded, FQANSDecoded == null ? "" : " - " + FQANSDecoded, 
				operation, filePathDecoded, decision);
		return evaluateDecision(decision);
	}

	/**
	 * @param DNDecoded
	 * @param FQANSDecoded
	 * @param filePathDecoded
	 * @param request
	 * @return never null
	 * @throws WebApplicationException
	 */
	static Boolean evaluateVomsGridUserPermission(String DNDecoded,
		String FQANSDecoded, String filePathDecoded, SRMFileRequest request)
		throws WebApplicationException {

		String[] FQANSArray = parseFQANS(FQANSDecoded);
		GridUserInterface gu = buildGridUser(DNDecoded, FQANSArray);

		VirtualFSInterface fileVFS;
		try {
			fileVFS = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(
				filePathDecoded);
		} catch (NamespaceException e) {
			log.error("Unable to determine a VFS that maps the requested file "
				+ "path '{}'. NamespaceException: {}", filePathDecoded, e.getMessage());
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.NOT_FOUND);
			responseBuilder
				.entity("Unable to determine file path\'s associated virtual file system");
			throw new WebApplicationException(responseBuilder.build());
		}
		if (!fileVFS.isApproachableByUser(gu)) {
			log.debug("User '{}' is not authorized to approach the requeste Storage "
				+ "Area '{}'", gu, fileVFS.getAliasName());
			return new Boolean(false);
		}
		StFN fileStFN = buildStFN(filePathDecoded, fileVFS);
		AuthzDecision decision = AuthzDirector.getPathAuthz().authorize(gu,
			request, fileStFN);
		log.info("Authorization decision for user '{}{}' requesting {} on {} is "
			+ "[{}]", DNDecoded, FQANSDecoded == null ? "" : " - " + FQANSDecoded, 
				request, filePathDecoded, decision);
		return evaluateDecision(decision);
	}

	static Boolean evaluateAnonymousPermission(String filePathDecoded,
		PathOperation request) {

		VirtualFSInterface fileVFS;
		try {
			fileVFS = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(
				filePathDecoded);
		} catch (NamespaceException e) {
			log.error("Unable to determine a VFS that maps the requested file "
				+ "path '{}'. NamespaceException: {}", filePathDecoded, e.getMessage());
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.NOT_FOUND);
			responseBuilder
				.entity("Unable to determine file path\'s associated virtual file system");
			throw new WebApplicationException(responseBuilder.build());
		}
		if (!fileVFS.getCapabilities().getAllManagedProtocols()
			.contains(Protocol.HTTP)) {
			log.debug("The requeste Storage Area '{}' is not approachable via "
				+ "HTTPS", fileVFS.getAliasName());
			return Boolean.FALSE;
		}
		log.info("Authorization decision for Anonymous user requesting {} on {} "
			+ "is [{}]", request, filePathDecoded, AuthzDecision.PERMIT);
		return Boolean.TRUE;
	}

	static Boolean evaluateAnonymousPermission(String filePathDecoded,
		SRMFileRequest request) {

		VirtualFSInterface fileVFS;
		try {
			fileVFS = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(
				filePathDecoded);
		} catch (NamespaceException e) {
			log.error("Unable to determine a VFS that maps the requested file "
				+ "path '{}'. NamespaceException: {}", filePathDecoded, e.getMessage());
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.NOT_FOUND);
			responseBuilder
				.entity("Unable to determine file path\'s associated virtual file system");
			throw new WebApplicationException(responseBuilder.build());
		}
		if (!fileVFS.isApproachableByAnonymous()
			&& !(request.isReadOnly() && fileVFS.isHttpWorldReadable())) {
			log.debug("The requeste Storage Area '{}' is not approachable by "
				+ "anonymous users", fileVFS.getAliasName());
			return new Boolean(false);
		}
		StFN fileStFN = buildStFN(filePathDecoded, fileVFS);
		AuthzDecision decision = AuthzDirector.getPathAuthz().authorizeAnonymous(
			request, fileStFN);
		log.info("Authorization decision for Anonymous user requesting {} "
			+ "on {} is [{}]", request, filePathDecoded, decision);
		return evaluateDecision(decision);
	}

	private static Boolean evaluateDecision(AuthzDecision decision) {

		if (decision.equals(AuthzDecision.PERMIT)) {
			return new Boolean(true);
		} else {
			if (decision.equals(AuthzDecision.DENY)) {
				return new Boolean(false);
			} else {
				if (decision.equals(AuthzDecision.INDETERMINATE)) {
					log.warn("Authorization decision is INDETERMINATE! Unable to "
						+ "determine authorization of the user to perform requested "
						+ "operation on the resource");
					return new Boolean(false);
				} else {
					log.warn("Authorization decision has an unknown value '{}'! "
						+ "Unable to determine authorization of the user to perform "
						+ "requested operation on the resource", decision);
					return new Boolean(false);
				}
			}
		}
	}

	private static StFN buildStFN(String filePathDecoded,
		VirtualFSInterface fileVFS) throws WebApplicationException {

		String VFSRootPath;
		String VFSStFNRoot;
		try {
			if (fileVFS != null) {

				VFSRootPath = fileVFS.getRootPath();
				if (VFSRootPath == null) {
					log.error("Unable to build StFN for path '{}'. VFS: {} has null "
						+ "RootPath", filePathDecoded, fileVFS.getAliasName());
					ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
					responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
					responseBuilder
						.entity("Unable to build StFN for path the provided path");
					throw new WebApplicationException(responseBuilder.build());
				}
				if (!VFSRootPath.startsWith("/")) {
					VFSRootPath = "/" + VFSRootPath;
				}
				if (VFSRootPath.endsWith("/")) {
					VFSRootPath = VFSRootPath.substring(0, VFSRootPath.length() - 1);
				}
				log.debug("Chosen VFSRootPath {}", VFSRootPath);
				List<MappingRule> VFSMappingRules = fileVFS.getMappingRules();
				if (VFSMappingRules != null && VFSMappingRules.size() > 0) {
					VFSStFNRoot = VFSMappingRules.get(0).getStFNRoot();
					if (VFSStFNRoot == null) {
						log.error("Unable to build StFN for path '{}'. VFS: {} has null "
							+ "StFNRoot", filePathDecoded, fileVFS.getAliasName());
						ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
						responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
						responseBuilder
							.entity("Unable to build StFN for path the provided path");
						throw new WebApplicationException(responseBuilder.build());
					}
					if (!VFSStFNRoot.startsWith("/")) {
						VFSStFNRoot = "/" + VFSStFNRoot;
					}
					if (VFSStFNRoot.endsWith("/")) {
						VFSStFNRoot = VFSStFNRoot.substring(0, VFSStFNRoot.length() - 1);
					}
					log.debug("Chosen StFNRoot {}", VFSStFNRoot);
				} else {
					log.error("Unable to determine the StFNRoot for file path's VFS. "
						+ "VFSMappingRules is empty!");
					ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
					responseBuilder.status(Response.Status.NOT_FOUND);
					responseBuilder
						.entity("Unable to determine the StFNRoot for file path's VFS");
					throw new WebApplicationException(responseBuilder.build());
				}
			} else {
				log.error("None of the VFS maps the requested file path '{}'. "
					+ "fileVFS is null!", filePathDecoded);
				ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
				responseBuilder.status(Response.Status.NOT_FOUND);
				responseBuilder
					.entity("Unable to determine file path\'s associated virtual file system");
				throw new WebApplicationException(responseBuilder.build());
			}
		} catch (NamespaceException e) {
			log.error("Unable to determine a VFS that maps the requested file "
				+ "path '{}'. NamespaceException: {}", filePathDecoded, e.getMessage());
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.NOT_FOUND);
			responseBuilder
				.entity("Unable to determine file path\'s associated virtual file system");
			throw new WebApplicationException(responseBuilder.build());
		}
		if (!filePathDecoded.startsWith(VFSRootPath)) {
			log.error("The provided file path does not starts with the VFSRoot "
				+ "of its VFS");
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
			responseBuilder
				.entity("The provided file path does not starts with the VFSRoot of its VFS");
			throw new WebApplicationException(responseBuilder.build());
		}
		String fileStFNpath = VFSStFNRoot
			+ filePathDecoded.substring(VFSRootPath.length(),
				filePathDecoded.length());
		try {
			return StFN.make(fileStFNpath);
		} catch (InvalidStFNAttributeException e) {
			log.error("Unable to build StFN for path '{}'. "
				+ "InvalidStFNAttributeException: {}", fileStFNpath, e.getMessage());
			ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
			responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
			responseBuilder
				.entity("Unable to determine file path\'s associated virtual file system");
			throw new WebApplicationException(responseBuilder.build());
		}
	}

	private static GridUserInterface buildGridUser(String DNDecoded,
		String[] FQANSArray) {

		try {
			if (FQANSArray == null || FQANSArray.length == 0) {
				return loadGridUser(DNDecoded);
			} else {
				return loadVomsGridUser(DNDecoded, FQANSArray);
			}

		} catch (IllegalArgumentException e) {
			// never thrown
			log.error("Unable to build the GridUserInterface object for DN '{}' "
				+ "and FQANS '{}'. IllegalArgumentException: {}", DNDecoded, 
				Arrays.toString(FQANSArray), e.getMessage());
			ResponseBuilderImpl builder = new ResponseBuilderImpl();
			builder.status(Response.Status.BAD_REQUEST);
			builder.entity("Unable to build a GridUser for DN \'" + DNDecoded
				+ "\' and FQANS \'" + Arrays.toString(FQANSArray)
				+ "\'. Missing argument(s)");
			throw new WebApplicationException(builder.build());
		}
	}

	/**
	 * @param fQANS
	 * @return
	 */
	private static String[] parseFQANS(String fQANS) {

		if (fQANS == null) {
			return new String[0];
		}
		return fQANS.trim().split(Constants.FQANS_SEPARATOR);
	}

	/**
	 * Creates a GridUserInterface from the provided DN and FQANS
	 * 
	 * @param dn
	 * @param fqansStringVector
	 * @return the VOMS grid user corresponding to the provided parameters. never
	 *         null
	 * @throws IllegalArgumentException
	 */
	private static GridUserInterface loadVomsGridUser(String dn,
		String[] fqansStringVector) throws IllegalArgumentException {

		if (dn == null || fqansStringVector == null
			|| fqansStringVector.length == 0) {
			log.error("Received invalid arguments DN parameter in loadVomsGridUser!");
			throw new IllegalArgumentException("Received null DN parameter");
		}

		FQAN[] fqansVector = new FQAN[fqansStringVector.length];
		for (int i = 0; i < fqansStringVector.length; i++) {
			fqansVector[i] = new FQAN(fqansStringVector[i]);
		}
		GridUserInterface gridUser = null;
		try {
			gridUser = GridUserManager.makeVOMSGridUser(dn, fqansVector);
		} catch (IllegalArgumentException e) {
			log.error("Unexpected error on voms grid user creation. Contact "
				+ "StoRM Support : IllegalArgumentException {}", e.getMessage(), e);
		}
		return gridUser;
	}

	/**
	 * Creates a GridUserInterface from the provided DN
	 * 
	 * @param dn
	 * @return the grid user corresponding to the provided parameter. never null
	 * @throws IllegalArgumentException
	 */
	private static GridUserInterface loadGridUser(String dn)
		throws IllegalArgumentException {

		if (dn == null) {
			log.error("Received null DN parameter in loadVomsGridUser!");
			throw new IllegalArgumentException("Received null DN parameter");
		}
		return GridUserManager.makeGridUser(dn);
	}

}

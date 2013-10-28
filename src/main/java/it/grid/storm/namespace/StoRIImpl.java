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

package it.grid.storm.namespace;

import it.grid.storm.balancer.BalancingStrategy;
import it.grid.storm.balancer.BalancingStrategyException;
import it.grid.storm.balancer.Node;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.StFN;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.Filesystem;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.ReservationException;
import it.grid.storm.filesystem.Space;
import it.grid.storm.filesystem.SpaceSystem;
import it.grid.storm.https.HTTPSPluginException;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.namespace.model.Capability;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.PathCreator;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.namespace.model.TransportProtocol;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.namespace.naming.NamingConst;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.namespace.util.userinfo.LocalGroups;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: INFN-CNAF and ICTP/eGrid project
 * </p>
 * 
 * @author Riccardo Zappi
 * @version 1.0
 */
public class StoRIImpl implements StoRI {

	private Logger log = NamespaceDirector.getLogger();

	private TSURL surl;
	private PFN pfn;
	private Capability.ACLMode aclMode = Capability.ACLMode.UNDEF;
	private TLifeTimeInSeconds lifetime = null;
	private Date startTime = null;
	private LocalFile localFile = null;
	private Space space;

	private VirtualFSInterface vfs;
	private Filesystem fs;
	private SpaceSystem spaceDriver;
	private StoRIType type;
	private Capability capability;

	// Elements of Name of StoRI
	private String stfn;
	private String vfsRoot;
	private String relativeStFN;
	private String relativePath;
	private String fileName;
	private String stfnPath;
	private String stfnRoot;

	private MappingRule winnerRule;

	// Boolean status for full detailed metadata
	private boolean volatileInformationAreSet = false;

	/*****************************************************************************
	 * BUILDING METHODs
	 ****************************************************************************/

	public StoRIImpl(VirtualFSInterface vfs, MappingRule winnerRule,
		String relativeStFN, StoRIType type) {

		if (vfs != null) {
			this.vfs = vfs;
			this.capability = (Capability) vfs.getCapabilities();
		} else {
			log.error("!!! StoRI built without VFS!!?!");
		}
		/**
		 * Retrieve from StFN the various part.
		 */
		if (winnerRule != null) {
			this.stfnRoot = winnerRule.getStFNRoot();
			this.stfn = stfnRoot + NamingConst.SEPARATOR + relativeStFN;
			
			this.vfsRoot = vfs.getRootPath();
			
			this.relativeStFN = relativeStFN;

			this.stfnPath = NamespaceUtil.getStFNPath(stfn);

			this.relativePath = NamespaceUtil.consumeFileName(relativeStFN);

			if (relativePath != null) {
				if (relativePath.startsWith(NamingConst.SEPARATOR)) {
					this.relativePath = relativePath.substring(1);
				}
			} else {
				this.relativePath = "/";
			}

			this.fileName = NamespaceUtil.getFileName(relativeStFN);
			log.debug("StFN Filename : " + fileName + " [StFN = '" + relativeStFN
				+ "']");

			if (type == null) {
				if (relativeStFN.endsWith(NamingConst.SEPARATOR)) {
					this.type = StoRIType.FOLDER;
				} else {
					this.type = StoRIType.UNKNOWN;
				}
			} else {
				this.type = type;
			}

		} else {
			log.warn("StoRI built without MAPPIG RULE!!");
		}
	}

	public StoRIImpl(VirtualFSInterface vfs, String stfnStr,
		TLifeTimeInSeconds lifetime, StoRIType type) {

		this.vfs = vfs;
		this.capability = (Capability) vfs.getCapabilities();
		// Relative path has to be a path in a relative form! (without "/" at
		// begins)
		if (relativePath != null) {
			if (relativePath.startsWith(NamingConst.SEPARATOR)) {
				this.relativePath = relativePath.substring(1);
			}
		} else {
			this.relativePath = "/";
		}

		this.lifetime = lifetime;

		if (type == null) {
			this.type = StoRIType.UNKNOWN;
		} else {
			this.type = type;
		}

		this.stfnRoot = null;

		this.fileName = NamespaceUtil.getFileName(stfnStr);
		log.debug("StFN Filename : " + fileName + " [StFN = '" + stfnStr + "']");

		this.stfnPath = NamespaceUtil.getStFNPath(stfnStr);
		log.debug("StFN StFNPath : " + stfnPath + " [StFN = '" + stfnStr + "']");

	}

	public void allotSpaceByToken(TSpaceToken token) throws ReservationException,
		ExpiredSpaceTokenException {

		// Retrieve SpaceSystem Driver
		if (spaceDriver == null) {
			try {
				this.spaceDriver = vfs.getSpaceSystemDriverInstance();
			} catch (NamespaceException ex) {
				log.error("Error while retrieving Space System Driver for VFS ", ex);
				throw new ReservationException(
					"Error while retrieving Space System Driver for VFS ");
			}
		}

		try {
			vfs.useAllSpaceForFile(token, this);
		} catch (NamespaceException ex1) {
			log.error("Error while using Space with token '" + token + "' for "
				+ this.fileName, ex1);
			throw new ReservationException("Error while using Space with token '"
				+ token + "' for " + this.fileName);
		}

	}

	public void allotSpaceByToken(TSpaceToken token, TSizeInBytes totSize)
		throws ReservationException, ExpiredSpaceTokenException {

		// Retrieve SpaceSystem Driver
		if (spaceDriver == null) {
			try {
				this.spaceDriver = vfs.getSpaceSystemDriverInstance();
			} catch (NamespaceException ex) {
				log.error("Error while retrieving Space System Driver for VFS ", ex);
				throw new ReservationException(
					"Error while retrieving Space System Driver for VFS ");
			}
		}

		try {
			vfs.useSpaceForFile(token, this, totSize);
		} catch (NamespaceException ex1) {
			log.error("Error while using Space with token '" + token + "' for "
				+ this.fileName, ex1);
			throw new ReservationException("Error while using Space with token '"
				+ token + "' for " + this.fileName);
		}

	}

	public void allotSpaceForFile(TSizeInBytes totSize)
		throws ReservationException {

		if (spaceDriver == null) {
			try {
				this.spaceDriver = vfs.getSpaceSystemDriverInstance();
			} catch (NamespaceException ex) {
				log.error("Error while retrieving Space System Driver for VFS ", ex);
				throw new ReservationException(
					"Error while retrieving Space System Driver for VFS ");
			}
		}

		// Make SILHOUETTE for File
		try {
			vfs.makeSilhouetteForFile(this, totSize);
		} catch (NamespaceException ex1) {
			log.error("Error while constructing 'Space Silhouette' for "
				+ this.fileName, ex1);
			throw new ReservationException(
				"Error while constructing 'Space Silhouette' for " + this.fileName);
		}

		log.debug("Space built. Space " + this.getSpace().getSpaceFile().getPath());

		// Make "space" physically in underlying file system
		this.getSpace().allot();

	}

	public String getAbsolutePath() {
		return vfs.getRootPath() + NamingConst.SEPARATOR + relativeStFN;
	}

	/*****************************************************************************
	 * BUSINESS METHODs
	 ****************************************************************************/

	/**
	 * Returns the SURL lifetime. This method queries the DB and retrieves also
	 * the startTime. The DB is queried only on the first invocation of this or
	 * the getFileStartTime() methods, therefore subsequent invocations of these
	 * two methods are computationally lighter.
	 * 
	 * If the file is PERMANENT, or this StoRI refeers to a non-valid file then -1
	 * is returned.
	 * 
	 * @return TLifeTimeInSeconds
	 */
	public TLifeTimeInSeconds getFileLifeTime() {

		if (!(volatileInformationAreSet)) {
			setVolatileInformation();
		}
		return lifetime;
	}

	public String getFilename() {

		return this.fileName;
	}

	/**
	 * Returns the SURL start time (time from which starts the lifetime). This
	 * method queries the DB and retrieves also the lifetime of the SURL. The DB
	 * is queried only on the first invocation of this or the getFileLifeTime()
	 * methods, therefore subsequent invocations of these two methods are
	 * computationally lighter.
	 * 
	 * If the file is permanent or this StoRI refeers to a non-valid file then
	 * NULL is retuned!
	 * 
	 * @return Date
	 */
	public Date getFileStartTime() {

		if (!(volatileInformationAreSet)) {
			setVolatileInformation();
		}
		return startTime;
	}

	public ArrayList<StoRI> getChildren(TDirOption dirOption)
		throws InvalidDescendantsEmptyRequestException,
		InvalidDescendantsAuthRequestException,
		InvalidDescendantsPathRequestException,
		InvalidDescendantsFileRequestException {

		ArrayList<StoRI> stoRIList = new ArrayList<StoRI>();
		File fileHandle = new File(getAbsolutePath());

		if (!fileHandle.isDirectory()) {
			if (fileHandle.isFile()) {
				log.error("SURL represents a File, not a Directory!");
				throw new InvalidDescendantsFileRequestException(fileHandle);
			} else {
				log.warn("SURL does not exists!");
				throw new InvalidDescendantsPathRequestException(fileHandle);
			}
		} else { // SURL point to an existent directory.
							// Create ArrayList containing all Valid fileName path found in
							// PFN of StoRI's SURL
			PathCreator pCreator = new PathCreator(fileHandle,
				dirOption.isAllLevelRecursive(), 1);
			Collection<String> pathList = pCreator.generateChildren();
			if (pathList.size() == 0) {
				log.debug("SURL point to an EMPTY DIRECTORY");
				throw new InvalidDescendantsEmptyRequestException(fileHandle, pathList);
			} else { // Creation of StoRI LIST
				NamespaceInterface namespace = NamespaceDirector.getNamespace();
				for (String childPath : pathList) {
					log.debug("<GetChildren>:Creation of new StoRI with path : "
						+ childPath);
					try {
						stoRIList.add(namespace.resolveStoRIbyAbsolutePath(childPath));
					} catch (NamespaceException ex) {
						log.error("Error occurred while resolving StoRI by absolute path",
							ex);
					}
				}
			}
		}
		return stoRIList;
	}

	public LocalFile getLocalFile() {

		if (localFile == null) {
			try {
				fs = vfs.getFilesystem();
			} catch (NamespaceException ex) {
				log.error("Error while retrieving FS driver ", ex);
			}
			localFile = new LocalFile(getAbsolutePath(), fs);
		}
		return localFile;
	}

	public MappingRule getMappingRule() {

		return this.winnerRule;
	}

	public List<StoRI> getParents() {

		StoRI createdStoRI = null;
		ArrayList<StoRI> parentList = new ArrayList<StoRI>();
		String consumeElements = this.relativePath;
		String consumed;
		boolean lastElements = false;

		do {
			createdStoRI = new StoRIImpl(this.vfs, this.winnerRule, consumeElements,
				StoRIType.FOLDER);
			parentList.add(createdStoRI);
			consumed = NamespaceUtil.consumeElement(consumeElements);
			if (consumed.equals(consumeElements)) {
				lastElements = true;
			} else {
				consumeElements = consumed;
			}
		} while ((!lastElements));

		return parentList;
	}

	public PFN getPFN() {

		if (pfn == null) {
			try {
				this.pfn = PFN.make(getAbsolutePath());
			} catch (InvalidPFNAttributeException ex) {
				log.error("Unable to build the PFN in the VFS '" + getVFSName()
					+ "' with this path :'" + getAbsolutePath() + "'");
			}
		}
		return this.pfn;
	}

	public String getRelativePath() {

		return this.relativePath;
	}

	public String getRelativeStFN() {

		return this.relativeStFN;
	}

	public Space getSpace() {

		if (space == null) {
			log.error("No space bound with this StoRI!");
			return null;
		}
		return this.space;
	}

	public StFN getStFN() {

		StFN stfn = null;
		if (this.surl == null) {
			getSURL();
		}
		stfn = surl.sfn().stfn();
		return stfn;
	}

	public String getStFNPath() {

		return this.stfnPath;
	}

	public String getStFNRoot() {

		return this.stfnRoot;
	}

	public StoRIType getStoRIType() {

		return this.type;
	}

	public TSURL getSURL() {

		/**
		 * The String passed to TSURL.makeFromString MUST contains a valid TSURL in
		 * string format, not only relativePath.
		 */
		if (this.surl == null) {
			try {
				this.surl = TSURL.makeFromStringValidate(buildSURLString());
			} catch (InvalidTSURLAttributesException ex) {
				log.error("Unable to build the SURL with relative path : '"
					+ relativePath + "'", ex);
			} catch (NamespaceException ex) {
				/** @todo Handle this exception */
				log.error("Unable to build the SURL with relative path : '"
					+ relativePath + "'", ex);
			}

		}
		return surl;
	}

	/*****************************************************************************
	 * READ METHODs
	 * 
	 * @throws Exception
	 ***************************************************************************/

	public TTURL getTURL(TURLPrefix desiredProtocols)
		throws IllegalArgumentException, InvalidGetTURLProtocolException,
		TURLBuildingException {

		// TransportProtocol protocolPrefix = null;
		TTURL resultTURL = null;

		if (desiredProtocols == null || desiredProtocols.size() == 0) {
			log
				.error("<GetTURL> request with NULL or empty prefixOfAcceptedTransferProtocol!");
			throw new IllegalArgumentException(
				"unable to build the TTURL, invalid arguments: desiredProtocols="
					+ desiredProtocols);
		} else {

			/**
			 * Retrieve Protocol to build the TURL
			 */
			// Within the request there are some protocol preferences
			// Calculate the intersection between Desired Protocols and Available
			// Protocols
			ArrayList<Protocol> desiredP = new ArrayList<Protocol>(
				desiredProtocols.getDesiredProtocols());
			ArrayList<Protocol> availableP = new ArrayList<Protocol>(
				this.capability.getAllManagedProtocols());
			desiredP.retainAll(availableP);
			if (desiredP.isEmpty()) {
				// No match found!
				log
					.error("stori:No match with Protocol Preferences and Protocol Managed!");
				throw new InvalidGetTURLProtocolException(desiredProtocols);
			} else {
				log.debug("Protocol matching.. Intersection size:" + desiredP.size());

				Protocol choosen = null;
				Authority authority = null;
				int index = 0;
				boolean turlBuilt = false;
				while (!turlBuilt && index < desiredP.size()) {
					choosen = desiredP.get(index);
					authority = null;
					log.debug("Selected Protocol :" + choosen);
					if (capability.isPooledProtocol(choosen)) {
						log.debug("The protocol selected is in POOL Configuration");
						try {
							authority = getPooledAuthority(choosen);
						} catch (BalancingStrategyException e) {
							log
								.warn("Unable to get the pool member to be used to build the turl. BalancerException : "
									+ e.getMessage());
							index++;
							continue;
						}
					} else {
						log.debug("The protocol selected is in NON-POOL Configuration");
						TransportProtocol transProt = null;
						List<TransportProtocol> protList = capability
							.getManagedProtocolByScheme(choosen);
						if (protList.size() > 1) { // Strange case
							log
								.warn("More than one protocol "
									+ choosen
									+ " defined but NOT in POOL Configuration. Taking the first one.");
						}
						transProt = protList.get(0);
						authority = transProt.getAuthority();
					}
					// TODO HTTPS TURL
					resultTURL = buildTURL(choosen, authority);
					turlBuilt = true;
				}
				if (!turlBuilt) {
					throw new TURLBuildingException(
						"Unable to build the turl given protocols " + desiredP.toString());
				}
			}
		}
		return resultTURL;
	}

	public VirtualFSInterface getVirtualFileSystem() {

		return this.vfs;
	}

	public boolean hasJustInTimeACLs() {

		boolean result = true;

		if (aclMode.equals(Capability.ACLMode.UNDEF)) {
			this.aclMode = vfs.getCapabilities().getACLMode();
		}
		if (aclMode.equals(Capability.ACLMode.JUST_IN_TIME)) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	public void setGroupTapeRead() {

		String groupName = Configuration.getInstance().getGroupTapeReadBuffer();
		boolean isGroupDefined = LocalGroups.getInstance()
			.isGroupDefined(groupName);
		if (isGroupDefined) {
			LocalFile localFile = getLocalFile();
			try {
				localFile.setGroupOwnership(groupName);
			} catch (FSException e) {
				log.warn("Unable to change in the new group owner ('" + groupName
					+ "') of the file: " + localFile.getAbsolutePath());
			}
		} else {
			log.warn("The group for Read buffer in Tape support '" + groupName
				+ "' is not defined.");
		}

	}

	public void setGroupTapeWrite() {

		String groupName = Configuration.getInstance().getGroupTapeWriteBuffer();
		boolean isGroupDefined = LocalGroups.getInstance()
			.isGroupDefined(groupName);
		if (isGroupDefined) {
			LocalFile localFile = getLocalFile();
			try {
				localFile.setGroupOwnership(groupName);
			} catch (FSException e) {
				log.warn("Unable to change in the new group owner ('" + groupName
					+ "') of the file: " + localFile.getAbsolutePath());
			}
		} else {
			log.warn("The group for Write buffer in Tape support '" + groupName
				+ "' is not defined.");
		}
	}

	public void setMappingRule(MappingRule winnerRule) {

		this.winnerRule = winnerRule;
	}

	public void setSpace(Space space) {

		this.space = space;
	}

	public void setStFNRoot(String stfnRoot) {

		this.stfnRoot = stfnRoot;
	}

	public void setStoRIType(StoRIType type) {

		this.type = type;
	}

	/***********************************************
	 * UTILITY METHODS
	 **********************************************/

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		sb.append(" stori.stfn           : " + this.getStFN().toString() + "\n");
		sb.append(" stori.vfs-root       :" + this.vfsRoot + "\n");
		sb.append(" stori.absolutePath   : " + this.getAbsolutePath() + "\n");
		sb.append(" stori.vfs NAME       : " + this.getVFSName() + "\n");
		sb.append(" stori.stfn FileName  : " + this.fileName + "\n");
		sb.append(" stori.stfn StFN path : " + this.stfnPath + "\n");
		sb.append(" stori.stfn rel. Path : " + this.relativePath + "\n");
		sb.append(" stori.relative StFN  : " + this.relativeStFN + "\n");
		sb.append(" stori.stfn-root      : " + this.stfnRoot + "\n");
		sb.append(" story.type           : " + this.type + "\n");
		sb.append(" stori.SURL           : " + this.getSURL() + "\n");
		sb.append(" stori.localFile      : " + this.getLocalFile() + "\n");

		return sb.toString();
	}

	private String buildSURLString() throws NamespaceException {

		String stfn = stfnRoot + NamingConst.SEPARATOR + relativeStFN;
		SURL surl = new SURL(stfn);
		return surl.toString();
	}

	// TODO MICHELE HTTPS here we can add a case to build https TURL...what really
	// matter is that probably because it is an URL we cannot build it
	// just using the infomation available actually from the parameters
	// I can make an hypothesis on web server url construction, maybe it is
	// https://server_name.server_domain:web_server_https_port/file_server_service_identifier/file_relative_url
	// in such a case we need: web_server_https_port -> can be retrieved from
	// Protocol object : we can associate a default port to protocol the effective
	// value
	// has to be demanded to the connector
	// file_server_service_identifier -> also from Protocol object ... not so
	// true... hummm : it has to be demanded to the connector
	// file_relative_url -> here start real problems... : we have to hope that
	// from the physicalFN we are able to build this value
	// - it has to be demanded to the connector
	// TODO HTTPS TURL
	// private TTURL buildTURL(Protocol protocol, Authority authority, PFN
	// physicalFN) throws InvalidProtocolForTURLException {
	private TTURL buildTURL(Protocol protocol, Authority authority)
		throws InvalidProtocolForTURLException {

		TTURL result = null;
		// TODO MICHELE HTTPS NOTE: this is the only access point to TURLBuilder
		// class (good sign)
		switch (protocol.getProtocolIndex()) {
		case 0: // EMPTY Protocol
			throw new InvalidProtocolForTURLException(protocol.getSchema());
		case 1:
			result = TURLBuilder.buildFileTURL(authority, this.getPFN());
			break; // FILE Protocol
		case 2:
			result = TURLBuilder.buildGsiftpTURL(authority, this.getPFN());
			break; // GSIFTP Protocol
		case 3:
			result = TURLBuilder.buildRFIOTURL(authority, this.getPFN());
			break; // RFIO Protocol
		case 4: // SRM Protocol
			throw new InvalidProtocolForTURLException(protocol.getSchema());
		case 5:
			result = TURLBuilder.buildROOTTURL(authority, this.getPFN());
			break; // ROOT Protocol
		// TODO HTTPS TURL
		case 6:
			try {
				result = TURLBuilder.buildHTTPTURL(authority, this.getLocalFile());
			} catch (HTTPSPluginException e) {
				log.error("Unable to build the TURL for protocol "
					+ protocol.toString() + " for authority " + authority.toString()
					+ " and file " + this.getLocalFile().toString()
					+ " . HTTPSPluginException: " + e.getMessage());
				throw new InvalidProtocolForTURLException(e, protocol.getSchema());
			}
			break; // HTTP Protocol
		case 7:
			try {
				result = TURLBuilder.buildHTTPSTURL(authority, this.getLocalFile());
			} catch (HTTPSPluginException e) {
				log.error("Unable to build the TURL for protocol "
					+ protocol.toString() + " for authority " + authority.toString()
					+ " and file " + this.getLocalFile().toString()
					+ " . HTTPSPluginException: " + e.getMessage());
				throw new InvalidProtocolForTURLException(e, protocol.getSchema());
			}
			break; // HTTPS Protocol

		default:
			throw new InvalidProtocolForTURLException(protocol.getSchema()); // UNKNOWN
																																				// Protocol
		}
		return result;
	}

	/**
	 * @param pooledProtocol
	 * @return
	 * @throws BalancerException
	 */
	private Authority getPooledAuthority(Protocol pooledProtocol)
		throws BalancingStrategyException {

		Authority authority = null;
		if (pooledProtocol.equals(Protocol.GSIFTP)
			|| pooledProtocol.equals(Protocol.HTTP)
			|| pooledProtocol.equals(Protocol.HTTPS)) {
			BalancingStrategy<? extends Node> bal = vfs
				.getProtocolBalancingStrategy(pooledProtocol);
			if (bal != null) {
				Node node = bal.getNextElement();
				authority = new Authority(node.getHostName(), node.getPort());
			}
		} else {
			log.error("Unable to manage pool with protocol different from GSIFTP.");
		}
		return authority;
	}

	private String getVFSName() {

		String result = "UNDEF";
		if (vfs != null) {
			result = vfs.getAliasName();
		}
		return result;
	}

	/**
	 * Set "lifetime" and "startTime" information. The corresponding values are
	 * retrieved from the DB.
	 */
	private void setVolatileInformation() {

		VolatileAndJiTCatalog catalog = VolatileAndJiTCatalog.getInstance();
		List<?> volatileInfo = catalog.volatileInfoOn(getPFN());
		if (volatileInfo.size() != 2) {
			lifetime = TLifeTimeInSeconds.makeInfinite();
			startTime = null;
			return;
		}
		startTime = new Date(((Calendar) volatileInfo.get(0)).getTimeInMillis());
		lifetime = (TLifeTimeInSeconds) volatileInfo.get(1);
		volatileInformationAreSet = true;
	}

}

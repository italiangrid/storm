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

package it.grid.storm.namespace.config.xml;

import it.grid.storm.balancer.BalancingStrategyType;
import it.grid.storm.check.sanity.filesystem.SupportedFSType;
import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.DefaultValuesInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.PropertyInterface;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.config.NamespaceCheck;
import it.grid.storm.namespace.config.NamespaceLoader;
import it.grid.storm.namespace.config.NamespaceParser;
import it.grid.storm.namespace.model.ACLEntry;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.namespace.model.Capability;
import it.grid.storm.namespace.model.DefaultValues;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.PermissionException;
import it.grid.storm.namespace.model.PoolMember;
import it.grid.storm.namespace.model.Property;
import it.grid.storm.namespace.model.Property.SizeUnitType;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.model.ProtocolPool;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.QuotaType;
import it.grid.storm.namespace.model.SAAuthzType;
import it.grid.storm.namespace.model.StorageClassType;
import it.grid.storm.namespace.model.SubjectRules;
import it.grid.storm.namespace.model.TransportProtocol;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.space.gpfsquota.GPFSFilesetQuotaInfo;
import it.grid.storm.space.gpfsquota.GPFSQuotaInfo;
import it.grid.storm.space.gpfsquota.GetGPFSFilesetQuotaInfoCommand;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.util.GPFSSizeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: INFN-CNAF and ICTP/eGrid project
 * </p>
 * 
 * @author Riccardo Zappi
 * @version 1.0
 */

public class XMLNamespaceParser implements NamespaceParser, Observer {

	private final Logger log = NamespaceDirector.getLogger();

	private String version;
	private Hashtable<String, VirtualFSInterface> vfss;
	private Hashtable<String, MappingRule> maprules;
	private Hashtable<String, ApproachableRule> apprules;
	// private ReservedSpaceCatalog spaceCatalog;

	private XMLParserUtil parserUtil;
	private final XMLConfiguration configuration;
	private XMLNamespaceLoader xmlLoader;

	private final Lock refreshing = new ReentrantLock();

	// For debug purpose only
	private final boolean internalLog;
	private final boolean testingMode;

	/**
	 * Constructor
	 * 
	 * @param loader
	 *          NamespaceLoader
	 */
	public XMLNamespaceParser(NamespaceLoader loader, boolean verboseLogging,
		boolean testingMode) {

		configuration = (XMLConfiguration) loader.getConfiguration();
		if (loader instanceof XMLNamespaceLoader) {
			xmlLoader = (XMLNamespaceLoader) loader;
			xmlLoader.setObserver(this);
		} else {
			log.error("XMLParser initialized with a non-XML Loader");
		}
		// this.internalLog = verboseLogging;
		// TOREMOVE
		internalLog = true;

		this.testingMode = testingMode;

		parserUtil = new XMLParserUtil(configuration);

		for (Iterator iter = parserUtil.getKeys(); iter.hasNext();) {
			Object item = iter.next();
			verboseLog(item.toString());
		}

		vfss = new Hashtable<String, VirtualFSInterface>();
		maprules = new Hashtable<String, MappingRule>();
		apprules = new Hashtable<String, ApproachableRule>();

		boolean validNamespaceConfiguration = refreshCachedData();
		if (!(validNamespaceConfiguration)) {
			log.error(" ???????????????????????????????????? ");
			log.error(" ????  NAMESPACE does not VALID  ???? ");
			log.error(" ???????????????????????????????????? ");
			log.error(" Please see the log. ");
			System.exit(0);
		}

	}

	public Map<String, VirtualFSInterface> getVFSs() {

		return vfss;
	}

	public Map<String, ApproachableRule> getApproachableRules() {

		return apprules;
	}

	public Map<String, MappingRule> getMappingRules() {

		return maprules;
	}

	public long getLastUpdateTime() {

		return 0L;
	}

	public void update(Observable observed, Object arg) {

		log.debug(arg + " Refreshing Namespace Memory Cache .. ");

		XMLNamespaceLoader loader = (XMLNamespaceLoader) observed;
		parserUtil = new XMLParserUtil(loader.getConfiguration());

		if (loader.schemaValidity) {
			refreshCachedData();
		}

		loader.setNotifyManaged();

		log.debug(" ... Cache Refreshing ended");
	}

	/****************************************************************
	 * PRIVATE METHODs
	 *****************************************************************/

	private boolean refreshCachedData() {

		boolean result = false;
		try {
			refreshing.lock();
			configuration.clear();
			configuration.clearTree("filesystems");
			configuration.clearTree("mapping-rules");
			configuration.clearTree("approachable-rules");
			try {
				configuration.load();
				log
					.debug(" ... reading and parsing the namespace configuration from file!");
			} catch (ConfigurationException ex) {
				ex.printStackTrace();
			}
			log.debug("REFRESHING CACHE..");
			// Save the cache content
			log.debug("  ..save the cache content before semantic check");
			Hashtable<String, VirtualFSInterface> vfssSAVED = vfss;
			Hashtable<String, MappingRule> maprulesSAVED = maprules;
			Hashtable<String, ApproachableRule> apprulesSAVED = apprules;
			// Refresh the cache content with new values

			log.debug("  ..refresh the cache");
			refreshCache();

			// Do the checking on Namespace
			log.debug("  ..semantic check of namespace");
			NamespaceCheck checker = new NamespaceCheck(vfss, maprules, apprules);
			boolean semanticCheck = checker.check();

			// If there is an error restore old cache content
			log.debug("REFRESHING ENDED.");
			if (semanticCheck) {
				log.debug("Namespace is semantically valid");
				result = true;
			} else {
				log
					.warn("Namespace does not semantically valid!, so no load performed!");
				vfss = vfssSAVED;
				maprules = maprulesSAVED;
				apprules = apprulesSAVED;
				result = false;
			}
		} finally {
			refreshing.unlock();
		}
		return result;
	}

	private void verboseLog(String msg) {

		if (internalLog) {
			log.debug(msg);
		}
	}

	private void refreshCache() {

		log
			.info("  ##############  REFRESHING NAMESPACE CONFIGURATION CACHE : start  ###############");

		/**************************
		 * Retrieve Version Number
		 *************************/
		try {
			retrieveVersion();
		} catch (NamespaceException ex1) {
			log
				.warn(
					"Namespace configuration does not contain a valid version number.",
					ex1);
			/**
			 * @todo Manage this exceptional status!
			 */
		}

		/**************************
		 * Building VIRTUAL FS
		 *************************/
		try {
			buildVFSs();
		} catch (ClassNotFoundException ex) {
			log
				.error("Namespace Configuration ERROR in VFS-DRIVER specification", ex);
			/**
			 * @todo Manage this exceptional status!
			 */
		} catch (NamespaceException ex) {
			log
				.error(
					"Namespace Configuration ERROR in VFS definition, please check it.",
					ex);
			/**
			 * @todo Manage this exceptional status!
			 */
		}

		/**************************
		 * Building MAPPING RULES
		 *************************/
		try {
			buildMapRules();
		} catch (NamespaceException ex1) {
			log
				.error(
					"Namespace Configuration ERROR in MAPPING RULES definition, please check it.",
					ex1);
			/**
			 * @todo Manage this exceptional status!
			 */
		}

		/**************************
		 * Building APPROACHABLE RULES
		 *************************/
		try {
			buildAppRules();
		} catch (NamespaceException ex2) {
			log
				.error(
					"Namespace Configuration ERROR in APPROACHABLE RULES definition, please check it.",
					ex2);
			/**
			 * @todo Manage this exceptional status!
			 */
		}
		log
			.info("  ##############  REFRESHING NAMESPACE CONFIGURATION CACHE : end ###############");

		handleTotalOnlineSizeFromGPFSQuota();
		// Update SA within Reserved Space Catalog
		updateSA();
	}

	private void handleTotalOnlineSizeFromGPFSQuota() {

		for (Entry<String, VirtualFSInterface> entry : vfss.entrySet()) {
			String storageAreaName = entry.getKey();
			VirtualFSInterface storageArea = entry.getValue();
			if (SupportedFSType.parseFS(storageArea.getFSType()) == SupportedFSType.GPFS) {
				Quota quota = storageArea.getCapabilities().getQuota();
				if (quota != null && quota.getEnabled()) {

					GPFSFilesetQuotaInfo quotaInfo = getGPFSQuotaInfo(storageArea);
					if (quotaInfo != null) {
						updateTotalOnlineSizeFromGPFSQuota(storageAreaName, storageArea,
							quotaInfo);
					}
				}
			}
		}
	}

	private GPFSFilesetQuotaInfo getGPFSQuotaInfo(VirtualFSInterface storageArea) {

		GetGPFSFilesetQuotaInfoCommand cmd = new GetGPFSFilesetQuotaInfoCommand(
			storageArea);

		try {
			return cmd.call();
		} catch (Throwable t) {
			log
				.warn(
					"Cannot get quota information out of GPFS. Using the TotalOnlineSize in namespace.xml "
						+ "for Storage Area {}. Reason: {}", storageArea.getAliasName(),
					t.getMessage());
			return null;
		}
	}

	private void updateTotalOnlineSizeFromGPFSQuota(String storageAreaName,
		VirtualFSInterface storageArea, GPFSFilesetQuotaInfo quotaInfo) {

		long gpfsTotalOnlineSize = GPFSSizeHelper.getBytesFromKIB(quotaInfo
			.getBlockSoftLimit());
		Property newProperties = Property.from(storageArea.getProperties());
		try {
			newProperties.setTotalOnlineSize(SizeUnitType.BYTE.getTypeName(),
				gpfsTotalOnlineSize);
			storageArea.setProperties(newProperties);
			log.warn("TotalOnlineSize as specified in namespace.xml will be ignored "
				+ "since quota is enabled on the GPFS {} Storage Area.",
				storageAreaName);
		} catch (NamespaceException e) {
			log
				.warn(
					"Cannot get quota information out of GPFS. Using the TotalOnlineSize in namespace.xml "
						+ "for Storage Area {}.", storageAreaName, e);
		}
	}

	// ******************* Update SA Catalog ***************************
	private void updateSA() {

		TSpaceToken spaceToken = null;
		// ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();
		SpaceHelper spaceHelp = new SpaceHelper();
		log
			.debug("Updating Space Catalog with Storage Area defined within NAMESPACE");
		VirtualFS vfs = null;
		Enumeration<VirtualFSInterface> scan = vfss.elements();
		while (scan.hasMoreElements()) {

			vfs = (VirtualFS) scan.nextElement();
			String vfsAliasName = vfs.getAliasName();
			verboseLog(" Considering VFS : " + vfsAliasName);
			String aliasName = vfs.getSpaceTokenDescription();
			if (aliasName == null) {
				// Found a VFS without the optional element Space Token Description
				log.debug("XMLNamespaceParser.UpdateSA() : Found a VFS ('"
					+ vfsAliasName
					+ "') without space-token-description. Skipping the Update of SA");
			} else {
				TSizeInBytes onlineSize = vfs.getProperties().getTotalOnlineSize();
				String spaceFileName = vfs.getRootPath();
				spaceToken = spaceHelp.createVOSA_Token(aliasName, onlineSize,
					spaceFileName);
				vfs.setSpaceToken(spaceToken);

				verboseLog(" Updating SA ('" + aliasName + "'), token:'" + spaceToken
					+ "', onlineSize:'" + onlineSize + "', spaceFileName:'"
					+ spaceFileName);
			}

		}
		spaceHelp.purgeOldVOSA_token();
		log.debug("Updating Space Catalog... DONE!!");

	}

	// ******************* VERSION NUMBER ***************************
	private void retrieveVersion() throws NamespaceException {

		version = parserUtil.getNamespaceVersion();
		verboseLog(" ====  NAMESPACE VERSION : '" + version + "'  ====");
	}

	// ******************* VIRTUAL FS ***************************

	private void buildVFSs() throws ClassNotFoundException, NamespaceException {

		int nrOfVFS = 0;

		nrOfVFS = parserUtil.getNumberOfFS();
		// For each VFS within configuration build VFS class istance
		VirtualFS vfs;
		String spaceTokenDescription = null;
		StorageClassType storageClass = StorageClassType.UNKNOWN;
		String root = null;
		String name;
		String fsType;
		Class driver;
		String storageAreaAuthz;
		PropertyInterface prop;
		CapabilityInterface cap;
		DefaultValuesInterface defValues;
		SAAuthzType saAuthzType = SAAuthzType.UNKNOWN;

		for (int i = 0; i < nrOfVFS; i++) {
			// Building VFS
			vfs = new VirtualFS(testingMode);

			name = parserUtil.getFSName(i);
			vfs.setAliasName(name);
			verboseLog("VFS(" + i + ").name = '" + name + "'");

			fsType = parserUtil.getFSType(name);
			vfs.setFSType(fsType);
			verboseLog("VFS(" + name + ").fs_type = '" + fsType + "'");

			spaceTokenDescription = parserUtil.getFSSpaceTokenDescription(name);
			vfs.setSpaceTokenDescription(spaceTokenDescription);
			verboseLog("VFS(" + name + ").space-token-description = '"
				+ spaceTokenDescription + "'");

			storageClass = StorageClassType.getStorageClassType(parserUtil
				.getStorageClass(name));
			vfs.setStorageClassType(storageClass);
			verboseLog("VFS(" + name + ").storage-class = '" + storageClass + "'");

			root = parserUtil.getFSRoot(name);
			vfs.setRoot(root);
			verboseLog("VFS(" + name + ").root = '" + root + "'");
			// log.debug("VFS(" + i + ").root = '" + root + "'");

			// verboseLog("VFS fs driver name:" + name + ", util: " +
			// parserUtil.getFSDriver(name) + "!");
			driver = Class.forName(parserUtil.getFSDriver(name));
			// verboseLog("FS-Driver createed!");
			vfs.setFSDriver(driver);
			verboseLog("VFS(" + name + ").fsDriver [CLASS Name] = '"
				+ driver.getName() + "'");

			driver = Class.forName(parserUtil.getSpaceDriver(name));
			vfs.setSpaceSystemDriver(driver);
			verboseLog("VFS(" + name + ").spaceDriver [CLASS Name] = '"
				+ driver.getName() + "'");

			saAuthzType = parserUtil.getStorageAreaAuthzType(name);
			vfs.setSAAuthzType(saAuthzType);
			verboseLog("VFS(" + name + ").storage-area-authz.TYPE = '" + saAuthzType
				+ "'");

			storageAreaAuthz = parserUtil.getStorageAreaAuthz(name, saAuthzType);
			vfs.setSAAuthzSource(storageAreaAuthz);
			verboseLog("VFS(" + name + ").storage-area-authz = '" + storageAreaAuthz
				+ "'");

			prop = buildProperties(name);
			vfs.setProperties(prop);

			cap = buildCapabilities(name);
			vfs.setCapabilities(cap);

			defValues = buildDefaultValues(name);
			vfs.setDefaultValues(defValues);

			// Adding VFS
			synchronized (this) {
				vfss.remove(name);
				vfss.put(name, vfs);
			}
		}
	}

	// ******************* PROPERTY ***************************
	private PropertyInterface buildProperties(String fsName)
		throws NamespaceException {

		Property prop = new Property();

		String accessLatency = parserUtil.getAccessLatencyType(fsName);
		prop.setAccessLatency(accessLatency);
		verboseLog("VFS(" + fsName + ").Properties.AccessLatency = '"
			+ accessLatency + "'");

		String expirationMode = parserUtil.getExpirationModeType(fsName);
		prop.setExpirationMode(expirationMode);
		verboseLog("VFS(" + fsName + ").Properties.ExpirationMode = '"
			+ expirationMode + "'");

		String retentionPolicy = parserUtil.getRetentionPolicyType(fsName);
		prop.setRetentionPolicy(retentionPolicy);
		verboseLog("VFS(" + fsName + ").Properties.RetentionPolicy = '"
			+ retentionPolicy + "'");

		String unitType = parserUtil.getNearlineSpaceUnitType(fsName);
		long nearLineSize = parserUtil.getNearlineSpaceSize(fsName);
		prop.setTotalNearlineSize(unitType, nearLineSize);
		verboseLog("VFS(" + fsName + ").Properties.NearlineSpaceSize = '"
			+ nearLineSize + " " + unitType + "'");

		unitType = parserUtil.getOnlineSpaceUnitType(fsName);
		long onlineSize = parserUtil.getOnlineSpaceSize(fsName);
		prop.setTotalOnlineSize(unitType, onlineSize);
		verboseLog("VFS(" + fsName + ").Properties.OnlineSpaceSize = '"
			+ onlineSize + " " + unitType + "'");

		boolean hasLimitedSize = parserUtil.getOnlineSpaceLimitedSize(fsName);
		prop.setLimitedSize(hasLimitedSize);
		verboseLog("VFS(" + fsName + ").Properties.OnlineSpaceLimitedSize = '"
			+ hasLimitedSize + "'");

		return prop;
	}

	// ******************* CAPABILITY ***************************

	private CapabilityInterface buildCapabilities(String fsName)
		throws NamespaceException {

		/**
		 * String[] fileType = parserUtil.getFileType(fsName); for (int j = 0; j <
		 * fileType.length; j++) { verboseLog("VFS(" + fsName +
		 * ").Capabilities.file.types(" + j + ") = '" + fileType[j] + "'"); }
		 * String[] spaceType = parserUtil.getSpaceType(fsName); for (int j = 0; j <
		 * spaceType.length; j++) { verboseLog("VFS(" + fsName +
		 * ").Capabilities.space.types(" + j + ") = '" + spaceType[j] + "'"); }
		 **/

		/**
		 * ACL MODE ELEMENT
		 */
		String aclMode = parserUtil.getACLMode(fsName);
		Capability cap = new Capability(aclMode);
		verboseLog("VFS(" + fsName + ").Capabilities.aclMode = '" + aclMode + "'");

		/**
		 * DEFAULT ACL
		 */
		boolean defaultACLDefined = parserUtil.getDefaultACLDefined(fsName);
		verboseLog("VFS(" + fsName + ").Capabilities.defaultACL [Defined?] ="
			+ defaultACLDefined);
		if (defaultACLDefined) {
			int nrACLEntries = parserUtil.getNumberOfACL(fsName);
			String groupName = null;
			String filePermString = null;
			ACLEntry aclEntry = null;
			for (int entryNumber = 0; entryNumber < nrACLEntries; entryNumber++) {
				groupName = parserUtil.getGroupName(fsName, entryNumber);
				filePermString = parserUtil.getPermissionString(fsName, entryNumber);
				try {
					aclEntry = new ACLEntry(groupName, filePermString);
					cap.addACLEntry(aclEntry);
				} catch (PermissionException permEx) {
					log
						.error("Namespace XML Parser -- ERROR -- : " + permEx.getMessage());
				}
			}
			verboseLog("VFS(" + fsName + ").Capabilities.defaultACL = "
				+ cap.getDefaultACL());
		}

		/**
		 * QUOTA ELEMENT
		 */
		boolean quotaDefined = parserUtil.getQuotaDefined(fsName);
		Quota quota = null;
		if (quotaDefined) {
			boolean quotaEnabled = parserUtil.getQuotaEnabled(fsName);
			String device = parserUtil.getQuotaDevice(fsName);

			QuotaType quotaType;
			String quotaValue = null;

			if (parserUtil.getQuotaFilesetDefined(fsName)) {
				quotaType = QuotaType.buildQuotaType(QuotaType.FILESET);
				quotaValue = parserUtil.getQuotaFileset(fsName);
			} else {
				if (parserUtil.getQuotaGroupIDDefined(fsName)) {
					quotaType = QuotaType.buildQuotaType(QuotaType.GRP);
					quotaValue = parserUtil.getQuotaGroupID(fsName);
				} else {
					if (parserUtil.getQuotaUserIDDefined(fsName)) {
						quotaType = QuotaType.buildQuotaType(QuotaType.USR);
						quotaValue = parserUtil.getQuotaUserID(fsName);
					} else {
						quotaType = QuotaType.buildQuotaType(QuotaType.UNKNOWN);
						quotaValue = "unknown";
					}
				}
			}

			quotaType.setValue(quotaValue);
			quota = new Quota(quotaEnabled, device, quotaType);

		} else {
			quota = new Quota();
		}
		cap.setQuota(quota);

		verboseLog("VFS(" + fsName + ").Capabilities.quota = '" + quota + "'");
		log.debug("VFS(" + fsName + ").Capabilities.quota = '" + quota + "'");

		/**
		 * TRANSFER PROTOCOL
		 */
		int nrProtocols = parserUtil.getNumberOfProt(fsName);
		Protocol protocol;
		Authority service;
		TransportProtocol transportProt;
		int protocolIndex;
		String serviceHostName;
		String servicePortValue;
		String schema;
		String name;
		for (int protCounter = 0; protCounter < nrProtocols; protCounter++) {
			protocolIndex = parserUtil.getProtId(fsName, protCounter); // 1.4.0
																																	// (Return -1
																																	// if ID is
																																	// not
																																	// present)
			name = parserUtil.getProtName(fsName, protCounter);
			schema = parserUtil.getProtSchema(fsName, protCounter);
			protocol = Protocol.getProtocol(schema);
			protocol.setProtocolServiceName(name);
			serviceHostName = parserUtil.getProtHost(fsName, protCounter);
			servicePortValue = parserUtil.getProtPort(fsName, protCounter);
			int portIntValue = -1;
			service = null;
			if (servicePortValue != null) {
				try {
					portIntValue = Integer.parseInt(servicePortValue);
					service = new Authority(serviceHostName, portIntValue);
					// log.debug("SERVICE PORT: "+service);
				} catch (NumberFormatException nfe) {
					log
						.warn("to evaluate the environmental variable " + servicePortValue);
				}
			} else {
				service = new Authority(serviceHostName);
				// log.debug("SERVICE : "+service);
			}
			transportProt = new TransportProtocol(protocol, service);
			transportProt.setProtocolID(protocolIndex); // 1.4.0
			verboseLog("VFS(" + fsName + ").Capabilities.protocol(" + protCounter
				+ ") = '" + transportProt + "'");
			cap.addTransportProtocolByScheme(protocol, transportProt);
			cap.addTransportProtocol(transportProt);
			if (protocolIndex != -1) {
				cap.addTransportProtocolByID(protocolIndex, transportProt);
			}

		}

		/**
		 * PROTOCOL POOL
		 */
		int nrPools = parserUtil.getNumberOfPool(fsName);
		if (nrPools > 0) {

			for (int poolCounter = 0; poolCounter < nrPools; poolCounter++) {
				BalancingStrategyType balanceStrategy = BalancingStrategyType
					.getByValue(parserUtil.getBalancerStrategy(fsName, poolCounter)); // 1.4.0
																																						// (Return
																																						// -1
																																						// if
																																						// ID
																																						// is
																																						// not
																																						// present)
				ArrayList<PoolMember> poolMembers = new ArrayList<PoolMember>();
				int nrMembers = parserUtil.getNumberOfPoolMembers(fsName, poolCounter);
				for (int i = 0; i < nrMembers; i++) {
					int protIndex = parserUtil.getMemberID(fsName, poolCounter, i);
					TransportProtocol tProtMember = cap.getProtocolByID(protIndex); // search
																																					// for
																																					// the
																																					// member
																																					// with
																																					// specified
																																					// ID
					if (tProtMember != null) { // member found!
						PoolMember poolMember;
						if (balanceStrategy.requireWeight()) { // Check for the weight
							int memberWeight = parserUtil.getMemberWeight(fsName,
								poolCounter, i);
							poolMember = new PoolMember(protIndex, tProtMember, memberWeight);
						} else {
							poolMember = new PoolMember(protIndex, tProtMember);
						}
						poolMembers.add(poolMember);
					} else { // member pointed out doesn't exist!!
						log.error("POOL Building: Protocol with index " + protIndex
							+ " does not exists in the VFS :" + fsName);
						throw new NamespaceException("POOL Building: Protocol with index "
							+ protIndex + " does not exists in the VFS :" + fsName);
					}
				}
				Protocol pooProtocol = poolMembers.get(0).getMemberProtocol()
					.getProtocol();
				verifyPoolIsValid(poolMembers);
				log.debug("Defined pool for protocol " + pooProtocol.toString()
					+ " with size " + poolMembers.size());
				cap.addProtocolPoolBySchema(pooProtocol, new ProtocolPool(
					balanceStrategy, poolMembers));
				log.debug("PROTOCOL POOL: " + cap.getPoolByScheme(pooProtocol));
			}
		} else {
			log.debug("Pool is not defined in VFS " + fsName);
		}

		return cap;
	}

	/**
	 * @param poolMembers
	 * @throws NamespaceException
	 */
	private void verifyPoolIsValid(ArrayList<PoolMember> poolMembers)
		throws NamespaceException {

		if (poolMembers.isEmpty()) {
			throw new NamespaceException("POOL Defined is EMPTY!");
		}
		Protocol prot = poolMembers.get(0).getMemberProtocol().getProtocol();
		for (PoolMember member : poolMembers) {
			if (!(member.getMemberProtocol().getProtocol().equals(prot))) {
				throw new NamespaceException(
					"Defined Pool is NOT HOMOGENEOUS! Protocols " + prot.toString()
						+ " and " + member.toString() + " differs");
			}
		}
	}

	// ******************* DEFAULT VALUES ***************************

	private DefaultValuesInterface buildDefaultValues(String fsName)
		throws NamespaceException {

		DefaultValues def = new DefaultValues();
		if (parserUtil.isDefaultElementPresent(fsName)) {
			setSpaceDef(fsName, def);
			setFileDef(fsName, def);
		} else { // Produce Default Values with default values :o !
			verboseLog("VFS(" + fsName
				+ ").DefaultValues is ABSENT.  Using DEFAULT values.");
		}
		return def;
	}

	private void setSpaceDef(String fsName, DefaultValues def)
		throws NamespaceException {

		String spaceType = parserUtil.getDefaultSpaceType(fsName);
		verboseLog("VFS(" + fsName + ").DefaultValues.space.type = '" + spaceType
			+ "'");
		long lifeTime = parserUtil.getDefaultSpaceLifeTime(fsName);
		verboseLog("VFS(" + fsName + ").DefaultValues.space.lifeTime = '"
			+ lifeTime + "'");
		long guarSize = parserUtil.getDefaultSpaceGuarSize(fsName);
		verboseLog("VFS(" + fsName + ").DefaultValues.space.guarSize = '"
			+ guarSize + "'");
		long totSize = parserUtil.getDefaultSpaceTotSize(fsName);
		verboseLog("VFS(" + fsName + ").DefaultValues.space.totSize = '" + totSize
			+ "'");
		def.setSpaceDefaults(spaceType, lifeTime, guarSize, totSize);
	}

	private void setFileDef(String fsName, DefaultValues def)
		throws NamespaceException {

		String fileType = parserUtil.getDefaultFileType(fsName);
		verboseLog("VFS(" + fsName + ").DefaultValues.file.type = '" + fileType
			+ "'");
		long lifeTime = parserUtil.getDefaultFileLifeTime(fsName);
		verboseLog("VFS(" + fsName + ").DefaultValues.file.lifeTime = '" + lifeTime
			+ "'");
		def.setFileDefaults(fileType, lifeTime);
	}

	// ******************* MAPPING RULE ***************************

	private void buildMapRules() throws NamespaceException {

		int numOfMapRules = parserUtil.getNumberOfMappingRule();
		String ruleName, stfnRoot, mappedFS;
		MappingRule mapRule;

		for (int i = 0; i < numOfMapRules; i++) {
			ruleName = parserUtil.getMapRuleName(i);
			mappedFS = parserUtil.getMapRule_mappedFS(ruleName);
			// Adding mapping rule to VFS within vfss;
			if (vfss.containsKey(mappedFS)) {
				verboseLog("VFS '" + mappedFS + "' pointed by RULE : '" + ruleName
					+ "' exists.");
				stfnRoot = parserUtil.getMapRule_StFNRoot(ruleName);
				VirtualFSInterface vfs = (VirtualFSInterface) vfss.get(mappedFS);
				mapRule = new MappingRule(ruleName, stfnRoot, vfs/* , mappedFS */);
				((VirtualFS) vfs).addMappingRule(mapRule);
				maprules.put(ruleName, mapRule);
			} else {
				log.error("VFS '" + mappedFS + "' pointed by RULE : '" + ruleName
					+ "' DOES NOT EXISTS.");
			}
		}
	}

	// ******************* APPROACHABLE RULE ***************************

	private void buildAppRules() throws NamespaceException {

		int numOfAppRules = parserUtil.getNumberOfApproachRule();

		verboseLog("Number of APP Rule : " + numOfAppRules);
		String ruleName, dn, vo_name, relPath, anonymousHttpReadString;
		List<String> appFSList;
		ApproachableRule appRule;
		for (int i = 0; i < numOfAppRules; i++) {
			ruleName = parserUtil.getApproachRuleName(i);
			verboseLog(" APP rule nr:" + i + " is named : " + ruleName);

			dn = parserUtil.getAppRule_SubjectDN(ruleName);
			vo_name = parserUtil.getAppRule_SubjectVO(ruleName);
			SubjectRules subjectRules = new SubjectRules(dn, vo_name);

			relPath = parserUtil.getAppRule_RelativePath(ruleName);

			anonymousHttpReadString = parserUtil
				.getAppRule_AnonymousHttpRead(ruleName);
			if (anonymousHttpReadString != null
				&& !anonymousHttpReadString.trim().isEmpty()) {
				appRule = new ApproachableRule(ruleName, subjectRules, relPath,
					Boolean.parseBoolean(anonymousHttpReadString));
			} else {
				appRule = new ApproachableRule(ruleName, subjectRules, relPath);
			}

			appFSList = parserUtil.getAppRule_AppFS(ruleName);
			for (String appFS : appFSList) {
				if (vfss.containsKey(appFS)) {
					verboseLog("VFS '" + appFS + "' pointed by RULE : '" + ruleName
						+ "' exists.");
					VirtualFSInterface vfs = vfss.get(appFS);
					((VirtualFS) vfs).addApproachableRule(appRule);
					appRule.addApproachableVFS(vfs);
				} else {
					log.error("VFS '" + appFS + "' pointed by RULE : '" + ruleName
						+ "' DOES NOT EXISTS.");
				}
			}
			apprules.put(ruleName, appRule);
		}
	}

	/*****************************************************************************
	 * BUSINESS METHODs
	 ****************************************************************************/

	public String getNamespaceVersion() {

		return version;
	}

	public List<String> getAllVFS_Roots() {

		Collection<VirtualFSInterface> elem = vfss.values();
		Vector<String> roots = new Vector<String>(vfss.size());
		Iterator<VirtualFSInterface> scan = elem.iterator();
		while (scan.hasNext()) {
			String root = null;
			root = scan.next().getRootPath();
			roots.add(root);
		}
		return roots;
	}

	public Map<String, VirtualFSInterface> getMapVFS_Root() {

		Hashtable<String, VirtualFSInterface> result = new Hashtable<String, VirtualFSInterface>();
		Collection<VirtualFSInterface> elem = vfss.values();
		Iterator<VirtualFSInterface> scan = elem.iterator();
		while (scan.hasNext()) {
			String root = null;
			VirtualFSInterface vfs = scan.next();
			root = vfs.getRootPath();
			result.put(root, vfs);
		}
		return result;
	}

	public List<String> getAllMappingRule_StFNRoots() {

		Collection<MappingRule> elem = maprules.values();
		Vector<String> roots = new Vector<String>(maprules.size());
		Iterator<MappingRule> scan = elem.iterator();
		String root = null;
		while (scan.hasNext()) {
			root = scan.next().getStFNRoot();
			roots.add(root);
		}
		return roots;
	}

	public Map<String, String> getMappingRuleMAP() {

		HashMap<String, String> map = new HashMap<String, String>();
		Collection<MappingRule> elem = maprules.values();
		Iterator<MappingRule> scan = elem.iterator();
		String root = null;
		String name = null;
		MappingRule rule;
		while (scan.hasNext()) {
			rule = scan.next();
			root = rule.getStFNRoot();
			name = rule.getRuleName();
			map.put(name, root);
		}
		return map;
	}

	public VirtualFSInterface getVFS(String vfsName) {

		return vfss.get(vfsName);
	}

}

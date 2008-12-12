package it.grid.storm.namespace.config.xml;

import java.util.*;

import org.apache.commons.configuration.*;
import org.apache.commons.logging.*;
import edu.emory.mathcs.backport.java.util.concurrent.locks.*;
import edu.emory.mathcs.backport.java.util.concurrent.locks.Lock;
import it.grid.storm.namespace.*;
import it.grid.storm.namespace.config.*;
import it.grid.storm.namespace.model.*;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.srm.types.TSizeInBytes;

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


public class XMLNamespaceParser implements NamespaceParser, Observer {

    private Log log = NamespaceDirector.getLogger();

    private String version;
    private Hashtable vfss;
    private Hashtable maprules;
    private Hashtable<String,ApproachableRule> apprules;
    //private ReservedSpaceCatalog spaceCatalog;

    private XMLParserUtil parserUtil;
    private XMLConfiguration configuration;
    private XMLNamespaceLoader xmlLoader;

    private Lock refreshing = new ReentrantLock();

//For debug purpose only
    private boolean internalLog;
    private boolean testingMode;

    /**
     * Constructor
     *
     * @param loader NamespaceLoader
     */
    public XMLNamespaceParser(NamespaceLoader loader, boolean verboseLogging, boolean testingMode) {
        configuration = (XMLConfiguration) loader.getConfiguration();
        if (loader instanceof XMLNamespaceLoader) {
            xmlLoader = (XMLNamespaceLoader) loader;
            xmlLoader.setObserver(this);
        }
        else {
            log.error("XMLParser initialized with a non-XML Loader");
        }
        //this.internalLog = verboseLogging;
        //TOREMOVE
        this.internalLog = true;

        this.testingMode = testingMode;

        parserUtil = new XMLParserUtil(configuration);


        for (Iterator iter = parserUtil.getKeys(); iter.hasNext(); ) {
          Object item = (Object) iter.next();
          verboseLog(item.toString());
        }


        vfss = new Hashtable();
        maprules = new Hashtable();
        apprules = new Hashtable();

        boolean validNamespaceConfiguration = refreshCachedData();
        if (! (validNamespaceConfiguration)) {
            log.error(" ???????????????????????????????????? ");
            log.error(" ????  NAMESPACE does not VALID  ???? ");
            log.error(" ???????????????????????????????????? ");
            log.error(" Please see the log. ");
            System.exit( -1);
        }

    }

    public Map getVFSs() {
        return vfss;
    }

    public Map getApproachableRules() {
        return apprules;
    }

    public Map getMappingRules() {
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
     *                         PRIVATE METHODs
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
                log.debug(" ... reading and parsing the namespace configuration from file!");
            }
            catch (ConfigurationException ex) {
                ex.printStackTrace();
            }
            log.debug("REFRESHING CACHE..");
            //Save the cache content
            log.debug("  ..save the cache content before semantic check");
            Hashtable vfssSAVED = vfss;
            Hashtable maprulesSAVED = maprules;
            Hashtable apprulesSAVED = apprules;
            //Refresh the cache content with new values

            log.debug("  ..refresh the cache");
            refreshCache();

            //Do the checking on Namespace
            log.debug("  ..semantic check of namespace");
            NamespaceCheck checker = new NamespaceCheck(vfss, maprules, apprules);
            boolean semanticCheck = checker.check();

            //If there is an error restore old cache content
            log.debug("REFRESHING ENDED.");
            if (semanticCheck) {
                log.debug("Namespace is semantically valid");
                result = true;
            }
            else {
                log.warn("Namespace does not semantically valid!, so no load performed!");
                vfss = vfssSAVED;
                maprules = maprulesSAVED;
                apprules = apprulesSAVED;
                result = false;
            }
        }
        finally {
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
        log.info("  ##############  REFRESHING NAMESPACE CONFIGURATION CACHE : start  ###############");

        /**************************
         * Retrieve Version Number
         *************************/
        try {
            retrieveVersion();
        }
        catch (NamespaceException ex1) {
            log.warn("Namespace configuration does not contain a valid version number.", ex1);
            /**
             * @todo Manage this exceptional status!
             */
        }

        /**************************
         * Building VIRTUAL FS
         *************************/
        try {
            buildVFSs();
            //Update SA within Reserved Space Catalog
            updateSA();
        }
        catch (ClassNotFoundException ex) {
            log.fatal("Namespace Configuration ERROR in VFS-DRIVER specification", ex);
            /**
             * @todo Manage this exceptional status!
             */
        }
        catch (NamespaceException ex) {
            log.fatal("Namespace Configuration ERROR in VFS definition, please check it.", ex);
            /**
             * @todo Manage this exceptional status!
             */
        }

        /**************************
         * Building MAPPING RULES
         *************************/
        try {
            buildMapRules();
        }
        catch (NamespaceException ex1) {
            log.fatal("Namespace Configuration ERROR in MAPPING RULES definition, please check it.", ex1);
            /**
             * @todo Manage this exceptional status!
             */
        }

        /**************************
         * Building APPROACHABLE RULES
         *************************/
        try {
            buildAppRules();
        }
        catch (NamespaceException ex2) {
            log.fatal("Namespace Configuration ERROR in APPROACHABLE RULES definition, please check it.", ex2);
            /**
             * @todo Manage this exceptional status!
             */
        }
        log.info("  ##############  REFRESHING NAMESPACE CONFIGURATION CACHE : end ###############");

    }

    //*******************  Update SA Catalog ***************************
     private void updateSA() throws NamespaceException {
         ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();
         log.debug("Updating Space Catalog with Storage Area defined within NAMESPACE");
         VirtualFS vfs = null;
         Enumeration scan = vfss.elements();
         while (scan.hasMoreElements()) {

           vfs = (VirtualFS)scan.nextElement();
           String vfsAliasName = vfs.getAliasName();
           verboseLog(" Considering VFS : "+vfsAliasName);
           String aliasName = vfs.getSpaceTokenDescription();
           if (aliasName == null) {
             //Found a VFS without the optional element Space Token Description
             log.debug("XMLNamespaceParser.UpdateSA() : Found a VFS ('" + vfsAliasName +
                       "') without space-token-description. Skipping the Update of SA");
           } else {
             TSizeInBytes onlineSize = vfs.getProperties().getTotalOnlineSize();
             String spaceFileName = vfs.getRootPath();
             spaceCatalog.createVOSA_Token(aliasName, onlineSize, spaceFileName);
             verboseLog(" Updating with Space Token Description:'" + aliasName + "', onlineSize:'" + onlineSize +
                        "', spaceFileName:'" + spaceFileName);
           }

         }
         spaceCatalog.purgeOldVOSA_token();
         log.debug("Updating Space Catalog... DONE!!");


     }

    //*******************  VERSION NUMBER ***************************
     private void retrieveVersion() throws NamespaceException {
       version = parserUtil.getNamespaceVersion();
       verboseLog(" ====  NAMESPACE VERSION : '" + version+"'  ====");
     }

    //*******************  VIRTUAL FS ***************************

     private void buildVFSs() throws ClassNotFoundException, NamespaceException {
         int nrOfVFS = 0;

         nrOfVFS = parserUtil.getNumberOfFS();
         //For each VFS within configuration build VFS class istance
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
             //Building VFS
             vfs = new VirtualFS(testingMode);

             name = parserUtil.getFSName(i);
             vfs.setAliasName(name);
             verboseLog("VFS(" + i + ").name = '" + name + "'");

             fsType = parserUtil.getFSType(name);
             vfs.setFSType(fsType);
             verboseLog("VFS(" + name + ").fs_type = '" + fsType + "'");

             spaceTokenDescription = parserUtil.getFSSpaceTokenDescription(name);
             vfs.setSpaceTokenDescription(spaceTokenDescription);
             verboseLog("VFS(" + name + ").space-token-description = '" + spaceTokenDescription + "'");

             storageClass = StorageClassType.getStorageClassType(parserUtil.getStorageClass(name));
             vfs.setStorageClassType(storageClass);
             verboseLog("VFS(" + name + ").storage-class = '" + storageClass + "'");

             root = parserUtil.getFSRoot(name);
             vfs.setRoot(root);
             verboseLog("VFS(" + name + ").root = '" + root + "'");
             //log.debug("VFS(" + i + ").root = '" + root + "'");

             //verboseLog("VFS fs driver name:" + name + ", util: " + parserUtil.getFSDriver(name) + "!");
             driver = Class.forName(parserUtil.getFSDriver(name));
             //verboseLog("FS-Driver createed!");
             vfs.setFSDriver(driver);
             verboseLog("VFS(" + name + ").fsDriver [CLASS Name] = '" + driver.getName() + "'");

             driver = Class.forName(parserUtil.getSpaceDriver(name));
             vfs.setSpaceSystemDriver(driver);
             verboseLog("VFS(" + name + ").spaceDriver [CLASS Name] = '" + driver.getName() + "'");

             saAuthzType = parserUtil.getStorageAreaAuthzType(name);
             vfs.setSAAuthzType(saAuthzType);
             verboseLog("VFS(" + name + ").storage-area-authz.TYPE = '" + saAuthzType + "'");

             storageAreaAuthz = parserUtil.getStorageAreaAuthz(name);
             vfs.setSAAuthzSource(storageAreaAuthz);
             verboseLog("VFS(" + name + ").storage-area-authz = '" + storageAreaAuthz + "'");

             prop = buildProperties(name);
             vfs.setProperties(prop);

             cap = buildCapabilities(name);
             vfs.setCapabilities(cap);

             defValues = buildDefaultValues(name);
             vfs.setDefaultValues(defValues);

             //Adding VFS
             synchronized (this) {
                 vfss.remove(name);
                 vfss.put(name, vfs);
             }
         }
     }

    //*******************  PROPERTY  ***************************
     private PropertyInterface buildProperties(String fsName) throws NamespaceException {
       Property prop = new Property();

       String accessLatency = parserUtil.getAccessLatencyType(fsName);
       prop.setAccessLatency(accessLatency);
       verboseLog("VFS(" + fsName + ").Properties.AccessLatency = '" + accessLatency + "'");

       String expirationMode = parserUtil.getExpirationModeType(fsName);
       prop.setExpirationMode(expirationMode);
       verboseLog("VFS(" + fsName + ").Properties.ExpirationMode = '" + expirationMode + "'");

       String retentionPolicy = parserUtil.getRetentionPolicyType(fsName);
       prop.setRetentionPolicy(retentionPolicy);
       verboseLog("VFS(" + fsName + ").Properties.RetentionPolicy = '" + retentionPolicy + "'");

       String unitType = parserUtil.getNearlineSpaceUnitType(fsName);
       long nearLineSize = parserUtil.getNearlineSpaceSize(fsName);
       prop.setTotalNearlineSize(unitType, nearLineSize);
       verboseLog("VFS(" + fsName + ").Properties.NearlineSpaceSize = '" + nearLineSize + " "+unitType+"'");

       unitType = parserUtil.getOnlineSpaceUnitType(fsName);
       long onlineSize = parserUtil.getOnlineSpaceSize(fsName);
       prop.setTotalOnlineSize(unitType, onlineSize);
       verboseLog("VFS(" + fsName + ").Properties.OnlineSpaceSize = '" + onlineSize + " "+unitType+"'");

       boolean hasLimitedSize = parserUtil.getOnlineSpaceLimitedSize(fsName);
       prop.setLimitedSize(hasLimitedSize);
       verboseLog("VFS(" + fsName + ").Properties.OnlineSpaceLimitedSize = '" + hasLimitedSize + "'");

       return prop;
     }


    //*******************  CAPABILITY  ***************************

     private CapabilityInterface buildCapabilities(String fsName) throws NamespaceException {
         /**
         String[] fileType = parserUtil.getFileType(fsName);
         for (int j = 0; j < fileType.length; j++) {
             verboseLog("VFS(" + fsName + ").Capabilities.file.types(" + j + ") = '" + fileType[j] + "'");
         }
         String[] spaceType = parserUtil.getSpaceType(fsName);
         for (int j = 0; j < spaceType.length; j++) {
             verboseLog("VFS(" + fsName + ").Capabilities.space.types(" + j + ") = '" + spaceType[j] + "'");
         }
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
         verboseLog("VFS(" + fsName + ").Capabilities.defaultACL [Defined?] ="+defaultACLDefined);
         if (defaultACLDefined) {
           int nrACLEntries = parserUtil.getNumberOfACL(fsName);
           String groupName = null;
           String filePermString = null;
           ACLEntry aclEntry = null;
           for (int entryNumber = 0; entryNumber< nrACLEntries; entryNumber++) {
             groupName = parserUtil.getGroupName(fsName, entryNumber);
             filePermString = parserUtil.getPermissionString(fsName, entryNumber);
             try {
               aclEntry = new ACLEntry(groupName, filePermString);
               cap.addACLEntry(aclEntry);
             }
             catch (PermissionException permEx) {
               log.error("Namespace XML Parser -- ERROR -- : "+permEx.getMessage());
             }
           }
           verboseLog("VFS(" + fsName + ").Capabilities.defaultACL = "+cap.getDefaultACL());
         }

         /**
          * QUOTA ELEMENT
          */
         boolean quotaDefined = parserUtil.getQuotaDefined(fsName);
         Quota quota = null;
         if (quotaDefined) {
           boolean quotaEnabled = parserUtil.getQuotaEnabled(fsName);
           if (parserUtil.getQuotaPropertiesFileDefined(fsName)) {
             String propertiesFile = parserUtil.getQuotaPropertiesFile(fsName);
             quota = new Quota(quotaEnabled,propertiesFile);
           }
           else {
             String device = parserUtil.getQuotaDevice(fsName);
             QuotaType quotaType;
             String quotaValue = null;
             if (parserUtil.getQuotaFilesetDefined(fsName)) {
               quotaType = QuotaType.buildQuotaType(QuotaType.FILESET);
               quotaValue = parserUtil.getQuotaFileset(fsName);
             }
             else {
               if (parserUtil.getQuotaGroupIDDefined(fsName)) {
                 quotaType = QuotaType.buildQuotaType(QuotaType.GRP);
                 quotaValue = parserUtil.getQuotaGroupID(fsName);
               }
               else {
                 if (parserUtil.getQuotaUserIDDefined(fsName)) {
                   quotaType = QuotaType.buildQuotaType(QuotaType.USR);
                   quotaValue = parserUtil.getQuotaUserID(fsName);
                 }
                 else {
                   quotaType = QuotaType.buildQuotaType(QuotaType.UNKNOWN);
                   quotaValue = "unknown";
                 }
               }
             }
             quotaType.setValue(quotaValue);
             quota = new Quota(quotaEnabled,device,quotaType);
           }
         } else {
           quota = new Quota();
         }
         cap.setQuota(quota);


         verboseLog("VFS(" + fsName + ").Capabilities.quota = '" + quota + "'");

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
         for (int i = 0; i < nrProtocols; i++) {
             protocolIndex = parserUtil.getProtId(fsName, i); //1.4.0 (Return -1 if ID is not present)
             name = parserUtil.getProtName(fsName, i);
             schema = parserUtil.getProtSchema(fsName, name);
             protocol = Protocol.getProtocol(schema);
             protocol.setProtocolServiceName(name);
             serviceHostName = parserUtil.getProtHost(fsName, name);
             servicePortValue = parserUtil.getProtPort(fsName, name);
             int portIntValue = -1;
             service = null;
             if (servicePortValue != null) {
                 try {
                     portIntValue = Integer.parseInt(servicePortValue);
                     service = new Authority(serviceHostName, portIntValue);
                     //log.debug("SERVICE PORT: "+service);
                 }
                 catch (NumberFormatException nfe) {
                     log.warn("to evaluate the environmental variable " + servicePortValue);
                 }
             }
             else {
                 service = new Authority(serviceHostName);
                 //log.debug("SERVICE : "+service);
             }
             transportProt = new TransportProtocol(protocol, service);
             transportProt.setProtocolID(protocolIndex); //1.4.0
             verboseLog("VFS(" + fsName + ").Capabilities.protocol("+i+") = '" + transportProt + "'");
             cap.addTransportProtocolByScheme(protocol,transportProt);
             if (protocolIndex!=-1) {
               cap.addTransportProtocolByID(protocolIndex,transportProt);
             }

         }

         /**
          * PROTOCOL POOL
          */
         String balanceStrategy = parserUtil.getBalancerStrategy(fsName);
         ArrayList<PoolMember> poolMembers = new ArrayList<PoolMember>();
         PoolMember poolMember;
         int nrMembers = parserUtil.getNumberOfPoolMembers(fsName);
         int protIndex;
         int memberWeight;
         for (int i = 0; i < nrMembers; i++) {
           protIndex = parserUtil.getProtId(fsName, i);
           memberWeight = parserUtil.getMemberWeight(fsName, i);
           poolMember = new PoolMember(protIndex,memberWeight);
           poolMembers.add(poolMember);
         }

         return cap;
     }




     //*******************  DEFAULT VALUES ***************************

      private DefaultValuesInterface buildDefaultValues(String fsName) throws NamespaceException {
        DefaultValues def = new DefaultValues();
        if (parserUtil.isDefaultElementPresent(fsName)) {
          setSpaceDef(fsName, def);
          setFileDef(fsName, def);
        } else { //Produce Default Values with default values :o !
           verboseLog("VFS(" + fsName + ").DefaultValues is ABSENT.  Using DEFAULT values.");
        }
        return def;
      }

     private void setSpaceDef(String fsName, DefaultValues def) throws NamespaceException {
       String spaceType = parserUtil.getDefaultSpaceType(fsName);
       verboseLog("VFS(" + fsName + ").DefaultValues.space.type = '" + spaceType + "'");
       long lifeTime = parserUtil.getDefaultSpaceLifeTime(fsName);
       verboseLog("VFS(" + fsName + ").DefaultValues.space.lifeTime = '" + lifeTime + "'");
       long guarSize = parserUtil.getDefaultSpaceGuarSize(fsName);
       verboseLog("VFS(" + fsName + ").DefaultValues.space.guarSize = '" + guarSize + "'");
       long totSize = parserUtil.getDefaultSpaceTotSize(fsName);
       verboseLog("VFS(" + fsName + ").DefaultValues.space.totSize = '" + totSize + "'");
       def.setSpaceDefaults(spaceType, lifeTime, guarSize, totSize);
     }

     private void setFileDef(String fsName, DefaultValues def) throws NamespaceException {
       String fileType = parserUtil.getDefaultFileType(fsName);
       verboseLog("VFS(" + fsName + ").DefaultValues.file.type = '" + fileType + "'");
       long lifeTime = parserUtil.getDefaultFileLifeTime(fsName);
       verboseLog("VFS(" + fsName + ").DefaultValues.file.lifeTime = '" + lifeTime + "'");
       def.setFileDefaults(fileType, lifeTime);
     }


    //*******************  MAPPING RULE  ***************************


     private void buildMapRules() throws NamespaceException {
         int numOfMapRules = parserUtil.getNumberOfMappingRule();
         String ruleName;
         String stfnRoot;
         String mappedFS;
         MappingRule mapRule;
         VirtualFS vfs;
         boolean vfsExists = false;

         for (int i = 0; i < numOfMapRules; i++) {
             ruleName = parserUtil.getMapRuleName(i);
             stfnRoot = parserUtil.getMapRule_StFNRoot(ruleName);
             mappedFS = parserUtil.getMapRule_mappedFS(ruleName);
             mapRule = new MappingRule(ruleName, stfnRoot, mappedFS);
             //Adding mapping rule to VFS within vfss;
             vfsExists = vfss.containsKey(mappedFS);
             if (vfsExists) {
                 verboseLog("VFS '" + mappedFS + "' pointed by RULE : '" + ruleName + "' exists.");
                 vfs = (VirtualFS) vfss.get(mappedFS);
                 vfs.addMappingRule(mapRule);
             }
             else {
                 log.error("VFS '" + mappedFS + "' pointed by RULE : '" + ruleName + "' DOES NOT EXISTS.");
             }
             maprules.put(ruleName, mapRule);
         }
     }

    //*******************  APPROACHABLE RULE  ***************************

     private void buildAppRules() throws NamespaceException {
         int numOfAppRules = parserUtil.getNumberOfApproachRule();

         verboseLog("Number of APP Rule : " + numOfAppRules);
         String ruleName;
         String dn;
         String vo_name;
         List appFS;
         String relPath;
         ApproachableRule appRule;
         for (int i = 0; i < numOfAppRules; i++) {
             ruleName = parserUtil.getApproachRuleName(i);
             verboseLog(" APP rule nr:" + i + " is named : " + ruleName);
             dn = parserUtil.getAppRule_SubjectDN(ruleName);
             vo_name = parserUtil.getAppRule_SubjectVO(ruleName);
             appFS = parserUtil.getAppRule_AppFS(ruleName);
             relPath = parserUtil.getAppRule_RelativePath(ruleName);
             SubjectRules subjectRules = null;
             subjectRules = new SubjectRules(dn, vo_name);
             appRule = new ApproachableRule(ruleName, subjectRules, relPath);
             appRule.setApproachableVFSList(appFS);
             apprules.put(ruleName, appRule);
         }
     }

    /*****************************************************************************
     *  BUSINESS METHODs
     ****************************************************************************/

    public String getNamespaceVersion() {
      return version;
  }


    public List getAllVFS_Roots() {

        Collection elem = vfss.values();
        Vector roots = new Vector(vfss.size());
        Iterator scan = elem.iterator();
        while (scan.hasNext()) {
            String root = null;
            try {
                root = ( (VirtualFSInterface) scan.next()).getRootPath();
            }
            catch (NamespaceException ex) {
                log.error("Error while retrieving all StFN roots of VFSs", ex);
            }
            roots.add(root);
        }
        return roots;
    }

    public Map getMapVFS_Root() {
        Hashtable result = new Hashtable();

        Collection elem = vfss.values();
        Iterator scan = elem.iterator();
        while (scan.hasNext()) {
            String root = null;
            VirtualFSInterface vfs = (VirtualFSInterface) scan.next();
            try {
                root = vfs.getRootPath();
            }
            catch (NamespaceException ex) {
                log.error("Error while retrieving all StFN roots of VFSs", ex);
            }
            result.put(root, vfs);
        }
        return result;
    }

    public List getAllMappingRule_StFNRoots() {
        Collection elem = maprules.values();
        Vector roots = new Vector(maprules.size());
        Iterator scan = elem.iterator();
        String root = null;
        while (scan.hasNext()) {
            root = ( (MappingRule) scan.next()).getStFNRoot();
            roots.add(root);
        }
        return roots;
    }

    public Map getMappingRuleMAP() {
        HashMap map = new HashMap();
        Collection elem = maprules.values();
        Iterator scan = elem.iterator();
        String root = null;
        String name = null;
        MappingRule rule;
        while (scan.hasNext()) {
            rule = (MappingRule) scan.next();
            root = rule.getStFNRoot();
            name = rule.getRuleName();
            map.put(name, root);
        }
        return map;
    }

    public VirtualFSInterface getVFS(String vfsName) {
        return (VirtualFSInterface) vfss.get(vfsName);
    }



}

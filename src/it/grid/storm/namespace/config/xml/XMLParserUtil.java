package it.grid.storm.namespace.config.xml;

import java.util.*;

import org.apache.commons.configuration.*;
import org.apache.commons.logging.*;
import it.grid.storm.namespace.*;

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
public class XMLParserUtil implements XMLConst {

    private Configuration configuration;
    private Log log = LogFactory.getLog(XMLParserUtil.class);

    public XMLParserUtil(Configuration config) {
        this.configuration = config;
    }

    /*****************************************************************************
     * GENERICS METHODS
     */

    public boolean validateXML() {
        return true;
    }

    public boolean areThereSustitutionCharInside(String element) {
        boolean result = false;
        result = (element.indexOf(XMLConst.PROT_SUB_PATTERN) != -1)
            || (element.indexOf(XMLConst.FS_SUB_PATTERN) != -1)
            || (element.indexOf(XMLConst.APPRULE_SUB_PATTERN) != -1)
            || (element.indexOf(XMLConst.MAP_SUB_PATTERN) != -1)
            || (element.indexOf(XMLConst.ACL_ENTRY_SUB_PATTERN) != -1);
        return result;
    }

    public char whicSubstitutionChar(String element) {
        if (element.indexOf(XMLConst.PROT_SUB_PATTERN) != -1) {
            return XMLConst.PROT_SUB_PATTERN;
        }
        else if (element.indexOf(XMLConst.FS_SUB_PATTERN) != -1) {
            return XMLConst.FS_SUB_PATTERN;
        }
        else if (element.indexOf(XMLConst.APPRULE_SUB_PATTERN) != -1) {
            return APPRULE_SUB_PATTERN;
        }
        else if (element.indexOf(XMLConst.MAP_SUB_PATTERN) != -1) {
            return XMLConst.MAP_SUB_PATTERN;
        }
        else if (element.indexOf(XMLConst.ACL_ENTRY_SUB_PATTERN) != -1) {
          return XMLConst.ACL_ENTRY_SUB_PATTERN;
        }
        return ' ';
    }

    /*****************************************************************************
     * FILESYSTEMS METHODS
     */
    public String getNamespaceVersion() throws NamespaceException {
      String result = null;
      result = getStringProperty(XMLConst.NAMESPACE_VERSION);
      return result;
    }

    public String getFSSpaceTokenDescription(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      String result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FS_SPACE_TOKEN_DESCRIPTION));
      return result;
    }

    public String getAuthorizationSource(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      String result = null;
      //Optional element
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.FS_AUTHZ_SOURCE))) {
        result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FS_AUTHZ_SOURCE));
      }
      else { //Default value needed.
        result = XMLConst.DEFAULT_AUTHZ_SOURCE;
        log.debug("AuthZ source for VFS(+'"+nameOfFS+"') is absent. Default value ('"+result+"') will be used.");
      }
      return result;
    }

/**
    public boolean getQuotaCheck(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      boolean result = false;
      //Optional element
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_CHECK))) {
        result = getBooleanProperty(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_CHECK));
      }
      else { //Default value needed.
        result = XMLConst.DEFAULT_CHECKING_QUOTA;
        log.debug("Checking quota flag in VFS(+'"+nameOfFS+"') is absent. Default value ('"+result+"') will be used.");
      }
      return result;
    }
**/

    public String getRetentionPolicyType(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      String result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.RETENTION_POLICY));
      return result;
    }

    public String getAccessLatencyType(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      String result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.ACCESS_LATENCY));
      return result;
    }

    public String getExpirationModeType(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      String result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.EXPIRATION_MODE));
      return result;
    }

    public String getOnlineSpaceUnitType(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      String result = null;
      //Optional element
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.ONLINE_SIZE_UNIT))) {
        result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.ONLINE_SIZE_UNIT));
      }
      else { //Default value needed.
        result = XMLConst.DEFAULT_UNIT_TYPE;
        log.debug("Online Space Unit type for VFS(+'"+nameOfFS+"') is absent. Default value ('"+result+"') will be used");
      }
      return result;
    }

    public long getOnlineSpaceSize(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      long result = getLongProperty(substituteNumberInFSElement(numOfFS, XMLConst.ONLINE_SIZE));
      return result;
    }

    public String getNearlineSpaceUnitType(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      String result = null;
      //Optional element
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.NEARLINE_SIZE_UNIT))) {
        result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.NEARLINE_SIZE_UNIT));
      }
      else { //Default value needed.
        result = XMLConst.DEFAULT_UNIT_TYPE;
        log.debug("Online Space Unit type for VFS(+'" + nameOfFS + "') is absent. Default value ('" + result +
                  "') will be used");
      }
      return result;
    }

    public long getNearlineSpaceSize(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      long result = getLongProperty(substituteNumberInFSElement(numOfFS, XMLConst.NEARLINE_SIZE));
      return result;
    }

    public int getNumberOfFS() throws NamespaceException {
        return getPropertyNumber(XMLConst.FS_COUNTING);
    }

    public String getFSName(int numOfFS) throws NamespaceException {
        return getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FILESYSTEM_NAME));
    }

    public int getFSNumber(String nameOfFS) throws NamespaceException {
        return retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    }

    public String getFSType(String nameOfFS) throws NamespaceException {
        //log.debug("-----FSTYPE------START");
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        //log.debug("-----FSTYPE------END");
        return getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FILESYSTEM_TYPE));
    }

    public String getFSRoot(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        String result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FS_ROOT));
        //log.debug("VFS ROOT = "+result);
        return result;
    }

    public String getFSDriver(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        return getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FS_DRIVER));
    }

    public String getSpaceDriver(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        return getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FS_SPACE_DRIVER));
    }

    public boolean isDefaultElementPresent(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        boolean result = false;
        //FS_DEFAULTVALUES
        result = isPresent(substituteNumberInFSElement(numOfFS, XMLConst.FS_DEFAULTVALUES));
        return result;
    }

    public String getDefaultSpaceType(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        return getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.DEF_SPACE_TYPE));
    }

    public long getDefaultSpaceLifeTime(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        return getLongProperty(substituteNumberInFSElement(numOfFS, XMLConst.DEF_SPACE_LT));
    }

    public long getDefaultSpaceGuarSize(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        return getLongProperty(substituteNumberInFSElement(numOfFS, XMLConst.DEF_SPACE_GUARSIZE));
    }

    public long getDefaultSpaceTotSize(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        return getLongProperty(substituteNumberInFSElement(numOfFS, XMLConst.DEF_SPACE_TOTSIZE));
    }

    public String getDefaultFileType(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        return getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.DEF_FILE_TYPE));
    }

    public long getDefaultFileLifeTime(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        return getLongProperty(substituteNumberInFSElement(numOfFS, XMLConst.DEF_FILE_LT));
    }

    public String getACLMode(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        return getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.ACL_MODE));
    }

    public int getNumberOfProt(String nameOfFS) throws NamespaceException {
        int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
        if (numOfFS == -1) {
            throw new NamespaceException("FS named '" + nameOfFS + "' does not exist in config");
        }
        String protCount = substitutionNumber(XMLConst.PROTOCOL_COUNTING, XMLConst.FS_SUB_PATTERN, numOfFS);
        //log.debug( configuration.getString(protCount));
        return getPropertyNumber(protCount);
    }

    public String getProtName(String nameOfFS, int numOfProt) throws NamespaceException {
        return getStringProperty(substituteNumberInProtocolElement(nameOfFS, numOfProt, XMLConst.PROTOCOL_NAME));
    }

    public int getProtNumberByName(String nameOfFS, String nameOfProt) throws NamespaceException {
        int numFS = getFSNumber(nameOfFS);
        String collElem = substituteNumberInFSElement(numFS, XMLConst.PROTOCOL_BY_NAME);
        //log.debug("COLLECTION = "+collElem);
        return retrieveNumberByName(nameOfProt, collElem);
    }

    public String getProtSchema(String nameOfFS, String protName) throws NamespaceException {
        int numOfProt = getProtNumberByName(nameOfFS, protName);
        return getStringProperty(substituteNumberInProtocolElement(nameOfFS, numOfProt, XMLConst.PROT_SCHEMA));
    }

    public String getProtHost(String nameOfFS, String protName) throws NamespaceException {
        int numOfProt = getProtNumberByName(nameOfFS, protName);
        return getStringProperty(substituteNumberInProtocolElement(nameOfFS, numOfProt, XMLConst.PROT_HOST));
    }

    public String getProtPort(String nameOfFS, String protName) throws NamespaceException {
        int numOfProt = getProtNumberByName(nameOfFS, protName);
        return getStringProperty(substituteNumberInProtocolElement(nameOfFS, numOfProt, XMLConst.PROT_PORT));
    }

    /*****************************************************************************
     * MAPPING RULES METHODS
     */


    public int getNumberOfMappingRule() throws NamespaceException {
        return getPropertyNumber(XMLConst.MAP_RULE_COUNTING);
    }

    public String getMapRuleName(int numOfMapRule) throws NamespaceException {
        return getStringProperty(substituteNumberInMAPElement(numOfMapRule, XMLConst.MAP_RULE_NAME));
    }

    public String getMapRule_StFNRoot(String nameOfMapRule) throws NamespaceException {
        int numOfMapRule = retrieveNumberByName(nameOfMapRule, XMLConst.MAP_RULE_BY_NAME);
        return getStringProperty(substituteNumberInMAPElement(numOfMapRule, XMLConst.MAP_RULE_STFNROOT));
    }

    public String getMapRule_mappedFS(String nameOfMapRule) throws NamespaceException {
        int numOfMapRule = retrieveNumberByName(nameOfMapRule, XMLConst.MAP_RULE_BY_NAME);
        return getStringProperty(substituteNumberInMAPElement(numOfMapRule, XMLConst.MAP_RULE_MAPPED_FS));
    }

    /*****************************************************************************
     * APPROACHING METHODS
     */

    public int getNumberOfApproachRule() throws NamespaceException {
        return getPropertyNumber(XMLConst.APP_RULE_COUNTING);
    }

    public String getApproachRuleName(int numOfAppRule) throws NamespaceException {
        return getStringProperty(substituteNumberInAPPElement(numOfAppRule, XMLConst.APP_RULE_NAME));
    }

    public String getAppRule_SubjectDN(String nameOfAppRule) throws NamespaceException {
        int numOfAppRule = retrieveNumberByName(nameOfAppRule, XMLConst.APP_RULE_BY_NAME);
        return getStringProperty(substituteNumberInAPPElement(numOfAppRule, XMLConst.APP_DN));
    }

    public String getAppRule_SubjectVO(String nameOfAppRule) throws NamespaceException {
        int numOfAppRule = retrieveNumberByName(nameOfAppRule, XMLConst.APP_RULE_BY_NAME);
        return getStringProperty(substituteNumberInAPPElement(numOfAppRule, XMLConst.APP_VO_NAME));
    }

    public List getAppRule_AppFS(String nameOfAppRule) throws NamespaceException {
        int numOfAppRule = retrieveNumberByName(nameOfAppRule, XMLConst.APP_RULE_BY_NAME);
        return getListValue(substituteNumberInAPPElement(numOfAppRule, XMLConst.APPROACHABLE_FS));
    }

    public String getAppRule_RelativePath(String nameOfAppRule) throws NamespaceException {
        int numOfAppRule = retrieveNumberByName(nameOfAppRule, XMLConst.APP_RULE_BY_NAME);
        return getStringProperty(substituteNumberInAPPElement(numOfAppRule, XMLConst.APP_SPACE_REL_PATH));
    }

    /*****************************************************************************
     * QUOTA METHODS
     */

    public boolean getQuotaDefined(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      boolean result = false;
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_ENABLED)))
        result = true;
      return result;
    }

    public boolean getQuotaEnabled(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      boolean result = false;
      result = getBooleanProperty(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_ENABLED));
      return result;
    }

    public boolean getQuotaPropertiesFileDefined(String nameOfFS) throws  NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      boolean result = false;
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_PROPERTIES_FILE)))
        result = true;
      return result;
    }

    public boolean getQuotaPropertiesDefined(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      boolean result = false;
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_PROPERTIES)))
        result = true;
      return result;
    }

    public String getQuotaPropertiesFile(String nameOfFS) throws NamespaceException {
      String result = null;
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_PROPERTIES_FILE))) {
        result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_PROPERTIES_FILE));
      } else {
        throw new NamespaceException("Unable to find the element '"+XMLConst.QUOTA_PROPERTIES_FILE+
                                     "' for the VFS:'"+nameOfFS+"'");
      }
      return result;
    }


    public boolean getQuotaDeviceDefined(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      boolean result = false;
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_DEVICE)))
        result = true;
      return result;
    }


    public String getQuotaDevice(String nameOfFS) throws NamespaceException {
      String result = null;
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_DEVICE))) {
        result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_DEVICE));
      } else {
        throw new NamespaceException("Unable to find the element '"+XMLConst.QUOTA_DEVICE+
                                     "' for the VFS:'"+nameOfFS+"'");
      }
      return result;
    }


    public boolean getQuotaFilesetDefined(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      boolean result = false;
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.FILE_SET_ID)))
        result = true;
      return result;
    }

    public String getQuotaFileset(String nameOfFS) throws NamespaceException {
      String result = null;
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.FILE_SET_ID))) {
        result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FILE_SET_ID));
      } else {
        throw new NamespaceException("Unable to find the element '"+XMLConst.FILE_SET_ID+
                                     "' for the VFS:'"+nameOfFS+"'");
      }
      return result;
    }

    public boolean getQuotaGroupIDDefined(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      boolean result = false;
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.GROUP_ID)))
        result = true;
      return result;
    }

    public String getQuotaGroupID(String nameOfFS) throws NamespaceException {
      String result = null;
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.GROUP_ID))) {
        result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.GROUP_ID));
      } else {
        throw new NamespaceException("Unable to find the element '"+XMLConst.GROUP_ID+
                                     "' for the VFS:'"+nameOfFS+"'");
      }
      return result;
    }

    public boolean getQuotaUserIDDefined(String nameOfFS) throws NamespaceException {
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      boolean result = false;
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.USER_ID)))
        result = true;
      return result;
    }

    public String getQuotaUserID(String nameOfFS) throws NamespaceException {
      String result = null;
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.USER_ID))) {
        result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.USER_ID));
      } else {
        throw new NamespaceException("Unable to find the element '"+XMLConst.USER_ID+
                                     "' for the VFS:'"+nameOfFS+"'");
      }
      return result;
    }






    /*****************************************************************************
     * STORAGE CLASS METHODs
     */
    public String getStorageClass(String nameOfFS) throws NamespaceException {
      String result = XMLConst.DEFAULT_STORAGE_CLASS;
      int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
      if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.FS_STORAGE_CLASS))) {
        result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FS_STORAGE_CLASS));
      } else {
        log.debug("Storage Class for VFS(+'"+nameOfFS+"') is absent. Default value ('"+result+"') will be used.");
      }
      return result;
    }


    /*****************************************************************************
     * PRIVATE METHOD
     *****************************************************************************/
    private String substitutionNumber(String xpath, char patternChar, int number) {
        int startIndex = 0;
        int pos = 0;
        StringBuffer result = new StringBuffer();
        pos = xpath.indexOf(patternChar, startIndex);
        String numStr = Integer.toString(number);
        result.append(xpath.substring(startIndex, pos));
        result.append(numStr);
        result.append(xpath.substring(pos + 1));
        return result.toString();
    }

    private String substituteNumberInFSElement(int numberOfFS, String element) throws NamespaceException {
        int numFS = getNumberOfFS();
        if (numberOfFS > numFS) {
            throw new NamespaceException("Invalid pointing of Virtual File system");
        }
        String new_element = substitutionNumber(element, XMLConst.FS_SUB_PATTERN, numberOfFS);
        return new_element;
    }


    private String substituteNumberInACLEntryElement(String nameOfFS, int numberOfACLEntry, String element) throws
        NamespaceException {
        int numFS = getFSNumber(nameOfFS);
        if (numFS == -1) {
            throw new NamespaceException("Virtual File system (" + nameOfFS + ") does not exists");
        }
        int numACL = getNumberOfACL(nameOfFS);
        if (numberOfACLEntry > numACL) {
            throw new NamespaceException("Invalid pointing of ACL Entry within VFS");
        }
        String new_element = substitutionNumber(element, XMLConst.FS_SUB_PATTERN, numFS);
        new_element = substitutionNumber(new_element, XMLConst.ACL_ENTRY_SUB_PATTERN, numberOfACLEntry);
        return new_element;
    }


    private String substituteNumberInProtocolElement(String nameOfFS, int numberOfProtocol, String element) throws
        NamespaceException {
        int numFS = getFSNumber(nameOfFS);
        if (numFS == -1) {
            throw new NamespaceException("Virtual File system (" + nameOfFS + ") does not exists");
        }
        int numProt = getNumberOfProt(nameOfFS);
        if (numberOfProtocol > numProt) {
            throw new NamespaceException("Invalid pointing of Protocol within VFS");
        }
        String new_element = substitutionNumber(element, XMLConst.FS_SUB_PATTERN, numFS);
        new_element = substitutionNumber(new_element, XMLConst.PROT_SUB_PATTERN, numberOfProtocol);
        return new_element;
    }

    private String substituteNumberInMAPElement(int numberOfMapRule, String element) throws NamespaceException {
        int numMapRule = getNumberOfMappingRule();

        if (numberOfMapRule > numMapRule) {
            throw new NamespaceException("Invalid pointing of Mapping Rule");
        }
        String new_element = substitutionNumber(element, XMLConst.MAP_SUB_PATTERN, numberOfMapRule);
        return new_element;
    }

    private String substituteNumberInAPPElement(int numberOfAppRule, String element) throws NamespaceException {
        int numAppRule = getNumberOfApproachRule();
        if (numberOfAppRule > numAppRule) {
            throw new NamespaceException("Invalid pointing of Approachable Rule");
        }
        String new_element = substitutionNumber(element, XMLConst.APPRULE_SUB_PATTERN, numberOfAppRule);
        return new_element;
    }

    private int retrieveNumberByName(String name, String collectionElement, boolean logging) {
        int result = -1;
        int size = -1;
        //log.debug(" NAME : "+name+"  |  Collection Element :"+collectionElement);
        List prop = configuration.getList(collectionElement);
        if (prop != null) {
            size = prop.size();
            //log.debug("Size = "+size);
            if (logging) {
                for (int i = 0; i < size; i++) {
                    log.debug(prop.get(i));
                }
            }
            result = prop.indexOf(name);
        }
        else {
            log.warn("[retrieveNumberByName_3] Element <" + collectionElement +
                     "> does not exists in namespace configuration file");
        }
        return result;
    }

    private int retrieveNumberByName(String name, String collectionElement) {
        int result = -1;
        int size = -1;
        //log.debug(" NAME : "+name+"  |  Collection Element :"+collectionElement);
        List prop = configuration.getList(collectionElement);
        if (prop != null) {
            size = prop.size();
            result = prop.indexOf(name);
        }
        else {
            log.warn("[retrieveNumberByName_2] Element <" + collectionElement +
                     "> does not exists in namespace configuration file");
        }
        return result;
    }


    public Iterator getKeys() {
      return configuration.getKeys();
    }

    /**
     *
     * @param element String
     * @return int
     */
    private int getPropertyNumber(String element) {
      int result = -1;
      Object prop = configuration.getProperty(element);
      if (prop != null) {
        result = 1; //If it is not null its value is atleast '1'!
        if (prop instanceof Collection) {
          result = ( (Collection) prop).size();
        }
      }
      else {
        log.warn("[getPropertyNumber] Element <" + element + "> does not exists in namespace configuration file");
      }

      return result;
    }


   private boolean isPresent(String element) {
     boolean result = false;
     result = configuration.containsKey(element);
     //log.debug("XMLPArserUtil: isPresent('"+element+"')="+result);
     return result;
   }

    /**
     *
     * @param element String
     * @return int
     */
    private String getStringProperty(String element) throws NamespaceException {
        String prop = null;
        try {
            prop = configuration.getString(element);
            //log.debug("ELEMENT = "+element+"  VALUE = "+prop);
        }
        catch (ConversionException ce) {
            log.warn("[getStringProperty] Element <" + element + "> does not contains a String value");
        }
        catch (NoSuchElementException note) {
            log.warn("[getStringProperty] Element <" + element + "> does not exists in namespace configuration file");
        }
        return prop;
    }

    /**
     *
     * @param element String
     * @return boolean
     */
    private boolean getBooleanProperty(String element) throws NamespaceException {
        boolean result = false;
        try {
            result = configuration.getBoolean(element);
        }
        catch (ConversionException ce) {
            log.warn("[getLongProperty] Element <" + element + "> does not contains a String value");
        }
        catch (NoSuchElementException note) {
            log.warn("[getLongProperty] Element <" + element + "> does not exists in namespace configuration file");
        }
        return result;
    }

    /**
     *
     * @param element String
     * @return int
     */
    private long getLongProperty(String element) throws NamespaceException {
        long prop = -1L;
        try {
            prop = configuration.getLong(element);
        }
        catch (ConversionException ce) {
            log.warn("[getLongProperty] Element <" + element + "> does not contains a String value");
        }
        catch (NoSuchElementException note) {
            log.warn("[getLongProperty] Element <" + element + "> does not exists in namespace configuration file");
        }
        return prop;
    }

    /**
     *
     * @param element String
     * @return int
     */
    private int getIntProperty(String element) throws NamespaceException {
        int prop = -1;
        try {
            prop = configuration.getInt(element);
        }
        catch (ConversionException ce) {
            log.warn("[getIntProperty] Element <" + element + "> does not contains a String value");
        }
        catch (NoSuchElementException note) {
            log.warn("[getIntProperty] Element <" + element + "> does not exists in namespace configuration file");
        }
        return prop;
    }

    /**
     *
     * @param element String
     * @return int
     */
    private String[] getListProperty(String element) throws NamespaceException {
        String prop = null;
        try {
            prop = configuration.getString(element);
        }
        catch (ConversionException ce) {
            log.warn("[getListProperty] Element <" + element + "> does not contains a String value");
        }
        catch (NoSuchElementException note) {
            log.warn("[getListProperty] Element <" + element + "> does not exists in namespace configuration file");
        }
        //log.debug("LIST : "+prop);
        String[] result = prop.split(",");
        //log.debug(" LIST lenght :"+result.length);
        return result;
    }


    private List getListValue(String collectionElement) {
        List<String> propList = configuration.getList(collectionElement);
        List<String> prop = new ArrayList();
        // For a set or list
        for (Iterator it = propList.iterator(); it.hasNext(); ) {
          String element = (String)it.next();
          prop.add( element.trim() );
        }

        log.debug("LIST - prop : "+prop);
        log.debug("Nr. of elements : "+prop.size());
        if (prop.size()==0) {
            log.warn("[retrieveNumberByName_2] Element <" + collectionElement +
                     "> does not exists in namespace configuration file");
        }
        return prop;
    }



  public boolean getDefaultACLDefined(String nameOfFS) throws NamespaceException {
    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.GROUP_NAME)))
      result = true;
    return result;
  }

  public int getNumberOfACL(String nameOfFS) throws NamespaceException {
    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    if (numOfFS == -1) {
      throw new NamespaceException("FS named '" + nameOfFS + "' does not exist in config");
    }
    String aclCount = substitutionNumber(XMLConst.ACL_ENTRY_COUNTING, XMLConst.FS_SUB_PATTERN, numOfFS);
    log.debug( "ACL Count = "+aclCount);
    return getPropertyNumber(aclCount);
  }


  public String getGroupName(String nameOfFS, int aclEntryNumber) throws NamespaceException {
    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    String aclCount = substitutionNumber(XMLConst.GROUP_NAME, XMLConst.FS_SUB_PATTERN, numOfFS);
    String result = null;
    Object prop = configuration.getProperty(aclCount);
    if (prop != null) {
      if (prop instanceof Collection) {
        ArrayList<String> propList = new ArrayList<String>((Collection)prop);
        if (propList.size()>aclEntryNumber) {
          result = propList.get(aclEntryNumber);
        }
      } else {
        if (prop instanceof String) {
          result = ( (String) prop );
        }
      }
    }
    else {
      log.warn("[getPropertyNumber] Element <" + aclCount + "> does not exists in namespace configuration file");
    }
    return result;
   // return getStringProperty(substituteNumberInACLEntryElement(nameOfFS, aclEntryNumber, XMLConst.GROUP_NAME));
  }

  public String getPermissionString(String nameOfFS, int aclEntryNumber) throws NamespaceException {
    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
   String aclCount = substitutionNumber(XMLConst.PERMISSIONS, XMLConst.FS_SUB_PATTERN, numOfFS);
   String result = null;
   Object prop = configuration.getProperty(aclCount);
   if (prop != null) {
     if (prop instanceof Collection) {
       ArrayList<String> propList = new ArrayList<String>((Collection)prop);
       if (propList.size()>aclEntryNumber) {
         result = propList.get(aclEntryNumber);
       }
     } else {
       if (prop instanceof String) {
         result = ( (String) prop );
       }
     }
   }
   else {
     log.warn("[getPropertyNumber] Element <" + aclCount + "> does not exists in namespace configuration file");
   }
   return result;


    //return getStringProperty(substituteNumberInACLEntryElement(nameOfFS, aclEntryNumber, XMLConst.PERMISSIONS));
  }

}

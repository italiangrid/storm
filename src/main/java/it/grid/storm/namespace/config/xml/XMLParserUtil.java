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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.SAAuthzType;

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
public class XMLParserUtil implements XMLConst {

  private final HierarchicalConfiguration configuration;
  private final Logger log = LoggerFactory.getLogger(XMLParserUtil.class);

  public XMLParserUtil(Configuration config) {

    configuration = (HierarchicalConfiguration) config;
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
    } else if (element.indexOf(XMLConst.FS_SUB_PATTERN) != -1) {
      return XMLConst.FS_SUB_PATTERN;
    } else if (element.indexOf(XMLConst.APPRULE_SUB_PATTERN) != -1) {
      return APPRULE_SUB_PATTERN;
    } else if (element.indexOf(XMLConst.MAP_SUB_PATTERN) != -1) {
      return XMLConst.MAP_SUB_PATTERN;
    } else if (element.indexOf(XMLConst.ACL_ENTRY_SUB_PATTERN) != -1) {
      return XMLConst.ACL_ENTRY_SUB_PATTERN;
    } else if (element.indexOf(XMLConst.MEMBER_SUB_PATTERN) != -1) {
      return XMLConst.MEMBER_SUB_PATTERN;
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
    String result = getStringProperty(
        substituteNumberInFSElement(numOfFS, XMLConst.FS_SPACE_TOKEN_DESCRIPTION));
    return result;
  }

  /**
   * public String getAuthorizationSource(String nameOfFS) throws NamespaceException { int numOfFS =
   * retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME); String result = null; //Optional element
   * if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.FS_AUTHZ))) { result =
   * getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FS_AUTHZ)); } else { //Default
   * value needed. result = XMLConst.DEFAULT_AUTHZ_SOURCE; log.debug("AuthZ source for
   * VFS(+'"+nameOfFS+ "') is absent. Default value ('"+result+"') will be used."); } return result;
   * }
   **/

  /**
   * public boolean getQuotaCheck(String nameOfFS) throws NamespaceException { int numOfFS =
   * retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME); boolean result = false; //Optional element
   * if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_CHECK))) { result =
   * getBooleanProperty(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_CHECK)); } else {
   * //Default value needed. result = XMLConst.DEFAULT_CHECKING_QUOTA; log.debug("Checking quota
   * flag in VFS(+'"+nameOfFS +"') is absent. Default value ('"+result+"') will be used."); } return
   * result; }
   **/

  public String getRetentionPolicyType(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    String result =
        getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.RETENTION_POLICY));
    return result;
  }

  public String getAccessLatencyType(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    String result =
        getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.ACCESS_LATENCY));
    return result;
  }

  public String getExpirationModeType(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    String result =
        getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.EXPIRATION_MODE));
    return result;
  }

  public String getOnlineSpaceUnitType(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    String result = null;
    // Optional element
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.ONLINE_SIZE_UNIT))) {
      result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.ONLINE_SIZE_UNIT));
    } else { // Default value needed.
      result = XMLConst.DEFAULT_UNIT_TYPE;
      log.debug("Online Space Unit type for VFS(+'" + nameOfFS + "') is absent. Default value ('"
          + result + "') will be used");
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
    // Optional element
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.NEARLINE_SIZE_UNIT))) {
      result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.NEARLINE_SIZE_UNIT));
    } else { // Default value needed.
      result = XMLConst.DEFAULT_UNIT_TYPE;
      log.debug("Online Space Unit type for VFS(+'" + nameOfFS + "') is absent. Default value ('"
          + result + "') will be used");
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

    // log.debug("-----FSTYPE------START");
    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    // log.debug("-----FSTYPE------END");
    return getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FILESYSTEM_TYPE));
  }

  public String getFSRoot(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    String result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.FS_ROOT));
    // log.debug("VFS ROOT = "+result);
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
    // FS_DEFAULTVALUES
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
    String protCount =
        substitutionNumber(XMLConst.PROTOCOL_COUNTING, XMLConst.FS_SUB_PATTERN, numOfFS);
    // log.debug( configuration.getString(protCount));
    return getPropertyNumber(protCount);
  }

  public String getProtName(String nameOfFS, int numOfProt) throws NamespaceException {

    return getStringProperty(
        substituteNumberInProtocolElement(nameOfFS, numOfProt, XMLConst.PROTOCOL_NAME));
  }

  public int getProtNumberByName(String nameOfFS, String nameOfProt) throws NamespaceException {

    int numFS = getFSNumber(nameOfFS);
    String collElem = substituteNumberInFSElement(numFS, XMLConst.PROTOCOL_BY_NAME);
    // log.debug("COLLECTION = "+collElem);
    return retrieveNumberByName(nameOfProt, collElem);
  }

  public String getProtSchema(String nameOfFS, int numOfProt) throws NamespaceException {

    return getStringProperty(
        substituteNumberInProtocolElement(nameOfFS, numOfProt, XMLConst.PROT_SCHEMA));
  }

  public String getProtHost(String nameOfFS, int numOfProt) throws NamespaceException {

    return getStringProperty(
        substituteNumberInProtocolElement(nameOfFS, numOfProt, XMLConst.PROT_HOST));
  }

  public String getProtPort(String nameOfFS, int numOfProt) throws NamespaceException {

    return getStringProperty(
        substituteNumberInProtocolElement(nameOfFS, numOfProt, XMLConst.PROT_PORT));
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
    return getStringProperty(
        substituteNumberInMAPElement(numOfMapRule, XMLConst.MAP_RULE_STFNROOT));
  }

  public String getMapRule_mappedFS(String nameOfMapRule) throws NamespaceException {

    int numOfMapRule = retrieveNumberByName(nameOfMapRule, XMLConst.MAP_RULE_BY_NAME);
    return getStringProperty(
        substituteNumberInMAPElement(numOfMapRule, XMLConst.MAP_RULE_MAPPED_FS));
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

  public List<String> getAppRule_AppFS(String nameOfAppRule) throws NamespaceException {

    int numOfAppRule = retrieveNumberByName(nameOfAppRule, XMLConst.APP_RULE_BY_NAME);
    return getListValue(substituteNumberInAPPElement(numOfAppRule, XMLConst.APPROACHABLE_FS));
  }

  public String getAppRule_RelativePath(String nameOfAppRule) throws NamespaceException {

    int numOfAppRule = retrieveNumberByName(nameOfAppRule, XMLConst.APP_RULE_BY_NAME);
    return getStringProperty(
        substituteNumberInAPPElement(numOfAppRule, XMLConst.APP_SPACE_REL_PATH));
  }

  public String getAppRule_AnonymousHttpRead(String nameOfAppRule) throws NamespaceException {

    int numOfAppRule = retrieveNumberByName(nameOfAppRule, XMLConst.APP_RULE_BY_NAME);
    return getStringProperty(
        substituteNumberInAPPElement(numOfAppRule, XMLConst.APP_ANONYMOUS_HTTP_READ));
  }

  /*****************************************************************************
   * QUOTA METHODS
   */

  public boolean getQuotaDefined(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_ENABLED))) {
      result = true;
    }
    return result;
  }

  public boolean getQuotaEnabled(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    result = getBooleanProperty(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_ENABLED));
    return result;
  }

  public boolean getQuotaDeviceDefined(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_DEVICE))) {
      result = true;
    }
    return result;
  }

  public String getQuotaDevice(String nameOfFS) throws NamespaceException {

    String result = null;
    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_DEVICE))) {
      result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_DEVICE));
    } else {
      throw new NamespaceException("Unable to find the element '" + XMLConst.QUOTA_DEVICE
          + "' for the VFS:'" + nameOfFS + "'");
    }
    return result;
  }

  public boolean getQuotaFilesetDefined(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_FILE_SET_NAME))) {
      result = true;
    }
    return result;
  }

  public String getQuotaFileset(String nameOfFS) throws NamespaceException {

    String result = null;
    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_FILE_SET_NAME))) {
      result =
          getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_FILE_SET_NAME));
    } else {
      throw new NamespaceException("Unable to find the element '" + XMLConst.QUOTA_FILE_SET_NAME
          + "' for the VFS:'" + nameOfFS + "'");
    }
    return result;
  }

  public boolean getQuotaGroupIDDefined(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_GROUP_NAME))) {
      result = true;
    }
    return result;
  }

  public String getQuotaGroupID(String nameOfFS) throws NamespaceException {

    String result = null;
    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_GROUP_NAME))) {
      result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_GROUP_NAME));
    } else {
      throw new NamespaceException("Unable to find the element '" + XMLConst.QUOTA_GROUP_NAME
          + "' for the VFS:'" + nameOfFS + "'");
    }
    return result;
  }

  public boolean getQuotaUserIDDefined(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_USER_NAME))) {
      result = true;
    }
    return result;
  }

  public String getQuotaUserID(String nameOfFS) throws NamespaceException {

    String result = null;
    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_USER_NAME))) {
      result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.QUOTA_USER_NAME));
    } else {
      throw new NamespaceException("Unable to find the element '" + XMLConst.QUOTA_USER_NAME
          + "' for the VFS:'" + nameOfFS + "'");
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
      log.debug("Storage Class for VFS(+'" + nameOfFS + "') is absent. Default value ('" + result
          + "') will be used.");
    }
    return result;
  }

  /*****************************************************************************
   * PRIVATE METHOD
   *****************************************************************************/
  private String substitutionNumber(String xpath, char patternChar, int number) {

    int startIndex = 0;
    int pos = 0;
    StringBuilder result = new StringBuilder();
    pos = xpath.indexOf(patternChar, startIndex);
    String numStr = Integer.toString(number);
    result.append(xpath.substring(startIndex, pos));
    result.append(numStr);
    result.append(xpath.substring(pos + 1));
    return result.toString();
  }

  private String substituteNumberInFSElement(int numberOfFS, String element)
      throws NamespaceException {

    int numFS = getNumberOfFS();
    if (numberOfFS > numFS) {
      throw new NamespaceException("Invalid pointing of Virtual File system");
    }
    String new_element = substitutionNumber(element, XMLConst.FS_SUB_PATTERN, numberOfFS);
    return new_element;
  }

  private String substituteNumberInProtocolElement(String nameOfFS, int numberOfProtocol,
      String element) throws NamespaceException {

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

  private String substituteNumberInPoolElement(String nameOfFS, int numberOfPool, String element)
      throws NamespaceException {

    int numFS = getFSNumber(nameOfFS);
    if (numFS == -1) {
      throw new NamespaceException("Virtual File system (" + nameOfFS + ") does not exists");
    }
    int numPool = getNumberOfPool(nameOfFS);
    if (numberOfPool > numPool) {
      throw new NamespaceException("Invalid pointing of Pool within VFS");
    }
    String new_element = substitutionNumber(element, XMLConst.FS_SUB_PATTERN, numFS);
    new_element = substitutionNumber(new_element, XMLConst.POOL_SUB_PATTERN, numberOfPool);
    return new_element;
  }

  private String substituteNumberInMembersElement(String nameOfFS, int numOfPool,
      int numberOfMember, String element) throws NamespaceException {

    int numFS = getFSNumber(nameOfFS);
    if (numFS == -1) {
      throw new NamespaceException("Virtual File system (" + nameOfFS + ") does not exists");
    }
    int numMembers = getNumberOfPoolMembers(nameOfFS, numOfPool);
    if (numberOfMember > numMembers) {
      throw new NamespaceException("Invalid pointing of Member within VFS");
    }
    String new_element = substitutionNumber(element, XMLConst.FS_SUB_PATTERN, numFS);
    new_element = substitutionNumber(new_element, XMLConst.POOL_SUB_PATTERN, numOfPool);
    new_element = substitutionNumber(new_element, XMLConst.MEMBER_SUB_PATTERN, numberOfMember);
    return new_element;
  }

  private String substituteNumberInMAPElement(int numberOfMapRule, String element)
      throws NamespaceException {

    int numMapRule = getNumberOfMappingRule();

    if (numberOfMapRule > numMapRule) {
      throw new NamespaceException("Invalid pointing of Mapping Rule");
    }
    String new_element = substitutionNumber(element, XMLConst.MAP_SUB_PATTERN, numberOfMapRule);
    return new_element;
  }

  private String substituteNumberInAPPElement(int numberOfAppRule, String element)
      throws NamespaceException {

    int numAppRule = getNumberOfApproachRule();
    if (numberOfAppRule > numAppRule) {
      throw new NamespaceException("Invalid pointing of Approachable Rule");
    }
    String new_element = substitutionNumber(element, XMLConst.APPRULE_SUB_PATTERN, numberOfAppRule);
    return new_element;
  }

  private int retrieveNumberByName(String name, String collectionElement) {

    int result = -1;
    List<Object> prop = configuration.getList(collectionElement);
    if (prop != null) {
      result = prop.indexOf(name);
    } else {
      log.warn("[retrieveNumberByName_2] Element <{}> does not exists in namespace configuration file", collectionElement);
    }
    return result;
  }

  public Iterator<String> getKeys() {

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
      result = 1; // If it is not null its value is at least '1'!
      if (prop instanceof Collection) {
        result = ((Collection<?>) prop).size();
      }
    } else {
      log.warn("[getPropertyNumber] Element <{}> does not exists in namespace configuration file", element);
    }

    return result;
  }

  private boolean isPresent(String element) {

    boolean result = false;
    result = configuration.containsKey(element);
    // log.debug("XMLPArserUtil: isPresent('"+element+"')="+result);
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
      // log.debug("ELEMENT = "+element+" VALUE = "+prop);
    } catch (ConversionException ce) {
      log.warn("[getStringProperty] Element <" + element + "> does not contains a String value");
    } catch (NoSuchElementException note) {
      log.warn("[getStringProperty] Element <" + element
          + "> does not exists in namespace configuration file");
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
    } catch (ConversionException ce) {
      log.warn("[getLongProperty] Element <" + element + "> does not contains a String value");
    } catch (NoSuchElementException note) {
      log.warn("[getLongProperty] Element <" + element
          + "> does not exists in namespace configuration file");
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
    } catch (ConversionException ce) {
      log.warn("[getLongProperty] Element <" + element + "> does not contains a String value");
    } catch (NoSuchElementException note) {
      log.warn("[getLongProperty] Element <" + element
          + "> does not exists in namespace configuration file");
    }
    return prop;
  }

  /**
   * 
   * @param element String
   * @return int
   */
  private int getIntProperty(String element) {

    int prop = -1;
    try {
      prop = configuration.getInt(element);
    } catch (ConversionException ce) {
      log.warn("[getIntProperty] Element <" + element + "> does not contains a String value");
    } catch (NoSuchElementException note) {
      log.warn("[getIntProperty] Element <" + element
          + "> does not exists in namespace configuration file");
    }
    return prop;
  }

  private List<String> getListValue(String collectionElement) {

    List<Object> propList = configuration.getList(collectionElement);
    List<String> prop = Lists.newArrayList();
    // For a set or list
    for (Object element2 : propList) {
      String element = (String) element2;
      prop.add(element.trim());
    }

    log.debug("LIST - prop : " + prop);
    log.debug("Nr. of elements : " + prop.size());
    if (prop.size() == 0) {
      log.warn("[retrieveNumberByName_2] Element <" + collectionElement
          + "> does not exists in namespace configuration file");
    }
    return prop;
  }

  public boolean getDefaultACLDefined(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.GROUP_NAME))) {
      result = true;
    }
    return result;
  }

  public int getNumberOfACL(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    if (numOfFS == -1) {
      throw new NamespaceException("FS named '" + nameOfFS + "' does not exist in config");
    }
    String aclCount =
        substitutionNumber(XMLConst.ACL_ENTRY_COUNTING, XMLConst.FS_SUB_PATTERN, numOfFS);
    log.debug("ACL Count = " + aclCount);
    return getPropertyNumber(aclCount);
  }

  @SuppressWarnings("unchecked")
  public String getGroupName(String nameOfFS, int aclEntryNumber) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    String aclCount = substitutionNumber(XMLConst.GROUP_NAME, XMLConst.FS_SUB_PATTERN, numOfFS);
    String result = null;
    Object prop = configuration.getProperty(aclCount);
    if (prop != null) {
      if (prop instanceof List<?>) {
        List<String> propList = Lists.newArrayList((List<String>) prop);
        if (propList.size() > aclEntryNumber) {
          result = (String) propList.get(aclEntryNumber);
        }
      } else {
        if (prop instanceof String) {
          result = ((String) prop);
        }
      }
    } else {
      log.warn("[getPropertyNumber] Element <{}> does not exists in namespace configuration file", aclCount);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public String getPermissionString(String nameOfFS, int aclEntryNumber) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    String aclCount = substitutionNumber(XMLConst.PERMISSIONS, XMLConst.FS_SUB_PATTERN, numOfFS);
    String result = null;
    Object prop = configuration.getProperty(aclCount);
    if (prop != null) {
      if (prop instanceof List<?>) {
        List<String> propList = Lists.newArrayList((List<String>) prop);
        if (propList.size() > aclEntryNumber) {
          result = propList.get(aclEntryNumber);
        }
      } else {
        if (prop instanceof String) {
          result = ((String) prop);
        }
      }
    } else {
      log.warn("[getPropertyNumber] Element <" + aclCount
          + "> does not exists in namespace configuration file");
    }
    return result;

    // return getStringProperty(substituteNumberInACLEntryElement(nameOfFS,
    // aclEntryNumber, XMLConst.PERMISSIONS));
  }

  /**
   * ********************************** VERSION 1.4.0
   ***************************************/

  public String getStorageAreaAuthz(String nameOfFS, SAAuthzType type) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    if (type.equals(SAAuthzType.FIXED)) {
      return getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.SA_AUTHZ_FIXED));
    } else {
      return getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.SA_AUTHZ_DB));
    }
  }

  public SAAuthzType getStorageAreaAuthzType(String nameOfFS) throws NamespaceException {

    if (getStorageAreaAuthzFixedDefined(nameOfFS)) {
      return SAAuthzType.FIXED;
    }
    if (getStorageAreaAuthzDBDefined(nameOfFS)) {
      return SAAuthzType.AUTHZDB;
    }
    throw new NamespaceException("Unable to find the SAAuthzType in " + nameOfFS);
  }

  public boolean getStorageAreaAuthzFixedDefined(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.SA_AUTHZ_FIXED))) {
      result = true;
    }
    return result;
  }

  public boolean getStorageAreaAuthzDBDefined(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.SA_AUTHZ_DB))) {
      result = true;
    }
    return result;
  }

  public int getProtId(String nameOfFS, int numOfProt) throws NamespaceException {

    // int numOfProt = getProtNumberByName(nameOfFS, protName);
    String protId = substituteNumberInProtocolElement(nameOfFS, numOfProt, XMLConst.PROT_ID);
    // log.debug("ProtID : "+protId);
    if (isPresent(protId)) {
      return getIntProperty(
          substituteNumberInProtocolElement(nameOfFS, numOfProt, XMLConst.PROT_ID));
    } else {
      return -1;
    }
  }

  public boolean getOnlineSpaceLimitedSize(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    result = getBooleanProperty(substituteNumberInFSElement(numOfFS, XMLConst.LIMITED_SIZE));
    return result;
  }

  public int getNumberOfPool(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    if (numOfFS == -1) {
      throw new NamespaceException("FS named '" + nameOfFS + "' does not exist in config");
    }
    if (!getPoolDefined(nameOfFS))
      return 0;
    String protCount = substitutionNumber(XMLConst.POOL_COUNTING, XMLConst.FS_SUB_PATTERN, numOfFS);
    return getPropertyNumber(protCount);
  }

  public boolean getPoolDefined(String nameOfFS) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    boolean result = false;
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.POOL_COUNTING))) {
      result = true;
    }
    return result;
  }

  public String getBalancerStrategy(String nameOfFS) throws NamespaceException {

    String result = null;
    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    if (isPresent(substituteNumberInFSElement(numOfFS, XMLConst.BALANCE_STRATEGY))) {
      result = getStringProperty(substituteNumberInFSElement(numOfFS, XMLConst.BALANCE_STRATEGY));
    } else {
      throw new NamespaceException("Unable to find the element '" + XMLConst.BALANCE_STRATEGY
          + "' for the VFS:'" + nameOfFS + "'");
    }
    return result;
  }

  public int getNumberOfPoolMembers(String nameOfFS, int poolCounter) throws NamespaceException {

    int numOfFS = retrieveNumberByName(nameOfFS, XMLConst.FS_BY_NAME);
    if (numOfFS == -1) {
      throw new NamespaceException("FS named '" + nameOfFS + "' does not exist in config");
    }
    String subTree = substituteNumberInPoolElement(nameOfFS, poolCounter, XMLConst.POOL);
    HierarchicalConfiguration sub = configuration.configurationAt(subTree);
    Object members = sub.getProperty("members.member[@member-id]");
    int numOfMembers = -1;
    if (members != null) {
      if (members instanceof Collection) {
        numOfMembers = ((Collection) members).size();
      } else {
        numOfMembers = 1;
      }
    } else {
      log.error("Error during the retrieve of the number of pool member of " + nameOfFS);
    }
    return numOfMembers;
  }

  public int getMemberID(String nameOfFS, int numOfPool, int memberNr) throws NamespaceException {

    return getIntProperty(
        substituteNumberInMembersElement(nameOfFS, numOfPool, memberNr, XMLConst.POOL_MEMBER_ID));
  }

  public int getMemberWeight(String nameOfFS, int numOfPool, int memberNr)
      throws NamespaceException {

    return getIntProperty(substituteNumberInMembersElement(nameOfFS, numOfPool, memberNr,
        XMLConst.POOL_MEMBER_WEIGHT));
  }

  public String getBalancerStrategy(String fsName, int poolCounter) throws NamespaceException {

    String poolId = substituteNumberInPoolElement(fsName, poolCounter, XMLConst.BALANCE_STRATEGY);
    if (isPresent(poolId)) {
      return getStringProperty(
          substituteNumberInPoolElement(fsName, poolCounter, XMLConst.BALANCE_STRATEGY));
    } else {
      throw new NamespaceException("Unable to find the element '" + XMLConst.BALANCE_STRATEGY
          + "' for the VFS:'" + fsName + "'");
    }
  }

}

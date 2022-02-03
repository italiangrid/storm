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

import static com.google.common.base.Preconditions.checkNotNull;
import static it.grid.storm.namespace.naming.NamespaceUtil.getWinnerRule;
import static it.grid.storm.namespace.naming.NamespaceUtil.getWinnerVFS;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import it.grid.storm.common.GUID;
import it.grid.storm.common.types.PFN;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.Space;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.config.NamespaceLoader;
import it.grid.storm.namespace.config.NamespaceParser;
import it.grid.storm.namespace.config.xml.XMLNamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLNamespaceParser;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.namespace.naming.NamingConst;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

public class Namespace {

  private static Namespace instance = null;

  private static final Logger log = LoggerFactory.getLogger(Namespace.class);

  private static final String SPACE_FILE_NAME_SUFFIX = ".space";
  private static final char SPACE_FILE_NAME_SEPARATOR = '_';

  private final NamespaceParser parser;

  public static void init(String namespaceFilePath, boolean semanticCheckEnabled)
      throws DOMException, ConfigurationException, ParserConfigurationException, SAXException,
      IOException, NamespaceException {

    NamespaceLoader loader = null;

    log.info("Initializing Namespace from {} ...", namespaceFilePath);
    loader = new XMLNamespaceLoader(namespaceFilePath);

    instance = new Namespace(new XMLNamespaceParser(loader, semanticCheckEnabled));

    log.debug("NAMESPACE INITIALIZATION : ... done!");
  }

  public static Namespace getInstance() {
    return instance;
  }

  private Namespace(NamespaceParser parser) {

    this.parser = parser;
  }

  public String getNamespaceVersion() throws NamespaceException {

    return parser.getNamespaceVersion();
  }

  public List<VirtualFS> getAllDefinedVFS() {

    return parser.getVFSs().values().stream().collect(Collectors.toList());
  }

  public Map<String, VirtualFS> getAllDefinedVFSAsDictionary() {

    return parser.getMapVFS_Root();
  }

  public List<MappingRule> getAllDefinedMappingRules() {

    return parser.getMappingRules().values().stream().collect(Collectors.toList());
  }

  public List<VirtualFS> getApproachableVFS(GridUserInterface user) {

    Map<String, ApproachableRule> apprules = Maps.newHashMap(parser.getApproachableRules());
    List<VirtualFS> approachVFS = Lists.newLinkedList();
    for (ApproachableRule appRule : apprules.values()) {
      if (appRule.match(user)) {
        approachVFS.addAll(appRule.getApproachableVFS());
      }
    }
    return approachVFS;
  }

  public List<VirtualFS> getApproachableByAnonymousVFS() throws NamespaceException {

    List<VirtualFS> anonymousVFS = Lists.newLinkedList();
    List<VirtualFS> allVFS = Lists.newLinkedList(getAllDefinedVFS());

    for (VirtualFS vfs : allVFS) {
      if (vfs.isApproachableByAnonymous()) {
        anonymousVFS.add(vfs);
      }
    }

    return anonymousVFS;
  }

  public List<VirtualFS> getReadableByAnonymousVFS() throws NamespaceException {

    List<VirtualFS> readableVFS = Lists.newLinkedList();
    List<VirtualFS> allVFS = Lists.newLinkedList(getAllDefinedVFS());

    for (VirtualFS vfs : allVFS) {
      if (vfs.isHttpWorldReadable()) {
        readableVFS.add(vfs);
      }
    }

    return readableVFS;
  }

  public List<VirtualFS> getReadableOrApproachableByAnonymousVFS() throws NamespaceException {

    List<VirtualFS> rowVFS = Lists.newLinkedList();
    List<VirtualFS> allVFS = Lists.newLinkedList(getAllDefinedVFS());

    for (VirtualFS vfs : allVFS) {
      if (vfs.isHttpWorldReadable() || vfs.isApproachableByAnonymous()) {
        rowVFS.add(vfs);
      }
    }

    return rowVFS;
  }

  public VirtualFS getDefaultVFS(GridUserInterface user) throws NamespaceException {

    SortedSet<ApproachableRule> appRules = Sets.newTreeSet(getApproachableRules(user));

    if (appRules.isEmpty()) {
      if (user instanceof AbstractGridUser) {
        String msg =
            String.format("No approachable rules found for user with DN='%s' and VO = '%s'",
                user.getDn(), ((AbstractGridUser) user).getVO());
        log.error(msg);
        throw new NamespaceException(msg);
      } else {
        String msg = String.format("No approachable rules found for user with "
            + "DN='%s' User certificate has not VOMS extension", user.getDn());
        log.error(msg);
        throw new NamespaceException(msg);
      }
    }
    log.debug("Compatible Approachable rules : {}", appRules);
    ApproachableRule firstAppRule = appRules.first();
    log.debug("Default APP_RULE is the first: {}", firstAppRule);

    VirtualFS vfs = getApproachableDefaultVFS(firstAppRule);
    log.debug("Default VFS for Space Files : {}", vfs);
    return vfs;
  }

  public boolean isApproachable(StoRI stori, GridUserInterface user) throws NamespaceException {

    return getApproachableVFS(user).contains(stori.getVirtualFileSystem());
  }

  /**
   * 
   * The resolution is based on the retrieving of the Winner Rule 1) First attempt is based on
   * StFN-Path 2) Second attempt is based on all StFN. That because is possible that SURL is
   * expressed without File Name so StFN is a directory. ( Special case is when the SFN does not
   * contain the File Name and ALL the StFN is considerable as StFN-Path. )
   * 
   * @param surl TSURL
   * @return StoRI
   * @throws NamespaceException
   * @throws InvalidSURLException
   */
  public StoRI resolveStoRIbySURL(TSURL surl)
      throws UnapprochableSurlException, NamespaceException, InvalidSURLException {

    return resolveStoRI(surl, null);
  }

  public StoRI resolveStoRIbySURL(TSURL surl, GridUserInterface user)
      throws UnapprochableSurlException, NamespaceException, InvalidSURLException {

    return resolveStoRI(surl, user);
  }

  private StoRI resolveStoRI(TSURL surl, GridUserInterface user)
      throws UnapprochableSurlException, NamespaceException, InvalidSURLException {

    checkNotNull(surl, "resolveStoRI: invalid null surl");

    StoRI stori = null;
    List<VirtualFS> vfsApproachable = null;

    /* 1. compute user's approachable VFS: */
    if (isAnonymous(user)) {
      vfsApproachable = Lists.newArrayList(getAllDefinedVFS());
    } else {
      vfsApproachable = getApproachableVFS(user);
    }

    if (vfsApproachable.isEmpty()) {
      String errorMsg = "No approachable VFS found for user!";
      log.debug(errorMsg);
      throw new UnapprochableSurlException(errorMsg);
    }

    /* get the winner rule for SURL */
    MappingRule winnerRule = getWinnerRule(surl, getAllDefinedMappingRules(), vfsApproachable);

    if (winnerRule == null) {
      /* check if surl can be resolved by this instance of StoRM */
      if (isSolvable(surl)) {
        log.debug("No mapping rule found for surl='{}' and user '{}'", surl, user);
        throw new UnapprochableSurlException(
            "User '" + user + "' is not " + "authorized to access '" + surl + "'");
      }
      /* this surl is invalid for this instance of StoRM? */
      String msg = "The requested SURL is not managed by this instance of StoRM";
      log.debug(msg);
      throw new InvalidSURLException(surl, msg);
    }

    log.debug("The winner rule is {}", winnerRule.getRuleName());

    // create StoRI
    stori = buildStoRI(winnerRule.getMappedFS(), winnerRule, surl);

    // verify if StoRI canonical path is enclosed into the winner VFS
    if (isStoRIEnclosed(stori, winnerRule.getMappedFS())) {
      log.debug("Resource '{}' belongs to '{}'", stori.getLocalFile(),
          winnerRule.getMappedFS().getAliasName());
      return stori;
    }

    log.debug("Resource '{}' doesn't belong to {}", stori.getLocalFile(),
        winnerRule.getMappedFS().getAliasName());

    if (isAnonymous(user)) {
      throw new UnapprochableSurlException(
          stori.getLocalFile() + " is not approachable by anonymous users!");
    }

    /* get the VFS where the resource is phisically located, if exists */
    String realPath = getStoRICanonicalPath(stori);
    VirtualFS targetVFS = getWinnerVFS(realPath, parser.getMapVFS_Root());
    if (targetVFS == null) {
      log.debug("Unable to find a valid VFS from path '{}'", realPath);
      throw new InvalidSURLException(surl,
          "The requested SURL is not managed by this instance of StoRM");
    }
    log.debug("{} belongs to {}", realPath, targetVFS.getAliasName());

    /* check if target VFS is approachable */
    if (!vfsApproachable.contains(targetVFS)) {
      String msg = String.format("%s is not approachable by the user", targetVFS.getAliasName());
      log.debug(msg);
      throw new UnapprochableSurlException(msg);
    }

    log.debug("{} is approachable by the user", targetVFS.getAliasName());
    return stori;

  }

  private boolean isSolvable(TSURL surl) {

    MappingRule rule = getWinnerRule(surl, getAllDefinedMappingRules(), getAllDefinedVFS());
    return rule != null;
  }

  private String getStoRICanonicalPath(StoRI stori) throws NamespaceException {

    String realPath = null;
    try {
      realPath = stori.getLocalFile().getCanonicalPath();
    } catch (IOException e) {
      throw new NamespaceException(e.getMessage(), e);
    }
    return realPath;
  }

  private boolean isStoRIEnclosed(StoRI stori, VirtualFS vfs) throws NamespaceException {

    return resolveVFSbyLocalFile(stori.getLocalFile()).getRootPath().equals(vfs.getRootPath());
  }

  private StoRI buildStoRI(VirtualFS vfs, MappingRule mappingRule, TSURL surl)
      throws NamespaceException {

    String stfnPath = surl.sfn().stfn().toString();
    String relativePath = NamespaceUtil.extractRelativePath(mappingRule.getStFNRoot(), stfnPath);
    StoRI stori = vfs.createFile(relativePath, StoRIType.FILE);
    stori.setStFNRoot(mappingRule.getStFNRoot());
    stori.setMappingRule(mappingRule);
    return stori;
  }

  private boolean isAnonymous(GridUserInterface user) {

    return user == null;
  }

  public VirtualFS resolveVFSbySURL(TSURL surl, GridUserInterface user)
      throws UnapprochableSurlException, InvalidSURLException, NamespaceException {

    if (surl == null || user == null) {
      String errorMsg = "Unable to perform resolveStoRIbySURL, invalid arguments";
      log.error(errorMsg);
      throw new IllegalArgumentException(errorMsg);
    }

    List<VirtualFS> vfsApproachable = getApproachableVFS(user);
    if (vfsApproachable.isEmpty()) {
      String errorMsg = String.format("Surl %s is not approachable by user %s", surl, user);
      log.debug(errorMsg);
      throw new UnapprochableSurlException(errorMsg);
    }

    MappingRule winnerRule = getWinnerRule(surl, getAllDefinedMappingRules(), vfsApproachable);
    log.debug("For surl {} the winner rule is {}", surl, winnerRule.getRuleName());

    return winnerRule.getMappedFS();
  }

  public StoRI resolveStoRIbyAbsolutePath(String absolutePath, GridUserInterface user)
      throws NamespaceException {

    return resolveStoRIbyAbsolutePath(absolutePath);
  }

  public StoRI resolveStoRIbyAbsolutePath(String absolutePath) throws NamespaceException {

    VirtualFS vfs = resolveVFSbyAbsolutePath(absolutePath);
    log.debug("VFS retrivied is {}", vfs.getAliasName());
    log.debug("VFS instance is {}", vfs.hashCode());
    return resolveStoRIbyAbsolutePath(absolutePath, vfs);
  }

  public StoRI resolveStoRIbyAbsolutePath(String absolutePath, VirtualFS vfs)
      throws NamespaceException {

    String relativePath = NamespaceUtil.extractRelativePath(vfs.getRootPath(), absolutePath);
    return vfs.createFile(relativePath);
  }

  public VirtualFS resolveVFSbyAbsolutePath(String absolutePath, GridUserInterface user)
      throws NamespaceException {

    /**
     * @todo Check the approachable rules
     */
    return getWinnerVFS(absolutePath, parser.getMapVFS_Root());
  }

  /**
   * Method used by srmGetSpaceMetadata
   * 
   * @param absolutePath String
   * @return VirtualFS
   * @throws NamespaceException
   */
  public VirtualFS resolveVFSbyRoot(String absolutePath) throws NamespaceException {

    return getWinnerVFS(absolutePath, parser.getMapVFS_Root());
  }

  public VirtualFS resolveVFSbyAbsolutePath(String absolutePath) throws NamespaceException {

    return getWinnerVFS(absolutePath, parser.getMapVFS_Root());
  }

  public VirtualFS resolveVFSbyLocalFile(LocalFile file) throws NamespaceException {

    try {
      return this.resolveVFSbyAbsolutePath(file.getCanonicalPath());
    } catch (IOException e) {
      throw new NamespaceException(e.getMessage(), e);
    }
  }

  public StoRI resolveStoRIbyPFN(PFN pfn) throws NamespaceException {

    /**
     * @todo Check the approachable rules
     */
    VirtualFS vfs = resolveVFSbyPFN(pfn);
    String vfsRoot = vfs.getRootPath();
    String relativePath = NamespaceUtil.extractRelativePath(vfsRoot, pfn.getValue());
    return vfs.createFile(relativePath);
  }

  /**
   * method used by GetSpaceMetaData Executor to retrieve the VFS and Quota Parameters.
   * 
   * @param pfn PFN
   * @return VirtualFS
   * @throws NamespaceException
   */
  public VirtualFS resolveVFSbyPFN(PFN pfn) throws NamespaceException {

    return getWinnerVFS(pfn.getValue(), parser.getMapVFS_Root());
  }

  public StoRI getDefaultSpaceFileForUser(GridUserInterface user) throws NamespaceException {

    return null;
  }

  public Space retrieveSpaceByToken(TSizeInBytes totSize, TSpaceToken token) {

    return null;
  }

  /***********************************************
   * UTILITY METHODS
   **********************************************/

  public String makeSpaceFileURI(GridUserInterface user) throws NamespaceException {

    String result = null;
    SortedSet<ApproachableRule> appRules = Sets.newTreeSet(getApproachableRules(user));

    log.debug("Compatible Approachable rules: {}", appRules);

    if (appRules.isEmpty()) {
      if (user instanceof AbstractGridUser) {

        log.error("No approachable rules found for user with DN='{}' " + "and VO='{}'",
            user.getDn(), ((AbstractGridUser) user).getVO());

        throw new NamespaceException("No approachable rules found for user with DN='" + user.getDn()
            + "' and VO = '" + ((AbstractGridUser) user).getVO() + "'");
      } else {
        log.error(
            "No approachable rules found for user with DN='{}'. " + "No VOMS extensions found.",
            user.getDn());

        throw new NamespaceException("No approachable rules found for user with DN='" + user.getDn()
            + "' User certificate has not VOMS extension");
      }
    }
    ApproachableRule firstAppRule = appRules.first();
    log.debug("First approachable rule: {}", firstAppRule);

    String spacePath = getRelativePathForSpaceFile(firstAppRule);
    VirtualFS vfs = getApproachableDefaultVFS(firstAppRule);
    log.debug("Default VFS for Space Files: {}", vfs);

    // Build the Space file path
    String rootPath = vfs.getRootPath();
    String spaceFileName = makeSpaceFileNameForUser(user);
    result = rootPath + spacePath + NamingConst.SEPARATOR + spaceFileName;
    return result;
  }

  public String getRelativePathForSpaceFile(ApproachableRule rule) {

    String result = rule.getSpaceRelativePath();
    if (result == null) {
      result = NamingConst.ROOT_PATH;
    }
    return result;
  }

  public String makeSpaceFileNameForUser(GridUserInterface user) {

    String userName = null;
    try {
      userName = user.getLocalUser().getLocalUserName();
    } catch (CannotMapUserException ex) {
      log.error("Cannot map user: {}", ex.getMessage(), ex);
    }
    if (userName == null) {
      userName = "unknown";
    }
    GUID guid = new GUID();
    return userName + SPACE_FILE_NAME_SEPARATOR + guid + SPACE_FILE_NAME_SUFFIX;
  }

  public boolean isSpaceFile(String fileName) {

    Preconditions.checkNotNull(fileName, "Unable to check space file name. Invalid null fileName");

    if (!fileName.endsWith(SPACE_FILE_NAME_SUFFIX))
      return false;
    if (fileName.indexOf(SPACE_FILE_NAME_SEPARATOR) <= 0)
      return false;
    if (fileName.substring(fileName.indexOf(SPACE_FILE_NAME_SEPARATOR) + 1)
      .length() <= SPACE_FILE_NAME_SUFFIX.length())
      return false;
    String uuidString = fileName.substring(fileName.indexOf(SPACE_FILE_NAME_SEPARATOR) + 1,
        fileName.lastIndexOf(SPACE_FILE_NAME_SUFFIX));
    try {
      UUID.fromString(uuidString);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  /**
   * 
   * @param user GridUserInterface
   * @return SortedSet
   */
  public SortedSet<ApproachableRule> getApproachableRules(GridUserInterface user) {

    SortedSet<ApproachableRule> appRules = Sets.newTreeSet();
    Map<String, ApproachableRule> rules = parser.getApproachableRules();

    // Purging incompatible rules from the results
    if (rules != null) {
      Map<Object, ApproachableRule> appRulesUnorderd =
          Maps.newHashMap(parser.getApproachableRules());
      // List the entries
      appRulesUnorderd.keySet().forEach(key -> {
        ApproachableRule appRule = appRulesUnorderd.get(key);
        if (matchSubject(appRule, user)) {
          // Insert into the result (that is an ordered set)
          appRules.add(appRule);
        }
      });
    }
    return appRules;
  }

  public Set<String> getSupportedVOs() {
    Set<String> vos = Sets.newHashSet();
    parser.getApproachableRules().forEach((key, value) -> {
      String voName = value.getSubjectRules().getVONameMatchingRule().getVOName();
      if (!voName.contains("*")) {
        vos.add(voName);
      }
    });
    return vos;
  }

  /**
   * 
   * @param appRule ApproachableRule
   * @return VirtualFS
   */
  public VirtualFS getApproachableDefaultVFS(ApproachableRule appRule) throws NamespaceException {

    VirtualFS defaultVFS = null;
    String defaultVFSName = null;

    List<VirtualFS> listVFS = appRule.getApproachableVFS();
    if (listVFS != null && !listVFS.isEmpty()) {
      log.debug(" VFS List = {}", listVFS);
      // Looking for the default element, signed with a '*' char at the end
      // Various VFS names exists. The default is '*' tagged or the first.
      String vfsName = null;
      for (VirtualFS element : listVFS) {
        if (element.getAliasName().endsWith("*")) {
          vfsName = element.getAliasName().substring(0, element.getAliasName().length() - 1);
          break;
        }
      }
      if (vfsName == null) {
        defaultVFSName = listVFS.get(0).getAliasName();
      } else {
        defaultVFSName = vfsName;
      }
      log.debug(" Default VFS detected : '{}'", defaultVFSName);
      defaultVFS = parser.getVFS(defaultVFSName);
      log.debug(" VFS Description {}", defaultVFS);
      return defaultVFS;
    } else {
      throw new NamespaceException("No VFS associated to the provided ApproachableRule " + appRule);
    }
  }

  private static boolean matchSubject(ApproachableRule approachableRule, GridUserInterface user) {

    boolean result = true;
    result = approachableRule.match(user);
    return result;
  }

  public VirtualFS resolveVFSbySpaceToken(TSpaceToken spaceToken) throws NamespaceException {

    Optional<VirtualFS> vfs =
        getAllDefinedVFS().stream().filter(v -> spaceToken.equals(v.getSpaceToken())).findFirst();
    if (vfs.isPresent()) {
      return vfs.get();
    }
    throw new NamespaceException(
        "Unable to found a VFS compatible with spaceToken :'" + spaceToken + "'");
  }

  public boolean isStfnFittingSomewhere(String surlString, GridUserInterface user)
      throws NamespaceException {

    List<String> stfnRoots = Lists.newArrayList();
    List<VirtualFS> listVFS = getApproachableVFS(user);
    Map<String, MappingRule> rules = Maps.newHashMap(parser.getMappingRules());

    // Retrieve the list of stfnRoot approachable
    for (Map.Entry<String, MappingRule> rule : rules.entrySet()) {
      VirtualFS mappedFS = rule.getValue().getMappedFS();
      if (listVFS.contains(mappedFS)) { // retrieve stfnRoot
        stfnRoots.add(rule.getValue().getStFNRoot());
      }
    }
    log.debug("FITTING: List of StFNRoots approachables = {}", stfnRoots);

    // Build SURL and retrieve the StFN part.
    String stfn = SURL.makeSURLfromString(surlString).getStFN();

    // Path elements of stfn
    List<String> stfnArray = NamespaceUtil.getPathElement(stfn);

    for (String stfnRoot : stfnRoots) {
      log.debug("FITTING: considering StFNRoot = {} agaist StFN = {}", stfnRoot, stfn);
      List<String> stfnRootArray = NamespaceUtil.getPathElement(stfnRoot);
      stfnRootArray.retainAll(stfnArray);
      if (!(stfnRootArray.isEmpty())) {
        return true;
      }
    }
    return false;
  }

  public List<VirtualFS> getVFSWithQuotaEnabled() {

    List<VirtualFS> vfsSet = getAllDefinedVFS();
    log.debug("Found '{}' VFS defined in Namespace.xml", vfsSet.size());
    List<VirtualFS> vfsSetQuota =
        vfsSet.stream().filter(vfs -> isGPFSQuotaEnabled(vfs)).collect(Collectors.toList());
    log.debug("Number of VFS with Quota enabled: {}", vfsSetQuota.size());
    return vfsSetQuota;
  }

  private boolean isGPFSQuotaEnabled(VirtualFS vfs) {

    Preconditions.checkNotNull(vfs, "vfsItem must not be null!");

    String fsType = vfs.getFSType();
    if (fsType == null) {
      log.debug("isGPFSQuotaEnabled: fsType is null!");
      return false;
    }
    if (!fsType.trim().equalsIgnoreCase("gpfs")) {
      log.debug("isGPFSQuotaEnabled: fsType {} is not gpfs, exiting...", fsType);
      return false;
    }
    CapabilityInterface cap = vfs.getCapabilities();
    if (cap == null) {
      log.debug("isGPFSQuotaEnabled: fs capabilities are null!");
      return false;
    }
    Quota quota = cap.getQuota();
    if (quota == null) {
      log.debug("isGPFSQuotaEnabled: quota is null!");
      return false;
    }
    return (quota.getDefined() && quota.getEnabled());

  }

  public Set<Authority> getManagedEndpoints(Protocol protocol) {

    Set<Authority> endpoints = Sets.newHashSet();
    this.getAllDefinedVFS().forEach(vfs -> {
      vfs.getCapabilities().getManagedProtocolByScheme(protocol).forEach(tp -> {
        endpoints.add(tp.getAuthority());
      });
    });
    return endpoints;
  }
}

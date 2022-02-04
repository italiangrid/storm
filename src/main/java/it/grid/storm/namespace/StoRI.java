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

import static org.apache.commons.lang.StringUtils.join;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.balancer.BalancingStrategy;
import it.grid.storm.balancer.BalancingStrategyException;
import it.grid.storm.balancer.Node;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.StFN;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.FilesystemIF;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.ReservationException;
import it.grid.storm.filesystem.Space;
import it.grid.storm.filesystem.SpaceSystem;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.namespace.model.Capability;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.PathCreator;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.namespace.model.TransportProtocol;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.namespace.naming.NamingConst;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;

public class StoRI {

  private Logger log = LoggerFactory.getLogger(StoRI.class);

  private final Configuration config = Configuration.getInstance();

  private TSURL surl;
  private PFN pfn;
  private Capability.ACLMode aclMode = Capability.ACLMode.UNDEF;
  private TLifeTimeInSeconds lifetime = null;
  private Date startTime = null;
  private LocalFile localFile = null;
  private Space space;

  private VirtualFS vfs;
  private FilesystemIF fs;
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

  public StoRI(VirtualFS vfs, MappingRule winnerRule, String relativeStFN,
      StoRIType type) {

    if (vfs != null) {
      this.vfs = vfs;
      capability = (Capability) vfs.getCapabilities();
    } else {
      log.error("StoRI built without VFS!");
    }

    if (winnerRule != null) {
      stfnRoot = winnerRule.getStFNRoot();
      stfn = stfnRoot + NamingConst.SEPARATOR + relativeStFN;

      vfsRoot = vfs.getRootPath();

      this.relativeStFN = relativeStFN;

      stfnPath = NamespaceUtil.getStFNPath(stfn);

      relativePath = NamespaceUtil.consumeFileName(relativeStFN);

      if (relativePath != null) {
        if (relativePath.startsWith(NamingConst.SEPARATOR)) {
          relativePath = relativePath.substring(1);
        }
      } else {
        relativePath = "/";
      }

      fileName = NamespaceUtil.getFileName(relativeStFN);
      log.debug("StFN Filename : {} [StFN = '{}']", fileName, relativeStFN);

      if (type == null) {
        if (relativeStFN.endsWith(NamingConst.SEPARATOR)) {
          type = StoRIType.FOLDER;
        } else {
          type = StoRIType.UNKNOWN;
        }
      } else {
        this.type = type;
      }

    } else {
      log.warn("StoRI built without mapping rule");
    }
  }

  public StoRI(VirtualFS vfs, String stfnStr, TLifeTimeInSeconds lifetime,
      StoRIType type) {

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
    log.debug("StFN Filename : {} [StFN = '{}']", fileName, stfnStr);

    this.stfnPath = NamespaceUtil.getStFNPath(stfnStr);
    log.debug("StFN StFNPath : {} [StFN = '{}']", stfnPath, stfnStr);

  }

  public void allotSpaceByToken(TSpaceToken token)
      throws ReservationException, ExpiredSpaceTokenException {

    // Retrieve SpaceSystem Driver
    if (spaceDriver == null) {
      try {
        this.spaceDriver = vfs.getSpaceSystemDriverInstance();
      } catch (NamespaceException e) {
        log.error(e.getMessage(), e);
        throw new ReservationException("Error while retrieving Space System Driver for VFS", e);
      }
    }

    try {
      vfs.useAllSpaceForFile(token, this);
    } catch (NamespaceException e) {
      log.error("Error using space token {} for file {}: {}", token, fileName, e.getMessage(), e);
      throw new ReservationException(e.getMessage(), e);
    }

  }

  public void allotSpaceByToken(TSpaceToken token, TSizeInBytes totSize)
      throws ReservationException, ExpiredSpaceTokenException {

    if (spaceDriver == null) {
      try {
        this.spaceDriver = vfs.getSpaceSystemDriverInstance();
      } catch (NamespaceException e) {
        log.error(e.getMessage(), e);
        throw new ReservationException("Error while retrieving Space System Driver for VFS", e);
      }
    }

    try {
      vfs.useSpaceForFile(token, this, totSize);
    } catch (NamespaceException e) {
      log.error("Error using space token {} for file {}: {}", token, fileName, e.getMessage(), e);
      throw new ReservationException(e.getMessage(), e);
    }

  }

  public void allotSpaceForFile(TSizeInBytes totSize) throws ReservationException {

    if (spaceDriver == null) {
      try {
        this.spaceDriver = vfs.getSpaceSystemDriverInstance();
      } catch (NamespaceException e) {
        log.error("Error while retrieving Space System Driver for VFS {}", e.getMessage(), e);

        throw new ReservationException("Error while retrieving Space System Driver for VFS", e);
      }
    }

    try {
      vfs.makeSilhouetteForFile(this, totSize);
    } catch (NamespaceException e) {
      log.error(e.getMessage(), e);
      throw new ReservationException(
          "Error while constructing 'Space Silhouette' for " + this.fileName, e);
    }

    log.debug("Space built. Space " + this.getSpace().getSpaceFile().getPath());
    this.getSpace().allot();
  }

  public String getAbsolutePath() {
    return vfs.getRootPath() + NamingConst.SEPARATOR + relativeStFN;
  }

  public TLifeTimeInSeconds getFileLifeTime() {
    if (!(volatileInformationAreSet)) {
      setVolatileInformation();
    }
    return lifetime;
  }

  public String getFilename() {

    return this.fileName;
  }

  public Date getFileStartTime() {

    if (!(volatileInformationAreSet)) {
      setVolatileInformation();
    }
    return startTime;
  }

  public ArrayList<StoRI> getChildren(TDirOption dirOption)
      throws InvalidDescendantsEmptyRequestException, InvalidDescendantsPathRequestException,
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
      PathCreator pCreator = new PathCreator(fileHandle, dirOption.isAllLevelRecursive(), 1);
      Collection<String> pathList = pCreator.generateChildren();
      if (pathList.size() == 0) {
        log.debug("SURL point to an EMPTY DIRECTORY");
        throw new InvalidDescendantsEmptyRequestException(fileHandle, pathList);
      } else { // Creation of StoRI LIST
        Namespace namespace = Namespace.getInstance();
        for (String childPath : pathList) {
          log.debug("<GetChildren>:Creation of new StoRI with path: {}", childPath);
          try {

            StoRI childStorI = namespace.resolveStoRIbyAbsolutePath(childPath, vfs);
            childStorI.setMappingRule(getMappingRule());

            stoRIList.add(childStorI);
          } catch (NamespaceException ex) {
            log.error("Error occurred while resolving StoRI by absolute path", ex);
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
      createdStoRI = new StoRI(this.vfs, this.winnerRule, consumeElements, StoRIType.FOLDER);
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
      } catch (InvalidPFNAttributeException e) {
        log.error(e.getMessage(), e);
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
     * The String passed to TSURL.makeFromString MUST contains a valid TSURL in string format, not
     * only relativePath.
     */
    if (this.surl == null) {
      try {
        this.surl = TSURL.makeFromStringValidate(buildSURLString());
      } catch (Throwable e) {
        log.error("Unable to build the SURL with relative path: {}. {}", relativePath,
            e.getMessage(), e);
      }
    }
    return surl;
  }

  public TTURL getTURL(TURLPrefix desiredProtocols)
      throws IllegalArgumentException, InvalidGetTURLProtocolException, TURLBuildingException {

    TTURL resultTURL = null;

    if (desiredProtocols == null || desiredProtocols.size() == 0) {
      log.error("<GetTURL> request with NULL or empty prefixOfAcceptedTransferProtocol!");
      throw new IllegalArgumentException(
          "unable to build the TTURL, invalid arguments: desiredProtocols=" + desiredProtocols);
    } else {

      // Within the request there are some protocol preferences
      // Calculate the intersection between Desired Protocols and Available
      // Protocols
      List<Protocol> desiredP = new ArrayList<>(desiredProtocols.getDesiredProtocols());
      List<Protocol> availableP = new ArrayList<>(capability.getAllManagedProtocols());
      desiredP.retainAll(availableP);

      if (desiredP.isEmpty()) {
        String msg =
            String.format("None of [%s] protocols matches the available " + "protocols [%s]",
                join(desiredP, ','), join(availableP, ','));
        log.error(msg);
        throw new InvalidGetTURLProtocolException(msg);

      } else {

        log.debug("Protocol matching.. Intersection size: {}", desiredP.size());

        Protocol choosen = null;
        Authority authority = null;
        int index = 0;
        boolean turlBuilt = false;
        while (!turlBuilt && index < desiredP.size()) {
          choosen = desiredP.get(index);
          authority = null;
          log.debug("Selected Protocol: {}", choosen);
          if (capability.isPooledProtocol(choosen)) {
            log.debug("The protocol selected is in POOL Configuration");
            try {
              authority = getPooledAuthority(choosen);
            } catch (BalancingStrategyException e) {
              log.warn(
                  "Unable to get the pool member to be used to build the turl. BalancerException :  {}",
                  e.getMessage());
              index++;
              continue;
            }
          } else {
            log.debug("The protocol selected is in NON-POOL Configuration");
            TransportProtocol transProt = null;
            List<TransportProtocol> protList = capability.getManagedProtocolByScheme(choosen);
            if (protList.size() > 1) { // Strange case
              log.warn("More than one protocol {}"
                  + " defined but NOT in POOL Configuration. Taking the first one.", choosen);
            }
            transProt = protList.get(0);
            authority = transProt.getAuthority();
          }

          if (choosen.equals(Protocol.HTTP) || choosen.equals(Protocol.HTTPS)) {
            resultTURL = buildHTTPTURL(choosen, authority);
          } else {
            resultTURL = buildTURL(choosen, authority);
          }

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

  public VirtualFS getVirtualFileSystem() {
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

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

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
    sb.append(" stori.mappingRule    : " + this.getMappingRule() + "\n");

    return sb.toString();
  }

  private String buildSURLString() throws NamespaceException, UnknownHostException {
    String stfn = stfnRoot + NamingConst.SEPARATOR + relativeStFN;
    SURL surl = new SURL(config.getSrmServiceHostname(), config.getSrmServicePort(), stfn);
    return surl.toString();
  }

  private TTURL buildHTTPTURL(Protocol p, Authority authority) {

    String prefix = Configuration.getInstance().getHTTPTURLPrefix();
    StringBuilder sb = new StringBuilder();
    sb.append(p.getProtocolPrefix());
    sb.append(authority);

    if (prefix != null) {
      sb.append(prefix);
    }

    sb.append(getStFN().toString());

    log.debug("built http turl: {}", sb.toString());

    return TTURL.makeFromString(sb.toString());

  }

  private TTURL buildTURL(Protocol protocol, Authority authority)
      throws InvalidProtocolForTURLException {

    TTURL result = null;

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
      case 8:
        result = TURLBuilder.buildXROOTTURL(authority, this.getPFN());
        break; // XROOT Protocol
      default:
        // Unknown protocol
        throw new InvalidProtocolForTURLException(protocol.getSchema());
    }
    return result;
  }

  /**
   * @param pooledProtocol
   * @return
   * @throws BalancerException
   */
  private Authority getPooledAuthority(Protocol pooledProtocol) throws BalancingStrategyException {

    Authority authority = null;
    if (pooledProtocol.equals(Protocol.GSIFTP) || pooledProtocol.equals(Protocol.HTTP)
        || pooledProtocol.equals(Protocol.HTTPS)) {
      BalancingStrategy<? extends Node> bal = vfs.getProtocolBalancingStrategy(pooledProtocol);
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
   * Set "lifetime" and "startTime" information. The corresponding values are retrieved from the DB.
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

  public StFN getStFNFromMappingRule() {
    try {

      if (getMappingRule() == null) {
        log.warn("Mapping rule is null for this StorI. " + "Falling back to VFS StFN.");
        return getStFN();
      }

      String mappingRuleRoot = getMappingRule().getStFNRoot();
      String mappedStfn = mappingRuleRoot + NamingConst.SEPARATOR + relativeStFN;

      return StFN.make(mappedStfn);

    } catch (InvalidStFNAttributeException e) {

      log.error("Error building StFN from mapping rule. Reason: {}", e.getMessage(), e);

      log.error("Falling back to VFS StFN.");

      return getStFN();

    }
  }

}

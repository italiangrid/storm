/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.namespace;

import it.grid.storm.balancer.Balancer;
import it.grid.storm.balancer.Node;
import it.grid.storm.balancer.ftp.FTPNode;
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
import it.grid.storm.griduser.GridUserInterface;
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
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class StoRIImpl
implements StoRI {

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



    // private Vector transferProtocolManaged = null;

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
     *  BUILDING METHODs
     ****************************************************************************/


    public StoRIImpl(VirtualFSInterface vfs, MappingRule winnerRule, String relativeStFN, StoRIType type) {
        if (vfs != null) {
            this.vfs = vfs;
            try {
                this.capability = (Capability) vfs.getCapabilities();
            }
            catch (NamespaceException ex) {
                log.error("Unable to retrieve Capability element of VFS :" + getVFSName() + " EXCEP:" + ex);
            }
        }
        else {
            log.error("!!! StoRI built without VFS!!?!");
        }
        /**
         * Retrieve from StFN the various part.
         */
        if (winnerRule != null) {
            this.stfnRoot = winnerRule.getStFNRoot();
            this.stfn = stfnRoot + NamingConst.SEPARATOR + relativeStFN;
            try {
                this.vfsRoot = vfs.getRootPath();
            }
            catch (NamespaceException ex) {
                log.error("VFS is not setted.", ex);
            }
            this.relativeStFN = relativeStFN;

            this.stfnPath = NamespaceUtil.getStFNPath(stfn);

            this.relativePath = NamespaceUtil.consumeFileName(relativeStFN);

            if (relativePath != null) {
                if (relativePath.startsWith(NamingConst.SEPARATOR)) {
                    this.relativePath = relativePath.substring(1);
                }
            }
            else {
                this.relativePath = "/";
            }

            this.fileName = NamespaceUtil.getFileName(relativeStFN);
            log.debug("StFN Filename : " + fileName + " [StFN = '" + relativeStFN + "']");

            if (type == null) {
                if (relativeStFN.endsWith(NamingConst.SEPARATOR)) {
                    this.type = StoRIType.FOLDER;
                }
                else {
                    this.type = StoRIType.UNKNOWN;
                }
            }
            else {
                this.type = type;
            }

        }
        else {
            log.warn("StoRI built without MAPPIG RULE!!");
        }
        /**
             log.debug(" ..............................");
             log.debug("StFN : "+stfn+" [StFN relative = '"+relativeStFN+"']");
             log.debug("vfs root : "+vfsRoot+" [StFN relative= '"+relativeStFN+"']");
             log.debug("relative StFN : "+relativeStFN+" [StFN relative = '"+relativeStFN+"']");
             log.debug("relative Path : "+relativePath+" [StFN relative= '"+relativeStFN+"']");
             log.debug("filename : "+fileName+" [StFN relative = '"+relativeStFN+"']");
             log.debug("stfn Path : "+stfnPath+" [StFN relative= '"+relativeStFN+"']");
             log.debug("stfn Root : "+stfnRoot+" [StFN relative= '"+relativeStFN+"']");
             log.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
         **/
    }

    public StoRIImpl(VirtualFSInterface vfs, String stfnStr, TLifeTimeInSeconds lifetime, StoRIType type) {
        this.vfs = vfs;
        try {
            this.capability = (Capability) vfs.getCapabilities();
        }
        catch (NamespaceException ex) {
            log.error("Unable to retrieve Capability element of VFS :" + getVFSName() + " EXCEP:" + ex);
        }
        //Relative path has to be a path in a relative form! (without "/" at begins)
        if (relativePath != null) {
            if (relativePath.startsWith(NamingConst.SEPARATOR)) {
                this.relativePath = relativePath.substring(1);
            }
        }
        else {
            this.relativePath = "/";
        }

        this.lifetime = lifetime;

        if (type == null) {
            this.type = StoRIType.UNKNOWN;
        }
        else {
            this.type = type;
        }

        this.stfnRoot = null;

        this.fileName = NamespaceUtil.getFileName(stfnStr);
        log.debug("StFN Filename : " + fileName + " [StFN = '" + stfnStr + "']");

        this.stfnPath = NamespaceUtil.getStFNPath(stfnStr);
        log.debug("StFN StFNPath : " + stfnPath + " [StFN = '" + stfnStr + "']");

    }


    public void allotSpaceByToken(TSpaceToken token) throws ReservationException, ExpiredSpaceTokenException {

        LocalFile localfile = this.getLocalFile();

        //Retrieve SpaceSystem Driver
        if (spaceDriver == null) {
            try {
                this.spaceDriver = vfs.getSpaceSystemDriverInstance();
            }
            catch (NamespaceException ex) {
                log.error("Error while retrieving Space System Driver for VFS ", ex);
                throw new ReservationException("Error while retrieving Space System Driver for VFS ");
            }
        }

        try {
            vfs.useAllSpaceForFile(token, this);
        }
        catch (NamespaceException ex1) {
            log.error("Error while using Space with token '" + token + "' for " + this.fileName, ex1);
            throw new ReservationException("Error while using Space with token '" + token + "' for " + this.fileName);
        }

        //this.getSpace().allot();

    }

    public void allotSpaceByToken(TSpaceToken token, TSizeInBytes totSize) throws ReservationException, ExpiredSpaceTokenException {

        LocalFile localfile = this.getLocalFile();

        //Retrieve SpaceSystem Driver
        if (spaceDriver == null) {
            try {
                this.spaceDriver = vfs.getSpaceSystemDriverInstance();
            }
            catch (NamespaceException ex) {
                log.error("Error while retrieving Space System Driver for VFS ", ex);
                throw new ReservationException("Error while retrieving Space System Driver for VFS ");
            }
        }

        try {
            vfs.useSpaceForFile(token, this, totSize);
        }
        catch (NamespaceException ex1) {
            log.error("Error while using Space with token '" + token + "' for " + this.fileName, ex1);
            throw new ReservationException("Error while using Space with token '" + token + "' for " + this.fileName);
        }

        //this.getSpace().allot();

    }

    public void allotSpaceForFile(TSizeInBytes totSize) throws ReservationException {

        if (spaceDriver == null) {
            try {
                this.spaceDriver = vfs.getSpaceSystemDriverInstance();
            }
            catch (NamespaceException ex) {
                log.error("Error while retrieving Space System Driver for VFS ", ex);
                throw new ReservationException("Error while retrieving Space System Driver for VFS ");
            }
        }

        //Make SILHOUETTE for File
        try {
            vfs.makeSilhouetteForFile(this, totSize);
        }
        catch (NamespaceException ex1) {
            log.error("Error while constructing 'Space Silhouette' for " + this.fileName, ex1);
            throw new ReservationException("Error while constructing 'Space Silhouette' for " + this.fileName);
        }

        log.debug("Space built. Space " + this.getSpace().getSpaceFile().getPath());

        //Make "space" physically in underlying file system
        this.getSpace().allot();

    }

    public String getAbsolutePath() {
        try {
            return vfs.getRootPath() + NamingConst.SEPARATOR + relativeStFN;
        }
        catch (NamespaceException ex) {
            log.error("VFS Root path error", ex);
            return null;
        }
    }


    /*****************************************************************************
     *  BUSINESS METHODs
     ****************************************************************************/

    @Deprecated
    public ArrayList<StoRI> getChildren(GridUserInterface gUser, TDirOption dirOption) throws
    InvalidDescendantsEmptyRequestException, InvalidDescendantsAuthRequestException,
    InvalidDescendantsPathRequestException, InvalidDescendantsFileRequestException, InvalidDescendantsTDirOptionRequestException {

        log.error("METHOD DEPRECATED! : getChildren (GridUser, DirOption); use without GridUser");
        return generateChildrenNoFolders(dirOption);
    }

    public ArrayList<StoRI> generateChildrenNoFolders(TDirOption dirOption) throws InvalidDescendantsEmptyRequestException,
            InvalidDescendantsAuthRequestException, InvalidDescendantsPathRequestException,
            InvalidDescendantsFileRequestException, InvalidDescendantsTDirOptionRequestException {

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
            // Create ArrayList containing all Valid fileName path found in PFN of StoRI's SURL
        	if(!dirOption.isAllLevelRecursive() && !(dirOption.getNumLevel() > 0))
        	{
        		log.debug("Requested to list the content of a folder without the folder " +
        				"itself specifying to not descend recursively in the directory (all" +
        				"Recursive is false and levelRecursive is 0)...nonsense!");
                throw new InvalidDescendantsTDirOptionRequestException(fileHandle, dirOption);
        	}
            PathCreator pCreator = new PathCreator(fileHandle,
                                                   dirOption.isAllLevelRecursive(),
                                                   dirOption.getNumLevel());
            Collection<String> pathList = pCreator.generateChildrenNoFolders();
            
            if (pathList.size() == 0) {
                
                log.debug("SURL point to an EMPTY DIRECTORY");
                throw new InvalidDescendantsEmptyRequestException(fileHandle, pathList);
                
            } else { // Creation of StoRI LIST
                
                NamespaceInterface namespace = NamespaceDirector.getNamespace();
                StoRI createdStoRI = null;
                
                for (String childPath : pathList) {
                    log.debug("<GetChildren>:Creation of new StoRI with path : " + childPath);
                    try {
                        createdStoRI = namespace.resolveStoRIbyAbsolutePath(childPath);
                        stoRIList.add(createdStoRI);
                    } catch (NamespaceException ex) {
                        log.error("Error occurred while resolving StoRI by absolute path", ex);
                    }
                }
            }
        }
        return stoRIList;
    }


    /**
     * Returns the SURL lifetime. This method queries the DB and retrieves also the startTime.
     * The DB is queried only on the first invocation of this or the getFileStartTime() methods,
     * therefore subsequent invocations of these two methods are computationally lighter.
     *
     * If the file is PERMANENT, or this StoRI refeers to a non-valid file then -1 is returned.
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
     * Returns the SURL start time (time from which starts the lifetime). This method queries the DB
     * and retrieves also the lifetime of the SURL.
     * The DB is queried only on the first invocation of this or the getFileLifeTime() methods,
     * therefore subsequent invocations of these two methods are computationally lighter.
     *
     * If the file is permanent or this StoRI refeers to a non-valid file then NULL is retuned!
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
            }
            else {
                log.warn("SURL does not exists!");
                throw new InvalidDescendantsPathRequestException(fileHandle);
            }
        }
        else { //SURL point to an existent directory.
            //Create ArrayList containing all Valid fileName path found in PFN of StoRI's SURL
            PathCreator pCreator = new PathCreator(fileHandle, dirOption.isAllLevelRecursive(), 1);
            Collection<String> pathList = pCreator.generateChildren();
            if (pathList.size() == 0) {
                log.debug("SURL point to an EMPTY DIRECTORY");
                throw new InvalidDescendantsEmptyRequestException(fileHandle, pathList);
            }
            else { //Creation of StoRI LIST
                NamespaceInterface namespace = NamespaceDirector.getNamespace();
                StoRI createdStoRI = null;
                for (String childPath : pathList) {
                    log.debug("<GetChildren>:Creation of new StoRI with path : " + childPath);
                    try {
                        createdStoRI = namespace.resolveStoRIbyAbsolutePath(childPath);
                        stoRIList.add(createdStoRI);
                    }
                    catch (NamespaceException ex) {
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
            }
            catch (NamespaceException ex) {
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
            createdStoRI = new StoRIImpl(this.vfs, this.winnerRule, consumeElements, StoRIType.FOLDER);
            parentList.add(createdStoRI);
            consumed = NamespaceUtil.consumeElement(consumeElements);
            if (consumed.equals(consumeElements)) {
                lastElements = true;
            }
            else {
                consumeElements = consumed;
            }
        }
        while ( (!lastElements));

        return parentList;
    }

    public PFN getPFN() {
        if (pfn == null) {
            try {
                this.pfn = PFN.make(getAbsolutePath());
            }
            catch (InvalidPFNAttributeException ex) {
                log.error("Unable to build the PFN in the VFS '" + getVFSName() + "' with this path :'" +
                        getAbsolutePath() +
                "'");
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
         *  The String passed to TSURL.makeFromString MUST contains
         *  a valid TSURL in string format, not only relativePath.
         */
        if (this.surl == null) {
            try {
                this.surl = TSURL.makeFromStringValidate(buildSURLString());
            }
            catch (InvalidTSURLAttributesException ex) {
                log.error("Unable to build the SURL with relative path : '" + relativePath + "'", ex);
            }
            catch (NamespaceException ex) {
                /** @todo Handle this exception */
                log.error("Unable to build the SURL with relative path : '" + relativePath + "'", ex);
            }

        }
        return surl;
    }

    /*****************************************************************************
     *  READ METHODs
     * @throws InvalidGetTURLProtocolException
     ***************************************************************************/


    public TTURL getTURL(TURLPrefix desiredProtocols) throws InvalidGetTURLNullPrefixAttributeException, InvalidGetTURLProtocolException {

        //TransportProtocol protocolPrefix = null;
        TTURL resultTURL = null;

        if (desiredProtocols == null) {
            log.error("<GetTURL> request with NULL prefixOfAcceptedTransferProtocol!");
            throw new InvalidGetTURLNullPrefixAttributeException(desiredProtocols);
        }
        else {

            /**
             * Retrieve Protocol to build the TURL
             */
            if ( (desiredProtocols.size() == 0)) {
                log.debug("<GetTURL> No matching transfer protocol Found! Returnig Error");
                //Creation TURL with DEFAULT Transport Prefix
                //protocolPrefix = getDefaultTransferProtocol();
                //Change here
                throw new InvalidGetTURLProtocolException(desiredProtocols);
            }
            else { //Within the request there are some protocol preferences
                //Calculate the intersection between Desired Protocols and Available Protocols
                ArrayList<Protocol> desiredP = new ArrayList<Protocol>(desiredProtocols.getDesiredProtocols());
                ArrayList<Protocol> availableP = new ArrayList<Protocol> (this.capability.getAllManagedProtocols());
                desiredP.retainAll(availableP);
                if (desiredP.isEmpty()) {
                    //No match found!
                    log.error("stori:No match with Protocol Preferences and Protocol Managed!");
                    throw new InvalidGetTURLProtocolException(desiredProtocols);
                } else {
                    log.debug("Protocol matching.. Intersection size:"+desiredP.size());
                    Protocol firstMatch = desiredP.get(0);
                    log.debug("Selected Protocol (the first) :"+firstMatch);
                    boolean pooledProtocol = capability.isPooledProtocol(firstMatch);
                    Authority authority = null;
                    if (pooledProtocol) { //POOLED PROTOCOL
                        log.debug("The protocol selected is in POOL Configuration");
                        authority = getPooledAuthority(firstMatch);
                    } else { //SINGLE PROTOCOL
                        log.debug("The protocol selected is in NON-POOL Configuration");
                        TransportProtocol transProt = null;
                        List<TransportProtocol> protList = capability.getManagedProtocolByScheme(firstMatch);
                        if (protList.size()>1) { //Strange case
                            log.warn("More than one protocol "+firstMatch+" defined but NOT in POOL Configuration. Taking the first one.");
                        }
                        transProt = protList.get(0);
                        authority = transProt.getAuthority();
                    }
                    resultTURL = buildTURL(firstMatch, authority, getPFN());
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
            try {
                this.aclMode = vfs.getCapabilities().getACLMode();
            }
            catch (NamespaceException ex) {
                log.error("Error while retrieving ACL Mode for SURL : '" + getSURL() + "'", ex);
            }
        }
        if (aclMode.equals(Capability.ACLMode.JUST_IN_TIME)) {
            result = true;
        }
        else {
            result = false;
        }

        return result;
    }

    public void setGroupTapeRead() {
        
        String groupName = Configuration.getInstance().getGroupTapeReadBuffer(); 
        boolean isGroupDefined = LocalGroups.isGroupDefined(groupName);
        if (isGroupDefined) {
            LocalFile localFile = getLocalFile();
            try {
                localFile.setGroupOwnership(groupName);
            } catch (FSException e) {
                log.warn("Unable to change in the new group owner ('"+groupName+"') of the file: "+localFile.getAbsolutePath());             
            }    
        } else {
            log.warn("The group for Read buffer in Tape support '"+groupName+"' is not defined.");
        }
         
    }
    
    public void setGroupTapeWrite() {
        
        String groupName = Configuration.getInstance().getGroupTapeWriteBuffer();
        boolean isGroupDefined = LocalGroups.isGroupDefined(groupName);
        if (isGroupDefined) {
            LocalFile localFile = getLocalFile();       
            try {
                localFile.setGroupOwnership(groupName);
            } catch (FSException e) {
                log.warn("Unable to change in the new group owner ('"+groupName+"') of the file: "+localFile.getAbsolutePath());             
            }   
        } else {
            log.warn("The group for Write buffer in Tape support '"+groupName+"' is not defined.");
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
     *              UTILITY METHODS
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

    private TTURL buildTURL(Protocol protocol, Authority authority, PFN physicalFN) throws InvalidProtocolForTURLException {
        TTURL result = null;
        switch (protocol.getProtocolIndex())  {
        case 0 :  throw new InvalidProtocolForTURLException(protocol.getSchema()); //EMPTY Protocol
        case 1 :  result = TURLBuilder.buildFileTURL(authority,physicalFN); break; //FILE Protocol
        case 2 :  result = TURLBuilder.buildGsiftpTURL(authority,physicalFN); break; //GSIFTP Protocol
        case 3 :  result = TURLBuilder.buildRFIOTURL(authority,physicalFN); break; //RFIO Protocol
        case 4 :  throw new InvalidProtocolForTURLException(protocol.getSchema()); //SRM Protocol
        case 5 :  result = TURLBuilder.buildROOTTURL(authority,physicalFN); break; //ROOT Protocol
        default : throw new InvalidProtocolForTURLException(protocol.getSchema()); //UNKNOWN Protocol
        }
        return result;
    }



    private Authority getPooledAuthority(Protocol pooledProtocol) {
        Authority authority = null;
        try {
            if (vfs.getProtocolBalancer(pooledProtocol) != null) {
                Balancer<? extends Node> bal = vfs.getProtocolBalancer(pooledProtocol);
                if (pooledProtocol.equals(Protocol.GSIFTP)) {
                    FTPNode node = (FTPNode) bal.getNextElement();
                    authority = new Authority(node.getHostName(), node.getPort());
                } else {
                    log.error("Unable to manage pool with protocol different from GSIFTP.");
                }
            }
        } catch (NamespaceException e) {
            log.error("Error getting the protocol balancer.");
        }
        return authority;
    }

    private String getVFSName() {
        String result = "UNDEF";
        if (vfs != null) {
            try {
                result = vfs.getAliasName();
            }
            catch (NamespaceException ex) {
                log.error("Unable to retrieve the VFS name!!", ex);
            }
        }
        return result;
    }


    private TransportProtocol retrieveTrasferProtocolByProtocolScheme(Protocol protocol) {
        TransportProtocol result = null;
        Capability capability = null;
        //Retrieve CAPABILITY Element

        //Retrieve LIST of TRANSFER PROTOCOL
        List<TransportProtocol> listTransProt = capability.getManagedProtocolByScheme(protocol);
        if (listTransProt.isEmpty()) {
            log.error("ERROR: protocol with protocol "+protocol+" is not supported in the VFS :"+getVFSName());
        } else {
            //Take the first element of the list
            result = listTransProt.get(0);
            if (listTransProt.size()>1) {
                log.warn("ATTENTION: Pool managed as a single element!");
            }
        }
        return result;
    }


    /**
     * Set "lifetime" and "startTime" information. The corresponding values are retrieved from the DB.
     */
    private void setVolatileInformation() {
        VolatileAndJiTCatalog catalog = VolatileAndJiTCatalog.getInstance();
        List volatileInfo = catalog.volatileInfoOn(getPFN());
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

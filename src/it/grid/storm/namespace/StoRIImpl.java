package it.grid.storm.namespace;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import it.grid.storm.catalogs.*;
import it.grid.storm.common.types.*;
import it.grid.storm.filesystem.*;
import it.grid.storm.griduser.*;
import it.grid.storm.namespace.model.*;
import it.grid.storm.namespace.naming.*;
import it.grid.storm.srm.types.*;

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

    private Log log = NamespaceDirector.getLogger();

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

    private Vector transferProtocolManaged = null;

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
        }
        else {
            log.fatal("!!! StoRI built without VFS!!?!");
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
                log.fatal("VFS is not setted.");
            }
            this.relativeStFN = relativeStFN;

            this.stfnPath = NamespaceUtil.getStFNPath(stfn);

            this.relativePath = NamespaceUtil.consumeFileName(relativeStFN);

            if (relativePath != null) {
                if (relativePath.startsWith(NamingConst.SEPARATOR)) {
                    this.relativePath = relativePath.substring(1);
                }
                else {
                    this.relativePath = relativePath;
                }
            }
            else {
                this.relativePath = relativePath;
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
            /**
                     //retrieve the Mapping rule that is winner
                     log.debug("No winner rule passed to StoRI constructor.");
                     try {
             log.debug(" VFS passed is " + vfs.getAliasName() + " holds " + vfs.getMappingRules().size() + " mapping rules.");
                String stfnPath = NamespaceUtil.getStFNPath(relativeStFN);
                winnerRule = NamespaceDirector.getNamespace().getWinnerRule(stfnPath);
                //Check on winner Rule
                if (winnerRule!=null) {
                  this.stfnRoot = winnerRule.getStFNRoot();
                } else {
             log.error("!!! StoRI built with a VFS not compatible with Mapping Rule (MAPRULE : "+winnerRule.getRuleName() +")");
                }
                     }
                     catch (NamespaceException ex) {
                log.error(" VFS attribute reading error.");
                     }
             **/
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

        //Relative path has to be a path in a relative form! (without "/" at begins)
        if (relativePath != null) {
            if (relativePath.startsWith(NamingConst.SEPARATOR)) {
                this.relativePath = relativePath.substring(1);
            }
            else {
                this.relativePath = relativePath;
            }
        }
        else {
            this.relativePath = relativePath;
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

    public StoRIImpl() {
        try {
            testInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setStoRIType(StoRIType type) {
        this.type = type;
    }

    public void setMappingRule(MappingRule winnerRule) {
        this.winnerRule = winnerRule;
    }

    public MappingRule getMappingRule() {
        return this.winnerRule;
    }

    /*****************************************************************************
     *  READ METHODs
     ***************************************************************************/


    public TTURL getTURL(TURLPrefix prefixOfAcceptedTransferProtocols) throws
        InvalidGetTURLNullPrefixAttributeException {

        if (transferProtocolManaged == null) {
            populateManagedTranferProtocol();
        }

        TransportPrefix protocolPrefix = null;
        TTURL resultTURL = null;

        boolean found = false;

        if (prefixOfAcceptedTransferProtocols == null) {
            log.error("<GetTURL> request with NULL prefixOfAcceptedTransferProtocol!");
            throw new InvalidGetTURLNullPrefixAttributeException(prefixOfAcceptedTransferProtocols);
        }
        else {

          /**
           * Retrieve Protocol to build the TURL
           */
            if ( (prefixOfAcceptedTransferProtocols.size() == 0)) {
                log.debug("<GetTURL> Creating new TURL with DEFAULT TransportPrefix");
                //Creation TURL with DEFAULT Transport Prefix
                protocolPrefix = getDefaultTransferProtocol();
            }
            else { //Within the request there are some protocol preferences
                int size = prefixOfAcceptedTransferProtocols.size();
                boolean prefixFound = false;
                TransferProtocol tp = null;
                for (int i = 0; i < size && !prefixFound; i++) {
                    tp = prefixOfAcceptedTransferProtocols.getTransferProtocol(i);
                    String scheme = tp.getValue();
                    protocolPrefix = retrieveTrasferProtocolByProtocolScheme(scheme);
                    if (protocolPrefix!=null)
                        prefixFound = true;
                }
            }
            if (protocolPrefix != null) {
              resultTURL = buildTURL(protocolPrefix, getPFN());
            }
            else { //No match found!
                    log.error("NO MATCH with Protocol Preferences and Procol Managed!");
                    /**
                     * @todo : Manage this exceptional situation!!!
                     */
                }
        }
        return resultTURL;
    }


    private static TTURL buildTURL(TransportPrefix transportPrefix, PFN physicalFN) throws InvalidProtocolForTURLException {

      TTURL result = null;
      Protocol protocol = transportPrefix.getProtocol();
      Authority authority = transportPrefix.getAuthority();
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



    public TTURL getTURL(Protocol prot) {
        if (transferProtocolManaged == null) {
            populateManagedTranferProtocol();
        }
        /**
         * @todo : search within vector the existence of Prot
         */
        return null;
    }

    public List getTURLs() {
        if (transferProtocolManaged == null) {
            populateManagedTranferProtocol();
        }
        return transferProtocolManaged;
    }

    public TSURL getSURL() {
        /**
         *  The String passed to TSURL.makeFromString MUST contains
         *  a valid TSURL in string format, not only relativePath.
         */
        if (this.surl == null) {
            try {
                this.surl = TSURL.makeFromString(buildSURLString());
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

    private String buildSURLString() throws NamespaceException {
        String stfn = stfnRoot + NamingConst.SEPARATOR + relativeStFN;
        SURL surl = new SURL(stfn);
        return surl.toString();
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

    public StFN getStFN() {
        StFN stfn = null;
        if (this.surl == null) {
            getSURL();
        }
        stfn = surl.sfn().stfn();
        return stfn;
    }

    public String getStFNRoot() {
        return this.stfnRoot;
    }

    public void setStFNRoot(String stfnRoot) {
        this.stfnRoot = stfnRoot;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public String getRelativeStFN() {
        return this.relativeStFN;
    }

    public String getFilename() {
        return this.fileName;
    }

    public String getStFNPath() {
        return this.stfnPath;
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
        if (!(volatileInformationAreSet))
            setVolatileInformation();
        return lifetime;
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
        if (!(volatileInformationAreSet))
            setVolatileInformation();
        return startTime;
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

    /**
     * Returns true if the status of the SURL is SRM_SPACE_AVAILABLE, false otherwise.
     * This method queries the DB, therefore pay attention to possible performance issues.
     * @return boolean
     */
    public boolean isSURLBusy() {
        PtPChunkCatalog putCatalog = PtPChunkCatalog.getInstance();
        boolean busyStatus = putCatalog.isSRM_SPACE_AVAILABLE(getSURL());
        return busyStatus;
    }

    public StoRIType getStoRIType() {
        return this.type;
    }

    public Space getSpace() {
        if (space == null) {
            log.error("No space bound with this StoRI!");
            return null;
        }
        return this.space;
    }

    public void setSpace(Space space) {
        this.space = space;
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

    public VirtualFSInterface getVirtualFileSystem() {
        return this.vfs;
    }

    /*****************************************************************************
     *  BUSINESS METHODs
     ****************************************************************************/

    public ArrayList getChildren(GridUserInterface gUser, TDirOption dirOption) throws
        InvalidDescendantsEmptyRequestException, InvalidDescendantsAuthRequestException,
        InvalidDescendantsPathRequestException, InvalidDescendantsFileRequestException {

        log.error("METHOD DEPRECATED! : getChildren (GridUser, DirOption); use without GridUser");
        return getChildren(dirOption);
    }

    public ArrayList getChildren(TDirOption dirOption) throws
        InvalidDescendantsEmptyRequestException, InvalidDescendantsAuthRequestException,
        InvalidDescendantsPathRequestException, InvalidDescendantsFileRequestException {

        ArrayList pathList = new ArrayList();
        ArrayList stoRIList = new ArrayList();
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
            PathCreator pCreator = new PathCreator(fileHandle, dirOption.isAllLevelRecursive(), dirOption.getNumLevel());
            pathList = (ArrayList) pCreator.generateChild(pathList);
            if (pathList.size() == 0) {
                log.debug("SURL point to an EMPTY DIRECTORY");
                throw new InvalidDescendantsEmptyRequestException(fileHandle, pathList);
            }
            else { //Creation of StoRI LIST
                NamespaceInterface namespace = NamespaceDirector.getNamespace();
                StoRI createdStoRI = null;
                String childPath;
                for (int i = 0; i < pathList.size(); i++) {
                    childPath = (String) pathList.get(i);
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

    public List getParents() {

        StoRI createdStoRI = null;
        ArrayList parentList = new ArrayList();
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

    public String getAbsolutePath() {
        try {
            return vfs.getRootPath() + NamingConst.SEPARATOR + relativeStFN;
        }
        catch (NamespaceException ex) {
            log.error("VFS Root path error", ex);
            return null;
        }
    }



    /***********************************************
     *              UTILITY METHODS
     **********************************************/

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

    private void populateManagedTranferProtocol() {
        try {
            transferProtocolManaged = new Vector(vfs.getCapabilities().getManagedProtocols());
            log.debug("Managed Transfer Protocol by StoRI : " + transferProtocolManaged);
        }
        catch (NamespaceException ex) {
            log.error("Error while retrieving managed Transfer Protocol", ex);
        }
    }

    private TransportPrefix getDefaultTransferProtocol() {
        if (transferProtocolManaged == null) {
            populateManagedTranferProtocol();
        }
        return (TransportPrefix) transferProtocolManaged.firstElement();
    }

    private TransportPrefix retrieveTrasferProtocolByProtocolScheme(String scheme) {
        TransportPrefix result = null;
        if (transferProtocolManaged == null) {
            populateManagedTranferProtocol();
        }
        int size = transferProtocolManaged.size();
        for (int i = 0; i < size; i++) {
            result = (TransportPrefix) transferProtocolManaged.elementAt(i);
            String schemeAllowed = result.getProtocol().getSchema();
            if (schemeAllowed.toLowerCase().equals(scheme.toLowerCase())) {
                break;
            }
        }
        return result;
    }

    private void testInit() throws Exception {
        throw new Exception("NO USE THIS METHOD! <'new StoRI();'> ");
    }

}

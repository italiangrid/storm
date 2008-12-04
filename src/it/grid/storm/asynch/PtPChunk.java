package it.grid.storm.asynch;

import java.util.List;
import java.util.Iterator;
import java.util.Calendar;

import org.apache.log4j.Logger;

import it.grid.storm.common.SpaceHelper;
import it.grid.storm.config.Configuration;

import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.scheduler.Streets;

import it.grid.storm.griduser.LocalUser;
import it.grid.storm.griduser.VomsGridUser;

import it.grid.storm.griduser.CannotMapUserException;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.PtPChunkData;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;

import it.grid.storm.srm.types.TOverwriteMode;

import it.grid.storm.namespace.InvalidGetTURLProtocolException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.InvalidGetTURLNullPrefixAttributeException;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.ExpiredSpaceTokenException;


import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TTURL;

import it.grid.storm.authorization.AuthorizationCollector;
import it.grid.storm.authorization.AuthorizationDecision;


import java.io.IOException;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.InvalidPathException;
import it.grid.storm.filesystem.InvalidPermissionOnFileException;
import it.grid.storm.filesystem.WrongFilesystemType;
import it.grid.storm.filesystem.Space;
import it.grid.storm.filesystem.ReservationException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;


/**
 * Class that represents a chunk of an srmPrepareToPut request: it handles a single
 * file of a multifile request. StoRM then sends the chunk to a chunk-scheduler.
 *
 * For an existing file: if TOverwriteMode is set to Never, then the chunk fails
 * with SRM_DUPLICATION_ERROR; if TOverwriteMode is Always or WhenFilesAreDifferent,
 * the file gets treated in the same fashion: moreover the behaviour is the same
 * as for the case of a non existing file described later on, except that the only
 * policy check made is about the presence of write rights, instead of create
 * rights, as well as erasing the file before going on with the processing - all
 * previous data gets lost!
 *
 * If the SURL refers to a file that does not exist, the behaviour is identical
 * whatever the TOverwriteMode; in particular:
 *
 * AuthorisationCollector is queried for File Creation policies: if it is set to
 * Deny, then the chunk is failed with SRM_AUTHORIZATION_FAILURE. If it is set to
 * Permit, the situation is decribed later on. For any other decisions, the chunk
 * is failed with SRM_FAILURE: it is caused when the policy is missing so no decision
 * can be made, or if there is a problem querying the Policies, or any new state
 * for the AuthorisationDecision is introduced but the PtP logic is not updated.
 *
 * In case Create rights are granted, the presence of a space token and file size
 * are evaluated. If no size and no token are available, a mock file is created
 * as placeholder; in case the size is available but there is no token, an implicit
 * space reservation takes place so a special mock file is also produced; an implicit
 * space reservation again is carried out if there is no space token and no known
 * size, but this time a default reserve size is used resulting once more in a
 * special mock file; finally if both space token and size are supplied, the space
 * is allocated as requested and again a special mock reserve file gets created.
 *
 * A Write ACL is setup on the file regardless of the Security Model (AoT or JiT);
 * if the file is specified as VOLATILE, it gets pinned in the PinnedFilesCatalog;
 * if JiT is active, the ACL will live only for the given time interval. A TURL
 * gets filled in, the status transits to SRM_SPACE_AVAILABLE, and the PtPCatalog
 * is updated.
 *
 * There are error situations which get handled as follows:
 *
 * If the placeHolder file cannot be created, or the implicit reservation fails,
 * or the supplied space token does not exist, the request fails and chenages
 * state to SRM_FAILURE.
 *
 * If the setting up of the ACL fails, the request fails too and the state changes
 * to SRM_FAILURE.
 *
 * Appropriate messagges get logged.
 *
 * @author  EGRID - ICTP Trieste
 * @date    June, 2005
 * @version 3.0
 */
public class PtPChunk implements Delegable, Chooser {

    private static Logger log = Logger.getLogger("asynch");
    //DA SOVRASCRIVER CON QUELLO IN CVSSSSS!!!!
    private VomsGridUser gu=null;        //GridUser that made the request
    private RequestSummaryData rsd=null; //RequestSummaryData containing all the statistics for the originating srmPrepareToPutRequest
    private PtPChunkData chunkData=null; //PtPChunkData that holds the specific info for this chunk
    private GlobalStatusManager gsm =null;  //GlobalStatusManager object in charge of computing the global status of the request This chunk belongs to
    private Calendar start = null; //Time that wil be used in all jit and volatile tracking.
    private boolean failure = false; //boolean that indicates the state of the shunk is failure
    private boolean spacefailure = false; //boolean that indicates a failed chunk because of an expired sapce token

    /**
     * Constructor requiring the VomsGridUser, the RequestSummaryData, the
     * PtPChunkData about this chunk, and the GlobalStatusManager. If the
     * supplied attributes are null, an InvalidPtPChunkAttributesException
     * is thrown.
     */
    public PtPChunk(VomsGridUser gu, RequestSummaryData rsd, PtPChunkData chunkData, GlobalStatusManager gsm) throws InvalidPtPChunkAttributesException {
        boolean ok = (gu!=null) &&
            (rsd!=null) &&
            (chunkData!=null) &&
            (gsm!=null);
        if (!ok) throw new InvalidPtPChunkAttributesException(gu,rsd,chunkData,gsm);
        this.gu = gu;
        this.rsd = rsd;
        this.chunkData = chunkData;
        this.gsm = gsm;
        this.start = Calendar.getInstance();
    }

    /**
     * Method that handles a chunk. It is invoked by the scheduler to carry out
     * the task.
     */
    public void doIt() {
        log.info("Handling PtP chunk for user DN: "+this.gu.getDn()+"; for SURL: "+this.chunkData.toSURL()+"; for requestToken: "+this.rsd.requestToken());
        LocalFile localFile = null;
        try {
            StoRI fileStoRI = NamespaceDirector.getNamespace().resolveStoRIbySURL(chunkData.toSURL(),gu);
            localFile = fileStoRI.getLocalFile();
            boolean exists= localFile.exists();
            if (!exists) manageNotExistentFile(fileStoRI); //overwrite option is irrelevant!!!
            else if (chunkData.overwriteOption()==TOverwriteMode.ALWAYS) manageOverwriteExistingFile(fileStoRI);
            else if (chunkData.overwriteOption()==TOverwriteMode.WHENFILESAREDIFFERENT) manageOverwriteExistingFile(fileStoRI);
            else if (chunkData.overwriteOption()==TOverwriteMode.NEVER) {
                //File exists but Overwrite set to NEVER!!!
                chunkData.changeStatusSRM_DUPLICATION_ERROR("Cannot srmPut file because it already exists!");
                this.failure = true; //gsm.failedChunk(chunkData);
            } else {
                //unexpected overwrite option!!!
                chunkData.changeStatusSRM_FAILURE("Unexpected overwrite option! Processing failed!");
                this.failure = true; //gsm.failedChunk(chunkData);
                log.error("UNEXPECTED ERROR in PtPChunk! The specified overwrite option is unknown!");
                log.error("Received Overwrite Option: "+chunkData.overwriteOption());
                log.error("Request: "+rsd.requestToken());
            }
        } catch (SecurityException e) {
            //The check for existence of the File failed because there is a SecurityManager installed that
            //denies read privileges for that File! Perhaps the local system administrator of StoRM set
            //up Java policies that contrast policies described by the PolicyCollector! There is a conflict here!
            chunkData.changeStatusSRM_FAILURE("StoRM is not allowed to work on requested file!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ATTENTION in PtPChunk! PtPChunk received a SecurityException from Java SecurityManager: StoRM cannot check for the existence of file: "+localFile.toString()+"; exception: "+e);
        } catch (NamespaceException e) {
            //The Supplied SURL does not contain a root that could be identified by the StoRI factory
            //as referring to a VO being managed by StoRM... that is SURLs begining with such root
            //are not handled by this SToRM!
            chunkData.changeStatusSRM_INVALID_PATH("The path specified in the SURL does not have a local equivalent!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.debug("ATTENTION in PtPChunk! PtPChunk received request for a SURL whose root is not recognised by StoRI!"); //info
        } catch (Exception e) {
            //There could be unexpected runtime errors given the fact that we have an ACL enabled filesystem!
            //I do not know the behaviour of Java File class!!!
            chunkData.changeStatusSRM_FAILURE("StoRM encountered an unexpected error!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk - doIt() method! StoRM process got an unexpected error! "+e);
            log.error("Complete error stack trace follows: ");
            StackTraceElement[] ste = e.getStackTrace();
            if (ste!=null) for (int ii=0; ii<ste.length; ii++) log.error(ste[ii].toString());
        }
       //update statistics! It is the same for both normal completion and exception throwing!
       PtPChunkCatalog.getInstance().update(chunkData);
       if (failure) gsm.failedChunk(chunkData);
       else if (spacefailure) gsm.expiredSpaceLifetimeChunk(chunkData);
       else gsm.successfulChunk(chunkData);
       log.info("Finished handling PtG chunk for user DN: "+this.gu.getDn()+"; for SURL: "+this.chunkData.toSURL()+"; for requestToken: "+this.rsd.requestToken()+"; result is: "+this.chunkData.status());
   }



   /**
    * Private method that manages the case of a SURL referring to a file that does
    * not exist: the steps are the same regardless of the TOverwriteMode!
    */
   private void manageNotExistentFile(StoRI fileStoRI) {
       //check for create+write policies
       AuthorizationDecision createAD = AuthorizationCollector.getInstance().canCreateNewFile(gu,fileStoRI);
       //AuthorizationDecision writeAD = AuthorizationCollector.getInstance().canWriteFile(gu,fileStoRI);
       if (createAD.isPermit() /*&& writeAD.isPermit()*/) managePermit(fileStoRI);
       else if (createAD.isDeny() /*|| writeAD.isDeny()*/) manageDeny();
       else manageAnomaly(createAD/*,writeAD*/);
       //NOTE: the check for writeAD was removed because when a new file is being put, it does _not_
       //exist so there is no way a policy source may express the write permission! It can only check
       //for the create one!!!
   }

    /**
     * Private method that handles the case of Permit on Create and Write rights!
     */
    private void managePermit(StoRI fileStoRI) {
        try {
            TTURL auxTURL = fileStoRI.getTURL(chunkData.transferProtocols());
            LocalUser localUser = gu.getLocalUser();
            //set right permissions to trasverse to file
            boolean ok1 = managePermitTraverseStep(fileStoRI,localUser);
            if (ok1) {
                //Use any reserved space which implies the existence of a file!
                boolean ok2 = managePermitReserveSpaceStep(fileStoRI);
                if (ok2) {
                    //set right permission on file!
                    boolean ok3 = managePermitSetFileStep(fileStoRI,localUser,auxTURL);
                    if (!ok3) {
                        //URGENT!!!
                        //roll back! ok3, ok2 and ok1
                    }
                } else {
                    //URGENT!!!
                    //roll back! ok2 and ok1
                }
            } else {
                //URGENT!!!
                //roll back ok1!
            }
        } catch (CannotMapUserException e) {
            //While setting up the ACL, the local mapping of the grid credentials failed!
            chunkData.changeStatusSRM_FAILURE("Unable to map grid credentials to local user!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! There was a failure in mapping "+gu.getDn()+" to a local user! Error returned: "+e);
        } catch (InvalidGetTURLNullPrefixAttributeException e) {
            //Handle null TURL prefix! This is a programming error: it should not occur!
            chunkData.changeStatusSRM_FAILURE("Unable to decide TURL!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! Null TURLPrefix in PtPChunkData caused StoRI to be unable to establish TTURL! StoRI object returned the following message: "+e);
        } catch (InvalidGetTURLProtocolException e) {
            //Handle null TURL prefix! This is a programming error: it should not occur!
            chunkData.changeStatusSRM_NOT_SUPPORTED("Unable to build TURL with specified transfer protocols!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! No valid transfer protocol found.");
       
        } catch (Error e) {
            //This is a temporary measure to catch an arror occurring because of the use of deprecated
            //method in VomsGridUser! It happens in exceptional conditions: when a user is mapped to
            //a specific account instead of a pool account, and the VomsGridUser class handles the situation
            //through a deprecated getEnv method!
            chunkData.changeStatusSRM_FAILURE("Unable to map grid credentials to local user!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! There was a failure in mapping "+gu.getDn()+" to a local user! Error returned: "+e);
       
        
        
        }
    }





    /**
     * Private method used to setup the right traverse permissions, and if necessary
     * create missing directories.
     *
     * Returns false if something went wrong!
     */
    private boolean managePermitTraverseStep(StoRI fileStoRI,LocalUser localUser) {
        //Create missing subdirectories and set trasverse ACL
        //ATTENTION!!! For AoT this turns out to be a PERMANENT ACL!!!
        log.debug("PtPChunk: entered TraverseStep for "+fileStoRI.getAbsolutePath());
        boolean automaticDirectoryCreation = Configuration.getInstance().getAutomaticDirectoryCreation();
        StoRI parentStoRI = null; //StoRI representing a parent directory
        LocalFile parentFile = null; //File representing a parent diretory
        boolean exists = false; //boolean that is true if parentFile exists
        boolean dir = false; //boolean true if parentFile is adirectory
        boolean anomaly = false; //boolean _true_ if the parent just treated, exists and is not a directory
        List parentList = fileStoRI.getParents();
        log.debug("PtPChunk TraverseStep - established the following parents: "+parentList.toString());
        Iterator i = parentList.iterator();
        while ((!anomaly) && i.hasNext()) {
            parentStoRI = (StoRI) i.next();
            parentFile = parentStoRI.getLocalFile();
            exists = parentFile.exists();
            dir = parentFile.isDirectory();
            anomaly = (exists && !dir); //true if parent exists and is not a directory!
            log.debug("PtPChunk TraverseStep - processing parent "+parentFile.toString());
            if (anomaly) {
                //error situation!
                //The current parent is a file and not a directory! The request should fail!
                chunkData.changeStatusSRM_INVALID_PATH("Invalid path; requested SURL is: "+fileStoRI.getSURL().toString()+", but its parent "+parentStoRI.getSURL().toString()+" is not a directory!");
                this.failure = true; //gsm.failedChunk(chunkData);
                log.debug("Specified SURL does not point to a valid directory; requested SURL is: "+fileStoRI.getSURL().toString()+", but its parent "+parentStoRI.getSURL().toString()+" points to "+parentStoRI.getLocalFile().toString()+" which is not a directory!"); //info
                //anomaly is already true: no need to restate it.
            } else {
                //process parent
                try {
                    anomaly = prepareDirectory(parentFile,automaticDirectoryCreation,exists);
                    if (!anomaly) {
                        //directory successfully created or already present!
                        FilesystemPermission fp = null;
                        boolean allowsTraverse = false;
                        if (fileStoRI.hasJustInTimeACLs()) {
                            //JiT
                            parentFile.grantUserPermission(localUser,FilesystemPermission.Traverse);
                            fp = parentFile.getEffectiveUserPermission(localUser);
                            if (fp!=null) {
                                allowsTraverse = fp.allows(FilesystemPermission.Traverse);
                                if (allowsTraverse) VolatileAndJiTCatalog.getInstance().trackJiT(parentStoRI.getPFN(),localUser,FilesystemPermission.Traverse,start,chunkData.pinLifetime());
                                else log.error("ATTENTION in PtGChunk! The local filesystem has a mask that does not allow Traverse User-ACL to be set up on"+parentFile.toString()+"!");
                            } else log.error("ERROR in PTPChunk! A Traverse User-ACL was set on "+fileStoRI.getAbsolutePath()+" for user "+localUser.toString()+" but when subsequently verifying its effectivity, a null ACE was found!");
                        } else {
                            //AoT
                            parentFile.grantGroupPermission(localUser,FilesystemPermission.Traverse);
                            fp = parentFile.getEffectiveGroupPermission(localUser);
                            if (fp!=null) {
                                allowsTraverse = fp.allows(FilesystemPermission.Traverse);
                                if (!allowsTraverse) log.error("ATTENTION in PtGChunk! The local filesystem has a mask that does not allow Traverse Group-ACL to be set up on"+parentFile.toString()+"!");
                            } else log.error("ERROR in PTPChunk! A Traverse Group-ACL was set on "+fileStoRI.getAbsolutePath()+" for user "+localUser.toString()+" but when subsequently verifying its effectivity, a null ACE was found!");
                        }

                        if (fp==null) {
                            //Problems when manipulating ACEs! Added a traverse permission but no corresponding ACE was found!
                            //Request fails!
                            chunkData.changeStatusSRM_FAILURE("Local filesystem has problems manipulating ACE!");
                            this.failure = true; //gsm.failedChunk(chunkData);
                            anomaly = true;
                        } else if (!allowsTraverse) {
                            //mask preset in filesystem does not allow the setting up of the required permissions!
                            //Request fails!
                            chunkData.changeStatusSRM_FAILURE("Local filesystem mask does not allow setting up correct ACLs for PtG!");
                            this.failure = true; //gsm.failedChunk(chunkData);
                            anomaly = true;
                        } else {
                            //anomaly is already false: no need to restate it!
                        }
                    }
                } catch (SecurityException e) {
                    //parentFile.mkdir() could not create directory because the Java SecurityManager did not grant
                    //write premission! This indicates a possible conflict between a local system administrator
                    //who applied a strict local policy, and policies as specified by the PolicyCollector!
                    chunkData.changeStatusSRM_FAILURE("StoRM Generic reservation/space failure!");
                    this.failure = true; //gsm.failedChunk(chunkData);
                    log.error("ERROR in PtPChunk! Generic reservation/space failure: "+e);
                    anomaly = true;
                } catch (WrongFilesystemType e) {
                    //StoRM configured with wrong Filesystem type!
                    chunkData.changeStatusSRM_FAILURE("StoRM configured for wrong filesystem!");
                    this.failure = true; //gsm.failedChunk(chunkData);
                    log.error("ERROR in PtPChunk! StoRM is not configured for underlying fileystem! "+e);
                    log.error("Chunk being handled: "+chunkData.toString());
                    anomaly = true;
                } catch (InvalidPermissionOnFileException e) {
                    //StoRM cannot carryout file manipulation because it lacks enough privileges!
                    chunkData.changeStatusSRM_FAILURE("StoRM cannot manipulate directory!");
                    this.failure = true; //gsm.failedChunk(chunkData);
                    log.error("ERROR in PtPChunk! StoRM process has not got enough privileges to work on: "+parentFile.toString()+"; exception: "+e);
                    log.error("Chunk being handled: "+chunkData.toString());
                    anomaly = true;
                } catch (InvalidPathException e) {
                    //Could not set ACL because file does not exist!
                    chunkData.changeStatusSRM_FAILURE("Unable to setup ACL!");
                    this.failure = true; //gsm.failedChunk(chunkData);
                    log.error("ERROR in PtPChunk! The file on which to set up the ACL does not exist!");
                    log.error("ERROR in PtPChunk! This should not happen because the directory has just been successfully created!");
                    anomaly = true;
                } catch (Exception e) {
                    //Catch any other Runtime exception: Filesystem component may throw other Runtime Exceptions!
                    chunkData.changeStatusSRM_FAILURE("StoRM encountered an unexpected error!");
                    this.failure = true; //gsm.failedChunk(chunkData);
                    log.error("ERROR in PtPChunk - traverseStep! StoRM process got an unexpected error! "+e);
                    log.error("Complete error stack trace follows: ");
                    StackTraceElement[] ste = e.getStackTrace();
                    if (ste!=null) for (int ii=0; ii<ste.length; ii++) log.error(ste[ii].toString());
                    anomaly = true;
                }
            }
        }
        log.debug("PtPChunk: finished TraverseStep for "+fileStoRI.getAbsolutePath());
        return !anomaly;
    }

    /**
     * Private method that handles directory creation: it considers whether it already exists,
     * and if automatic creation is enabled or not. BEWARE! It returns a boolean true if any
     * _anomaly_ occurs!
     */
    private boolean prepareDirectory(LocalFile f, boolean canCreate, boolean exists) throws SecurityException {
        boolean anomaly = false;
        if ((!exists) && (!canCreate)) {
            //The directory does not exist, but automatic creation is disabled!
            //Fail chunk with SRM_INVALID_PATH!!!
            chunkData.changeStatusSRM_INVALID_PATH("Directory structure as specified in SURL does not exist!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.debug("ATTENTION in PtPChunk! Directory structure as specified in "+f+" does not exist, and automatic directory ceration is disbaled! Failing this chunk of request!"); //info
            anomaly = true;
        } else if (!exists) {
            //directory does not exist but automatic directory creation is on!
            anomaly = !(f.mkdirs()); //create directory!
            if (anomaly) {
                //Directory creation failed for some reason!!!
                chunkData.changeStatusSRM_FAILURE("Local filesystem error: could not crete directory!");
                this.failure = true; //gsm.failedChunk(chunkData);
                log.error("ERROR in PtPChunk! Filesystem was unable to successfully create directory: "+f.toString());
                //anomaly is already true! No need to re-state it!
            }
        }
        return anomaly;
    }





    /**
     * Private method used to manage ReserveSpace.
     *
     * Returns false if something went wrong!
     */
    private boolean managePermitReserveSpaceStep(StoRI fileStoRI) {
        log.debug("PtPChunk: entered ReserveSpaceStep for "+fileStoRI.getAbsolutePath());
        TSizeInBytes size = chunkData.expectedFileSize();
        TSpaceToken spaceToken = chunkData.spaceToken();
        LocalFile localFile = fileStoRI.getLocalFile();
        
        // In case of SRM Storage Area limitation enabled, 
        // the Storage Area free size is retrieved from the database
        // and the PtP fails if there is not enougth space.
        Configuration config = Configuration.getInstance();
        
        // @TODO aggiungere il controllo solo se e' abilitato nel namespace
        // per la Storage Area corrispondente
        
        if(true) {
            SpaceHelper sp = new SpaceHelper();
            long freeSpace = sp.getSAFreeSpace(log, fileStoRI);
            if( (sp.isSAFull(log, fileStoRI)) ||
                    ( (!size.isEmpty() && ((freeSpace != -1) && (freeSpace <= size.value())))) ) {
                
                log.debug("PtPChunk - ReserveSpaceStep: no free space on Storage Area!");
                chunkData.changeStatusSRM_FAILURE("No free space on Storage Area");
                this.failure = true; //gsm.failedChunk(chunkData);
                return false;
            }
        }
             
                
        try {
            //set space!
            boolean successful = localFile.createNewFile();
            if ((!successful) && (chunkData.overwriteOption()==TOverwriteMode.NEVER)) {
                //atomic operation of creation of file if non existent, failed: so the file
                //did exist before. The Overwrite option was set to NEVER, so fail request!
                //
                //this test is done here, in case concurrent PtP chunks get executed at the
                //same time: this is the _only_ place where the concurrent operation can be
                //spotted without incurring in exceptionally tough syncronisation issues!
                //
                //File exists but Overwrite set to NEVER!!!
                log.debug("PtPChunk - ReserveSpaceStep: no overwrite allowed! Failing chunk... ");
                chunkData.changeStatusSRM_DUPLICATION_ERROR("Cannot srmPut file because it already exists!");
                this.failure = true; //gsm.failedChunk(chunkData);
                return false;
            } else if ((!successful) && PtPChunkCatalog.getInstance().isSRM_SPACE_AVAILABLE(chunkData.toSURL())) {
                //atomic operation of creation of file if non existent, failed: so the file
                //did exist before. A check in the catalogue shows that there is a past request
                //which has the SURL in SRM_SPACE_AVAILABLE! So request must be failed with
                //SRM_FILE_BUSY
                //
                //WARNING! Unfortunately although this test is done here, in case concurrent PtP
                //chunks get executed at the same time, it is still NOT guaranteed that one of the
                //two PtP will get an SRM_FILE_BUSY.
                //
                //The operation isSRM_SPACE_AVAILABLE _is_ atomic because that method is
                //synchronised. But the couple createNewFile AND isSRM_FILE_BUSY
                //is NOT atomic! Hence the inability to guarantee one of the two
                //will get an SRM_FILE_BUSY.
                //
                //Putting the check here minimizes the chances, but cannot be eliminated
                //altogether.
                //
                //fail request with SRM_FILE_BUSY
                chunkData.changeStatusSRM_FILE_BUSY("Requested file is still in SRM_SPACE_AVAILABLE state!");
                this.failure = true; //gsm.failedChunk(chunkData);
                log.debug("ATTENTION in PtPChunk! PtPChunk received request for SURL that is still in SRM_SPACE_AVAILABLE state!");
                return false;
            } else if ((spaceToken.isEmpty()) && (size.isEmpty())) {
                log.debug("PtPChunk - ReserveSpaceStep: no SpaceToken and no FileSize specified; mock file will be created... ");
            } else if ((spaceToken.isEmpty()) && (!size.isEmpty())) {
                log.debug("PtPChunk - ReserveSpaceStep: no SpaceToken available but there is a FileSize specified; implicit space reservation taking place...");
                fileStoRI.allotSpaceForFile(size);
            } else if ((!spaceToken.isEmpty()) && (!size.isEmpty())) {
                log.debug("PtPChunk - ReserveSpaceStep: SpaceToken available and FileSize specified; reserving space by token...");
                fileStoRI.allotSpaceByToken(spaceToken,size);
            } else {
                //spaceToken is NOT empty but size IS!
                //Use of EMPTY Space Size. That means total size of Storage Space will be used!!
                log.debug("PtPChunk - ReserveSpaceStep: SpaceToken available and FileSize specified; reserving space by token...");
                fileStoRI.allotSpaceByToken(spaceToken);
            }
            log.debug("PtPChunk: finished ReserveSpaceStep for "+fileStoRI.getAbsolutePath());
            return true;
        } catch (SecurityException e) {
            //file.createNewFile could not create file because the Java SecurityManager did not grant
            //write premission! This indicates a possible conflict between a local system administrator
            //who applied a strict local policy, and policies as specified by the PolicyCollector!
            chunkData.changeStatusSRM_FAILURE("Space Management step in srmPrepareToPut failed!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! During space reservation step in PtP, could not create file: "+localFile.toString()+"; Java s SecurityManager does not allow writing the file! "+e);
            return false;
        } catch (IOException e) {
            //file.createNewFile could not create file because of a local IO Error!
            chunkData.changeStatusSRM_FAILURE("Space Management step in srmPrepareToPut failed!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! During space reservation step in PtP, an error occured while trying to create the file:"+localFile.toString()+"; error: "+e);
            return false;
        } catch (it.grid.storm.filesystem.InvalidPermissionOnFileException e) {
            //I haven t got the right to create a file as StoRM user!
            //This is thrown when executing createNewFile method!
            chunkData.changeStatusSRM_FAILURE("Space Management step in srmPrepareToPut failed!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! During space reservation step in PtP, an attempt to create file "+localFile.toString()+" failed because StoRM lacks the privileges to do so! Exception follows: "+e);
            return false;
        } catch (ReservationException e) {
            //Something went wrong while using space reservation component!
            chunkData.changeStatusSRM_FAILURE("Space Management step in srmPrepareToPut failed!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! Space component failed! Exception follows: "+e);
            return false;
        } catch (ExpiredSpaceTokenException e) {
            //The supplied space token is expired
            chunkData.changeStatusSRM_FAILURE("Space Token expired!");
            this.spacefailure = true; //gsm.expiredSpaceLifetimeChunk(chunkData);
            log.error("ERROR in PtPChunk! Space component failed! Exception follows: "+e);
            return false;
        } catch (Exception e) {
            //This could be thrown by Java from Filesystem component given that there is GPFS under the hoods,
            //but I do not know exactly how java.io.File behaves with an ACL capable filesystem!!
            chunkData.changeStatusSRM_FAILURE("Space Management step in srmPrepareToPut failed!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk - space Step! Unexpected error in reserve space step of PtP for file "+localFile.toString()+"! Exception follows: "+e);
            log.error("Complete error stack trace follows: ");
            StackTraceElement[] ste = e.getStackTrace();
            if (ste!=null) for (int ii=0; ii<ste.length; ii++) log.error(ste[ii].toString());

            //ROOLBACK??
            //The file already exists!!!
            return false;
        }
    }

    /**
     * Private method used to setup the right file permission
     *
     * Returns false if something goes wrong!
     */
    private boolean managePermitSetFileStep(StoRI fileStoRI,LocalUser localUser,TTURL turl) {
        //BEWARE THAT READ PERMISSION IS NEEDED BECAUSE GRID_FTP SERVER _ALSO_ REQUIRES READ RIGHTS ELSE IT WON T BE ABLE TO WRITE THE FILE!!!
        log.debug("PtPChunk: entered SetFileStep for "+fileStoRI.getAbsolutePath());
        LocalFile localFile = fileStoRI.getLocalFile();
        try {
            FilesystemPermission fp = null;
            boolean effective = false;
            if (fileStoRI.hasJustInTimeACLs()) {
                //JiT
                localFile.grantUserPermission(localUser,FilesystemPermission.ReadWrite);
                fp = localFile.getEffectiveUserPermission(localUser);
                if (fp!=null) {
                    effective = fp.allows(FilesystemPermission.ReadWrite);
                    if (effective) {
                        //ACL was correctly set up! Track JiT!
                        VolatileAndJiTCatalog.getInstance().trackJiT(fileStoRI.getPFN(),localUser,FilesystemPermission.Read,start,chunkData.pinLifetime());
                        VolatileAndJiTCatalog.getInstance().trackJiT(fileStoRI.getPFN(),localUser,FilesystemPermission.Write,start,chunkData.pinLifetime());
                    } else log.error("ATTENTION in PTP CHUNK! The local filesystem has a mask that does not allow ReadWrite User-ACL to be set up on"+localFile.toString()+"!");
                } else log.error("ERROR in PTP CHUNK! A ReadWrite User-ACL was set on "+fileStoRI.getAbsolutePath()+" for user "+localUser.toString()+" but when subsequently verifying its effectivity, a null ACE was found!");
            } else {
                //AoT
                localFile.grantGroupPermission(localUser,FilesystemPermission.ReadWrite);
                fp = localFile.getEffectiveGroupPermission(localUser);
                if (fp!=null) {
                    effective = fp.allows(FilesystemPermission.ReadWrite);
                    if (!effective) log.error("ATTENTION in PTP CHUNK! The local filesystem has a mask that does not allow ReadWrite Group-ACL to be set up on"+localFile.toString()+"!");
                } else log.error("ERROR in PTP CHUNK! ReadWrite Group-ACL was set on "+fileStoRI.getAbsolutePath()+" for group "+localUser.toString()+" but when subsequently verifying its effectivity, a null ACE was found!");
            }

            if (fp==null) {
                //This is a programming bug and should not occur! An attempt was just made to grant Read
                //permission; so regardless of the umask allowing the permission or not, there must be at least an ACE
                //present! Yet none was found!
                chunkData.changeStatusSRM_FAILURE("Local filesystem has problems manipulating ACE!");
                this.failure = true; //gsm.failedChunk(chunkData);
                return false;
            }
            if (!effective) {
                //mask preset in filesystem does not allow the setting up of the required permissions!
                //Request fails!
                chunkData.changeStatusSRM_FAILURE("Local filesystem mask does not allow setting up correct ACLs for PtP!");
                this.failure = true; //gsm.failedChunk(chunkData);
                return false;
            }
            //ACL was correctly set up!
            //if (chunkData.fileStorageType()==TFileStorageType.VOLATILE) VolatileAndJiTCatalog.getInstance().trackVolatile(fileStoRI.getPFN(),start,chunkData.fileLifetime());
            log.debug("PTP CHUNK. Addition of ReadWrite ACL on file successfully completed for "+fileStoRI.getAbsolutePath());
            chunkData.setTransferURL(turl);
            chunkData.changeStatusSRM_SPACE_AVAILABLE("srmPrepareToPut successfully handled!");
            this.failure = false; //gsm.successfulChunk(chunkData);
            return true;

        } catch (WrongFilesystemType e) {
            //StoRM configured with wrong Filesystem type!
            chunkData.changeStatusSRM_FAILURE("StoRM configured for wrong filesystem!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! StoRM is not configured for underlying fileystem! "+e);
            return false;
        } catch (InvalidPermissionOnFileException e) {
            //StoRM cannot carryout file manipulation because it lacks enough privileges!
            chunkData.changeStatusSRM_FAILURE("StoRM cannot manipulate requested file!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! StoRM process has not got enough privileges to work on: "+localFile.toString()+"; exception: "+e);
            return false;
        } catch (InvalidPathException e) {
            //Could not set ACL because file does not exist!
            chunkData.changeStatusSRM_FAILURE("Unable to setup ACL!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk! The file on which to set up the ACL does not exist!");
            log.error("ERROR in PtPChunk! This should not happen because the implicit space reservation or the mock space file were successfully executed!");
            return false;
        } catch (Exception e) {
            //Catch any other Runtime exception: Filesystem component may throw other Runtime Exceptions!
            chunkData.changeStatusSRM_FAILURE("StoRM encountered an unexpected error!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("ERROR in PtPChunk - setFile step! StoRM process got an unexpected error! "+e);
            log.error("Complete error stack trace follows: ");
            StackTraceElement[] ste = e.getStackTrace();
            if (ste!=null) for (int ii=0; ii<ste.length; ii++) log.error(ste[ii].toString());
            return false;
        }
    }




    /**
     * Method that handles the case of either Create or Write policies are
     * Deny: beware that only one policy needs to be Deny for this method to
     * get invoked!
     */
    private void manageDeny() {
        chunkData.changeStatusSRM_AUTHORIZATION_FAILURE("Create/Write access to "+chunkData.toSURL()+" denied!");
        this.failure = true; //gsm.failedChunk(chunkData);
        log.debug("Create/Write access to "+chunkData.toSURL()+" for user "+gu.getDn()+" denied!"); //info
    }

    /**
     * Private method that handles all cases where: new states are added to AuthorizationCollector
     * but this PtP logic does not get updated, or there are missing policies, or there are problems
     * with the PolicyCollector.
     */
    private void manageAnomaly(AuthorizationDecision decision) {
        if (decision.isNotApplicable()) {
            //Missing policy
            chunkData.changeStatusSRM_FAILURE("Missing Policy! Access rights cannot be established!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("PtPChunk: PolicyCollector warned of missing policies for the supplied SURL!");
            log.error("Request: "+rsd.requestToken());
            log.error("Requested SURL: "+chunkData.toSURL());
        } else if (decision.isIndeterminate()) {
            //PolicyCollector error
            chunkData.changeStatusSRM_FAILURE("PolicyCollector error! Access rights cannot be established!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("PtPChunk: PolicyCollector encountered internal problems!");
            log.error("Request: "+rsd.requestToken());
            log.error("Requested SURL: "+chunkData.toSURL());
        } else {
            //Unexpected policy!
            chunkData.changeStatusSRM_FAILURE("Unexpected Policy! StoRM does not know what to do!");
            this.failure = true; //gsm.failedChunk(chunkData);
            log.error("PtPChunk: unexpected policy returned by PolicyCollector for the supplied SURL!");
            log.error("Request: "+rsd.requestToken());
            log.error("Requested SURL: "+chunkData.toSURL());
        }
    }





    /**
     * Private method that handles the case of overwriting of an existing file: this
     * gets invoked if TOverwriteMode is set to Always or WhenFilesAreDifferet, and
     * a file already exists.
     *
     * Notice that the logic for permit is exactly the same as that for the case of
     * non existent files! This is because the placeHolder/reserveSpace functions of the
     * FileSystem object, in case of existing files, they erase it and create a new mock
     * file - thereby following the SRM specs that no append is possible.
     *
     * So the only difference between the two cases is the kind of policies that are checked
     * before going on and carrying out the processing.
     */
    private void manageOverwriteExistingFile(StoRI fileStoRI) {
       //check for write policies
       AuthorizationDecision writeAD = AuthorizationCollector.getInstance().canWriteFile(gu,fileStoRI);
       if (writeAD.isPermit()) {
           managePermit(fileStoRI);
       } else if (writeAD.isDeny()) {
           chunkData.changeStatusSRM_AUTHORIZATION_FAILURE("Write access to "+chunkData.toSURL()+" denied!");
           this.failure = true; //gsm.failedChunk(chunkData);
           log.debug("Write access to "+chunkData.toSURL()+" for user "+gu.getDn()+" denied!"); //info
       } else manageAnomaly(writeAD);
    }







    /**
     * Method that supplies a String describing this PtGChunk - for scheduler Log
     * purposes! It returns the request token and the SURL asked for in This
     * request.
     */
    public String getName() {
        return "PtPChunk of request "+rsd.requestToken()+" for SURL "+chunkData.toSURL();
    }

    /**
     * Method used in a callback fashion in the scheduler for separately handling
     * PtG, PtP and Copy chunks.
     */
    public void choose(Streets s) {
        s.ptpStreet(this);
    }

////////////////////////////////////////////////////////////////////////////////

    public String getUserDN() {
      return this.gu.getDn();
    }

    public String getSURL() {
      return this.chunkData.toSURL().toString();
    }

    public String getRequestToken() {
      return this.rsd.requestToken().toString();
    }

    /**
     *
     * @return boolean
     */
    public boolean isResultSuccess() {
      boolean result = false;
      TStatusCode statusCode = this.chunkData.status().getStatusCode();
      if ((statusCode.getValue().equals(TStatusCode.SRM_SPACE_AVAILABLE.getValue()))||(this.chunkData.status().isSRM_SUCCESS())) {
        result = true;
      }
      return result;
    }


}

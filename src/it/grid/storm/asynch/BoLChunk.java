package it.grid.storm.asynch;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.BoLChunkData;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.Streets;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents a chunk of an srmBringOnLine request: it handles a single file of a
 * multifile/directory-expansion request. StoRM then sends the chunk to a chunk-scheduler.
 * 
 * Security checks performed as follows: both in the JiT and AoT approach, policies are checked to
 * see if the Griduser has read rights on the requested SURL.
 * 
 * If the AuthorisationCollector replies with an isDeny, then the request fails with
 * SRM_AUTHORIZATION_FAILURE status.
 * 
 * If the AuthorisationCollector replies with isIndeterminate, then the request fails with
 * SRM_FAILURE and explanation string "Failure in PolicySource prevented PolicyCollector from
 * establishing access rights! Processing failed!"; a message gets logged as well.
 * 
 * If the AuthorisationCollector replies with isNotApplicabale, then the request fails with
 * SRM_FAILURE and error string "No policies found for the requested SURL! Therefore access rights
 * cannot be established! Processing cannot continue!"; a message gets logged as well.
 * 
 * If the AuthorisationCollector returns a state for which there is no business logic, then the
 * request fails again with SRM_FAILURE and error string "Unexpected authorization state! Processing
 * failed!"; a message gets logged.
 * 
 * If the AuthorisationCollector returns an isPermit, then processing continues as follows:
 * 
 * (1) The local file that corresponds to the supplied SURL is determined, together with the local
 * user to whom the grid credentials get mapped; the TURL finally gets constructed. If the local
 * file does not exist the request fails with SRM_INVALID_PATH and corresponding explanation string;
 * if the user cannot be mapped locally, the request fails with SRM_FAILURE and an explanation
 * String which includes the DN used for maping; if there are internal problems constructing the
 * TURL again the request fails with SRM_FAILURE. Appropriate error messages get logged.
 * 
 * (2) Traverse permissions get set on all parent directories to allow access to the file. The
 * operation may fail for several reasons: the file or any of the parent directories may have been
 * removed resulting in SRM_INVALID_PATH; StoRM cannot set the requested permissions because a
 * filesystem mask does not allow the permissions to be set up; StoRM may be configured for the
 * wrong filesystem; StoRM has not got the right permissions to manipulate the ACLs on the
 * filesystem; StoRM may have encountered an unexpected error when working with the filesystem. In
 * all these circumstances, the status changes to SRM_FAILURE, together with an appropriate
 * explanation String, and a respective log message.
 * 
 * (3) The file size is determined. The operation may fail and hence the request too gets failed, in
 * the following circumstances: the file somehow does not exist, the path to the file is not found,
 * an error while communicating with the underlaying FileSystem, or a JVM SecurityManager forbids
 * such operation. In the first two cases the state changes to SRM_INVALID_PATH, while in the other
 * ones it changes to SRM_FAILURE; proper error strings explain the situation further. Error
 * messages get logged.
 * 
 * (3) If AoT acls are in place, then the PinnedFilesCatalog is asked to pinExistingVolatileEntry,
 * that is, it is asked to pin the entry if it is already present thereby extending its lifetime (if
 * it is not present, it just means that the requested file is PERMANENT and there is no need to pin
 * it); status changes to SRM_FILE_PINNED.
 * 
 * (4) If it is the JiT that is in place, a ReadACL is first added, the ACL gets tracked by calling
 * the proper method in the PinnedFilesCatalog, and the file gets pinned by invoking
 * pinExistingVolatileEntry; the status changes to SRM_FILE_PINNED. The addition of the ACL could go
 * wrong in several ways: the file may not exist, the ACL could not be set up because there is a
 * mask that does not allow the Read permission to be set, StoRM does not have the permission to
 * manipulate the ACLs, StoRM was not configured for the underlying FileSystem, or there was an
 * unexpected error; in the first case the status changes to SRM_INVALID_PATH, while in all other
 * ones it changes to SRM_FAILURE; corresponding messages get logged.
 * 
 * @author CNAF
 * @date Aug 2009
 * @version 1.0
 */
public class BoLChunk implements Delegable, Chooser {

    private static Logger log = LoggerFactory.getLogger(BoLChunk.class);

    /** GridUser that made the request */
    private GridUserInterface gu = null;
    /** RequestSummaryData containing all the statistics for the originating srmBringOnLineRequest */
    private RequestSummaryData rsd = null;
    /** BoLChunkData that holds the specific info for this chunk */
    private BoLChunkData chunkData = null;
    /** manager for global status computation */
    private GlobalStatusManager gsm = null;
    /** boolean that indicates a chunk failure */
    private boolean failure = false;

    /**
     * Constructor requiring the GridUser, the RequestSummaryData and the BoLChunkData about this
     * chunk. If the supplied attributes are null, an InvalidBoLChunkAttributesException is thrown.
     */
    public BoLChunk(GridUserInterface gu, RequestSummaryData rsd, BoLChunkData chunkData,
            GlobalStatusManager gsm) throws InvalidBoLChunkAttributesException {

        boolean ok = (gu != null) && (rsd != null) && (chunkData != null) && (gsm != null);
        if (!ok) {
            throw new InvalidBoLChunkAttributesException(gu, rsd, chunkData, gsm);
        }

        this.gu = gu;
        this.rsd = rsd;
        this.chunkData = chunkData;
        this.gsm = gsm;
    }

    /**
     * Method that handles a chunk. It is invoked by the scheduler to carry out the task.
     */
    public void doIt() {

        log.info("Handling BoL chunk for user DN: " + this.gu.getDn() + "; for SURL: "
                + this.chunkData.getFromSURL() + "; for requestToken: " + this.rsd.requestToken());

        if (PtPChunkCatalog.getInstance().isSRM_SPACE_AVAILABLE(chunkData.getFromSURL())) {

            chunkData.changeStatusSRM_FILE_BUSY("Requested file is still in SRM_SPACE_AVAILABLE state!");
            failure = true;
            log.debug("ATTENTION in BoLChunk! BoLChunk received request for SURL that is still in SRM_SPACE_AVAILABLE state!");

        } else {
            try {

                StoRI fileStoRI = NamespaceDirector.getNamespace()
                                                   .resolveStoRIbySURL(chunkData.getFromSURL(), gu);

                SpaceHelper sp = new SpaceHelper();
                TSpaceToken token = sp.getTokenFromStoRI(log, fileStoRI);
                SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

                if (spaceAuth.authorize(gu, SRMSpaceRequest.BOL)) {

                    manageIsPermit(fileStoRI);

                } else {
                    this.failure = true;
                    chunkData.changeStatusSRM_AUTHORIZATION_FAILURE("Read access to "
                            + chunkData.getFromSURL() + " in Storage Area: " + token + " denied!");
                    log.debug("Read access to " + chunkData.getFromSURL() + " in Storage Area: " + token
                            + " denied!");
                }

            } catch (NamespaceException e) {
                // The Supplied SURL does not contain a root that could be identified by the StoRI factory
                // as referring to a VO being managed by StoRM... that is SURLs begining with such root
                // are not handled by this SToRM!
                chunkData.changeStatusSRM_INVALID_PATH("The path specified in the SURL does not have a local equivalent!");
                failure = true;
                log.debug("ATTENTION in BoLChunk! BoLChunk received request for a SURL whose root is not recognised by StoRI! "
                        + e);
            }
        }

        BoLChunkCatalog.getInstance().update(chunkData); // update status in persistence!!!

        /*
         * If the status is still SRM_REQUEST_INPROGRESS means that the file is on the tape and it's
         * being recalled. The status will be changed to SRM_FILE_PINNED by the function that
         * updates the TapeRecall table putting a SUCCESS in the corresponding row (and the file
         * will have been recalled).
         */
        if (chunkData.getStatus().getStatusCode() != TStatusCode.SRM_REQUEST_INPROGRESS) {
            if (failure) {
                gsm.failedChunk(chunkData);
            } else {
                gsm.successfulChunk(chunkData); // update global status!!!
            }
        }
        log.info("Finished handling BoL chunk for user DN: " + this.gu.getDn() + "; for SURL: "
                + this.chunkData.getFromSURL() + "; for requestToken: " + this.rsd.requestToken()
                + "; result is: " + this.chunkData.getStatus());
    }

    /**
     * Manager of the IsPermit state: the user may indeed read the specified SURL
     */
    private void manageIsPermit(StoRI fileStoRI) {

        LocalFile localFile = fileStoRI.getLocalFile();

        try {
            if ((!localFile.exists()) || (localFile.isDirectory())) {
                // File does not exist, or it is a directory! Fail request with
                // SRM_INVALID_PATH!
                chunkData.changeStatusSRM_INVALID_PATH("The requested file either does not exist, or it is a directory!");
                failure = true;
                log.debug("BoLChunk: the requested file either does not exist, or it is a directory!");
            } else {
                // File exists and it is not a directory

                if (fileStoRI.getVirtualFileSystem().getStorageClassType().isTapeEnabled()) {

                    StormEA.setPinned(localFile.getAbsolutePath());
                    fileStoRI.setGroupTapeRead();

                    if (localFile.isOnDisk()) {

//                        chunkData.changeStatusSRM_FILE_PINNED("srmBringOnLine successfully handled!");
                        chunkData.changeStatusSRM_SUCCESS("srmBringOnLine successfully handled!");
                        chunkData.setFileSize(TSizeInBytes.make(localFile.length(), SizeUnit.BYTES));

                    } else {

                        chunkData.changeStatusSRM_REQUEST_INPROGRESS("Recalling file from tape");

                        String voName = null;
                        if (gu instanceof VomsGridUser) {
                            voName = ((VomsGridUser) gu).getVO().getValue();
                        }

                        PersistenceDirector.getDAOFactory().getTapeRecallDAO().insertTask(chunkData,
                                                                                          gsm,
                                                                                          voName,
                                                                                          localFile.getAbsolutePath());
                    }
                } else {
                    chunkData.changeStatusSRM_NOT_SUPPORTED("Tape not supported for this filesystem");
                }

            }
        } catch (SecurityException e) {
            // The check for existence of the File failed because there is a SecurityManager installed that
            // denies read privileges for that File! Perhaps the local system administrator of StoRM set
            // up Java policies that contrast policies described by the PolicyCollector! There is a conflict here!
            chunkData.changeStatusSRM_FAILURE("StoRM is not allowed to work on requested file!");
            failure = true;
            log.error("ATTENTION in BoLChunk! BoLChunk received a SecurityException from Java SecurityManager; StoRM cannot check-existence or check-if-directory for: "
                    + localFile.toString() + "; exception: " + e);
        } catch (Exception e) {
            chunkData.changeStatusSRM_FAILURE("StoRM encountered an unexpected error!");
            failure = true;
            log.error("ERROR in BoLChunk! StoRM process got an unexpected error! " + e);
        }
    }

    /**
     * Method that supplies a String describing this BoLChunk - for scheduler Log purposes! It
     * returns the request token and the SURL that was asked for.
     */
    public String getName() {
        return "BoLChunk of request " + rsd.requestToken() + " for SURL " + chunkData.getFromSURL();
    }

    /**
     * Method used in a callback fashion in the scheduler for separately handling PtG, BoL, PtP and
     * Copy chunks.
     */
    public void choose(Streets s) {
        s.bolStreet(this);
    }

    // //////////////////////////////////////////////////////////////////////////////

    public String getUserDN() {
        return this.gu.getDn();
    }

    public String getSURL() {
        return this.chunkData.getFromSURL().toString();
    }

    public String getRequestToken() {
        return this.rsd.requestToken().toString();
    }

    public boolean isResultSuccess() {
        boolean result = false;
        TStatusCode statusCode = this.chunkData.getStatus().getStatusCode();
        if ((statusCode.getValue().equals(TStatusCode.SRM_FILE_PINNED.getValue()))
                || this.chunkData.getStatus().isSRM_SUCCESS()) {
            result = true;
        }
        return result;
    }
}

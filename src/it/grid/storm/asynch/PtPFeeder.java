package it.grid.storm.asynch;

import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.PtPChunkData;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.SchedulerException;
import it.grid.storm.srm.types.TSURL;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a PrepareToPut Feeder: the Feeder that will handle the
 * srmPrepareToPut statements. It chops a multifile request into its constituent
 * parts.
 *
 * If the request contains nothing to process, an error message gets logged, the
 * number of queued requests is decreased, and the number of finished requests is
 * increased.
 *
 * Each single part of the request is handled as follows: the number of
 * queued requests is decreased, the number of progressing requests is increased,
 * the status of that chunk is changed to SRM_REQUEST_INPROGRESS; the chunk is
 * given to the scheduler for handling. In case the scheduler cannot accept the
 * chunk for any reason, a messagge with the requestToken and the chunk s data is
 * logged, status of the chunk passes to SRM_ABORTED, and at the end the counters
 * are such that the queued-requests is decreased while the finished-requests is
 * increased.
 *
 * @author  EGRID - ICTP Trieste
 * @date    June, 2005
 * @version 2.0
 */
public final class PtPFeeder implements Delegable {

    private static Logger log = LoggerFactory.getLogger(PtPFeeder.class);
    private RequestSummaryData rsd = null; //RequestSummaryData this PtPFeeder refers to.
    private GridUserInterface gu = null; //GridUser for this PtPFeeder.
    private GlobalStatusManager gsm = null; //Overall request status.

    /**
     * Public constructor requiring the RequestSummaryData to which this PtPFeeder
     * refers, as well as the GridUser. In case of null objects, an InvalidPtPFeederAttributesException
     * is thrown; likewise if the OverallRequest object cannot be instantiated for this
     * request.
     */
    public PtPFeeder(RequestSummaryData rsd) throws InvalidPtPFeederAttributesException {
        if (rsd == null) {
            throw new InvalidPtPFeederAttributesException(null, null, null);
        }
        if (rsd.gridUser() == null) {
            throw new InvalidPtPFeederAttributesException(rsd, null, null);
        }
        try {
            gu = rsd.gridUser();
            this.rsd = rsd;
            gsm = new GlobalStatusManager(rsd.requestToken());
        } catch (InvalidOverallRequestAttributeException e) {
            log.error("ATTENTION in PtPFeeder! Programming bug when creating GlobalStatusManager! " + e);
            throw new InvalidPtPFeederAttributesException(rsd, gu, null);
        }
    }

    /**
     * This method splits a multifile request; it then creates the necessary tasks and
     * loads them into the PtP chunk scheduler.
     */
    public void doIt() {
        log.debug("PtPFeeder: pre-processing " + rsd.requestToken()); //info
        //Get all parts in request
        Collection chunks = PtPChunkCatalog.getInstance().lookup(rsd.requestToken());
        if (chunks.isEmpty()) {
            log.warn("ATTENTION in PtPFeeder! This SRM put request contained nothing to process! " + rsd.requestToken());
            RequestSummaryCatalog.getInstance().failRequest(rsd, "This SRM put request contained nothing to process!");
        } else {
            manageChunks(chunks);
            log.debug("PtPFeeder: finished pre-processing " + rsd.requestToken()); //info
        }
    }

    /**
     * Private method that handles the Collection of chunks associated with
     * the srm command!
     */
    private void manageChunks(Collection chunks) {
        log.debug("PtPFeeder: number of chunks in request " + chunks.size());
        PtPChunkData auxChunkData; //chunk currently being processed
        for (Iterator i = chunks.iterator(); i.hasNext();) {
            auxChunkData = (PtPChunkData) i.next();
            gsm.addChunk(auxChunkData); //add chunk for global status consideration
            if (TSURL.isValid(auxChunkData.toSURL())) {
                manage(auxChunkData); //manage the request
            } else {
                //toSURL does _not_ correspond to this installation of StoRM: fail chunk!
                log.warn("PtPFeeder: srmPtP contract violation! toSURL does not refer to this machine!");
                log.warn("Request: " + rsd.requestToken());
                log.warn("Chunk: " + auxChunkData);
                auxChunkData.changeStatusSRM_FAILURE("SRM protocol violation! Cannot do an srmPtP of a SURL that is not local!");
                PtPChunkCatalog.getInstance().update(auxChunkData); //update persistence!!!
                gsm.failedChunk(auxChunkData); //inform global status computation of the chunk s failure
            }
        }
        gsm.finishedAdding(); //no more chunks need to be cosidered for the overall status computation
    }

    /**
     * Private method that handles the chunk!
     */
    private void manage(PtPChunkData auxChunkData) {
        log.debug("PtPFeeder - scheduling... ");
        try {
            auxChunkData.changeStatusSRM_REQUEST_INPROGRESS("srmPrepareToPut chunk is being processed!"); //change status of this chunk to being processed!
            PtPChunkCatalog.getInstance().update(auxChunkData); //update persistence!!!
            SchedulerFacade.getInstance().chunkScheduler().schedule(new PtPChunk(gu, rsd, auxChunkData, gsm)); //hand it to scheduler!
            log.debug("PtPFeeder - chunk scheduled.");
        } catch (InvalidPtPChunkAttributesException e) {
            //for some reason gu, or, rsd, or auxChunkData may be null! This should not be so!
            log.error("UNEXPECTED ERROR in PtPFeeder! Chunk could not be created!\n" + e);
            auxChunkData.changeStatusSRM_FAILURE("StoRM internal error does not allow this chunk to be processed!");
            PtPChunkCatalog.getInstance().update(auxChunkData); //update persistence!!!
            gsm.failedChunk(auxChunkData);
        } catch (SchedulerException e) {
            //Internal error of scheduler!
            log.error("UNEXPECTED ERROR in ChunkScheduler! Chunk could not be scheduled!\n" + e);
            auxChunkData.changeStatusSRM_FAILURE("StoRM internal scheduler error prevented this chunk from being processed!");
            PtPChunkCatalog.getInstance().update(auxChunkData); //update persistence!!!
            gsm.failedChunk(auxChunkData);
        }
    }

    /**
     * Method used by chunk scheduler for internal logging; it returns the request
     * token!
     */
    public String getName() {
        return "PtPFeeder of request: " + rsd.requestToken();
    }
}

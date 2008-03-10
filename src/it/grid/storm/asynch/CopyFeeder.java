package it.grid.storm.asynch;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import it.grid.storm.config.Configuration;

import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.SchedulerException;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.CopyChunkCatalog;
import it.grid.storm.catalogs.CopyChunkData;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.common.types.EndPoint;



/**
 * This class represents a Copy Feeder: the Feeder that will handle the
 * srmCopy statements. It chops a multifile request into its constituent
 * parts. Recursive chunks will also get expanded.
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
 * FOR NOW RECURSIVE REQUESTS ARE *NOT* HANDLED! The chunk fails with SRM_ABORT
 * and an appropriate error string.
 *
 * @author  EGRID - ICTP Trieste
 * @date    September, 2005
 * @version 2.0
 */
public final class CopyFeeder implements Delegable {

    private static Logger log = Logger.getLogger("asynch");
    private RequestSummaryData rsd = null; //RequestSummaryData this PtPFeeder refers to.
    private VomsGridUser gu = null; //GridUser for this PtPFeeder.
    private GlobalStatusManager gsm = null; //Overall request status.

    /**
     * Public constructor requiring the RequestSummaryData to which this CopyFeeder
     * refers to, as well as the GridUser. In case of null objects, an
     * InvalidCopyFeederAttributesException is thrown.
     */
    public CopyFeeder(RequestSummaryData rsd) throws InvalidCopyFeederAttributesException {
        if (rsd==null) throw new InvalidCopyFeederAttributesException(null,null,null);
        if (rsd.gridUser()==null) throw new InvalidCopyFeederAttributesException(rsd,null,null);
        try {
            this.gu = rsd.gridUser();
            this.rsd = rsd;
            this.gsm = new GlobalStatusManager(rsd.requestToken());
        } catch (InvalidOverallRequestAttributeException e) {
            log.error("ATTENTION in CopyFeeder! Programming bug when creating GlobalStatusManager! "+e);
            throw new InvalidCopyFeederAttributesException(rsd,gu,gsm);
        }
    }





    /**
     * This method splits a multifile request; it then creates the necessary tasks and
     * loads them into the Copy chunk scheduler.
     */
    public void doIt() {
        log.debug("CopyFeeder: pre-processing "+rsd.requestToken()); //info
        //Get all parts in request
        Collection chunks = CopyChunkCatalog.getInstance().lookup(rsd.requestToken());
        if (chunks.isEmpty()) {
            log.warn("ATTENTION in CopyFeeder! This SRM Copy request contained nothing to process! " + rsd.requestToken());
            RequestSummaryCatalog.getInstance().failRequest(rsd,"This SRM Copy request contained nothing to process!");
        } else {
            manageChunks(chunks);
            log.debug("CopyFeeder: finished pre-processing "+rsd.requestToken()); //info
        }
    }

    /**
     * Private method that handles the Collection of chunks associated with
     * the srm command!
     */
    private void manageChunks(Collection chunks) {
        log.debug("CopyFeeder: number of chunks in request "+chunks.size()); //info
        CopyChunkData auxChunkData; //chunk currently being processed
        int counter=0; //counter of the number of chunk retrieved
        for (Iterator i = chunks.iterator(); i.hasNext(); ) {
            auxChunkData = (CopyChunkData) i.next();
            gsm.addChunk(auxChunkData); //add chunk for global status consideration
            manage(auxChunkData,counter++); //manage the request
        }
        gsm.finishedAdding(); //no more chunks need to be cosidered for the overall status computation
    }

    /**
     * Private method that handles the chunk!
     */
    private void manage(CopyChunkData auxChunkData,int counter) {
        log.debug("CopyFeeder: scheduling chunk... "); //info
        try {
            auxChunkData.changeStatusSRM_REQUEST_INPROGRESS("srmCopy chunk is being processed!"); //change status of this chunk to being processed!
            CopyChunkCatalog.getInstance().update(auxChunkData); //update persistence!!!
            boolean okfrom = correct(auxChunkData.fromSURL());
            boolean okto = correct(auxChunkData.toSURL());
            if (okfrom && okto) {
                //source and destination are the same physical machine!
                //make a local copy!
                //
                //For now it is being handled as a special case of push copy!
                //MUST BE CHANGED SOON!!! ONLY FOR DEBUG PURPOSES!!!
                log.info("CopyFeeder: chunk is localCopy.");
                log.debug("Request: " + rsd.requestToken());
                log.debug("Chunk: " + auxChunkData);
                SchedulerFacade.getInstance().chunkScheduler().schedule(new PushCopyChunk(gu,rsd,auxChunkData,counter,gsm));
                log.info("CopyFeeder: chunk scheduled.");
            } else if (okfrom && (!okto)) {
                //source is this machine, but destination is elsewhere!
                //make a push copy to destination!
                log.info("CopyFeeder: chunk is pushCopy.");
                log.debug("Request: " + rsd.requestToken());
                log.debug("Chunk: " + auxChunkData);
                SchedulerFacade.getInstance().chunkScheduler().schedule(new PushCopyChunk(gu,rsd,auxChunkData,counter,gsm));
                log.info("CopyFeeder: chunk scheduled.");
            } else if ((!okfrom) && okto) {
                //destiantion is this machine, but _source_ is elsewhere!
                //make a pull copy from the source!
                //
                //WARNING!!! OPERATION NOT SUPPORTED!!! MUST BE CHANGED SOON!!!
                log.warn("CopyFeeder: srmCopy in pull mode NOT supported yet!");
                log.debug("Request: " + rsd.requestToken());
                log.debug("Chunk: " + auxChunkData);
                auxChunkData.changeStatusSRM_NOT_SUPPORTED("srmCopy in pull mode NOT supported yet!");
                CopyChunkCatalog.getInstance().update(auxChunkData); //update persistence!!!
                gsm.failedChunk(auxChunkData); //inform global status computation of the chunk s failure
            } else {
                //boolean condition is (!names.contains(from) && !names.contains(to))
                //operation between two foreign machines!
                //it is forbidden!
                log.warn("CopyFeeder: srmCopy contract violation! Neither fromSURL nor toSURL are this machine! Cannot do a third party SRM transfer as per protocol!");
                log.warn("Request: " + rsd.requestToken());
                log.warn("Chunk: " + auxChunkData);
                auxChunkData.changeStatusSRM_FAILURE("SRM protocol violation! Cannot do an srmCopy between third parties!");
                CopyChunkCatalog.getInstance().update(auxChunkData); //update persistence!!!
                gsm.failedChunk(auxChunkData); //inform global status computation of the chunk s failure
            }
        } catch (InvalidCopyChunkAttributesException e) {
            //for some reason gu, rsd or auxChunkData may be null! This should not be so!
            log.error("UNEXPECTED ERROR in CopyFeeder! Chunk could not be created!\n" +e);
            log.error("Request: " + rsd.requestToken());
            log.error("Chunk: " + auxChunkData);
            auxChunkData.changeStatusSRM_FAILURE("StoRM internal error does not allow this chunk to be processed!");
            CopyChunkCatalog.getInstance().update(auxChunkData); //update persistence!!!
            gsm.failedChunk(auxChunkData); //inform global status computation of the chunk s failure
        } catch (SchedulerException e) {
            //Internal error of scheduler!
            log.error("UNEXPECTED ERROR in ChunkScheduler! Chunk could not be scheduled!\n" +e);
            log.error("Request: " + rsd.requestToken());
            log.error("Chunk: " + auxChunkData);
            auxChunkData.changeStatusSRM_FAILURE("StoRM internal scheduler error prevented this chunk from being processed!");
            CopyChunkCatalog.getInstance().update(auxChunkData); //update persistence!!!
            gsm.failedChunk(auxChunkData); //inform global status computation of the chunk s failure
        }
    }

    /**
     * Auxiliary method that returns true if the supplied TSURL corresponds to the host
     * where StoRM is installed.
     */
    private boolean correct(TSURL surl) {
        String machine = surl.sfn().machine().toString().toLowerCase();
        EndPoint ep = surl.sfn().endPoint();
        int port = surl.sfn().port().toInt();
        List stormNames = Configuration.getInstance().getListOfMachineNames();
        String stormEndpoint = Configuration.getInstance().getServiceEndpoint().toLowerCase();
        int stormPort = Configuration.getInstance().getFEPort();
        log.debug("COPY FEEDER: machine="+machine+"; port="+port+"; endPoint="+ep.toString());
        log.debug("COPY FEEDER: storm-machines="+stormNames+"; storm-port="+stormPort+"; endPoint="+stormEndpoint);
        if (!stormNames.contains(machine)) return false;
        if (stormPort!=port) return false;
        if ((!ep.isEmpty()) && (!ep.toString().toLowerCase().equals(stormEndpoint))) return false;
        return true;
    }

    /**
     * Method used by chunk scheduler for internal logging; it returns the request
     * token!
     */
    public String getName() {
       return "CopyFeeder of request: "+rsd.requestToken();
    }
}

package it.grid.storm.catalogs;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.InvalidTDirOptionAttributesException;
import it.grid.storm.srm.types.InvalidTLifeTimeAttributeException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.InvalidTTURLAttributesException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents StoRMs PtGChunkCatalog: it collects PtGChunkData and
 * provides methods for looking up a PtGChunkData based on TRequestToken, as
 * well as for adding a new entry and removing an existing one.
 *
 * @author  EGRID - ICTP Trieste
 * @date    April 26th, 2005
 * @version 4.0
 */
public class PtGChunkCatalog {
    private static final Logger log = LoggerFactory.getLogger(PtGChunkCatalog.class);
    private static final PtGChunkCatalog cat = new PtGChunkCatalog(); //only instance of PtGChunkCatalog present in StoRM!
    private final PtGChunkDAO dao = PtGChunkDAO.getInstance();
    private final Timer transiter = new Timer(); //Timer object in charge of transiting expired requests from SRM_FILE_PINNED to SRM_RELEASED!
    private final long delay = Configuration.getInstance().getTransitInitialDelay()*1000;  //Delay time before starting cleaning thread!
    private final long period = Configuration.getInstance().getTransitTimeInterval()*1000; //Period of execution of cleaning!

    /**
     * Private constructor that starts the internal timer needed to periodically
     * check and transit requests whose pinLifetime has expired and are in
     * SRM_FILE_PINNED, to SRM_RELEASED.
     */
    private PtGChunkCatalog() {
        TimerTask transitTask = new TimerTask() {
            @Override
            public void run() {
                transitExpiredSRM_FILE_PINNED();
            }
        };
        transiter.scheduleAtFixedRate(transitTask,delay,period);
    }

    /**
     * Method that returns the only instance of PtGChunkCatalog available.
     */
    public static PtGChunkCatalog getInstance() {
        return cat;
    }



    /**
     * Method that returns a Collection of PtGChunkData Objects matching the
     * supplied TRequestToken.
     *
     * If any of the data associated to the TRequestToken is not well formed and
     * so does not allow a PtGChunkData Object to be created, then that part of
     * the request is dropped and gets logged, and the processing continues with
     * the next part. All valid chunks get returned: the others get dropped.
     *
     * If there are no chunks to process then an empty Collection is returned,
     * and a messagge gets logged.
     */
    synchronized public Collection lookup(TRequestToken rt) {
        Collection c = dao.find(rt);
        log.debug("PtG CHUNK CATALOG: retrieved data "+c);
        List list = new ArrayList();
        if (c.isEmpty()) {
            log.warn("PtG CHUNK CATALOG! No chunks found in persistence for specified request: "+rt);
        } else {
            PtGChunkDataTO auxTO;
            PtGChunkData aux;
            for (Iterator i = c.iterator(); i.hasNext(); ) {
                auxTO = (PtGChunkDataTO) i.next();
                aux = makeOne(auxTO,rt);
                if (aux!=null) {
                    list.add(aux);
                }
            }
        }
        log.debug("PtG CHUNK CATALOG: returning "+ list);
        return list;
    }




    /**
     * Refresh method.
     * TODO
     * THIS IS A WORK IN PROGRESS!!!!
     * This method have to synch the ChunkData information with the
     * database status.
     * @param auxTO
     * @param PtGChunkData inputChunk
     * @return PtGChunkData outputChunk
     */

    synchronized public PtGChunkData refreshStatus(PtGChunkData inputChunk) {
        //Call the dao refresh method to synch with the db status
        PtGChunkDataTO auxTO = dao.refresh(inputChunk.primaryKey());

        log.debug("PtG CHUNK CATALOG: retrieved data "+auxTO);
        if (auxTO == null) {
            log.warn("PtG CHUNK CATALOG! Empty TO found in persistence for specified request: "+inputChunk.primaryKey());
        } else {
            /*
             * In this first version the only field updated is the Status.
             * Once updated, the new status is rewritten into the input ChunkData
             */

            //status
            TReturnStatus status=null;
            TStatusCode code = StatusCodeConverter.getInstance().toSTORM(auxTO.status());
            if (code==TStatusCode.EMPTY) {
                //sb.append("\nRetrieved StatusCode was not recognised: "+auxTO.status());
            } else {
                try {
                    status = new TReturnStatus(code,auxTO.errString());
                } catch (InvalidTReturnStatusAttributeException e) {
                    //sb.append("\n");
                    //sb.append(e);
                }
            }
            inputChunk.setStatus(status);

            //TTURL
            TTURL turl = null;
            try {
                turl = TTURL.makeFromString(auxTO.turl());
            } catch (InvalidTTURLAttributesException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                log.info("PtGChunkCatalog (FALSE-ERROR-in-abort-refresh-status?): built a TURL with protocol NULL (retrieved from the DB..)");
            }
            inputChunk.setTransferURL(turl);


        }

        return inputChunk;
    }


    private PtGChunkData makeOne(PtGChunkDataTO auxTO, TRequestToken rt) {
        StringBuffer sb = new StringBuffer();
        //fromSURL
        TSURL fromSURL=null;
        try {
            fromSURL = TSURL.makeFromString(auxTO.fromSURL());
        } catch (InvalidTSURLAttributesException e) {
            sb.append(e);
        }
        //lifeTime
        TLifeTimeInSeconds lifeTime=null;
        try {
            lifeTime = TLifeTimeInSeconds.make(PinLifetimeConverter.getInstance().toStoRM(auxTO.lifeTime()), TimeUnit.SECONDS);
        } catch (InvalidTLifeTimeAttributeException e) {
            sb.append("\n");
            sb.append(e);
        }
        //dirOption
        TDirOption dirOption=null;
        try {
            dirOption = new TDirOption(auxTO.dirOption(),auxTO.allLevelRecursive(),auxTO.numLevel());
        } catch (InvalidTDirOptionAttributesException e) {
            sb.append("\n");
            sb.append(e);
        }
        //transferProtocols
        TURLPrefix transferProtocols = TransferProtocolListConverter.toSTORM(auxTO.protocolList());
        if (transferProtocols.size()==0) {
            sb.append("\nEmpty list of TransferProtocols or could not translate TransferProtocols!");
            transferProtocols = null; //fail construction of PtGChunkData!
        }
        //fileSize
        TSizeInBytes fileSize=null;
        try {
            fileSize = TSizeInBytes.make(auxTO.fileSize(),SizeUnit.BYTES);
        } catch (InvalidTSizeAttributesException e) {
            sb.append("\n");
            sb.append(e);
        }
        //status
        TReturnStatus status=null;
        TStatusCode code = StatusCodeConverter.getInstance().toSTORM(auxTO.status());
        if (code==TStatusCode.EMPTY) {
            sb.append("\nRetrieved StatusCode was not recognised: "+auxTO.status());
        } else {
            try {
                status = new TReturnStatus(code,auxTO.errString());
            } catch (InvalidTReturnStatusAttributeException e) {
                sb.append("\n");
                sb.append(e);
            }
        }
        //transferURL
        TTURL transferURL = TTURL.makeEmpty(); //whatever is read is just meaningless because PtG will fill it in!!! So create an Empty TTURL by default! Vital to avoid problems with unknown DPM NULL/EMPTY logic policy!
        //make PtGChunkData
        PtGChunkData aux=null;
        try {
            aux = new PtGChunkData(rt, fromSURL, lifeTime, dirOption, transferProtocols, fileSize, status, transferURL);
            aux.setPrimaryKey(auxTO.primaryKey());
        } catch (InvalidPtGChunkDataAttributesException e) {
            dao.signalMalformedPtGChunk(auxTO);
            log.warn("PtG CHUNK CATALOG! Retrieved malformed PtG chunk data from persistence. Dropping chunk from request "+rt);
            log.warn(e.getMessage(), e);
            log.warn(sb.toString());
        }
        //end...
        return aux;
    }




    /**
     * Method that returns a Collection of ReducedPtGChunkData Objects associated to the supplied
     * TRequestToken.
     * 
     * If any of the data retrieved for a given chunk is not well formed and so does not allow a
     * ReducedPtGChunkData Object to be created, then that chunk is dropped and gets logged, while processing
     * continues with the next one. All valid chunks get returned: the others get dropped.
     * 
     * If there are no chunks associated to the given TRequestToken, then an empty Collection is returned and
     * a message gets logged.
     */
    synchronized public Collection lookupReducedPtGChunkData(TRequestToken rt) {
        Collection<ReducedPtGChunkDataTO> cl = dao.findReduced(rt.getValue());
        log.debug("PtG CHUNK CATALOG: retrieved data " + cl);
        List<ReducedPtGChunkData> list = new ArrayList<ReducedPtGChunkData>();
        if (cl.isEmpty()) {
            log.debug("PtG CHUNK CATALOG! No chunks found in persistence for " + rt);
        } else {
            for (ReducedPtGChunkDataTO auxTO : cl) {
                ReducedPtGChunkData aux = makeReducedPtGChunkData(auxTO);
                if (aux != null) {
                    list.add(aux);
                }
            }
            log.debug("PtG CHUNK CATALOG: returning " + list);
        }
        return list;
    }

    /**
     * Method that returns a Collection of ReducedPtGChunkData Objects matching
     * the supplied GridUser and Collection of TSURLs.
     *
     * If any of the data retrieved for a given chunk is not well formed and so
     * does not allow a ReducedPtGChunkData Object to be created, then that chunk
     * is dropped and gets logged, while processing continues with the next one.
     * All valid chunks get returned: the others get dropped.
     *
     * If there are no chunks associated to the given GridUser and Collection of
     * TSURLs, then an empty Collection is returned and a message gets logged.
     */
    synchronized public Collection lookupReducedPtGChunkData(GridUserInterface gu, Collection c) {
        Object[] surlsobj = (new ArrayList(c)).toArray();
        int n = surlsobj.length;
        String[] surls = new String[n];
        for (int i=0; i<n; i++) {
            surls[i] = ((TSURL)surlsobj[i]).toString();
        }
        Collection cl = dao.findReduced(gu.getDn(),surls);
        log.debug("PtG CHUNK CATALOG: retrieved data "+cl);
        List list = new ArrayList();
        if (cl.isEmpty()) {
            log.debug("PtG CHUNK CATALOG! No chunks found in persistence for "+gu+" "+cl);
        } else {
            ReducedPtGChunkDataTO auxTO;
            ReducedPtGChunkData aux;
            for (Iterator i = cl.iterator(); i.hasNext(); ) {
                auxTO = (ReducedPtGChunkDataTO) i.next();
                aux = makeReducedPtGChunkData(auxTO);
                if (aux!=null) {
                    list.add(aux);
                }
            }
            log.debug("PtG CHUNK CATALOG: returning "+ list);
        }
        return list;
    }

    private ReducedPtGChunkData makeReducedPtGChunkData(ReducedPtGChunkDataTO auxTO) {
        StringBuffer sb = new StringBuffer();
        //fromSURL
        TSURL fromSURL=null;
        try {
            fromSURL = TSURL.makeFromString(auxTO.fromSURL());
        } catch (InvalidTSURLAttributesException e) {
            sb.append(e);
        }
        //status
        TReturnStatus status=null;
        TStatusCode code = StatusCodeConverter.getInstance().toSTORM(auxTO.status());
        if (code==TStatusCode.EMPTY) {
            sb.append("\nRetrieved StatusCode was not recognised: "+auxTO.status());
        } else {
            try {
                status = new TReturnStatus(code,auxTO.errString());
            } catch (InvalidTReturnStatusAttributeException e) {
                sb.append("\n");
                sb.append(e);
            }
        }
        //make ReducedPtGChunkData
        ReducedPtGChunkData aux=null;
        try {
            aux = new ReducedPtGChunkData(fromSURL, status);
            aux.setPrimaryKey(auxTO.primaryKey());
        } catch (InvalidReducedPtGChunkDataAttributesException e) {
            log.warn("PtG CHUNK CATALOG! Retrieved malformed Reduced PtG chunk data from persistence: dropping reduced chunk...");
            log.warn(e.getMessage(), e);
            log.warn(sb.toString());
        }
        //end...
        return aux;
    }







    /**
     * Method used to update into Persistence a retrieved PtGChunkData. In case
     * any error occurs, the operation does not proceed but no Exception is
     * thrown. Error messagges get logged.
     *
     * Only fileSize, StatusCode, errString and transferURL are updated. Likewise
     * for the request pinLifetime.
     */
    synchronized public void update(PtGChunkData cd) {
        PtGChunkDataTO to = new PtGChunkDataTO();
        to.setPrimaryKey(cd.primaryKey()); //primary key needed by DAO Object
        to.setFileSize(cd.fileSize().value());
        to.setStatus(StatusCodeConverter.getInstance().toDB(cd.status().getStatusCode()));
        to.setErrString(cd.status().getExplanation());
        to.setTurl(TURLConverter.getInstance().toDB(cd.transferURL().toString()));
        to.setLifeTime(PinLifetimeConverter.getInstance().toDB(cd.lifeTime().value()));
        dao.update(to);
    }





    /**
     * Method used to add into Persistence a new entry. The supplied PtGChunkData
     * gets the primary key changed to the value assigned in Persistence.
     *
     * This method is intended to be used by a recursive PtG request: the parent
     * request supplies a directory which must be expanded, so all new children
     * requests resulting from the files in the directory are added into
     * persistence.
     *
     * So this method does _not_ add a new SRM prepare_to_get request into the DB!
     *
     * The only children data written into the DB are: sourceSURL, TDirOption,
     * statusCode and explanation.
     *
     * In case of any error the operation does not proceed, but no Exception is
     * thrown! Proper messages get logged by underlaying DAO.
     */
    synchronized public void addChild(PtGChunkData chunkData) {
        PtGChunkDataTO to = new PtGChunkDataTO();
        to.setRequestToken(chunkData.requestToken().toString()); //needed for now to find ID of request! Must be changed soon!
        to.setFromSURL(chunkData.fromSURL().toString());
        to.setAllLevelRecursive(chunkData.dirOption().isAllLevelRecursive());
        to.setDirOption(chunkData.dirOption().isDirectory());
        to.setNumLevel(chunkData.dirOption().getNumLevel());
        to.setStatus(StatusCodeConverter.getInstance().toDB(chunkData.status().getStatusCode()));
        to.setErrString(chunkData.status().getExplanation());
        dao.addChild(to); //add the entry and update the Primary Key field!
        chunkData.setPrimaryKey(to.primaryKey()); //set the assigned PrimaryKey!
    }

    /**
     * Method used to add into Persistence a new entry. The supplied PtGChunkData
     * gets the primary key changed to the value assigned in the Persistence. The
     * method requires the GridUser to whom associate the added request.
     *
     * This method is intended to be used by an srmCopy request in push mode
     * which implies a local srmPtG. The only fields from PtGChunkData that are
     * considered are: the requestToken, the sourceSURL, the pinLifetime, the
     * dirOption, the protocolList, the status and error string.
     *
     * So this method _adds_ a new SRM prepare_to_get request into the DB!
     *
     * In case of any error the operation does not proceed, but no Exception is
     * thrown! The underlaying DAO logs proper error messagges.
     */
    synchronized public void add(PtGChunkData chunkData, GridUserInterface gu) {
        PtGChunkDataTO to = new PtGChunkDataTO();
        to.setRequestToken(chunkData.requestToken().toString());
        to.setFromSURL(chunkData.fromSURL().toString());
        to.setLifeTime(new Long(chunkData.lifeTime().value()).intValue() );
        to.setAllLevelRecursive(chunkData.dirOption().isAllLevelRecursive());
        to.setDirOption(chunkData.dirOption().isDirectory());
        to.setNumLevel(chunkData.dirOption().getNumLevel());
        to.setProtocolList(TransferProtocolListConverter.toDB(chunkData.desiredProtocols()));
        to.setStatus(StatusCodeConverter.getInstance().toDB(chunkData.status().getStatusCode()));
        to.setErrString(chunkData.status().getExplanation());
        dao.addNew(to,gu.getDn()); //add the entry and update the Primary Key field!
        chunkData.setPrimaryKey(to.primaryKey()); //set the assigned PrimaryKey!
    }



    /**
     * Method used to establish if in Persistence there is a PtGChunkData working
     * on the supplied SURL, and whose state is SRM_FILE_PINNED, in which
     * case true is returned. In case none are found or there is any problem,
     * false is returned. This method is intended to be used by srmMv.
     */
    synchronized public boolean isSRM_FILE_PINNED(TSURL surl) {
        int n = dao.numberInSRM_FILE_PINNED(surl.toString());
        return (n>0);
    }

    /**
     * Method used to transit the specified Collection of ReducedPtGChunkData from SRM_FILE_PINNED to
     * SRM_RELEASED. Chunks in any other starting state are not transited. In case of any error nothing is
     * done, but proper error messages get logged by the underlaying DAO.
     */
    synchronized public void transitSRM_FILE_PINNEDtoSRM_RELEASED(Collection<ReducedPtGChunkData> chunks,
            TRequestToken token) {
        
        if (chunks.isEmpty()) {
            return;
        }
        
        List<Long> aux = new ArrayList<Long>();
        
        for (ReducedPtGChunkData auxData : chunks) {
            aux.add(new Long(auxData.primaryKey()));
        }
        
        int n = aux.size();
        long[] auxlong = new long[n];
        for (int i = 0; i < n; i++) {
            auxlong[i] = ((Long) aux.get(i)).longValue();
        }
        
        dao.transitSRM_FILE_PINNEDtoSRM_RELEASED(auxlong, token);
    }

    /**
     * This method is intended to be used by srmRm to transit all PtG chunks on
     * the given SURL which are in the SRM_FILE_PINNED state, to SRM_ABORTED.
     * The supplied String will be used as explanation in those chunks return
     * status. The global status of the request is _not_ changed.
     *
     * The TURL of those requests will automatically be set to empty. Notice that
     * both removeAllJit(SURL) and removeVolatile(SURL) are automatically invoked
     * on PinnedFilesCatalog, to remove any entry and corresponding physical ACLs.
     *
     * Beware, that the chunks may be part of requests that have finished, or that
     * still have not finished because other chunks are being processed.
     */
    synchronized public void transitSRM_FILE_PINNEDtoSRM_ABORTED(TSURL surl,String explanation) {
        if (explanation==null) {
            explanation="";
        }
        dao.transitSRM_FILE_PINNEDtoSRM_ABORTED(surl.toString(),explanation);
        //PinnedFilesCatalog.getInstance().removeAllJit(surl);
        //PinnedfilesCatalog.getInstance().removeVolatile(surl);
    }



    /**
     * Method used to force transition to SRM_RELEASED from SRM_FILE_PINNED,
     * of all PtG Requests whose pinLifetime has expired and the state still has
     * not been changed (a user forgot to run srmReleaseFiles)!
     */
    synchronized public void transitExpiredSRM_FILE_PINNED() {
        dao.transitExpiredSRM_FILE_PINNED();
    }
}


package it.grid.storm.catalogs;

import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.common.types.StFN;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.InvalidTLifeTimeAttributeException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
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
 * Class that represents StoRMs PtPChunkCatalog: it collects PtPChunkData and provides methods for looking up a
 * PtPChunkData based on TRequestToken, as well as for updating data into persistence. Methods are also supplied to
 * evaluate if a SURL is in SRM_SPACE_AVAILABLE state, and to transit expired SURLs in SRM_SPACE_AVAILABLE state to
 * SRM_FILE_LIFETIME_EXPIRED.
 * 
 * @author EGRID - ICTP Trieste
 * @date June, 2005
 * @version 3.0
 */
public class PtPChunkCatalog {
    private static final Logger log = LoggerFactory.getLogger(PtPChunkCatalog.class);
    /** only instance of PtPChunkCatalog present in StoRM! */
    private static final PtPChunkCatalog cat = new PtPChunkCatalog();
    private final PtPChunkDAO dao = PtPChunkDAO.getInstance();
    /** Timer object in charge of transiting expired requests from SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED! */
    private final Timer transiter = new Timer();
    /** Delay time before starting cleaning thread! Set to 1 minute */
    private final long delay = Configuration.getInstance().getTransitInitialDelay() * 1000;
    /** Period of execution of cleaning! Set to 1 hour */
    private final long period = Configuration.getInstance().getTransitTimeInterval() * 1000;

    /**
     * Private constructor that starts the internal timer needed to periodically check and transit requests whose
     * pinLifetime has expired and are in SRM_SPACE_AVAILABLE, to SRM_FILE_LIFETIME_EXPIRED. Moreover, the physical file
     * corresponding to the SURL gets removed; then any JiT entry gets removed, except those on traverse for the parent
     * directory; finally any volatile entry gets removed too.
     */
    private PtPChunkCatalog() {
        TimerTask transitTask = new TimerTask() {
            @Override
            public void run() {
                List<Long> ids = transitExpiredSRM_SPACE_AVAILABLE();
                if (!ids.isEmpty()) {
                    List<ReducedPtPChunkData> reduced = fetchReducedPtPChunkDataFor(ids);
                    if (reduced.isEmpty()) {
                        log.error("ATTENTION in PtP CHUNK CATALOG! Attempt to handle physical files for transited expired entries failed! No data could be translated from persitence for PtP Chunks with ID "
                                + ids);
                    }
                    for (ReducedPtPChunkData auxReduced : reduced) {
                        try {
                            StoRI auxStoRI = NamespaceDirector.getNamespace()
                                                              .resolveStoRIbySURL(auxReduced.toSURL());
                            PFN auxPFN = auxStoRI.getPFN();
                            //
                            // remove file
                            try {
                                log.info("PtP CHUNK CATALOG: transition of expired SRM_SPACE_AVAILABLE. Deleting file "
                                        + auxPFN);
                                LocalFile auxFile = NamespaceDirector.getNamespace()
                                                                     .resolveStoRIbyPFN(auxPFN)
                                                                     .getLocalFile();
                                boolean ok = auxFile.delete();
                                if (!ok) {
                                    throw new Exception("Java File deletion failed!");
                                }
                            } catch (Exception e) {
                                // log exceptions
                                log.error("PtP CHUNK CATALOG: transition of expired SRM_SPACE_AVAILABLE. Request status transited to SRM_FILE_LIFETIME_EXPIRED but physical file "
                                        + auxPFN + " could NOT be deleted!");
                                log.error(e.getMessage(), e);
                            }
                            //
                            // remove any Jit
                            VolatileAndJiTCatalog.getInstance().removeAllJiTsOn(auxPFN);
                            //
                            // remove any Volatile
                            VolatileAndJiTCatalog.getInstance().removeVolatile(auxPFN);
                        } catch (NamespaceException e) {
                            log.error("ATTENTION IN PtP CHUNK CATALOG! While transiting expired requests in SRM_SPACE_AVAILABLE, there were problems with the NameSpace!\nChunk: "
                                    + auxReduced + "\nError: " + e);
                        }
                    }
                }
            }
        };
        transiter.scheduleAtFixedRate(transitTask, delay, period);
    }

    /**
     * Method that returns the only instance of PtPChunkCatalog available.
     */
    public static PtPChunkCatalog getInstance() {
        return cat;
    }

    /**
     * Method that returns a Collection of PtPChunkData Objects matching the supplied TRequestToken. If any of the data
     * associated to the TRequestToken is not well formed and so does not allow a PtPChunkData Object to be created,
     * then that part of the request is dropped, gets logged and an attempt is made to write in the DB that the chunk
     * was malformed; the processing continues with the next part. Only the valid chunks get returned. If there are no
     * chunks to process then an empty Collection is returned, and a messagge gets logged. NOTE! Chunks in SRM_ABORTED
     * status are NOT returned! This is imporant because this method is intended to be used by the Feeders to fetch all
     * chunks in the request, and aborted chunks should not be picked up for processing!
     */
    synchronized public Collection<PtPChunkData> lookup(final TRequestToken rt) {
        Collection<PtPChunkDataTO> c = dao.find(rt);
        log.debug("PtPChunkCatalog: retrieved data " + c);
        List<PtPChunkData> list = new ArrayList<PtPChunkData>();
        if (c.isEmpty()) {
            log.warn("PtP CHUNK CATALOG! No chunks found in persistence for specified request: " + rt);
        } else {
            for (PtPChunkDataTO auxTO : c) {
                PtPChunkData aux = makeOne(auxTO, rt);
                if (aux != null) {
                    list.add(aux);
                }
            }
        }
        log.debug("PtPChunkCatalog: returning " + list + "\n\n");
        return list;
    }

    /**
     * Method that returns a Collection of ReducedPtPChunkData Objects associated to the supplied TRequestToken. If any
     * of the data retrieved for a given chunk is not well formed and so does not allow a ReducedPtPChunkData Object to
     * be created, then that chunk is dropped and gets logged, while processing continues with the next one. All valid
     * chunks get returned: the others get dropped. If there are no chunks associated to the given TRequestToken, then
     * an empty Collection is returned and a messagge gets logged. All ReducedChunks, regardless of their status, are
     * returned.
     */
    synchronized public Collection lookupReducedPtPChunkData(TRequestToken rt) {
        Collection cl = dao.findReduced(rt.getValue());
        log.debug("PtP CHUNK CATALOG: retrieved data " + cl);
        List list = new ArrayList();
        if (cl.isEmpty()) {
            log.debug("PtP CHUNK CATALOG! No chunks found in persistence for " + rt);
        } else {
            ReducedPtPChunkDataTO auxTO;
            ReducedPtPChunkData aux;
            for (Iterator i = cl.iterator(); i.hasNext();) {
                auxTO = (ReducedPtPChunkDataTO) i.next();
                aux = makeReducedPtPChunkData(auxTO);
                if (aux != null) {
                    list.add(aux);
                }
            }
            log.debug("PtP CHUNK CATALOG: returning " + list);
        }
        return list;
    }

    /**
     * Method that returns a Collection of ReducedPtPChunkData Objects corresponding to each of the IDs contained inthe
     * supplied List of Long objects. If any of the data retrieved for a given chunk is not well formed and so does not
     * allow a ReducedPtPChunkData Object to be created, then that chunk is dropped and gets logged, while processing
     * continues with the next one. All valid chunks get returned: the others get dropped. WARNING! If there are no
     * chunks associated to any of the given IDs, no messagge gets written in the logs!
     */
    synchronized public List<ReducedPtPChunkData> fetchReducedPtPChunkDataFor(List<Long> volids) {
        Collection<ReducedPtPChunkDataTO> cl = dao.fetchReduced(volids);
        log.debug("PtP CHUNK CATALOG: fetched data " + cl);
        List<ReducedPtPChunkData> list = new ArrayList<ReducedPtPChunkData>();
        if (cl.isEmpty()) {
            log.debug("PtP CHUNK CATALOG! No chunks found in persistence for " + volids);
        } else {
            ReducedPtPChunkData aux;
            for (ReducedPtPChunkDataTO auxTO : cl) {
                aux = makeReducedPtPChunkData(auxTO);
                if (aux != null) {
                    list.add(aux);
                }
            }
            log.debug("PtP CHUNK CATALOG: returning " + list);
        }
        return list;
    }

    private ReducedPtPChunkData makeReducedPtPChunkData(ReducedPtPChunkDataTO auxTO) {
        StringBuffer sb = new StringBuffer();
        // fromSURL
        TSURL toSURL = null;
        try {
            toSURL = TSURL.makeFromString(auxTO.toSURL());
        } catch (InvalidTSURLAttributesException e) {
            sb.append(e);
        }
        // status
        TReturnStatus status = null;
        TStatusCode code = StatusCodeConverter.getInstance().toSTORM(auxTO.status());
        if (code == TStatusCode.EMPTY) {
            sb.append("\nRetrieved StatusCode was not recognised: " + auxTO.status());
        } else {
            try {
                status = new TReturnStatus(code, auxTO.errString());
            } catch (InvalidTReturnStatusAttributeException e) {
                sb.append("\n");
                sb.append(e);
            }
        }
        // fileStorageType
        TFileStorageType fileStorageType = FileStorageTypeConverter.getInstance()
                                                                   .toSTORM(auxTO.fileStorageType());
        if (fileStorageType == TFileStorageType.EMPTY) {
            sb.append("\nTFileStorageType could not be translated from its String representation! String: "
                    + auxTO.fileStorageType());
            fileStorageType = null; // fail creation of PtPChunk!
        }
        // fileLifetime
        TLifeTimeInSeconds fileLifetime = null;
        try {
            fileLifetime = TLifeTimeInSeconds.make(FileLifetimeConverter.getInstance()
                                                                        .toStoRM(auxTO.fileLifetime()),
                                                   TimeUnit.SECONDS);
        } catch (InvalidTLifeTimeAttributeException e) {
            sb.append("\n");
            sb.append(e);
        }
        // make ReducedPtPChunkData
        ReducedPtPChunkData aux = null;
        try {
            aux = new ReducedPtPChunkData(toSURL, status, fileStorageType, fileLifetime);
            aux.setPrimaryKey(auxTO.primaryKey());
        } catch (InvalidReducedPtPChunkDataAttributesException e) {
            log.warn("PtP CHUNK CATALOG! Retrieved malformed Reduced PtP chunk data from persistence: dropping reduced chunk...");
            log.warn(e.getMessage(), e);
            log.warn(sb.toString());
        }
        // end...
        return aux;
    }

    /**
     * Method that synchronises the supplied PtPChunkData with the information present in Persistence. BE WARNED: a new
     * object is returned, and the original PtPChunkData is left untouched! null is returned in case of any error.
     */
    synchronized public PtPChunkData refreshStatus(PtPChunkData inputChunk) {
        PtPChunkDataTO auxTO = dao.refresh(inputChunk.primaryKey());
        log.debug("PtP CHUNK CATALOG refreshStatus: retrieved data " + auxTO);
        if (auxTO == null) {
            log.warn("PtP CHUNK CATALOG! Empty TO found in persistence for specified request: "
                    + inputChunk.primaryKey());
            return null;
        } else {
            return makeOne(auxTO, inputChunk.requestToken());
        }
    }

    /**
     * Private method used to create a PtPChunkData object, from a PtPChunkDataTO and TRequestToken. If a chunk cannot
     * be created, an error messagge gets logged and an attempt is made to signal in the DB that the chunk is malformed.
     */
    private PtPChunkData makeOne(PtPChunkDataTO auxTO, TRequestToken rt) {
        StringBuffer sb = new StringBuffer();
        // toSURL
        TSURL toSURL = null;
        try {
            toSURL = TSURL.makeFromString(auxTO.toSURL());
        } catch (InvalidTSURLAttributesException e) {
            sb.append(e);
        }
        // pinLifetime
        TLifeTimeInSeconds pinLifetime = null;
        try {
            pinLifetime = TLifeTimeInSeconds.make(PinLifetimeConverter.getInstance()
                                                                      .toStoRM(auxTO.pinLifetime()),
                                                  TimeUnit.SECONDS);
        } catch (InvalidTLifeTimeAttributeException e) {
            sb.append("\n");
            sb.append(e);
        }
        // fileLifetime
        TLifeTimeInSeconds fileLifetime = null;
        try {
            fileLifetime = TLifeTimeInSeconds.make(FileLifetimeConverter.getInstance()
                                                                        .toStoRM(auxTO.fileLifetime()),
                                                   TimeUnit.SECONDS);
        } catch (InvalidTLifeTimeAttributeException e) {
            sb.append("\n");
            sb.append(e);
        }
        // fileStorageType
        TFileStorageType fileStorageType = FileStorageTypeConverter.getInstance()
                                                                   .toSTORM(auxTO.fileStorageType());
        if (fileStorageType == TFileStorageType.EMPTY) {
            sb.append("\nTFileStorageType could not be translated from its String representation! String: "
                    + auxTO.fileStorageType());
            fileStorageType = null; // fail creation of PtPChunk!
        }
        // expectedFileSize
        //
        // WARNING! A converter is used because the DB uses 0 for empty, whereas
        // StoRM object model does allow a 0 size! Since this is an optional field
        // in the SRM specs, null must be converted explicitly to Empty TSizeInBytes
        // because it is indeed well formed!
        TSizeInBytes expectedFileSize = null;
        TSizeInBytes emptySize = TSizeInBytes.makeEmpty();
        long sizeTranslation = SizeInBytesIntConverter.getInstance().toStoRM(auxTO.expectedFileSize());
        if (emptySize.value() == sizeTranslation) {
            expectedFileSize = emptySize;
        } else {
            try {
                expectedFileSize = TSizeInBytes.make(auxTO.expectedFileSize(), SizeUnit.BYTES);
            } catch (InvalidTSizeAttributesException e) {
                sb.append("\n");
                sb.append(e);
            }
        }
        // spaceToken!
        //
        // WARNING! A converter is still needed because of DB logic for missing
        // SpaceToken makes use of NULL, whereas StoRM object model does not allow
        // for null! It makes use of a specific Empty type.
        //
        // Indeed, the SpaceToken field is optional, so a request with a null value
        // for the SpaceToken field in the DB, _is_ well formed!
        TSpaceToken spaceToken = null;
        TSpaceToken emptyToken = TSpaceToken.makeEmpty();
        /** convert empty string representation of DPM into StoRM representation; */
        String spaceTokenTranslation = SpaceTokenStringConverter.getInstance().toStoRM(auxTO.spaceToken());
        if (emptyToken.toString().equals(spaceTokenTranslation)) {
            spaceToken = emptyToken;
        } else {
            try {
                spaceToken = TSpaceToken.make(spaceTokenTranslation);
            } catch (InvalidTSpaceTokenAttributesException e) {
                sb.append("\n");
                sb.append(e);
            }
        }
        // overwriteOption!
        TOverwriteMode overwriteOption = OverwriteModeConverter.getInstance()
                                                               .toSTORM(auxTO.overwriteOption());
        if (overwriteOption == TOverwriteMode.EMPTY) {
            sb.append("\nTOverwriteMode could not be translated from its String representation! String: "
                    + auxTO.overwriteOption());
            overwriteOption = null;
        }
        // transferProtocols
        TURLPrefix transferProtocols = TransferProtocolListConverter.toSTORM(auxTO.protocolList());
        if (transferProtocols.size() == 0) {
            sb.append("\nEmpty list of TransferProtocols or could not translate TransferProtocols!");
            transferProtocols = null; // fail construction of PtPChunkData!
        }
        // status
        TReturnStatus status = null;
        TStatusCode code = StatusCodeConverter.getInstance().toSTORM(auxTO.status());
        if (code == TStatusCode.EMPTY) {
            sb.append("\nRetrieved StatusCode was not recognised: " + auxTO.status());
        } else {
            try {
                status = new TReturnStatus(code, auxTO.errString());
            } catch (InvalidTReturnStatusAttributeException e) {
                sb.append("\n");
                sb.append(e);
            }
        }
        // transferURL
        /**
         * whatever is read is just meaningless because PtP will fill it in!!! So create an Empty TTURL by default!
         * Vital to avoid problems with unknown DPM NULL/EMPTY logic policy!
         */
        TTURL transferURL = TTURL.makeEmpty();
        // make PtPChunkData
        PtPChunkData aux = null;
        try {
            aux = new PtPChunkData(rt,
                                   toSURL,
                                   pinLifetime,
                                   fileLifetime,
                                   fileStorageType,
                                   spaceToken,
                                   expectedFileSize,
                                   transferProtocols,
                                   overwriteOption,
                                   status,
                                   transferURL);
            aux.setPrimaryKey(auxTO.primaryKey());
        } catch (InvalidPtPChunkDataAttributesException e) {
            dao.signalMalformedPtPChunk(auxTO);
            log.warn("PtP CHUNK CATALOG! Retrieved malformed PtP chunk data from persistence. Dropping chunk from request: "
                    + rt);
            log.warn(e.getMessage(), e);
            log.warn(sb.toString());
        }
        // end...
        return aux;
    }

    /**
     * Method used to update into Persistence a retrieved PtPChunkData.
     */
    synchronized public void update(PtPChunkData cd) {
        PtPChunkDataTO to = new PtPChunkDataTO();
        to.setPrimaryKey(cd.primaryKey()); // primary key needed by DAO Object
        to.setStatus(StatusCodeConverter.getInstance().toDB(cd.status().getStatusCode()));
        to.setErrString(cd.status().getExplanation());
        to.setTransferURL(TURLConverter.getInstance().toDB(cd.transferURL().toString()));
        to.setPinLifetime(PinLifetimeConverter.getInstance().toDB(cd.pinLifetime().value()));
        to.setFileLifetime(FileLifetimeConverter.getInstance().toDB(cd.fileLifetime().value()));
        to.setFileStorageType(FileStorageTypeConverter.getInstance().toDB(cd.fileStorageType()));
        to.setOverwriteOption(OverwriteModeConverter.getInstance().toDB(cd.overwriteOption()));
        dao.update(to);
    }

    /**
     * Method used to establish if in Persistence there is a PtPChunkData working on the supplied SURL, and whose state
     * is SRM_SPACE_AVAILABLE, in which case true is returned. In case none are found or there is any problem, false is
     * returned.
     */
    synchronized public boolean isSRM_SPACE_AVAILABLE(TSURL surl) {
        // Since the SURLs in the DB can be in normal or query form, we use the StFN
        // as input for the numberInSRM_SPACE_AVAILABLE() function.
        StFN stfn = surl.sfn().stfn();
        if (stfn == null) {
            return false;
        }
        int n = dao.numberInSRM_SPACE_AVAILABLE(stfn.toString());
        return (n > 0);
    }

    /**
     * Method used to force transition to SRM_SUCCESS from SRM_SPACE_AVAILABLE, of all PtP Requests whose pinLifetime
     * has expired and the state still has not been changed (a user forgot to run srmPutDone)! The method returns a List
     * containing all ids of transited chunks that are also Volatile.
     */
    synchronized public List<Long> transitExpiredSRM_SPACE_AVAILABLE() {
        return dao.transitExpiredSRM_SPACE_AVAILABLE();
    }

    /**
     * Method used to transit the specified Collection of ReducedPtPChunkData of the request identified by the supplied
     * TRequestToken, from SRM_SPACE_AVAILABLE to SRM_SUCCESS. Chunks in any other starting state are not transited. In
     * case of any error nothing is done, but proper error messages get logged.
     */
    synchronized public void transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(Collection<ReducedPtPChunkData> chunks) {
        List<Long> aux = new ArrayList<Long>();
        long[] auxlong = null;
        for (ReducedPtPChunkData auxData : chunks) {
            aux.add(new Long(auxData.primaryKey()));
        }
        int n = aux.size();
        auxlong = new long[n];
        for (int i = 0; i < n; i++) {
            auxlong[i] = ((Long) aux.get(i)).longValue();
        }
        dao.transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(auxlong);
    }

    /**
     * This method is intended to be used by srmRm to transit all PtP chunks on the given SURL which are in the
     * SRM_SPACE_AVAILABLE state, to SRM_ABORTED. The supplied String will be used as explanation in those chunks return
     * status. The global status of the request is not changed. The TURL of those requests will automatically be set to
     * empty. Notice that both removeAllJit(SURL) and removeVolatile(SURL) are automatically invoked on
     * PinnedFilesCatalog, to remove any entry and corresponding physical ACLs. Beware, that the chunks may be part of
     * requests that have finished, or that still have not finished because other chunks are being processed.
     */
    synchronized public void transitSRM_SPACE_AVAILABLEtoSRM_ABORTED(TSURL surl, String explanation) {
        if (explanation == null) {
            explanation = "";
        }
        dao.transitSRM_SPACE_AVAILABLEtoSRM_ABORTED(surl.toString(), explanation);
    }

}

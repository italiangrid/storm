
package it.grid.storm.catalogs;

import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.srm.types.InvalidTLifeTimeAttributeException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents StoRMs CopyChunkCatalog: it collects CopyChunkData and
 * provides methods for looking up a CopyChunkData based on TRequestToken, as
 * well as for updating an existing one.
 *
 * @author EGRID - ICTP Trieste
 * @date   september, 2005
 * @version 2.0
 */
public class CopyChunkCatalog {
    private static final Logger log = LoggerFactory.getLogger(CopyChunkCatalog.class);
    private static final CopyChunkCatalog cat = new CopyChunkCatalog(); //only instance of CopyChunkCatalog present in StoRM!
    private CopyChunkDAO dao = CopyChunkDAO.getInstance(); //WARNING!!! TO BE MODIFIED WITH FACTORY!!!

    private CopyChunkCatalog() {
    }

    /**
     * Method that returns the only instance of PtPChunkCatalog available.
     */
    public static CopyChunkCatalog getInstance() {
        return cat;
    }



    /**
     * Method that returns a Collection of CopyChunkData Objects matching the
     * supplied TRequestToken.
     *
     * If any of the data associated to the TRequestToken is not well formed and
     * so does not allow a CopyChunkData Object to be created, then that part of
     * the request is dropped and gets logged, and the processing continues with
     * the next part. All valid chunks get returned: the others get dropped.
     *
     * If there are no chunks to process then an empty Collection is returned,
     * and a messagge gets logged.
     */
    synchronized public Collection lookup(TRequestToken rt) {
        Collection c = dao.find(rt);
        log.debug("COPY CHUNK CATALOG: retrieved data "+c);
        List list = new ArrayList();
        if (c.isEmpty()) {
            log.warn("COPY CHUNK CATALOG! No chunks found in persistence for specified request: "+rt);
        } else {
            CopyChunkDataTO auxTO;
            CopyChunkData aux;
            for (Iterator i = c.iterator(); i.hasNext(); ) {
                auxTO = (CopyChunkDataTO) i.next();
                aux = makeOne(auxTO,rt);
                if (aux!=null) {
                    list.add(aux);
                }
            }
        }
        log.debug("COPY CHUNK CATALOG: returning "+ list +"\n\n");
        return list;
    }


    private CopyChunkData makeOne(CopyChunkDataTO auxTO, TRequestToken rt) {
        StringBuffer sb = new StringBuffer();
        //fromSURL
        TSURL fromSURL=null;
        try {
            fromSURL = TSURL.makeFromStringValidate(auxTO.fromSURL());
        } catch (InvalidTSURLAttributesException e) {
            sb.append(e);
        }
        //toSURL
        TSURL toSURL=null;
        try {
            toSURL = TSURL.makeFromStringValidate(auxTO.toSURL());
        } catch (InvalidTSURLAttributesException e) {
            sb.append(e);
        }
        //lifeTime
        TLifeTimeInSeconds lifeTime=null;
        try {
            lifeTime = TLifeTimeInSeconds.make(FileLifetimeConverter.getInstance().toStoRM(auxTO.lifeTime()), TimeUnit.SECONDS);
        } catch (InvalidTLifeTimeAttributeException e) {
            sb.append("\n");
            sb.append(e);
        }
        //fileStorageType
        TFileStorageType fileStorageType = FileStorageTypeConverter.getInstance().toSTORM(auxTO.fileStorageType());
        if (fileStorageType==TFileStorageType.EMPTY) {
            log.error("\nTFileStorageType could not be translated from its String representation! String: "+auxTO.fileStorageType());
            fileStorageType=null; //fail creation of PtPChunk!
        }
        //spaceToken!
        //
        //WARNING! Although this field is in common between StoRM and DPM, a converter is still used
        //because DPM logic for NULL/EMPTY is not known. StoRM model does not allow for null, so it must
        //be taken care of!
        TSpaceToken spaceToken=null;
        TSpaceToken emptyToken = TSpaceToken.makeEmpty();
        String spaceTokenTranslation = SpaceTokenStringConverter.getInstance().toStoRM(auxTO.spaceToken()); //convert empty string representation of DPM into StoRM representation;
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
        //overwriteOption!
        TOverwriteMode globalOverwriteOption = OverwriteModeConverter.getInstance().toSTORM(auxTO.overwriteOption());
        if (globalOverwriteOption==TOverwriteMode.EMPTY) {
            sb.append("\nTOverwriteMode could not be translated from its String representation! String: "+auxTO.overwriteOption());
            globalOverwriteOption=null;
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
        //make CopyChunkData
        CopyChunkData aux=null;
        try {
            aux = new CopyChunkData(rt, fromSURL, toSURL, lifeTime, fileStorageType,
                    spaceToken, globalOverwriteOption, status);
            aux.setPrimaryKey(auxTO.primaryKey());
        } catch (InvalidCopyChunkDataAttributesException e) {
            dao.signalMalformedCopyChunk(auxTO);
            log.warn("COPY CHUNK CATALOG! Retrieved malformed Copy chunk data from persistence. Dropping chunk from request: "+rt);
            log.warn(e.getMessage());
            log.warn(sb.toString());
        }
        //end...
        return aux;
    }




    /**
     * Method used to update into Persistence a retrieved CopyChunkData. In case
     * any error occurs, the operation does not proceed and no Exception is
     * thrown.
     *
     * Beware that the only fields updated into persistence are the StatusCode and
     * the errorString.
     */
    synchronized public void update(CopyChunkData cd) {
        CopyChunkDataTO to = new CopyChunkDataTO();
        to.setPrimaryKey(cd.primaryKey()); //primary key needed by DAO Object
        to.setLifeTime( FileLifetimeConverter.getInstance().toDB(cd.lifetime().value()) );
        to.setStatus(StatusCodeConverter.getInstance().toDB(cd.status().getStatusCode()));
        to.setErrString(cd.status().getExplanation());
        to.setFileStorageType( FileStorageTypeConverter.getInstance().toDB(cd.fileStorageType()) );
        to.setOverwriteOption( OverwriteModeConverter.getInstance().toDB(cd.overwriteOption()) );
        dao.update(to);
    }
}


package it.grid.storm.asynch;

import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.common.types.TransferProtocol;

import it.grid.storm.common.types.*;
import it.grid.storm.griduser.GridUserInterface;

/**
 * Class that represents a stub of an SRMClient, which does not contact an external
 * web service.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September, 2005
 */
public class SRMClientStub implements SRMClient {

    /**
     * Method that returns an SRMPrepareToPutReply containging a dummy request token.
     */
    public SRMPrepareToPutReply prepareToPut(GridUserInterface su, TSURL toSURL, TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType, TSpaceToken spaceToken, TSizeInBytes filesize, TransferProtocol protocol, String description, TOverwriteMode overwriteOption, TLifeTimeInSeconds retryTime) throws SRMClientException {
        try {
            return new SRMPrepareToPutReply(new TRequestToken("DUMMY REQUEST TOKEN!"));
        } catch (Exception e) {
            throw new SRMClientException();
        }
    }

    /**
     * Method that returns an SRMStatusOfPutRequestReply, whose toTURL method returns
     * gsiftp://dummy.dummy-cnaf.dummy-infn.it:2811/mnt/gpfs/cnaf/tmp/srmCopyDummy.txt, and
     * whose returnStatus returns SRM_SUCCESS.
     */
    public SRMStatusOfPutRequestReply statusOfPutRequest(TRequestToken rt, GridUserInterface gu, TSURL toSURL) throws SRMClientException {
        try {
            TTURL aux = TTURL.make(TransferProtocol.GSIFTP,TFN.make(Machine.make("dummy.dummy-cnaf.dummy-infn.it"),Port.make(2811),PFN.make("/mnt/gpfs/cnaf/tmp/srmCopyDummy.txt")));
            TReturnStatus auxst = new TReturnStatus(TStatusCode.SRM_SUCCESS,"Success!");
            return new SRMStatusOfPutRequestReply(aux,auxst);
        } catch (Exception e) {
            throw new SRMClientException();
        }
    }

    /**
     * Method that returns an SRMPutDoneReply containing TReturnStatus with TStatusCode.SRM_SUCCESS,
     * and explanation string "DUMMY SUCCESS".
     */
    public SRMPutDoneReply srmPutDone(TRequestToken rt, GridUserInterface gu, TSURL toSURL) throws SRMClientException {
        try {
            return new SRMPutDoneReply(new TReturnStatus(TStatusCode.SRM_SUCCESS,"DUMMY SUCCESS"));
        } catch (Exception e) {
            throw new SRMClientException();
        }
    }
}

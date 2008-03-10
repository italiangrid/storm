package it.grid.storm.asynch;

import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.TTURL;

/**
 * Interface that represents a GridFTP client: it supplies functionality to execute
 * a file transfer through the GSIFTP protocol.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September 2005
 */
public interface GridFTPTransferClient {

    /**
     * Method used to transfer a local file to a remote location; it needs the GridUser whose
     * credentails (proxy) will be used forthe transfer, a local TURL that designates a file and
     * so must have file as protocol, a remote TURL that designates the destination and so must
     * have gsiftp as protocol.
     *
     * If any problem does not allow the transfer to proceed, a GridFTPTransferClientException is
     * thrown containing a String that explains what went wrong.
     */
    public void putFile(VomsGridUser gu, TTURL local, TTURL remote) throws GridFTPTransferClientException;
}

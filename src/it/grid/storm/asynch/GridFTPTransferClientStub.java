package it.grid.storm.asynch;

import it.grid.storm.srm.types.TTURL;
import it.grid.storm.griduser.GridUserInterface;

/**
 * Class that represents a Stub for the GridFTPTransferClient. The methods implemeted
 * do not carry out any action.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    September, 2005
 */
public class GridFTPTransferClientStub implements GridFTPTransferClient {

    /**
     * This is a stub implementation that does nothing.
     */
    public void putFile(GridUserInterface gu, TTURL local, TTURL remote) throws GridFTPTransferClientException {
        if (false) throw new GridFTPTransferClientException("Exception in stub! Should not occur!");
    }
}

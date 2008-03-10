package it.grid.storm.asynch;

import it.grid.storm.config.Configuration;

/**
 * Class that represents a factory of GridFTPTransferClients. It allows for
 * the creation of clients from classes specified in the configuration file.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September 2005
 */
public class GridFTPTransferClientFactory {
    final private static GridFTPTransferClientFactory factory = new GridFTPTransferClientFactory(); //only instance of GridFTPTraqnsferClientFactory!

    private GridFTPTransferClientFactory() {}

    /**
     * Methos that returns the only instance of the factory.
     */
    public static GridFTPTransferClientFactory getInstance() {
        return factory;
    }

    /**
     * Method that returns a new instance of a GridFTPTransferClient. If the configured
     * class cannot be supplied, a NoGridFTPTransferClientFoundException is thrown.
     */
    synchronized public GridFTPTransferClient client() throws NoGridFTPTransferClientFoundException {
        try {
            String client = Configuration.getInstance().getGridFTPTransferClient();
            return (GridFTPTransferClient) Class.forName(client).newInstance();
        } catch (ClassNotFoundException e) {
            throw new NoGridFTPTransferClientFoundException(e.toString());
        } catch (InstantiationException e) {
            throw new NoGridFTPTransferClientFoundException(e.toString());
        } catch (IllegalAccessException e) {
            throw new NoGridFTPTransferClientFoundException(e.toString());
        }
    }
}

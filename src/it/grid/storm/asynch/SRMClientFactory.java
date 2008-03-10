package it.grid.storm.asynch;

import it.grid.storm.config.Configuration;

/**
 * Class that represents a factory for SRMClients. It allows for the creation of
 * clients from classes specified in the configuration file.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    October 2005
 */
public class SRMClientFactory {
    final private static SRMClientFactory factory = new SRMClientFactory(); //only instance of GridFTPTraqnsferClientFactory!

    private SRMClientFactory() {}

    /**
     * Method that returns the only instance of the factory.
     */
    public static SRMClientFactory getInstance() {
        return factory;
    }

    /**
     * Method that returns a new instance of an SRMClient. If the configured
     * class cannot be supplied, a NoSRMClientFoundException is thrown.
     */
    synchronized public SRMClient client() throws NoSRMClientFoundException {
        try {
            String client = Configuration.getInstance().getSRMClient();
            return (SRMClient) Class.forName(client).newInstance();
        } catch (ClassNotFoundException e) {
            throw new NoSRMClientFoundException(e.toString());
        } catch (InstantiationException e) {
            throw new NoSRMClientFoundException(e.toString());
        } catch (IllegalAccessException e) {
            throw new NoSRMClientFoundException(e.toString());
        }
    }
}

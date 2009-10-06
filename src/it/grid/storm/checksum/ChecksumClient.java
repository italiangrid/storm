package it.grid.storm.checksum;

import java.io.IOException;
import java.net.MalformedURLException;

public interface ChecksumClient {

    /**
     * Ping the server.
     * 
     * @return <code>true</code> if the server is up and running, <code>false</code> otherwise.
     */
    public boolean ping();

    /**
     * Return all the supported checksum algorithms.
     * 
     * @return the supported checksum algorithms.
     */
    public String[] getSupportedAlgorithms() throws IOException;

    /**
     * Contacts the checksum service and returns the computed checksum for the given file.
     * 
     * @param fileAbsolutePath file to compute the checksum for.
     * @param algorithm checksum algorithm.
     * @return the computed checksum for the given file.
     * @throws IOException in case of error contacting the remote service
     * @throws ChecksumRuntimeException in case there was an error computing the checksum. The exception message is set
     *             with the error explanation.
     */
    public String getChecksum(String fileAbsolutePath, String algorithm) throws IOException,
            ChecksumRuntimeException;

    /**
     * Set the service endpoint to contact.
     * 
     * @param service endpoint (e.g. http://host:port/).
     */
    public void setEndpoint(String url) throws MalformedURLException;

}

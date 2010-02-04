package it.grid.storm.checksum;

import it.grid.storm.config.Configuration;
import it.grid.storm.ea.StormEA;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChecksumManager {

    private static final Logger log = LoggerFactory.getLogger(ChecksumManager.class);
    private static final String URL_FORMAT= "http://%s:%d/";
    
    private static ChecksumManager instance = null;
    private static List<String> serviceUrlList;
    private static List<String> statusUrlList;
    private static int urlListSize;
    private static volatile int currentUrlIndex = -1;
    private static String algorithm;

    public static ChecksumManager getInstance() {
        if (instance == null) {
            instance = new ChecksumManager(Configuration.getInstance().getChecksumServiceURLArray());
        }
        return instance;
    }
    
    private static synchronized int getNextIndex() {
        currentUrlIndex++;
        if (currentUrlIndex >= urlListSize) {
            currentUrlIndex = 0;
        }
        return currentUrlIndex;
    }

    /**
     * Round-robin load balancer of checksum services.
     * 
     * @return the checksum service URL. Return <code>null</code> if all the servers do not respond.
     */
    private static String getTargetURL() {

        ChecksumClient client = ChecksumClientFactory.getChecksumClient();
        boolean isAlive = false;
        int iter = 0;
        String url;

        ChecksumServerStatus status;
        
        do {
            int index = getNextIndex();
            
            url = statusUrlList.get(index);

            try {
                client.setEndpoint(url);
            } catch (MalformedURLException e) {
                log.error("BUG, this exception should had never be thrown.", e);
            }

            try {
                
                status = client.getStatus();
                isAlive = status.isRunning();
                
            } catch (IOException e) {
                return null;
            }
            isAlive = client.ping();
            
            if (!isAlive) {
                log.warn("Skipping checksum service because it doesn't respond: " + url.toString());
            }

            iter++;
            
        } while ((iter < urlListSize) && !isAlive);

        if ((iter == urlListSize) && !isAlive) {
            return null;
        }
        
        log.info("Selected checksum server: " + url + " (requestQueue=" + status.getRequestQueue() + ", idleThreads=" + status.getIdleThreads());

        return url;
    } 

    private ChecksumManager(String[] urlStringArray) {

        algorithm = Configuration.getInstance().getChecksumAlgorithm().toLowerCase();
        initUrlArrays();
        
    }
    
    /**
     * Return the algorithm used to compute checksums as well as retrieve the value from extended attributes.
     * 
     * @return the algorithm used to compute checksums as well as retrieve the value from extended attributes.
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Return the computed checksum for the given file. If the checksum is already stored in an extended attribute then
     * that value is given back, otherwise: - check if the computation of checksum is enabled. - if ENABLED then the
     * checksum is computed by an external service and stored in an extended attribute. - if NOT ENABLED return with a
     * NULL value. This method is blocking (i.e. waits for the checksum to be computed, if it is enabled).
     * 
     * @param fileName file absolute path.
     * @return the computed checksum for the given file or <code>null</code> if some error occurred. The error is
     *         logged.
     */
    public String getChecksum(String fileName) {

        log.debug("Requesting checksum for file: " + fileName);

        String checksum = StormEA.getChecksum(fileName, algorithm);

        if (checksum == null) {

            // check if Checksum computation is Enabled or not
            if (Configuration.getInstance().getChecksumEnabled()) {

                // Get current time
                long start = System.currentTimeMillis();

                log.debug("Checksum Computation: START.");

                checksum = retrieveChecksumFromExternalService(fileName);

                if (checksum == null) {
                    return null;
                }

                // Get elapsed time in milliseconds
                long elapsedTimeMillis = System.currentTimeMillis() - start;

                log.debug("Checksum Computation: END. Elapsed Time (ms) = " + elapsedTimeMillis);
                StormEA.setChecksum(fileName, checksum, algorithm);
            } else {
                log.debug("Checksum Computation: The computation will not take place. Feature DISABLED.");
            }
        }

        return checksum;
    }

    /**
     * Checks whether the given file has a checksum stored in an extended attribute.
     * 
     * @param fileName file absolute path.
     * @return <code>true</code> if an extended attribute storing the checksum was found, <code>false</code> otherwise.
     */
    public boolean hasChecksum(String fileName) {

        String value = StormEA.getChecksum(fileName, algorithm);

        if (value == null) {

            return false;
        }

        return true;
    }

    private void initUrlArrays() {
        List<String> idList = Configuration.getInstance().getChecksumServiceIds();
        
        urlListSize = idList.size();
        serviceUrlList = new ArrayList<String>(urlListSize);
        statusUrlList = new ArrayList<String>(urlListSize);
        
        for (String id : idList) {
            
            String hostname = Configuration.getInstance().getChecksumHost(id);
            if (hostname == null) {
                log.error("Configuration error: hostname not defined for checksum server id: " + id + ". Skipping it.");
                continue;
            }
            
            int servicePort = Configuration.getInstance().getChecksumServicePort(id);
            if (servicePort == -1) {
                log.error("Configuration error: service_port not defined for checksum server id: " + id + ". Skipping it.");
                continue;
            }
            
            int statusPort = Configuration.getInstance().getChecksumStatusPort(id);
            if (statusPort == -1) {
                log.error("Configuration error: status_port not defined for checksum server id: " + id + ". Skipping it.");
                continue;
            }
            
            URL url;
            try {
                
                url = new URL(String.format(URL_FORMAT, hostname, servicePort));
                serviceUrlList.add(url.toString());
                log.info("Added checksum service: " + url.toString());
                
            } catch (MalformedURLException e) {
                log.error("Configuration error: unable to build an URL for the following hostname and port: " + hostname + ":" + servicePort);
                continue;
            }
            

            try {

                url = new URL(String.format(URL_FORMAT, hostname, statusPort));
                statusUrlList.add(url.toString());
                log.info("Added checksum service for status request: " + url.toString());

            } catch (MalformedURLException e) {
                log.error("Configuration error: unable to build an URL for the following hostname and port: "
                        + hostname + ":" + statusPort);
                continue;
            }                
        }
    }

    private String retrieveChecksumFromExternalService(String fileName) {

        if (serviceUrlList.isEmpty()) {

            log.warn("No external checksum servers found, no checksum returned for file: " + fileName);
            return null;
        }

        String targetURL = getTargetURL();
        if (targetURL == null) {
            log.warn("Checksum computation ('" + fileName + "') request failed: none of the servers has responded");
            return null;
        }

        log.debug("Requesting checksum to service: " + targetURL);

        ChecksumClient client = ChecksumClientFactory.getChecksumClient();

        try {
            client.setEndpoint(targetURL);
        } catch (MalformedURLException e) {
            log.error("BUG, this exception should had never be thrown.", e);
            return null;
        }

        try {

            return client.getChecksum(fileName, algorithm);

        } catch (ChecksumRuntimeException e) {
            log.error(e.getMessage());
            return null;
        } catch (IOException e) {
            log.error("Error contacting server: " + targetURL.toString());
            return null;
        }
    }

    /**
     * Computes the checksum of the given file and stores it in to an extended attribute.
     * 
     * @param fileName fileName file absolute path.
     */
    public boolean setChecksum(String fileName) {

        String checksum = retrieveChecksumFromExternalService(fileName);

        if (checksum == null) {
            StormEA.removeChecksum(fileName);
            return false;
        }

        StormEA.setChecksum(fileName, checksum, algorithm);

        return true;
    }
}


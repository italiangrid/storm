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

    private static Logger log = LoggerFactory.getLogger(ChecksumManager.class);
    private static ChecksumManager instance = null;
    private static List<String> urlList;
    private static int currentURLIndex = -1;
    private static String algorithm;

    private ChecksumManager(String[] urlStringArray, String checksumAlgorithm) {

        algorithm = checksumAlgorithm.toLowerCase();

        urlList = new ArrayList<String>(urlStringArray.length);

        for (String urlString : urlStringArray) {
            try {

                // Check the URL is not malformed
                @SuppressWarnings("unused")
                URL url = new URL(urlString);

                urlList.add(urlString);
                
                log.info("Adding URL for external checksum server: " + urlString);

            } catch (MalformedURLException e) {
                log.error("Skipping malformed URL in storm.properties (checksum.serviceURL): " + urlString);
            }
        }
    }

    public static ChecksumManager getInstance() {
        if (instance == null) {
            instance = new ChecksumManager(Configuration.getInstance().getChecksumServiceURLArray(),
                                           Configuration.getInstance().getChecksumAlgorithm());
        }
        return instance;
    }

    public static void main(String[] args) {

        String[] urlA = new String[2];

        urlA[0] = "http://localhost:8080/pippo";
        urlA[1] = "http://storm-devel1.cnaf.infn.it:9997/";

        ChecksumManager cm = new ChecksumManager(urlA, "crc32");

        System.out.println("PIPPO: " + cm.getChecksum("/home/alb/portfolio-1.0.0-1.noarch.rpm") + " :PIPPO");

    }

    /**
     * Round-robin load balancer of checksum services.
     * 
     * @return the checksum service URL. Return <code>null</code> if all the servers do not respond.
     */
    private synchronized static String getTargetURL() {

        ChecksumClient client = ChecksumClientFactory.getChecksumClient();
        int index = currentURLIndex;
        String url;

        do {
            currentURLIndex++;
            if (currentURLIndex >= urlList.size()) {
                currentURLIndex = 0;
            }

            url = urlList.get(currentURLIndex);

            try {
                client.setEndpoint(url);
            } catch (MalformedURLException e) {
                log.error("BUG, this exception should had never be thrown.", e);
            }

        } while ((index != currentURLIndex) && (client.ping() == false));

        if (index == currentURLIndex) {
            return null;
        }

        return url;
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
     * that value is given back, otherwise the checksum is computed by an external service and stored in an extended
     * attribute. This method is blocking (i.e. waits for the checksum to be computed).
     * 
     * @param fileName file absolute path.
     * @return the computed checksum for the given file or <code>null</code> if some error occurred. The error is
     *         logged.
     */
    public String getChecksum(String fileName) {

        log.debug("Requesting checksum for file: " + fileName);
        
        String value = StormEA.getChecksum(fileName, algorithm);

        if (value == null) {

            value = retrieveChecksumFromExternalService(fileName);
            
            if (value == null) {
                return null;
            }

            StormEA.setChecksum(fileName, value, algorithm);

        }

        return value;
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

    /**
     * Computes the checksum of the given file and stores it to an extended attribute.
     * 
     * @param fileName fileName file absolute path.
     */
    public void setChecksum(String fileName) {

        getChecksum(fileName);

    }

    private String retrieveChecksumFromExternalService(String fileName) {

        if (urlList.isEmpty()) {
            
            log.debug("No external checksum servers found, no checksum returned for file: " + fileName);
            return null;
        }
        

        String targetURL = getTargetURL();
        if (targetURL == null) {
            log.error("Checksum computation (" + fileName
                    + ") request failed: none of the servers has responded");
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
            log.error("Error contacting server: " + targetURL.toString(), e);
            return null;
        }
    }
}

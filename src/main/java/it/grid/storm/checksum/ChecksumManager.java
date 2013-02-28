/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.checksum;

import it.grid.storm.config.Configuration;
import it.grid.storm.ea.ExtendedAttributesException;
import it.grid.storm.ea.FileNotFoundException;
import it.grid.storm.ea.NotSupportedException;
import it.grid.storm.ea.StormEA;

//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChecksumManager {

    private static final Logger log = LoggerFactory.getLogger(ChecksumManager.class);
//    private final String URL_FORMAT = "http://%s:%d/";

    private static volatile ChecksumManager instance = null;
//    private List<String> serviceUrlList = null;
//    private List<String> statusUrlList = null;
//    private int urlListSize;
//    private volatile int currentUrlIndex = -1;
    private String defaultAlgorithm;
    
    
    private ChecksumManager() {

        defaultAlgorithm = Configuration.getInstance().getChecksumAlgorithm().toLowerCase();
    }

    public static synchronized ChecksumManager getInstance() {        
        if (instance == null) {
            instance = new ChecksumManager();
        }
        return instance;
    }

//    private synchronized int getNextIndex() {
//        currentUrlIndex++;
//        if (currentUrlIndex >= urlListSize) {
//            currentUrlIndex = 0;
//        }
//        return currentUrlIndex;
//    }

    /**
     * Return the algorithm used to compute checksums as well as retrieve the value from extended attributes.
     * 
     * @return the algorithm used to compute checksums as well as retrieve the value from extended attributes.
     */
    public String getDefaultAlgorithm() {
        return defaultAlgorithm;
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
     * @throws FileNotFoundException 
     */
    public String getDefaultChecksum(String fileName) throws FileNotFoundException {

        log.debug("Requesting checksum for file: " + fileName);

        String checksum = null;
        try
        {
            checksum = StormEA.getChecksum(fileName, defaultAlgorithm);
        } catch(NotSupportedException e)
        {
            log.warn("Cannot retrieve checksum EA for default algorithm " + defaultAlgorithm
                    + " (operation not supported) from file: " + fileName
                    + " NotSupportedException: " + e.getMessage());
        } catch(ExtendedAttributesException e)
        {
            if (e instanceof FileNotFoundException)
                throw (FileNotFoundException) e;
            log.warn("Error manipulating EA for default algorithm " + defaultAlgorithm + " on file: "
                    + fileName + " ExtendedAttributesException: " + e.getMessage());
        }

//        if (checksum == null) {
//
//            // check if Checksum computation is Enabled or not
//            if (Configuration.getInstance().getChecksumEnabled()) {
//
//                // Get current time
//                long start = System.currentTimeMillis();
//
//                log.debug("Checksum Computation: START.");
//
//                checksum = retrieveChecksumFromExternalService(fileName, defaultAlgorithm);
//
//                if (checksum == null) {
//                    return null;
//                }
//
//                // Get elapsed time in milliseconds
//                long elapsedTimeMillis = System.currentTimeMillis() - start;
//
//                log.debug("Checksum Computation: END. Elapsed Time (ms) = " + elapsedTimeMillis);
//                StormEA.setChecksum(fileName, checksum, defaultAlgorithm);
//            } else {
//                log.debug("Checksum Computation: The computation will not take place. Feature DISABLED.");
//            }
//        }

        return checksum;
    }

    
//    public String getChecksumWithAlgorithm(String fileName, ChecksumAlgorithm algorithm) {
//
//        log.debug("Requesting checksum "+algorithm+" for file: " + fileName);
//        
//        String checksum = StormEA.getChecksum(fileName, algorithm.toString());
//
//        if (checksum == null) {
//
//            // check if Checksum computation is Enabled or not
//            if (Configuration.getInstance().getChecksumEnabled()) {
//
//                // Get current time
//                long start = System.currentTimeMillis();
//
//                log.debug("Checksum Computation: START.");
//
//                checksum = retrieveChecksumFromExternalService(fileName, algorithm.toString());
//
//                if (checksum == null) {
//                    return null;
//                }
//
//                // Get elapsed time in milliseconds
//                long elapsedTimeMillis = System.currentTimeMillis() - start;
//
//                log.debug("Checksum Computation: END. Elapsed Time (ms) = " + elapsedTimeMillis);
//                StormEA.setChecksum(fileName, checksum, algorithm.toString());
//            } else {
//                log.debug("Checksum Computation: The computation will not take place. Feature DISABLED.");
//            }
//        }
//
//        return checksum;
//    }
    
    /**
     * Checks whether the given file has a checksum stored in an extended attribute.
     * 
     * @param fileName file absolute path.
     * @return <code>true</code> if an extended attribute storing the checksum was found, <code>false</code> otherwise.
     * @throws ExtendedAttributesException 
     * @throws NotSupportedException 
     * @throws FileNotFoundException 
     */
    public boolean hasChecksum(String fileName) throws FileNotFoundException {

        String value = null;
        try
        {
            value = StormEA.getChecksum(fileName, defaultAlgorithm);
        } catch(NotSupportedException e)
        {
            log.warn("Cannot retrieve checksum EA for default algorithm " + defaultAlgorithm
                    + " (operation not supported) from file: " + fileName
                    + " NotSupportedException: " + e.getMessage());
        } catch(ExtendedAttributesException e)
        {
            if (e instanceof FileNotFoundException)
                throw (FileNotFoundException) e;
            log.warn("Error manipulating EA for default algorithm " + defaultAlgorithm + " on file: "
                    + fileName + " ExtendedAttributesException: " + e.getMessage());
        }

        if (value == null) {

            return false;
        }

        return true;
    }

    
    public Map<String,String> getChecksums(String fileName) throws FileNotFoundException {

        return StormEA.getChecksums(fileName);
    }
    
//    /**
//     * Computes the checksum of the given file and stores it in to an extended attribute.
//     * 
//     * @param fileName fileName file absolute path.
//     */
//    public boolean setChecksum(String fileName) {
//
//    	if(this.serviceUrlList == null && this.statusUrlList == null)
//    	{
//    		log.debug("Loading checksum remote hosts URLs");
//    		initUrlArrays();
//    	}
//        String checksum = retrieveChecksumFromExternalService(fileName, defaultAlgorithm);
//
//        if (checksum == null) {
//            StormEA.removeChecksum(fileName);
//            return false;
//        }
//
//        StormEA.setChecksum(fileName, checksum, defaultAlgorithm);
//
//        return true;
//    }

//    /**
//     * Round-robin load balancer of checksum services.
//     * 
//     * @param Temporary workaround that allows separation of checksum servers given a file path.
//     * @return the checksum service URL. Return <code>null</code> if all the servers do not respond.
//     */
//    private String getTargetURL(String filePath) {
//
//        ChecksumClient client = ChecksumClientFactory.getChecksumClient();
//        boolean isAlive = false;
//        int iter = 0;
//        String url;
//
//        ChecksumServerStatus status;
//        int index = -1;
//        int nextIndex = getNextIndex();
//        do {
//
//            index = nextIndex;
//            url = statusUrlList.get(index);
//            
//            nextIndex++;
//            if (nextIndex >= urlListSize) {
//                nextIndex = 0;
//            }
//
//            try {
//
//                client.setEndpoint(url);
//                status = client.getStatus(filePath);
//                isAlive = status.isRunning();
//
//            } catch (MalformedURLException e) {
//                log.error("BUG, this exception should had never be thrown.", e);
//                status = null;
//                isAlive = false;
//            } catch (IOException e) {
//                status = null;
//                isAlive = false;
//            }
//
//            if (!isAlive) {
//                log.warn("Skipping checksum service because it doesn't respond or cannot access the requested file: " + url.toString());
//            }
//
//            iter++;
//
//        } while ((iter < urlListSize) && !isAlive);
//
//        if ((iter == urlListSize) && !isAlive) {
//            return null;
//        }
//
//        log.info("Selected checksum server: " + url + " (requestQueue=" + status.getRequestQueue()
//                + ", idleThreads=" + status.getIdleThreads() + ")");
//
//        return serviceUrlList.get(index);
//    }

//    private void initUrlArrays() {
//        
//        String[] hostArray = Configuration.getInstance().getChecksumHosts();
//        urlListSize = hostArray.length;
//        
//        int[] servicePortArray = Configuration.getInstance().getChecksumServicePorts();
//        if (servicePortArray.length != urlListSize) {
//            log.error("Configuration error: 'checksum.server.hostnames' and 'checksum.server.service_ports' have different sizes. Assuming empty list of checksum servers.");
//            serviceUrlList = new ArrayList<String>(0);
//            statusUrlList = new ArrayList<String>(0);
//        }
//        
//        int[] statusPortArray = Configuration.getInstance().getChecksumStatusPorts();
//        if (servicePortArray.length != urlListSize) {
//            log.error("Configuration error: 'checksum.server.hostnames' and 'checksum.server.status_ports' have different sizes. Assuming empty list of checksum servers.");
//            serviceUrlList = new ArrayList<String>(0);
//            statusUrlList = new ArrayList<String>(0);
//        }
//
//        serviceUrlList = new ArrayList<String>(urlListSize);
//        statusUrlList = new ArrayList<String>(urlListSize);
//        
//        for (int i=0; i<urlListSize; i++) {
//            String hostname = hostArray[i];
//            int servicePort = servicePortArray[i];
//            int statusPort = statusPortArray[i];
//            
//            if (servicePort == -1) {
//                log.error("Configuration error: invalid service port element '" + i + "', '" + servicePort + "'. Skipping checksum server '" + hostname + "'.");
//                continue;
//            }
//            if (statusPort == -1) {
//                log.error("Configuration error: invalid status port element '" + i + "', '" + statusPort + "'. Skipping checksum server '" + hostname + "'.");
//                continue;
//            }
//            
//            URL url;
//            try {
//
//                url = new URL(String.format(URL_FORMAT, hostname, servicePort));
//                serviceUrlList.add(url.toString());
//                log.info("Added checksum service_port: " + url.toString());
//
//            } catch (MalformedURLException e) {
//                log.error("Configuration error: unable to build an URL for the following hostname and port: "
//                        + hostname + ":" + servicePort);
//                continue;
//            }
//
//            try {
//
//                url = new URL(String.format(URL_FORMAT, hostname, statusPort));
//                statusUrlList.add(url.toString());
//                log.info("Added checksum status_port: " + url.toString());
//
//            } catch (MalformedURLException e) {
//                log.error("Configuration error: unable to build an URL for the following hostname and port: "
//                        + hostname + ":" + statusPort);
//                continue;
//            }
//            
//        }
//    }
//
//    private String retrieveChecksumFromExternalService(String fileName, String algorithm) {
//
//        if (serviceUrlList.isEmpty()) {
//
//            log.warn("No external checksum servers found, no checksum returned for file: " + fileName);
//            return null;
//        }
//
//        String targetURL = getTargetURL(fileName);
//        if (targetURL == null) {
//            log.warn("Checksum computation ('" + fileName
//                    + "') request failed: none of the servers has responded");
//            return null;
//        }
//
//        log.debug("Requesting checksum to service: " + targetURL);
//
//        ChecksumClient client = ChecksumClientFactory.getChecksumClient();
//
//        try {
//            client.setEndpoint(targetURL);
//        } catch (MalformedURLException e) {
//            log.error("BUG, this exception should had never be thrown.", e);
//            return null;
//        }
//
//        try {
//
//            return client.getChecksum(fileName, algorithm);
//
//        } catch (ChecksumRuntimeException e) {
//            log.error(e.getMessage());
//            return null;
//        } catch (IOException e) {
//            log.error("Error contacting server: " + targetURL.toString());
//            return null;
//        }
//    }
}

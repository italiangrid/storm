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
     * Returns all the supported checksum algorithms.
     * 
     * @return the supported checksum algorithms.
     */
    public String[] getSupportedAlgorithms() throws IOException;

    /**
     * Returns the status of the server.
     * 
     * @param filePath temporary workaround, checking existence of the file.
     * @return the status of the server.
     * @throws IOException
     */
    public ChecksumServerStatus getStatus(String filePath) throws IOException;

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

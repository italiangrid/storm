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

package it.grid.storm.griduser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;

public class SimpleUserMapper implements MapperInterface {

    private static final Logger log = GridUserManager.log;

    public SimpleUserMapper() {
    }

    /**
     * Template factory method for Mapper objects.
     *
     * @param dn Grid user certificate subject DN
     * @param fqans array of VOMS FQANs
     * @return a new LocalUser object holding the local credentials (UID,
     *   GIDs) of the POSIX account the given Grid user is mapped to.
     * @throws CannotMapUserException
     * @todo Implement this it.grid.storm.griduser.MapperInterface method
     */
    public LocalUser map(String dn, String[] fqans) throws CannotMapUserException {

        LocalUser localUser = null;
        int uid = 0;
        int gid = 0;
        try {
            //String[] retrieveUserCmd = new String[1];
            String retrieveUserCmd = "id -r -u";
            String userIdStr = getOutput(retrieveUserCmd);
            //log.debug("Output = "+userIdStr);
            uid = Integer.parseInt(userIdStr);
        }
        catch (CannotMapUserException ex) {
            log.error("Unable to retrieve User ID from the system." + ex);
            throw ex;
        }
        catch (NumberFormatException nfe) {
            log.error("Getting UID returns a result different from a integer");
            throw new CannotMapUserException(nfe);
        }

        try {
            //String[] retrieveUserCmd = new String[0];
            String retrieveUserCmd = "id -r -g";
            String groupIdStr = getOutput(retrieveUserCmd);
            //log.debug("Output = "+groupIdStr);
            gid = Integer.parseInt(groupIdStr);
        }
        catch (CannotMapUserException ex) {
            log.error("Unable to retrieve Group ID from the system." + ex);
            throw ex;
        }
        catch (NumberFormatException nfe) {
            log.error("Getting GID returns a result different from a integer");
            throw new CannotMapUserException(nfe);
        }

        localUser = new LocalUser(uid,gid);
        return localUser;
    }

    /**
     *
     * @param command String[]
     * @return String
     */
    private String getOutput(String command) throws CannotMapUserException {
        String result = null;
        try {
            Process child = Runtime.getRuntime().exec(command);
            // Get the input stream and read from it
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));

            //process the Command Output (Input for StoRM ;) )
            String line;
            int row = 0;
            //log.debug("UserInfo Command Output :");
            while ( (line = stdInput.readLine()) != null) {
                //log.debug(row + ": " + line);
                boolean lineOk = processOutput(row, line);
                if (lineOk) {
                    result = line;
                    break;
                }
                row++;
            }

            //process the Errors
            String errLine;
            while ( (errLine = stdError.readLine()) != null) {
                log.warn("User Info Command Output contains an ERROR message " + errLine);
                throw new CannotMapUserException(errLine);
            }

        }
        catch (IOException ex) {
            log.error("getUserInfo (id) I/O Exception: " + ex);
            throw new CannotMapUserException(ex);
        }
        return result;
    }

    private boolean processOutput(int row, String line) {
        boolean result = false;
        if (row >= 0) {
            /**
             * @todo : Implement a more smart check to verify the right line
             */
            result = true;
        }
        return result;
    }

}

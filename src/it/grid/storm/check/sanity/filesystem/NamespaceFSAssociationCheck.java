/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.grid.storm.check.sanity.filesystem;


import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.check.GenericCheckException;
import it.grid.storm.check.Check;
import it.grid.storm.check.CheckResponse;
import it.grid.storm.check.CheckStatus;
import it.grid.storm.filesystem.MtabUtil;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;

/**
 * @author Michele Dibenedetto
 */
public class NamespaceFSAssociationCheck implements Check
{

    private static final Logger log = LoggerFactory.getLogger(NamespaceFSAssociationCheck.class);

    private static final String CHECK_NAME = "NamespaceFSvalidation";

    private static final String CHECK_DESCRIPTION = "Namespace declared FS and phisical FS correspondance Check";

    /*
     * (non-Javadoc)
     * @see it.grid.storm.check.Check#execute()
     */
    @Override
    public CheckResponse execute() throws GenericCheckException
    {
        CheckStatus status = CheckStatus.SUCCESS;
        String errorMessage = "";
        try
        {
            // load mstab mount points and file system types
            Map<String, String> mountPoints = MtabUtil.getFSMountPoints();
            log.debug("Retrieved MountPoints: " + printMapCoupples(mountPoints));
            try
            {
                // load declared file systems from namespace.xml
                for (VirtualFSInterface vfs : NamespaceDirector.getNamespace().getAllDefinedVFS())
                {
                    if (vfs.getFSType() == null || vfs.getRootPath() == null)
                    {
                        log.error("Skipping chek on VFS with alias \'" + vfs.getAliasName()
                                + "\' has null type ->" + vfs.getFSType() + "<- or root path ->"
                                + vfs.getRootPath() + "<-");
                    }
                    else
                    {
                        // check their association against mtab
                        boolean currentResponse = this.check(vfs.getRootPath(), vfs.getFSType(), mountPoints);
                        if (!currentResponse)
                        {
                            log.warn("Check on VFS " + vfs.getAliasName() + " failed. Type ="
                                    + vfs.getFSType() + " , root path =" + vfs.getRootPath());
                            errorMessage += "Check on VFS " + vfs.getAliasName() + " failed. Type ="
                                    + vfs.getFSType() + " , root path =" + vfs.getRootPath()
                                    + " , loaded Mount Points =" + printMapCoupples(mountPoints) + "; ";
                        }
                        log.debug("Check response for path " + vfs.getRootPath() + " is "
                                + (currentResponse ? "success" : "failure"));
                        status = CheckStatus.and(status, currentResponse);
                        log.debug("Partial result is " + status.toString());
                    }
                }
            }
            catch (NamespaceException e)
            {
                // NOTE: this exception is never thrown
                log.error("Unable to proceede received a NamespaceException : " + e.getMessage());
                errorMessage += "Unable to proceede received a NamespaceException : " + e.getMessage() + "; ";
                status = CheckStatus.INDETERMINATE;
            }
        }
        catch (IOException e)
        {
            log.error("Unable to proceede received an IOException : " + e.getMessage());
            errorMessage += "Unable to proceede received a NamespaceException : " + e.getMessage() + "; ";
            status = CheckStatus.INDETERMINATE;
        }
        return new CheckResponse(status, errorMessage);
    }

    /**
     * Prints the couple <key,value> from a Map
     * 
     * @param map
     * @return
     */
    private String printMapCoupples(Map<String, String> map)
    {
        String output = "";
        for (Entry<String, String> couple : map.entrySet())
        {
            if (output.trim().length() != 0)
            {
                output += " ; ";
            }
            output += "<" + couple.getKey() + "," + couple.getValue() + ">";
        }
        return output;
    }

    /**
     * Checks if the provided fsRootPath in the provided mountPoints map has the provided fsType
     * 
     * @param fsRootPath
     * @param fsType
     * @param mountPoints
     * @return
     */
    private boolean check(String fsRootPath, String fsType, Map<String, String> mountPoints)
    {
        boolean response = false;
        boolean found = false;
        log.debug("Checking fs at " + fsRootPath + " as a " + fsType);
        for (String mountPoint : mountPoints.keySet())
        {
            if (fsRootPath.startsWith(mountPoint))
            {
                found = true;
                String mountPointFSType = mountPoints.get(mountPoint);
                log.debug("Found on mountPoint " + mountPoint + " Related FS is " + mountPointFSType);
                if (fsType.equals(mountPointFSType))
                {
                    response = true;
                }
                else
                {
                    log.info("Mount point File System type " + mountPointFSType
                            + " differs from the declared " + fsType + ". Check failed");
                }
            }
        }
        if (!found)
        {
            log.info("No file systems are mounted at path " + fsRootPath + "! Check failed");
        }
        return response;
    }

    @Override
    public String getName()
    {
        return CHECK_NAME;
    }

    @Override
    public String getDescription()
    {
        return CHECK_DESCRIPTION;
    }
}

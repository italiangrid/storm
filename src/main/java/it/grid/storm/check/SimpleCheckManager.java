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

package it.grid.storm.check;


import it.grid.storm.check.sanity.filesystem.NamespaceFSAssociationCheck;
import it.grid.storm.check.sanity.filesystem.NamespaceFSExtendedACLUsageCheck;
import it.grid.storm.check.sanity.filesystem.NamespaceFSExtendedAttributeUsageCheck;
import it.grid.storm.filesystem.MtabUtil;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 */
public class SimpleCheckManager extends CheckManager
{

    private static final Logger log = LoggerFactory.getLogger(SimpleCheckManager.class);

    /**
     * A list of checks to be executed
     */
    private ArrayList<Check> checks = new ArrayList<Check>();

    @Override
    protected Logger getLogger()
    {
        return log;
    }

    @Override
    protected void loadChecks()
    {
        /* Add by hand a new element for each requested check */
        try
        {
            checks.add(getNamespaceFSAssociationCheck());
        }
        catch (IllegalStateException e)
        {
            log.warn("Skipping NamespaceFSAssociationCheck. IllegalStateException: " + e.getMessage());
        }
//        checks.add(new NamespaceFSExtendedAttributeDeclarationCheck()); Removed
        checks.add(new NamespaceFSExtendedAttributeUsageCheck());
        checks.add(new NamespaceFSExtendedACLUsageCheck());
    }

    /**
     * 
     */
    private Check getNamespaceFSAssociationCheck() throws IllegalStateException
    {
        Map<String, String> mountPoints;
        // load mstab mount points and file system types
        try
        {
            mountPoints = MtabUtil.getFSMountPoints();
        }
        catch (Exception e)
        {
            log.error("Unable to get filesystem mount points. Exception: " + e.getMessage() );
            throw new IllegalStateException("Unable to get filesystem mount points");
        }
        log.debug("Retrieved MountPoints: " + printMapCoupples(mountPoints));
        Collection<VirtualFSInterface> vfsSet;
        try
        {
            vfsSet = NamespaceDirector.getNamespace().getAllDefinedVFS();
        }
        catch (NamespaceException e)
        {
            //never thrown
            log.error("Unexpected NamespaceException during vfsSet retriving " + e.getMessage() + " . Unable to add NamespaceFSAssociationCheck" );
            throw new IllegalStateException("Unexpected NamespaceException from getAllDefinedVFS");
        }
        return new NamespaceFSAssociationCheck(mountPoints, vfsSet);
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
    
    
    @Override
    protected List<Check> prepareSchedule()
    {
        return checks;
    }
}

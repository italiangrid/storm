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


import it.grid.storm.check.Check;
import it.grid.storm.check.CheckResponse;
import it.grid.storm.check.CheckStatus;
import it.grid.storm.check.GenericCheckException;
import it.grid.storm.filesystem.Filesystem;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.DistinguishedName;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 *
 */
public class NamespaceFSExtendedACLUsageCheck implements Check
{


    private static final Logger log = LoggerFactory.getLogger(NamespaceFSExtendedACLUsageCheck.class);
    private static final String CHECK_NAME = "NamespaceFSEACLTest";
    private static final String CHECK_DESCRIPTION = "This check tries to use file system extended ACL on all the file systems declared in namespace.xml";
    /**
     * The maximum number of attempts of temporary file creation
     */
    private static final int MAX_FILE_CREATION_ATTEMPTS = 10;
//    private static final GridUserInterface TEST_USER = 
//        GridUserManager.makeVOMSGridUser("/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Michele Dibenedetto", new FQAN[]{ new FQAN("/dteam")});
    private static final GridUserInterface TEST_USER = new FakeGridUser("/C=IT/O=INFN/L=CNAF/CN=Fake User");
    private static LocalUser TEST_LOCAL_USER = null;
    private static final FilesystemPermission TEST_PERMISSION = FilesystemPermission.ListTraverseWrite;
    private static final String TEST_FILE_INFIX = "ACL-check-file-N_";

    private static final boolean criticalCheck = true;

    @Override
    public CheckResponse execute() throws GenericCheckException
    {
        CheckStatus status = CheckStatus.SUCCESS;
        String errorMessage = "";
        try
        {
            TEST_LOCAL_USER = TEST_USER.getLocalUser();
        }
        catch (CannotMapUserException e)
        {
            log.warn("Unable to obtain local user for test user " + TEST_USER);
            throw new GenericCheckException("Unable to obtain local user for test user " + TEST_USER);
        }
        try
        {
            // load declared file systems from namespace.xml
            for (VirtualFSInterface vfs : NamespaceDirector.getNamespace().getAllDefinedVFS())
            {
                String fsRootPath = vfs.getRootPath().trim();
                if (fsRootPath.charAt(fsRootPath.length() - 1) != File.separatorChar)
                {
                    fsRootPath += File.separatorChar;
                }
                // for each root path get a temporary file in it
                File checkFile;
                try
                {
                    checkFile = provideCheckFile(fsRootPath, TEST_FILE_INFIX);
                }
                catch (GenericCheckException e)
                {
                    log.warn("Unable to obtain a check temporary file. GenericCheckException : " + e.getMessage());
                    errorMessage += "Unable to obtain a check temporary file. GenericCheckException : " + e.getMessage() + "; ";
                    status = CheckStatus.INDETERMINATE;
                    continue;
                }
                Filesystem filesystem = vfs.getFilesystem();
                // tries to manage the extended attributes on file checkFile
                boolean currentResponse = this.checkEACL(checkFile, filesystem);
                if (!currentResponse)
                {
                    log.error("Check on VFS " + vfs.getAliasName() + " to add an extended ACL on file " + checkFile.getAbsolutePath()
                            + " failed. File System type =" + vfs.getFSType() + " , root path =" + fsRootPath);
                    errorMessage += "Check on VFS " + vfs.getAliasName() + " to add an extended ACL on file " + checkFile.getAbsolutePath()
                            + " failed. File System type =" + vfs.getFSType() + " , root path =" + fsRootPath + "; ";
                }
                log.debug("Check response for path " + fsRootPath + " is " + (currentResponse ? "success" : "failure"));
                status = CheckStatus.and(status, currentResponse);
                log.debug("Partial result is " + status.toString());
                if (!checkFile.delete())
                {
                    log.warn("Unable to delete the temporary file used for the check " + checkFile.getAbsolutePath());
                }
            }
        }
        catch (NamespaceException e)
        {
            // NOTE: this exception is never thrown
            log.warn("Unable to proceede. NamespaceException : " + e.getMessage());
            errorMessage += "Unable to proceede. NamespaceException : " + e.getMessage() + "; ";
            status = CheckStatus.INDETERMINATE;
        }
        return new CheckResponse(status, errorMessage);
    }


    /**
     * Provides a File located in rootPath with a pseudo-random name. It tries to provide the file and in case
     * of error
     * retries for MAX_FILE_CREATION_ATTEMPTS times changing file name
     * 
     * @param rootPath
     * @return
     * @throws GenericCheckException if is unable to provide a valid file
     */
    private File provideCheckFile(String rootPath, String infix) throws GenericCheckException
    {
        int attempCount = 1;
        boolean fileAvailable = false;
        File checkFile = null;
        while (attempCount <= MAX_FILE_CREATION_ATTEMPTS && !fileAvailable)
        {
            checkFile = new File(rootPath + infix + attempCount + "-" + Calendar.getInstance().getTimeInMillis());
            if (checkFile.exists())
            {
                if (checkFile.isFile())
                {
                    fileAvailable = true;
                    log.debug("A good check temporary file already exists at " + checkFile.getAbsolutePath());
                }
                else
                {
                    log.warn("Unable to create check file, it already exists but is not a simple file : " + checkFile.getAbsolutePath());
                }
            }
            else
            {
                try
                {
                    fileAvailable = checkFile.createNewFile();
                    if (fileAvailable)
                    {
                        log.debug("Created check temporary file at " + checkFile.getAbsolutePath());
                    }
                }
                catch (IOException e)
                {
                    log.warn("Unable to create the check file : " + checkFile.getAbsolutePath() + ". IOException: " + e.getMessage());
                }
            }
            attempCount++;
        }
        if (!fileAvailable)
        {
            log.warn("Unable to create check file, reaced maximum iterations at path : " + checkFile.getAbsolutePath());
            throw new GenericCheckException("Unable to create the check file for root path '" + rootPath + "'");
        }
        return checkFile;
    }


    /**
     * Tries to write CHECK_ATTRIBUTE_NAME EA on file with value CHECK_ATTRIBUTE_VALUE, retrieve its value and
     * remove it
     * 
     * @param file
     * @param filesystem
     * @return true if the write, read and remove operations succeeds and the retrieved value matches
     *         CHECK_ATTRIBUTE_VALUE
     */
    private boolean checkEACL(File file, Filesystem filesystem)
    {
        boolean response = true;
        log.debug("Testing extended attribute management on file " + file.getAbsolutePath());
        log.debug("Trying to set the extended ACL " + TEST_PERMISSION + " to group " + TEST_LOCAL_USER.getPrimaryGid() + " on file "
                + file.getAbsolutePath());
        FilesystemPermission oldPermisssion = filesystem.grantGroupPermission(TEST_LOCAL_USER, file.getAbsolutePath(), TEST_PERMISSION);
        if(oldPermisssion == null)
        {
            oldPermisssion = FilesystemPermission.None;
        }
        log.debug("Original group permission : " + oldPermisssion);
        log.debug("Trying to get the extended ACL  of group " + TEST_LOCAL_USER.getPrimaryGid() + " from file " + file.getAbsolutePath());
        FilesystemPermission currentPermission = filesystem.getGroupPermission(TEST_LOCAL_USER, file.getAbsolutePath());
        if(currentPermission == null)
        {
            currentPermission = FilesystemPermission.None;
        }
        log.debug("Returned value is \'" + currentPermission + "\'");
        log.debug("Trying to remove the extended group ACL " + TEST_PERMISSION + " from file " + file.getAbsolutePath());
        FilesystemPermission previousPermission = filesystem.revokeGroupPermission(TEST_LOCAL_USER, file.getAbsolutePath(), TEST_PERMISSION);
        if(previousPermission == null)
        {
            previousPermission = FilesystemPermission.None;
        }
        log.debug("Revoked group permission is : " + previousPermission);
        if (currentPermission.getInt() != previousPermission.getInt())
        {
            log.warn("Undesired behaviour! The revoked extended group ACL value \'" + previousPermission + "\' differs from the one setted \'"
                    + currentPermission + "\'");
            response &= false;
        }
        else
        {
            response &= true;
        }
        currentPermission = filesystem.getGroupPermission(TEST_LOCAL_USER, file.getAbsolutePath());
        if(currentPermission == null)
        {
            currentPermission = FilesystemPermission.None;
        }
        log.debug("Final group permission is : " + currentPermission);
        if (currentPermission.getInt() != oldPermisssion.getInt())
        {
            log.warn("Undesired behaviour! The final extended group ACL value \'" + currentPermission + "\' differs from the original \'"
                    + oldPermisssion + "\'");
            response &= false;
        }
        else
        {
            response &= true;
        }
        log.debug("Trying to set the extended ACL " + TEST_PERMISSION + " to user " + TEST_LOCAL_USER.getUid() + " on file "
                  + file.getAbsolutePath());
        oldPermisssion = filesystem.grantUserPermission(TEST_LOCAL_USER, file.getAbsolutePath(), TEST_PERMISSION);
        if(oldPermisssion == null)
        {
            oldPermisssion = FilesystemPermission.None;
        }
        log.debug("Original user permission : " + oldPermisssion);
        log.debug("Trying to get the extended ACL  of user " + TEST_LOCAL_USER.getUid() + " from file " + file.getAbsolutePath());
        currentPermission = filesystem.getUserPermission(TEST_LOCAL_USER, file.getAbsolutePath());
        if(currentPermission == null)
        {
            currentPermission = FilesystemPermission.None;
        }
        log.debug("Returned value is \'" + currentPermission + "\'");
        log.debug("Trying to remove the extended user ACL " + TEST_PERMISSION + " from file " + file.getAbsolutePath());
        previousPermission = filesystem.revokeUserPermission(TEST_LOCAL_USER, file.getAbsolutePath(), TEST_PERMISSION);
        if(previousPermission == null)
        {
            previousPermission = FilesystemPermission.None;
        }
        log.debug("Revoked user permission is : " + previousPermission);
        if (currentPermission.getInt() != previousPermission.getInt())
        {
            log.warn("Undesired behaviour! The removed extended user ACL value \'" + previousPermission + "\' differs from the one setted \'"
                    + currentPermission + "\'");
            response &= false;
        }
        else
        {
            response &= true;
        }
        currentPermission = filesystem.getUserPermission(TEST_LOCAL_USER, file.getAbsolutePath());
        if(currentPermission == null)
        {
            currentPermission = FilesystemPermission.None;
        }
        log.debug("Final user permission is : " + currentPermission);
        if (currentPermission.getInt() != oldPermisssion.getInt())
        {
            log.warn("Undesired behaviour! The final extended user ACL value \'" + currentPermission + "\' differs from the original \'"
                    + oldPermisssion + "\'");
            response &= false;
        }
        else
        {
            response &= true;
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
    
    @Override
    public boolean isCritical()
    {
        return criticalCheck;
    }
    
    private static class FakeGridUser implements GridUserInterface {

        /**
         * 
         */
        private DistinguishedName dn;

        /**
         * @param dn
         */
        public FakeGridUser(String dn) {
            this.setDN(dn);
        }

        /**
         * @param dnString
         */
        private void setDN(String dnString)
        {
            this.dn = new DistinguishedName(dnString);
        }

        /* (non-Javadoc)
         * @see it.grid.storm.griduser.GridUserInterface#getDn()
         */
        public String getDn() {
            return dn.toString();
        }

        /* (non-Javadoc)
         * @see it.grid.storm.griduser.GridUserInterface#getLocalUser()
         */
        public LocalUser getLocalUser() throws CannotMapUserException {
            return new LocalUser(0,0);
        }

        /* (non-Javadoc)
         * @see it.grid.storm.griduser.GridUserInterface#getDistinguishedName()
         */
        public DistinguishedName getDistinguishedName() {
            return this.dn;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "Fake Grid User (no VOMS): '" + getDistinguishedName().getX500DN_rfc1779() + "'";
        }
    }
}

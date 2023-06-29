/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.check.sanity.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.check.Check;
import it.grid.storm.check.CheckResponse;
import it.grid.storm.check.CheckStatus;
import it.grid.storm.check.GenericCheckException;
import it.grid.storm.filesystem.FilesystemIF;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.VirtualFS;

/**
 * @author Michele Dibenedetto
 * 
 */
public class NamespaceFSExtendedACLUsageCheck implements Check {

  private static final Logger log = LoggerFactory.getLogger(NamespaceFSExtendedACLUsageCheck.class);
  private static final String CHECK_NAME = "NamespaceFSEACLTest";
  private static final String CHECK_DESCRIPTION =
      "This check tries to use file system extended ACL on all the file systems declared in namespace.xml";
  /**
   * The maximum number of attempts of temporary file creation
   */
  private static final int MAX_FILE_CREATION_ATTEMPTS = 10;
  private static final GridUserInterface TEST_USER =
      new FakeGridUser("/C=IT/O=INFN/L=CNAF/CN=Fake User");
  private static LocalUser TEST_LOCAL_USER = null;
  private static final FilesystemPermission TEST_PERMISSION =
      FilesystemPermission.ListTraverseWrite;
  private static final String TEST_FILE_INFIX = "ACL-check-file-N_";

  private static final boolean criticalCheck = true;

  @Override
  public CheckResponse execute() throws GenericCheckException {

    CheckStatus status = CheckStatus.SUCCESS;
    String errorMessage = "";
    try {
      TEST_LOCAL_USER = TEST_USER.getLocalUser();
    } catch (CannotMapUserException e) {
      log.warn("Unable to obtain local user for test user {}", TEST_USER);
      throw new GenericCheckException("Unable to obtain local user for test user " + TEST_USER);
    }
    try {
      // load declared file systems from namespace.xml
      for (VirtualFS vfs : NamespaceDirector.getNamespace().getAllDefinedVFS()) {
        String fsRootPath = vfs.getRootPath().trim();
        if (fsRootPath.charAt(fsRootPath.length() - 1) != File.separatorChar) {
          fsRootPath += File.separatorChar;
        }
        // for each root path get a temporary file in it
        File checkFile;
        try {
          checkFile = provideCheckFile(fsRootPath, TEST_FILE_INFIX);
        } catch (GenericCheckException e) {
          log.warn("Unable to obtain a check temporary file. " + "GenericCheckException: {}",
              e.getMessage());
          errorMessage += "Unable to obtain a check temporary file. GenericCheckException : "
              + e.getMessage() + "; ";
          status = CheckStatus.INDETERMINATE;
          continue;
        }
        FilesystemIF filesystem = vfs.getFilesystem();
        // tries to manage the extended attributes on file checkFile
        boolean currentResponse = this.checkEACL(checkFile, filesystem);
        if (!currentResponse) {
          log.error(
              "Check on VFS {} to add an extended ACL on file {} failed. "
                  + "File System type = {}, root path = {}",
              vfs.getAliasName(), checkFile.getAbsolutePath(), vfs.getFSType(), fsRootPath);
          errorMessage += "Check on VFS " + vfs.getAliasName() + " to add an extended ACL on file "
              + checkFile.getAbsolutePath() + " failed. File System type =" + vfs.getFSType()
              + " , root path =" + fsRootPath + "; ";
        }
        log.debug("Check response for path {} is {}", fsRootPath,
            currentResponse ? "success" : "failure");
        status = CheckStatus.and(status, currentResponse);
        log.debug("Partial result is {}", status.toString());
        if (!checkFile.delete()) {
          log.warn("Unable to delete the temporary file used for the check {}",
              checkFile.getAbsolutePath());
        }
      }
    } catch (NamespaceException e) {
      // NOTE: this exception is never thrown
      log.warn("Unable to proceede. NamespaceException : {}", e.getMessage());
      errorMessage += "Unable to proceede. NamespaceException : " + e.getMessage() + "; ";
      status = CheckStatus.INDETERMINATE;
    }
    return new CheckResponse(status, errorMessage);
  }

  /**
   * Provides a File located in rootPath with a pseudo-random name. It tries to provide the file and
   * in case of error retries for MAX_FILE_CREATION_ATTEMPTS times changing file name
   * 
   * @param rootPath
   * @return
   * @throws GenericCheckException if is unable to provide a valid file
   */
  private File provideCheckFile(String rootPath, String infix) throws GenericCheckException {

    int attempCount = 1;
    boolean fileAvailable = false;
    File checkFile = null;
    while (attempCount <= MAX_FILE_CREATION_ATTEMPTS && !fileAvailable) {
      checkFile =
          new File(rootPath + infix + attempCount + "-" + Calendar.getInstance().getTimeInMillis());
      if (checkFile.exists()) {
        if (checkFile.isFile()) {
          fileAvailable = true;
          log.debug("A good check temporary file already exists at {}",
              checkFile.getAbsolutePath());
        } else {
          log.warn(
              "Unable to create check file, it already exists but is not " + "a simple file : {}",
              checkFile.getAbsolutePath());
        }
      } else {
        try {
          fileAvailable = checkFile.createNewFile();
          if (fileAvailable) {
            log.debug("Created check temporary file at {}", checkFile.getAbsolutePath());
          }
        } catch (IOException e) {
          log.warn("Unable to create the check file : {}. IOException: {}",
              checkFile.getAbsolutePath(), e.getMessage());
        }
      }
      attempCount++;
    }
    if (!fileAvailable) {
      log.warn("Unable to create check file, reaced maximum iterations at " + "path : {}",
          checkFile.getAbsolutePath());
      throw new GenericCheckException(
          "Unable to create the check file for root path '" + rootPath + "'");
    }
    return checkFile;
  }

  /**
   * Tries to write CHECK_ATTRIBUTE_NAME EA on file with value CHECK_ATTRIBUTE_VALUE, retrieve its
   * value and remove it
   * 
   * @param file
   * @param filesystem
   * @return true if the write, read and remove operations succeeds and the retrieved value matches
   *         CHECK_ATTRIBUTE_VALUE
   */
  private boolean checkEACL(File file, FilesystemIF filesystem) {

    boolean response = true;
    log.debug("Testing extended attribute management on file {}", file.getAbsolutePath());

    FilesystemPermission oldPermisssion =
        filesystem.getGroupPermission(TEST_LOCAL_USER, file.getAbsolutePath());
    if (oldPermisssion == null) {
      oldPermisssion = FilesystemPermission.None;
    }
    FilesystemPermission testPermission = TEST_PERMISSION.deny(oldPermisssion);
    log.debug("Trying to set the extended ACL {} to group {} on file {}", testPermission,
        TEST_LOCAL_USER.getPrimaryGid(), file.getAbsolutePath());
    filesystem.grantGroupPermission(TEST_LOCAL_USER, file.getAbsolutePath(), testPermission);

    log.debug("Original group permission : {}", oldPermisssion);
    log.debug("Trying to get the extended ACL  of group {} from file {}",
        TEST_LOCAL_USER.getPrimaryGid(), file.getAbsolutePath());
    FilesystemPermission currentPermission =
        filesystem.getGroupPermission(TEST_LOCAL_USER, file.getAbsolutePath());
    if (currentPermission == null) {
      currentPermission = FilesystemPermission.None;
    }
    log.debug("Returned value is '{}'", currentPermission);
    log.debug("Trying to remove the extended group ACL {} from file {}", testPermission,
        file.getAbsolutePath());
    FilesystemPermission previousPermission =
        filesystem.revokeGroupPermission(TEST_LOCAL_USER, file.getAbsolutePath(), testPermission);
    if (previousPermission == null) {
      previousPermission = FilesystemPermission.None;
    }
    log.debug("Revoked group permission is : {}", previousPermission);
    if (currentPermission.getInt() != previousPermission.getInt()) {
      log.warn("Undesired behaviour! The revoked extended group ACL value '{}' "
          + "differs from the one setted '{}'", previousPermission, currentPermission);
      response &= false;
    } else {
      response &= true;
    }
    currentPermission = filesystem.getGroupPermission(TEST_LOCAL_USER, file.getAbsolutePath());
    if (currentPermission == null) {
      currentPermission = FilesystemPermission.None;
    }
    log.debug("Final group permission is : {}", currentPermission);
    if (currentPermission.getInt() != oldPermisssion.getInt()) {
      log.warn("Undesired behaviour! The final extended group ACL value '{}' "
          + "differs from the original '{}'", currentPermission, oldPermisssion);
      response &= false;
    } else {
      response &= true;
    }
    oldPermisssion = filesystem.getUserPermission(TEST_LOCAL_USER, file.getAbsolutePath());
    if (oldPermisssion == null) {
      oldPermisssion = FilesystemPermission.None;
    }
    testPermission = TEST_PERMISSION.deny(oldPermisssion);
    log.debug("Trying to set the extended ACL {} to user {} on file {}", testPermission,
        TEST_LOCAL_USER.getUid(), file.getAbsolutePath());
    filesystem.grantUserPermission(TEST_LOCAL_USER, file.getAbsolutePath(), testPermission);
    log.debug("Original user permission : {}", oldPermisssion);
    log.debug("Trying to get the extended ACL  of user {} from file {}", TEST_LOCAL_USER.getUid(),
        file.getAbsolutePath());
    currentPermission = filesystem.getUserPermission(TEST_LOCAL_USER, file.getAbsolutePath());
    if (currentPermission == null) {
      currentPermission = FilesystemPermission.None;
    }
    log.debug("Returned value is '{}'", currentPermission);
    log.debug("Trying to remove the extended user ACL {} from file {}", testPermission,
        file.getAbsolutePath());
    previousPermission =
        filesystem.revokeUserPermission(TEST_LOCAL_USER, file.getAbsolutePath(), testPermission);
    if (previousPermission == null) {
      previousPermission = FilesystemPermission.None;
    }
    log.debug("Revoked user permission is : {}", previousPermission);
    if (currentPermission.getInt() != previousPermission.getInt()) {
      log.warn("Undesired behaviour! The removed extended user ACL value '{}' "
          + "differs from the one setted '{}'", previousPermission, currentPermission);
      response &= false;
    } else {
      response &= true;
    }
    currentPermission = filesystem.getUserPermission(TEST_LOCAL_USER, file.getAbsolutePath());
    if (currentPermission == null) {
      currentPermission = FilesystemPermission.None;
    }
    log.debug("Final user permission is : {}", currentPermission);
    if (currentPermission.getInt() != oldPermisssion.getInt()) {
      log.warn("Undesired behaviour! The final extended user ACL value '{}' "
          + "differs from the original '{}'", currentPermission, oldPermisssion);
      response &= false;
    } else {
      response &= true;
    }
    return response;
  }

  @Override
  public String getName() {

    return CHECK_NAME;
  }

  @Override
  public String getDescription() {

    return CHECK_DESCRIPTION;
  }

  @Override
  public boolean isCritical() {

    return criticalCheck;
  }
}

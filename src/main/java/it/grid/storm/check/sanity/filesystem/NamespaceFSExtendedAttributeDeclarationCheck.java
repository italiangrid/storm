/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package it.grid.storm.check.sanity.filesystem;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.check.GenericCheckException;
import it.grid.storm.check.Check;
import it.grid.storm.check.CheckResponse;
import it.grid.storm.check.CheckStatus;
import it.grid.storm.filesystem.MtabRow;
import it.grid.storm.filesystem.MtabUtil;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;

/**
 * @author Michele Dibenedetto
 */
public class NamespaceFSExtendedAttributeDeclarationCheck implements Check {

  private static final Logger log =
      LoggerFactory.getLogger(NamespaceFSExtendedAttributeDeclarationCheck.class);

  private static final String POSIX_EXTENDED_ATTRIBUTES_OPTION_NAME = "user_xattr";

  private static final String CHECK_NAME = "NamespaceFSEAValidation";
  private static final String CHECK_DESCRIPTION =
      "This check verifies that all the file systems declared "
          + "in namespace.xml are mounted at boot time with the mount \'extended attribute abilitation\' option ";

  private static final boolean criticalCheck = false;

  @Override
  public CheckResponse execute() throws GenericCheckException {

    CheckStatus status = CheckStatus.SUCCESS;
    String errorMessage = "";
    // load mtab rows
    List<MtabRow> rows;
    try {
      rows = MtabUtil.getRows();
    } catch (IOException e) {
      log.warn("Unable to get the rows from mtab. IOException : {}", e.getMessage());
      return new CheckResponse(CheckStatus.INDETERMINATE,
          "Check not performed. Unable to get the rows from mtab. IOException : " + e.getMessage());
    }
    log.debug("Retrieved Mtab : {}", rows.toString());
    try {
      // load declared file systems from namespace.xml
      for (VirtualFSInterface vfs : NamespaceDirector.getNamespace().getAllDefinedVFS()) {
        String fsTypeName = vfs.getFSType();
        String fsRootPath = vfs.getRootPath();
        if (fsTypeName == null || fsRootPath == null) {
          log.warn(
              "Skipping chek on VFS with alias '{}' has null type ->{}<- " + "or root path ->{}<-",
              vfs.getAliasName(), vfs.getFSType(), vfs.getRootPath());
          continue;
        }
        log.debug("Checking fs at {} with type {}", fsRootPath, fsTypeName);
        boolean found = false;
        // for each root path get the matching line in mstab
        for (MtabRow row : rows) {
          if (fsRootPath.startsWith(row.getMountPoint())) {
            log.debug("Found on mountPoint {}", row.getMountPoint());
            // this is the row to check
            found = true;
            SupportedFSType fsType;
            try {
              fsType = SupportedFSType.parseFS(fsTypeName);
            } catch (IllegalArgumentException e) {
              log.warn("Unable to get the SupportedFSType for file system '{}'. "
                  + "IllegalArgumentException: {}", fsTypeName, e.getMessage());
              throw new GenericCheckException(
                  "Unable to get the " + "SupportedFSType for file system \'" + fsTypeName
                      + "\' IllegalArgumentException: " + e.getMessage());
            }

            // given the file system specified in the row check if the
            // appropriate flag enabling EA is set
            CheckStatus retrievedStatus;
            switch (fsType) {
              case EXT3:
                retrievedStatus = checkEXT3(row.getMountOptions());
                break;
              case GPFS:
                retrievedStatus = checkGPFS(row.getMountOptions());
                break;
              default: {
                log.error("Unable to switch on the provided SupportedFSType " + "(unknown): {}",
                    fsType);
                throw new GenericCheckException(
                    "Unable to switch on the " + "provided SupportedFSType (unknown) : " + fsType);
              }
            }
            if (!retrievedStatus.equals(CheckStatus.SUCCESS)) {
              log.error("Check failed for file system at {} with type {}", fsRootPath, fsType);
              errorMessage +=
                  "Check failed for file system at " + fsRootPath + " with type " + fsType + "; ";
            }
            status = CheckStatus.and(status, retrievedStatus);
            break;
          }
        }
        if (!found) {
          log.error("No file systems are mounted at path {}!", fsRootPath);
          errorMessage += "No file systems are mounted at path " + fsRootPath + ";";
          status = CheckStatus.INDETERMINATE;
        }
      }
    } catch (NamespaceException e) {
      // NOTE: this exception is never thrown
      log.warn("Unable to proceede received a NamespaceException: {}", e.getMessage());
      errorMessage += "Unable to proceede received a NamespaceException : " + e.getMessage() + "; ";
      status = CheckStatus.INDETERMINATE;
    }
    return new CheckResponse(status, errorMessage);
  }

  /**
   * Checks if the ext3 mount option POSIX_EXTENDED_ATTRIBUTES_OPTION_NAME is in the provided mount
   * options list
   * 
   * @param fsOptions a comma separated list of mount options
   * @return a successful CheckStatus if the option is available
   */
  private CheckStatus checkEXT3(List<String> fsOptions) {

    log.debug("Checking ext3 file system estended attribute options " + "against '{}'",
        fsOptions.toString());
    CheckStatus response = CheckStatus.FAILURE;
    if (fsOptions.contains(POSIX_EXTENDED_ATTRIBUTES_OPTION_NAME)) {
      log.debug("Options for ext3 correctly set");
      response = CheckStatus.SUCCESS;
    }
    return response;
  }

  /**
   * Checks if the gpfs mount option is in the provided mount options list
   * 
   * @param fsOptions a comma separated list of mount options
   * @return always a successful CheckStatus, gpfs has always EA enabled
   */
  private CheckStatus checkGPFS(List<String> fsOptions) {

    log.debug("Checking gpfs file system estended attribute options " + "against '{}'",
        fsOptions.toString());
    /*
     * According to Vladimir for GPFS the EA are enabled by default and their status doesn't have
     * any info in mtab
     */
    CheckStatus response = CheckStatus.SUCCESS;
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

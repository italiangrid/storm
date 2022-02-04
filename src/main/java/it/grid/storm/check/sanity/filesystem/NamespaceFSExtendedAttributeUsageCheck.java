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

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.check.Check;
import it.grid.storm.check.CheckResponse;
import it.grid.storm.check.CheckStatus;
import it.grid.storm.check.GenericCheckException;
import it.grid.storm.ea.ExtendedAttributes;
import it.grid.storm.ea.ExtendedAttributesException;
import it.grid.storm.ea.ExtendedAttributesFactory;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.model.VirtualFS;

/**
 * @author Michele Dibenedetto
 */
public class NamespaceFSExtendedAttributeUsageCheck implements Check {

  private static final Logger log =
      LoggerFactory.getLogger(NamespaceFSExtendedAttributeUsageCheck.class);

  private static final String CHECK_NAME = "NamespaceFSEATest";

  private static final String CHECK_DESCRIPTION =
      "This check tries to use file system extended attributes on all the file systems declared in namespace.xml";

  /**
   * The maximum number of attempts of temporary file creation
   */
  private static final int MAX_FILE_CREATION_ATTEMPTS = 10;

  private static final String TEST_FILE_INFIX = "EA-check-file-N_";

  /**
   * An extended attribute to be used in the check
   */
  private static final String CHECK_ATTRIBUTE_NAME = "user.Are.you.a.check";

  /**
   * THe value to be assigned to the extended attribute CHECK_ATTRIBUTE_NAME in the check
   */
  private static final String CHECK_ATTRIBUTE_VALUE = "Yes.I.am";

  private final ExtendedAttributes extendedAttribute =
      ExtendedAttributesFactory.getExtendedAttributes();

  private static final boolean CRITICAL_CHECK = true;

  @Override
  public CheckResponse execute() throws GenericCheckException {

    CheckStatus status = CheckStatus.SUCCESS;
    String errorMessage = "";
    // load declared file systems from namespace.xml
    for (VirtualFS vfs : Namespace.getInstance().getAllDefinedVFS()) {
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
        errorMessage += "Unable to obtain a check temporary file. " + "GenericCheckException : "
            + e.getMessage() + "; ";
        status = CheckStatus.INDETERMINATE;
        continue;
      }
      // tries to manage the extended attributes on file checkFile
      boolean currentResponse = this.checkEA(checkFile);
      if (!currentResponse) {
        log.warn(
            "Check on VFS {} to add EA on file {} failed. File System "
                + "type = {}, root path = {}",
            vfs.getAliasName(), checkFile.getAbsolutePath(), vfs.getFSType(), fsRootPath);
        errorMessage += "Check on VFS " + vfs.getAliasName() + " to add EA on file "
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
    return new CheckResponse(status, errorMessage);
  }

  /**
   * Provides a File located in rootPath with a pseudo-random name. It tries to provide the file and
   * in case of error retries for MAX_FILE_CREATION_ATTEMPTS times changing file name
   * 
   * @param rootPath
   * @param infix
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
              "Unable to create check file, it already exists but is not " + "a simple file: {}",
              checkFile.getAbsolutePath());
        }
      } else {
        try {
          fileAvailable = checkFile.createNewFile();
          if (fileAvailable) {
            log.debug("Created check temporary file at {}", checkFile.getAbsolutePath());
          }
        } catch (IOException e) {
          log.warn("Unable to create the check file: {}. IOException: {}",
              checkFile.getAbsolutePath(), e.getMessage());
        }
      }
      attempCount++;
    }
    if (!fileAvailable) {
      log.warn("Unable to create check file, reached maximum iterations at " + "path: {}",
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
   * @return true if the write, read and remove operations succeeds and the retrieved value matches
   *         CHECK_ATTRIBUTE_VALUE
   */
  private boolean checkEA(File file) {

    boolean response = false;
    log.debug("Testing extended attribute management on file {}", file.getAbsolutePath());
    try {
      log.debug("Trying to set the extended attribute {} to value {} on file {}",
          CHECK_ATTRIBUTE_NAME, CHECK_ATTRIBUTE_VALUE, file.getAbsolutePath());

      extendedAttribute.setXAttr(file.getAbsolutePath(), CHECK_ATTRIBUTE_NAME,
          CHECK_ATTRIBUTE_VALUE);

      log.debug("Trying to get the extended attribute {} from file {}", CHECK_ATTRIBUTE_NAME,
          file.getAbsolutePath());
      String value = extendedAttribute.getXAttr(file.getAbsolutePath(), CHECK_ATTRIBUTE_NAME);
      log.debug("Returned value is '{}'", value);
      log.debug("Trying to remove the extended attribute {} from file {}", CHECK_ATTRIBUTE_NAME,
          file.getAbsolutePath());
      extendedAttribute.rmXAttr(file.getAbsolutePath(), CHECK_ATTRIBUTE_NAME);
      if (!CHECK_ATTRIBUTE_VALUE.equals(value)) {
        log.warn("Undesired behaviour! The returned extended attribute "
            + "value '{}' differs from the one setted '{}'", value, CHECK_ATTRIBUTE_VALUE);
      } else {
        response = true;
      }
    } catch (ExtendedAttributesException e) {
      log.warn(
          "Unable to manage extended attributes on file {}. " + "ExtendedAttributesException: {}",
          file.getAbsolutePath(), e.getMessage());
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

    return CRITICAL_CHECK;
  }
}

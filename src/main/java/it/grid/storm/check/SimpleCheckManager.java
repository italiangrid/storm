/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.check;

import com.google.common.collect.Lists;
import it.grid.storm.check.sanity.filesystem.NamespaceFSAssociationCheck;
import it.grid.storm.check.sanity.filesystem.NamespaceFSExtendedACLUsageCheck;
import it.grid.storm.check.sanity.filesystem.NamespaceFSExtendedAttributeUsageCheck;
import it.grid.storm.filesystem.MtabUtil;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.model.VirtualFS;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Michele Dibenedetto */
public class SimpleCheckManager extends CheckManager {

  private static final Logger log = LoggerFactory.getLogger(SimpleCheckManager.class);

  /** A list of checks to be executed */
  private List<Check> checks = Lists.newArrayList();

  @Override
  protected Logger getLogger() {

    return log;
  }

  @Override
  protected void loadChecks() {

    /* Add by hand a new element for each requested check */
    try {
      checks.add(getNamespaceFSAssociationCheck());
    } catch (IllegalStateException e) {
      log.warn(
          "Skipping NamespaceFSAssociationCheck. " + "IllegalStateException: {}", e.getMessage());
    }
    // checks.add(new NamespaceFSExtendedAttributeDeclarationCheck()); Removed
    checks.add(new NamespaceFSExtendedAttributeUsageCheck());
    checks.add(new NamespaceFSExtendedACLUsageCheck());
  }

  /** */
  private Check getNamespaceFSAssociationCheck() {

    Map<String, String> mountPoints;
    // load mstab mount points and file system types
    try {
      mountPoints = MtabUtil.getFSMountPoints();
    } catch (Exception e) {
      log.error("Unable to get filesystem mount points. Exception: {}", e.getMessage());
      throw new IllegalStateException("Unable to get filesystem mount points");
    }
    if (log.isDebugEnabled()) {
      log.debug("Retrieved MountPoints: {}", printMapCouples(mountPoints));
    }
    List<VirtualFS> vfsSet = NamespaceDirector.getNamespace().getAllDefinedVFS();
    return new NamespaceFSAssociationCheck(mountPoints, vfsSet);
  }

  /**
   * Prints the couple <key,value> from a Map
   *
   * @param map
   * @return
   */
  private String printMapCouples(Map<String, String> map) {

    String output = "";
    for (Entry<String, String> couple : map.entrySet()) {
      if (output.trim().length() != 0) {
        output += " ; ";
      }
      output += "<" + couple.getKey() + "," + couple.getValue() + ">";
    }
    return output;
  }

  @Override
  protected List<Check> prepareSchedule() {

    return checks;
  }
}

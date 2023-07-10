/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.filesystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Michele Dibenedetto */
public class MtabUtil {

  private static final Logger log = LoggerFactory.getLogger(MtabUtil.class);

  private static final String MTAB_FILE_PATH = "/etc/mtab";

  private static final int MTAB_DEVICE_INDEX = 0;

  private static final int MTAB_MOUNT_POINT_INDEX = 1;

  private static final int MTAB_FS_NAME_INDEX = 2;

  private static final int MTAB_MOUNT_OPTIONS_INDEX = 3;

  private static final int MTAB_DUMP_INDEX = 4;

  private static final int MTAB_FSC_ORDER_POSITION_INDEX = 5;

  public static String getFilePath() {

    return MTAB_FILE_PATH;
  }

  /** @return the mtabDeviceIndex */
  public static final int getMtabDeviceIndex() {

    return MTAB_DEVICE_INDEX;
  }

  public static int getMountPointIndex() {

    return MTAB_MOUNT_POINT_INDEX;
  }

  public static int getFsNameIndex() {

    return MTAB_FS_NAME_INDEX;
  }

  /** @return the mtabMountOptionsIndex */
  public static final int getMtabMountOptionsIndex() {

    return MTAB_MOUNT_OPTIONS_INDEX;
  }

  /** @return the mtabDumpIndex */
  public static final int getMtabDumpIndex() {

    return MTAB_DUMP_INDEX;
  }

  /** @return the mtabFscOrderPositionIndex */
  public static final int getMtabFscOrderPositionIndex() {

    return MTAB_FSC_ORDER_POSITION_INDEX;
  }

  protected static boolean skipLineForMountPoints(String line) {

    return line.startsWith("#") || line.isEmpty();
  }

  public static Map<String, String> getFSMountPoints() throws Exception {

    HashMap<String, String> mountPointToFSMap = new HashMap<String, String>();
    BufferedReader mtab = null;
    try {
      try {
        mtab = new BufferedReader(new FileReader(getFilePath()));
      } catch (FileNotFoundException e) {
        log.error(e.getMessage(), e);
        throw new Exception("Unable to get mount points. mtab file not found", e);
      }
      String line;
      try {
        while ((line = mtab.readLine()) != null) {
          if (skipLineForMountPoints(line)) {
            continue;
          }
          LinkedList<String> elementsList = tokenizeLine(line);
          if ((elementsList.size() - 1) < getMountPointIndex()
              || (elementsList.size() - 1) < getFsNameIndex()) {
            log.warn(
                "FS mount point parsing error. "
                    + "Not enough elements found: {}. Skipping current line...",
                elementsList);
          } else {
            mountPointToFSMap.put(
                elementsList.get(getMountPointIndex()), elementsList.get(getFsNameIndex()));
          }
        }
      } catch (IOException e) {
        log.error(e.getMessage(), e);
        throw new Exception("Unable to get mount points. Erro reading from mtab");
      }
    } finally {
      if (mtab != null) {
        try {
          mtab.close();
        } catch (IOException e) {
        }
      }
    }
    return mountPointToFSMap;
  }

  public static List<MtabRow> getRows() throws IOException {

    List<MtabRow> rows = new ArrayList<MtabRow>();
    BufferedReader mtab = new BufferedReader(new FileReader(getFilePath()));
    String line;
    while ((line = mtab.readLine()) != null) {
      if (skipLineForMountPoints(line)) {
        continue;
      }
      log.debug("mtab row from string {}", line);
      MtabRow row = null;
      try {
        row = produceRow(line);
      } catch (IllegalArgumentException e) {
        log.warn("Skipping line {}. {}", line, e.getMessage(), e);
      }
      if (row != null) {
        rows.add(row);
      }
    }
    log.debug("Parsed {} mtab rows from file {}", rows.size(), MTAB_FILE_PATH);
    return rows;
  }

  private static MtabRow produceRow(String line) throws IllegalArgumentException {

    LinkedList<String> elementsList = tokenizeLine(line);
    return new MtabRow(elementsList);
  }

  public static LinkedList<String> tokenizeLine(String line) {

    String[] elementsArray = line.split(" ");
    LinkedList<String> elementsList = new LinkedList<String>(Arrays.asList(elementsArray));
    while (elementsList.remove("")) {}
    return elementsList;
  }
}

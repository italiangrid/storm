/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz.remote;

/** @author Michele Dibenedetto */
public class Constants {

  public static final String ENCODING_SCHEME = "UTF-8";

  public static final String RESOURCE = "authorization";

  public static final String VERSION = "1.1";

  public static final String VERSION_1_0 = "1.0";

  public static final String READ_OPERATION = "read";

  public static final String WRITE_OPERATION = "write";

  public static final String PREPARE_TO_GET_OPERATION = "ptg";

  public static final String PREPARE_TO_PUT_OPERATION = "ptp";

  public static final String PREPARE_TO_PUT_OVERWRITE_OPERATION = "ptpOverwrite";

  public static final String RM_OPERATION = "rm";

  public static final String LS_OPERATION = "ls";

  public static final String MKDIR_OPERATION = "mkdir";

  public static final String CP_FROM_OPERATION = "cpFrom";

  public static final String CP_TO_OPERATION = "cpTo";

  public static final String CP_TO_OVERWRITE_OPERATION = "cpToOverwrite";

  public static final String MOVE_FROM_OPERATION = "moveFrom";

  public static final String MOVE_TO_OVERWRITE_OPERATION = "moveToOverwrite";

  public static final String MOVE_TO_OPERATION = "moveTo";

  public static final String VOMS_EXTENSIONS = "voms";

  public static final String PLAIN = "plain";

  public static final String USER = "user";

  public static final String DN_KEY = "DN";

  public static final String FQANS_KEY = "FQANS";

  public static final String FQANS_SEPARATOR = ",";

  /*
   * /RESOURCE/VERSION/path/READ_OPERATION/VOMS_EXTENSIONS/USER?DN_KEY=dn&FQANS_KEY
   * =fquanFQANS_SEPARATORfquan /RESOURCE/VERSION/path/WRITE_OPERATION/VOMS_EXTENSIONS
   * /USER?DN_KEY=dn&FQANS_KEY=fquanFQANS_SEPARATORfquan /RESOURCE/VERSION/path/READ_OPERATION
   * /PLAIN/USER?DN_KEY=dn&FQANS_KEY=fquanFQANS_SEPARATORfquan /RESOURCE/VERSION/
   * path/WRITE_OPERATION/PLAIN/USER?DN_KEY=dn&FQANS_KEY=fquanFQANS_SEPARATORfquan
   *
   * /RESOURCE/VERSION/path/READ_OPERATION
   */
}

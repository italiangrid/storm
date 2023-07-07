/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.StFN;
import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.srm.types.InvalidTTURLAttributesException;
import it.grid.storm.srm.types.TTURL;

public class TURLBuilder {

  private static final Logger log = LoggerFactory.getLogger(TURLBuilder.class);

  private TURLBuilder() {}

  private static TTURL buildTURL(Protocol protocol, Authority authority, String extraSlashes,
      String path) {

    String turlString = String.format("%s://%s%s%s", protocol.getSchema(), authority ,extraSlashes, path);
    log.debug("turlString used to build the TURL : {}", turlString);
    try {
      return TTURL.makeFromString(turlString);
    } catch (InvalidTTURLAttributesException ex) {
      log.error("Error while constructing TURL with Authority '{}': {}", authority, ex.getMessage(),
          ex);
    }
    return null;
  }

  public static TTURL buildFileTURL(Authority authority, PFN physicalFN) {

    String extraSlashesForFile = Configuration.getInstance().getExtraSlashesForFileTURL();
    return buildTURL(Protocol.FILE, authority, extraSlashesForFile, physicalFN.getValue());
  }

  public static TTURL buildGsiftpTURL(Authority authority, PFN physicalFN) {

    String extraSlashesForGSIFTP = Configuration.getInstance().getExtraSlashesForGsiFTPTURL();
    return buildTURL(Protocol.GSIFTP, authority, extraSlashesForGSIFTP, physicalFN.getValue());
  }

  public static TTURL buildRFIOTURL(Authority authority, PFN physicalFN) {

    String extraSlashesForRFIO = Configuration.getInstance().getExtraSlashesForRFIOTURL();
    return buildTURL(Protocol.RFIO, authority, extraSlashesForRFIO, physicalFN.getValue());
  }

  public static TTURL buildROOTTURL(Authority authority, PFN physicalFN) {

    String extraSlashesForROOT = Configuration.getInstance().getExtraSlashesForROOTTURL();
    return buildTURL(Protocol.ROOT, authority, extraSlashesForROOT, physicalFN.getValue());
  }

  public static TTURL buildXROOTTURL(Authority authority, PFN physicalFN) {

    return buildROOTTURL(authority, physicalFN);
  }

  public static TTURL buildHttpURL(Authority authority, StFN stfn) {

    String prefix = Configuration.getInstance().getHTTPTURLPrefix(); 
    return buildTURL(Protocol.HTTP, authority, prefix, stfn.toString());
  }

  public static TTURL buildHttpsURL(Authority authority, StFN stfn) {

    String prefix = Configuration.getInstance().getHTTPTURLPrefix(); 
    return buildTURL(Protocol.HTTPS, authority, prefix, stfn.toString());
  }

  public static TTURL buildDavURL(Authority authority, StFN stfn) {

    String prefix = Configuration.getInstance().getHTTPTURLPrefix(); 
    return buildTURL(Protocol.DAV, authority, prefix, stfn.toString());
  }

  public static TTURL buildDavsURL(Authority authority, StFN stfn) {

    String prefix = Configuration.getInstance().getHTTPTURLPrefix(); 
    return buildTURL(Protocol.DAVS, authority, prefix, stfn.toString());
  }
}

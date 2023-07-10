/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import it.grid.storm.common.types.TransferProtocol;

/**
 * This class represents a TURL, that is a Transfer URL. It is made up of a TransferProtocol and a
 * TFN.
 * 
 * @author EGRID ICTP Trieste - CNAF Bologna
 * @date March 26th, 2005
 * @version 2.0
 */
public class TTURL {

  public static final String PNAME_TURL = "turl";

  private static Logger log = LoggerFactory.getLogger(TTURL.class);;
  private URI uri;

  private TTURL(URI uri) {

    Preconditions.checkNotNull(uri, "Invalid null URI");
    this.uri = uri;
  }

  private TTURL() {

    this.uri = null;
  }

  public static TTURL makeEmpty() {

    return new TTURL();
  }

  private static TTURL make(URI uri) {

    return new TTURL(uri);
  }

  public static TTURL makeFromString(String s) throws InvalidTTURLAttributesException {

    log.debug("Processing TURL string '{}'", s);
    URI turl = null;
    try {
      turl = new URI(s);
    } catch (URISyntaxException e) {
      throw new InvalidTTURLAttributesException("Invalid TURL: " + e.getMessage());
    }
    try {
      TransferProtocol.valueOf(turl.getScheme());
    } catch (IllegalArgumentException e) {
      log.warn("Unsupported TURL scheme: {}", turl.getScheme());
      throw new InvalidTTURLAttributesException(
          "Invalid TURL: Unsupported TURL scheme: " + turl.getScheme());
    }
    return TTURL.make(turl);

//    log.debug("Found valid Transfer Protocol '{}'.", tp);
////    if (turl.getPath() == null || turl.getPath().isEmpty() || turl.getPath().equals("/")) {
////      log.debug("Found invalid TURL path '{}'.", turl.getPath());
////      throw new InvalidTTURLAttributesException(
////          "Invalid TURL: Unsupported TURL path: " + turl.getPath());
////    }
//    TFN tfn = null;
//    if ((turl.getHost() != null) && (turl.getPort() != -1)) {
//      // host and port defined
//      tfn = TFN.make(Machine.make(turl.getHost()), Port.make(turl.getPort()), PFN.make(turl.getPath()));
//    } else if ((turl.getHost() != null) && (turl.getPort() == -1)) {
//      // host defined and port not
//      tfn = TFN.make(Machine.make(turl.getHost()), PFN.make(turl.getPath()));
//    } else if ((turl.getHost() == null) && (turl.getPort() != -1)) {
//      // port defined and host not
//      throw new InvalidTTURLAttributesException("Invalid TURL: port without hostname!");
//    } else {
//      // port and host not defined
//      tfn = TFN.make(PFN.make(turl.getPath()));
//    }
////    if (TransferProtocol.file.equals(tp)) {
////      if (turl.getHost() != null) {
////        tfn = TFN.make(Machine.make(turl.getHost()), PFN.make(turl.getPath()));
////      } else {
////        tfn = TFN.make(PFN.make(turl.getPath()));
////      }
////    } else {
////      if (turl.getPort() != -1) {
////        tfn = TFN.make(Machine.make(turl.getHost()), Port.make(turl.getPort()), PFN.make(turl.getPath()));
////      } else {
////        tfn = TFN.make(Machine.make(turl.getHost()), PFN.make(turl.getPath()));
////      }
////    }
//    log.debug("Built TFN: {}", tfn);
//    return TTURL.make(tp, tfn);
  }

//  public static TTURL makeFromStringLegacy(String s) throws InvalidTTURLAttributesException {
//
//    log.debug("Processing TURL string '{}'", s);
//    if (s == null) {
//      throw new InvalidTTURLAttributesException("Invalid TURL: null string provided");
//    }
//    int separator = s.indexOf("://");
//    if ((separator == -1) || (separator == 0)) {
//      throw new InvalidTTURLAttributesException("Invalid TURL: '://' not found");
//    }
//    String tpString = s.substring(0, separator);
//    TransferProtocol tp = null;
//    try {
//      tp = TransferProtocol.valueOf(tpString);
//    } catch (IllegalArgumentException e) {
//      throw new InvalidTTURLAttributesException(
//          "Invalid TURL: schema " + tpString + " not supported");
//    }
//    if ((separator + 3) > (s.length())) {
//      throw new InvalidTTURLAttributesException("Invalid TURL: empty hostname");
//    }
//    String tfnString = s.substring(separator + 3, s.length());
//    TFN tfn = null;
//    if (tfnString.startsWith("/")) {
//      tfn = TFN.make(PFN.make(tfnString));
//    } else {
//      try {
//        tfn = TFN.makeFromString(tfnString);
//      } catch (InvalidTFNAttributesException e) {
//        log.warn("TFN by {} is empty, but that's fine.", tfnString);
//      }
//    }
//    return TTURL.make(tp, tfn);
//  }

  /**
   * Encode TTURL for XMLRPC communication.
   */
  public void encode(Map<String, Object> param, String name) {

    param.put(name, toString());
  }

  @Override
  public String toString() {

    return uri.toString();
//    if (empty) {
//      return "Empty TTURL";
//    }
//    return tp + "://" + tfn;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TTURL other = (TTURL) obj;
    return Objects.equals(uri, other.uri);
  }

//  @Override
//  public boolean equals(Object o) {
//
//    if (o == this) {
//      return true;
//    }
//    if (!(o instanceof TTURL)) {
//      return false;
//    }
//    TTURL turlo = (TTURL) o;
//    if (empty && turlo.empty) {
//      return true;
//    }
//    return (!empty) && (!turlo.empty) && tp.equals(turlo.tp) && tfn.equals(turlo.tfn);
//  }
//
//  @Override
//  public int hashCode() {
//
//    if (empty) {
//      return 0;
//    }
//    int hash = 17;
//    hash = 37 * hash + tp.hashCode();
//    hash = 37 * hash + tfn.hashCode();
//    return hash;
//  }
  
  
}

/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.common.types.EndPoint;
import it.grid.storm.common.types.InvalidEndPointAttributeException;
import it.grid.storm.common.types.InvalidSFNAttributesException;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.Machine;
import it.grid.storm.common.types.Port;
import it.grid.storm.common.types.SFN;
import it.grid.storm.common.types.SiteProtocol;
import it.grid.storm.common.types.StFN;
import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.naming.SURL;

/**
 * This class represents a TSURL, that is a Site URL. It is made up of a SiteProtocol and a SFN.
 * 
 * @author Ezio Corso - Magnoni Luca
 * @author EGRID ICTP Trieste / CNAF INFN Bologna
 * @date Avril, 2005
 * @version 2.0
 */
public class TSURL {

  private static Logger log = LoggerFactory.getLogger(TSURL.class);

  private static final String EMPTY_STRING = "";

  /**
   * The SURL as provided by User
   */
  private final String rawSurl;
  private final SiteProtocol sp;
  private final SFN sfn;
  private String normalizedStFN = null;
  private int uniqueID = 0;

  private boolean empty = true;

  public static final String PNAME_SURL = "surl";
  public static final String PNAME_FROMSURL = "fromSURL";
  public static final String PNAME_TOSURL = "toSURL";

  private static ArrayList<TSURL> tsurlManaged = new ArrayList<TSURL>();
  private static LinkedList<Port> defaultPorts = new LinkedList<Port>();

  static {

    // Lazy initialization from Configuration
    if (tsurlManaged.isEmpty()) {

      TSURL checkTSURL;
      String[] surlValid = Configuration.getInstance().getManagedSURLs();
      for (String checkSurl : surlValid) {
        try {

          checkTSURL = TSURL.makeFromStringWellFormed(checkSurl);
          tsurlManaged.add(checkTSURL);
          log.debug("### SURL Managed : {}", checkTSURL);

        } catch (InvalidTSURLAttributesException e) {

          log.error("Unable to build a TSURL : {}", checkSurl, e);
        }
      }
    }

    if (defaultPorts.isEmpty()) {

      Integer[] ports = Configuration.getInstance().getManagedSurlDefaultPorts();

      for (Integer portInteger : ports) {
        defaultPorts.add(Port.make(portInteger.intValue()));
        log.debug("### Default SURL port : {}", defaultPorts.getLast());
      }
    }
  }

  private TSURL(SiteProtocol sp, SFN sfn, String rawSurl, boolean empty) {

    this.sp = sp;
    this.sfn = sfn;
    this.rawSurl = rawSurl;
    this.empty = empty;

  }

  /**
   * Method that create a TSURL from structure received from FE.
   * 
   * @throws InvalidTSURLAttributesException
   */
  public static TSURL decode(Map<String, Object> inputParam, String name)
      throws InvalidTSURLAttributesException {

    String surlstring = (String) inputParam.get(name);
    return TSURL.makeFromStringWellFormed(surlstring);
  }

  /**
   * Build a TSURL by extracting the content of the received SURL object and storing the received
   * raw surl string
   * 
   * @param surl
   * @param rawSurl
   * @return
   * @throws InvalidTSURLAttributesException
   */
  public static TSURL getWellFormed(SURL surl, String rawSurl)
      throws InvalidTSURLAttributesException {

    TSURL result;
    SFN sfn;
    Machine machine = null;
    String stfn = null;
    EndPoint serviceEndpoint = null;
    Port port = null;

    try {

      machine = Machine.make(surl.getServiceHostname());

      log.debug("Machine: {}", machine);

      stfn = surl.getStFN();
      StFN stfnClass = StFN.make(stfn);

      log.debug("StFN Class: {}", stfnClass);

      if (surl.isQueriedFormSURL()) {
        String serviceEndPointString = surl.getServiceEndPoint();

        serviceEndpoint = EndPoint.make(serviceEndPointString);
        log.debug("EndPoint: {}", serviceEndpoint);

      }

      if (surl.getServiceHostPort() > -1) {

        port = Port.make(surl.getServiceHostPort());
        log.debug("Port: {}", port);

      }

      if (port != null) {

        if (serviceEndpoint == null) {
          sfn = SFN.makeInSimpleForm(machine, port, stfnClass);
        } else {
          sfn = SFN.makeInQueryForm(machine, port, serviceEndpoint, stfnClass);
        }

      } else {

        if (serviceEndpoint == null) {
          sfn = SFN.makeInSimpleForm(machine, stfnClass);
        } else {
          sfn = SFN.makeInQueryForm(machine, serviceEndpoint, stfnClass);
        }
      }

      log.debug("SFN: {}", sfn);

      result = TSURL.make(SiteProtocol.SRM, sfn, rawSurl);
      return result;

    } catch (InvalidStFNAttributeException e) {

      log.error("Invalid StFN: {}", stfn, e);
      throw new InvalidTSURLAttributesException(null, null);

    } catch (InvalidEndPointAttributeException e) {

      log.error("Invalid endpoint: {}", serviceEndpoint, e);
      throw new InvalidTSURLAttributesException(null, null);

    } catch (InvalidSFNAttributesException e) {

      log.error("Error building SFN: {}", e.getMessage(), e);
      throw new InvalidTSURLAttributesException(null, null);
    }

  }

  /**
   * Static factory method that returns a TSURL and that requires the SiteProtocol and the SFN of
   * this TSURL: if any is null or empty an InvalidTSURLAttributesException is thrown. Check for
   * ".." in Storage File Name for security issues.
   */
  private static TSURL make(SiteProtocol sp, SFN sfn, String userSurl)
      throws InvalidTSURLAttributesException {

    if ((sp == null) || (sfn == null) || (sp == SiteProtocol.EMPTY) || sfn.isEmpty()) {
      throw new InvalidTSURLAttributesException(sp, sfn);
    }
    return new TSURL(sp, sfn, userSurl, false);
  }

  /**
   * Static factory method that returns an empty TSURL.
   */
  public static TSURL makeEmpty() {

    return new TSURL(SiteProtocol.EMPTY, SFN.makeEmpty(), "", true);
  }

  /**
   * Static factory method that returns a TSURL from a String representation: if it is null or
   * malformed then an Invalid TSURLAttributesException is thrown.
   */
  public static TSURL makeFromStringWellFormed(String surlString)
      throws InvalidTSURLAttributesException {

    TSURL result = null;
    if (surlString == null) {
      throw new InvalidTSURLAttributesException(null, null);
    }
    // first occurrences of ://
    int separator = surlString.indexOf("://");
    if ((separator == -1) || (separator == 0)) {
      // separator not found or right at the beginning!
      throw new InvalidTSURLAttributesException(null, null);
    }
    String spString = surlString.substring(0, separator);
    SiteProtocol sp = null;
    try {
      sp = SiteProtocol.fromString(spString);
    } catch (IllegalArgumentException e) {
      // do nothing - sp remains null and that is fine!
      log.warn("TSURL: Site protocol by {} is empty, but that's fine.", spString);
    }
    if ((separator + 3) > (surlString.length())) {
      // separator found at the end!
      throw new InvalidTSURLAttributesException(sp, null);
    }

    log.debug("SURL string: {}", surlString);
    SURL surl;

    try {
      surl = SURL.makeSURLfromString(surlString);
    } catch (NamespaceException ex) {
      log.error("Invalid surl: {}", surlString, ex);
      throw new InvalidTSURLAttributesException(null, null);
    }

    result = getWellFormed(surl, surlString);

    return result;
  }

  /**
   * Static factory method that returns a TSURL from a String representation: if it is null or
   * malformed then an Invalid TSURLAttributesException is thrown.
   */
  public static TSURL makeFromStringValidate(String surlString)
      throws InvalidTSURLAttributesException {

    TSURL tsurl = makeFromStringWellFormed(surlString);

    if (!isValid(tsurl)) {

      log.warn("SURL {} is not managed by this StoRM instance.", tsurl);
      throw new InvalidTSURLAttributesException(tsurl.sp, tsurl.sfn());

    }
    return tsurl;
  }

  /**
   * Auxiliary method that returns true if the supplied TSURL corresponds to some managed SURL as
   * declared in Configuration.
   * 
   */
  public static boolean isValid(TSURL surl) {
    return isManaged(surl, TSURL.tsurlManaged);
  }

  public static boolean isManaged(TSURL surl, List<TSURL> managedSurls) {

    boolean result = false;
    for (TSURL tsurlReference : managedSurls) {
      if (isSURLManaged(surl, tsurlReference)) {
        result = true;
        break;
      }
    }
    return result;
  }

  /**
   * A SURL is managed by a managed SURL if their hosts are the same and if the comingSURL specifies
   * a port this port is the same as the one specified on the managed SURL or, if the managed SURL
   * doesn't specifies a port this port is listed in the default ports
   * 
   * @param comingSURL
   * @param managedSURL
   * @return
   */
  private static boolean isSURLManaged(TSURL comingSURL, TSURL managedSURL) {

    String serviceHost = comingSURL.sfn().machine().toString();
    String expectedServiceHost = managedSURL.sfn().machine().toString();

    log.debug("SURL VALID [ coming-service-host = {}, expected = {} ]", serviceHost,
        expectedServiceHost);

    if (!serviceHost.equalsIgnoreCase(expectedServiceHost)) {
      return false;
    }
    if (comingSURL.sfn().port().isEmpty()) {
      return true;
    }
    int expectedServicePort = managedSURL.sfn().port().getValue();
    int port = comingSURL.sfn().port().getValue();

    log.debug("SURL VALID [ coming-service-port = {}, expected = {} ]", port, expectedServicePort);
    return expectedServicePort == port;
  }

  public void encode(Map<String, Object> param, String name) {

    param.put(name, toString());
  }

  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + (empty ? 1231 : 1237);
    result = prime * result + ((normalizedStFN() == null) ? 0 : normalizedStFN().hashCode());
    result = prime * result + ((rawSurl == null) ? 0 : rawSurl.hashCode());
    result = prime * result + ((sfn() == null) ? 0 : sfn().hashCode());
    result = prime * result + ((protocol() == null) ? 0 : protocol().hashCode());
    result = prime * result + uniqueId();
    return result;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TSURL other = (TSURL) obj;
    if (empty != other.empty) {
      return false;
    }
    if (normalizedStFN() == null) {
      if (other.normalizedStFN() != null) {
        return false;
      }
    } else if (!normalizedStFN().equals(other.normalizedStFN())) {
      return false;
    }
    if (rawSurl == null) {
      if (other.rawSurl != null) {
        return false;
      }
    } else if (!rawSurl.equals(other.rawSurl)) {
      return false;
    }
    if (sfn() == null) {
      if (other.sfn() != null) {
        return false;
      }
    } else if (!sfn().equals(other.sfn())) {
      return false;
    }
    if (protocol() == null) {
      if (other.protocol() != null) {
        return false;
      }
    } else if (!protocol().equals(other.protocol())) {
      return false;
    }
    if (uniqueId() != other.uniqueId()) {
      return false;
    }
    return true;
  }

  /**
   * Returns a string representation of the SURL.
   * 
   * @return String
   */
  public String getSURLString() {

    if (empty) {
      return EMPTY_STRING;
    }

    StringBuilder builder = new StringBuilder();
    builder.append(sp);
    builder.append("://");
    builder.append(sfn);

    return builder.toString();
  }

  public boolean isEmpty() {

    return empty;
  }

  /**
   * Method that returns the SiteProtocol of this TSURL. If this is empty, then an empty
   * SiteProtocol is returned.
   */
  public SiteProtocol protocol() {

    if (empty) {
      return SiteProtocol.EMPTY;
    }
    return sp;
  }

  /**
   * @return the rawSurl
   */
  public String rawSurl() {

    return rawSurl;
  }

  /**
   * Method that returns the SFN of this SURL. If this is empty, then an empty SFN is returned.
   */
  public SFN sfn() {

    if (empty) {
      return SFN.makeEmpty();
    }
    return sfn;
  }

  /**
   * @return
   */
  public String normalizedStFN() {

    if (this.normalizedStFN == null) {
      this.normalizedStFN = this.sfn.stfn().toString();
    }
    return this.normalizedStFN;
  }

  /**
   * @param normalizedStFN the normalizedStFN to set
   */
  public void setNormalizedStFN(String normalizedStFN) {

    this.normalizedStFN = normalizedStFN;
  }

  /**
   * @param uniqueID the uniqueID to set
   */
  public void setUniqueID(int uniqueID) {

    this.uniqueID = uniqueID;
  }

  /**
   * @return
   */
  public int uniqueId() {

    if (this.uniqueID == 0) {
      this.uniqueID = this.sfn.stfn().hashCode();
    }
    return this.uniqueID;
  }

  @Override
  public String toString() {

    if (empty) {
      return "Empty TSURL";
    }

    StringBuilder builder = new StringBuilder();
    builder.append(sp);
    builder.append("://");
    builder.append(sfn);

    return builder.toString();
  }
}

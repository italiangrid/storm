/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the possible site protocols of StoRM.
 *
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 26th, 2005
 * @version 1.0
 */
public class SiteProtocol {

  private String protocol = null;
  private static Map<String, SiteProtocol> m = new HashMap<String, SiteProtocol>();

  public static final SiteProtocol SRM =
      new SiteProtocol("srm") {

        public int hashCode() {

          return 1;
        }
      };

  public static final SiteProtocol EMPTY =
      new SiteProtocol("empty") {

        public int hashCode() {

          return 0;
        }
      };

  private SiteProtocol(String protocol) {

    this.protocol = protocol;
    m.put(protocol, this);
  }

  /**
   * Facility method to obtain a SiteProtocol object from its String representation. An
   * IllegalArgumentExceptin is thrown if the supplied String does not have a SiteProtocol
   * counterpart. The supplied String may contain white spaces and be in a mixture of upper and
   * lower case characters.
   */
  public static SiteProtocol fromString(String value) throws IllegalArgumentException {

    value = value.toLowerCase().replaceAll(" ", "");
    SiteProtocol aux = (SiteProtocol) m.get(value);
    if (aux == null) throw new IllegalArgumentException();
    return aux;
  }

  public String toString() {

    return protocol;
  }

  // Maybe should be removed!
  public String getValue() {

    return protocol;
  }
}

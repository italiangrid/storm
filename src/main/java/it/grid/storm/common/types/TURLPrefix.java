/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.model.Protocol;

/**
 * This class represent the Transport Protocol available to get file from a certain Storage Element.
 * This Transport Protocol prefix will be used to match with user specified prefix to TTURL
 * Creation.
 */
public class TURLPrefix {

  public static final String PNAME_TURL_PREFIX = "turlPrefix";
  private List<Protocol> desiredProtocols;

  public TURLPrefix() {

    desiredProtocols = Lists.newArrayList();
  }

  public TURLPrefix(Collection<Protocol> protocols) {

    desiredProtocols = Lists.newArrayList(protocols);
  }

  /**
   * Method used to add a TransferProtocol to this holding structure. Null may also be added. A
   * boolean true is returned if the holding structure changed as a result of the add. If this
   * holding structure does not change, then false is returned.
   */
  public boolean addProtocol(Protocol protocol) {

    return desiredProtocols.add(protocol);
  }

  /**
   * Method used to retrieve a TransferProtocol from this holding structure. An int is needed as
   * index to the TransferProtocol to retrieve. Elements are not removed!
   */
  public Protocol getProtocol(int index) {

    return desiredProtocols.get(index);
  }

  public List<Protocol> getDesiredProtocols() {

    return this.desiredProtocols;
  }

  public int size() {

    return desiredProtocols.size();
  }

  public void print() {

  }

  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("TURLPrefix: ");
    for (Iterator<Protocol> i = desiredProtocols.iterator(); i.hasNext();) {
      sb.append(i.next());
      sb.append(" ");
    }
    return sb.toString();
  }

  /**
   * @param inputParam
   * @param memberName
   * @return
   */
  public static TURLPrefix decode(Map<String, Object> inputParam, String memberName) {

    TURLPrefix decodedTurlPrefix = null;
    if (inputParam.containsKey(memberName)) {
      if (inputParam.get(memberName) != null) {
        Object[] valueArray = null;
        if (inputParam.get(memberName).getClass().isArray()) {
          valueArray = (Object[]) inputParam.get(memberName);
        } else {
          valueArray = new Object[] {inputParam.get(memberName)};
        }
        List<Protocol> protocols = Lists.newLinkedList();
        for (Object value : valueArray) {
          protocols.add(Protocol.valueOf(value.toString()));
        }
        if (protocols.size() > 0) {
          decodedTurlPrefix = new TURLPrefix(protocols);
        }
      }
    }
    return decodedTurlPrefix;
  }

  public boolean allows(Protocol protocol) {

    return desiredProtocols.contains(protocol);
  }
}

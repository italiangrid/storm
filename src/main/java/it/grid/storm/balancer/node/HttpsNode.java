/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer.node;

import it.grid.storm.namespace.model.Protocol;

public class HttpsNode extends AbstractNode {

  public HttpsNode(int id, String hostname, int port) {
    super(Protocol.HTTPS, id, hostname, port);
  }

  public HttpsNode(int id, String hostname, int port, int weight) {
    super(Protocol.HTTPS, id, hostname, port, weight);
  }
}

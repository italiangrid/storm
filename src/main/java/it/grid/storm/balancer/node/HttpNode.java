/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer.node;

import it.grid.storm.namespace.model.Protocol;

public class HttpNode extends AbstractNode {

  public HttpNode(int id, String hostname, int port) {
    super(Protocol.HTTP, id, hostname, port);
  }

  public HttpNode(int id, String hostname, int port, int weight) {
    super(Protocol.HTTP, id, hostname, port, weight);
  }
}

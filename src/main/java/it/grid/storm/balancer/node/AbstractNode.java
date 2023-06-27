/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.balancer.node;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.balancer.Node;
import it.grid.storm.namespace.model.Protocol;

public abstract class AbstractNode implements Node {

  private static final Logger log = LoggerFactory.getLogger(AbstractNode.class);

  private final Protocol protocol;
  private final int id;
  private final String hostname;
  private final int port;
  private final Optional<Integer> weight;

  private final TelnetClient telnet;

  public AbstractNode(Protocol protocol, int id, String hostname, int port) {

    this(protocol, id, hostname, port, null);
  }

  public AbstractNode(Protocol protocol, int id, String hostname, int port, Integer weight) {

    this.protocol = protocol;
    this.id = id;
    this.hostname = hostname;
    this.port = port;
    // If defined,weight between 1 and 100
    this.weight = weight != null ? Optional.of(weight % 100) : Optional.empty();
    this.telnet = new TelnetClient();
  }

  public int getId() {
    return id;
  }

  public String getHostname() {
    return hostname;
  }

  public Protocol getProtocol() {
    return protocol;
  }

  public int getPort() {
    return port;
  }

  public int getWeight() throws IllegalStateException {

    if (weight.isPresent()) {
      return weight.get().intValue();
    }
    throw new IllegalStateException("No weight available for this node: " + toString());
  }

  @Override
  public boolean checkServer() {

    boolean response = false;

    try {
      telnet.connect(getHostname(), getPort());
    } catch (IOException e) {
      log.warn("Unable to connect to {}:{}", getHostname(), getPort());
      return false;
    }

    response = telnet.isConnected();

    try {
      telnet.disconnect();
    } catch (IOException e) {
      log.warn("Unable to disconnect from {}:{}", getHostname(), getPort());
      return false;
    }

    return response;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append(getProtocol().getProtocolPrefix());
    sb.append(getHostname());
    sb.append(":" + getPort());
    return sb.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(hostname, id, port, protocol, weight);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractNode other = (AbstractNode) obj;
    return Objects.equals(hostname, other.hostname) && id == other.id && port == other.port
        && Objects.equals(protocol, other.protocol) && Objects.equals(weight, other.weight);
  }


}

/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer;

public interface Node {

  int getId();

  int getWeight();

  String getHostname();

  int getPort();

  boolean checkServer();

}

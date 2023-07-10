/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.metrics;

import static com.codahale.metrics.MetricRegistry.name;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

public class NamedInstrumentedSelectChannelConnector extends SelectChannelConnector {

  private final Timer duration;
  private final Meter accepts, connects, disconnects;
  private final Counter connections;
  private final Clock clock;

  public NamedInstrumentedSelectChannelConnector(String name, int port, MetricRegistry registry) {
    super();
    clock = Clock.defaultClock();
    setPort(port);
    setName(name);
    this.duration = registry.timer(name(name, Integer.toString(port), "connection-duration"));
    this.accepts = registry.meter(name(name, Integer.toString(port), "accepts"));
    this.connects = registry.meter(name(name, Integer.toString(port), "connects"));
    this.disconnects = registry.meter(name(name, Integer.toString(port), "disconnects"));
    this.connections = registry.counter(name(name, Integer.toString(port), "active-connections"));
  }

  @Override
  public void accept(int acceptorID) throws IOException {
    super.accept(acceptorID);
    accepts.mark();
  }

  @Override
  protected void connectionOpened(Connection connection) {
    connections.inc();
    super.connectionOpened(connection);
    connects.mark();
  }

  @Override
  protected void connectionClosed(Connection connection) {
    super.connectionClosed(connection);
    disconnects.mark();
    final long duration = clock.getTime() - connection.getTimeStamp();
    this.duration.update(duration, TimeUnit.MILLISECONDS);
    connections.dec();
  }
}

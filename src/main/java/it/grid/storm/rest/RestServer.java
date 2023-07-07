/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.rest;

import static it.grid.storm.metrics.StormMetricRegistry.METRIC_REGISTRY;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.jetty8.InstrumentedHandler;
import com.codahale.metrics.servlets.MetricsServlet;
import com.google.common.base.Preconditions;

import it.grid.storm.authz.remote.resource.AuthorizationResource;
import it.grid.storm.authz.remote.resource.AuthorizationResourceCompat_1_0;
import it.grid.storm.ea.remote.resource.StormEAResource;
import it.grid.storm.info.remote.resources.Ping;
import it.grid.storm.info.remote.resources.SpaceStatusResource;
import it.grid.storm.metrics.NamedInstrumentedSelectChannelConnector;
import it.grid.storm.metrics.NamedInstrumentedThreadPool;
import it.grid.storm.namespace.remote.resource.VirtualFSResource;
import it.grid.storm.rest.auth.RestTokenFilter;
import it.grid.storm.rest.metadata.Metadata;
import it.grid.storm.tape.recalltable.providers.TapeRecallTOListMessageBodyWriter;
import it.grid.storm.tape.recalltable.resources.TaskResource;
import it.grid.storm.tape.recalltable.resources.TasksCardinality;
import it.grid.storm.tape.recalltable.resources.TasksResource;

/**
 * This class provides static methods for starting and stopping the storm-backend restful services.
 * 
 * @author zappi
 * @author valerioventuri
 */
public class RestServer {

  public static final int DEFAULT_MAX_THREAD_NUM = 100;
  public static final int DEFAULT_MAX_QUEUE_SIZE = 1000;

  private static final Logger LOG = LoggerFactory.getLogger(RestServer.class);

  private final Server server;

  private int restServicePort;
  private boolean isTokenEnabled;
  private String token;
  private int maxThreads;
  private int maxQueueSize;

  boolean running = false;

  public RestServer(int restServicePort, int maxThreads, int maxQueueSize, boolean isTokenEnabled,
      String token) {

    this.restServicePort = restServicePort;
    this.maxThreads = maxThreads;
    this.maxQueueSize = maxQueueSize;
    this.isTokenEnabled = isTokenEnabled;
    this.token = token;
    this.server = new Server();

    if (isTokenEnabled) {
      Preconditions.checkNotNull(token, "Rest server security token enabled, but token not found");
    }

    configure();
  }

  /**
   * Configure the {@link Server}. Install the Jersey {@link ServletContainer} and configure it to
   * with resources locations.
   * 
   * @throws RestServiceException
   * 
   * @throws Exception
   * 
   */
  private void configure() {

    ResourceConfig resourceConfig = new ResourceConfig();
    /* Register resources: */
    resourceConfig.register(TaskResource.class);
    resourceConfig.register(TasksResource.class);
    resourceConfig.register(TasksCardinality.class);
    resourceConfig.register(TapeRecallTOListMessageBodyWriter.class);
    resourceConfig.register(AuthorizationResource.class);
    resourceConfig.register(AuthorizationResourceCompat_1_0.class);
    resourceConfig.register(VirtualFSResource.class);
    resourceConfig.register(StormEAResource.class);
    resourceConfig.register(Metadata.class);
    resourceConfig.register(Ping.class);
    resourceConfig.register(SpaceStatusResource.class);
    /* JSON POJO support: */
    resourceConfig.register(JacksonFeature.class);

    ServletHolder holder = new ServletHolder(new ServletContainer(resourceConfig));

    ServletContextHandler servletContextHandler =
        new ServletContextHandler(ServletContextHandler.SESSIONS);

    servletContextHandler.setContextPath("/");


    ServletHolder metrics = new ServletHolder(new MetricsServlet(METRIC_REGISTRY.getRegistry()));

    servletContextHandler.addServlet(metrics, "/metrics");
    servletContextHandler.addServlet(holder, "/*");
    if (isTokenEnabled) {

      LOG.info("Enabling security filter for rest server requests");
      FilterHolder filterHolder = new FilterHolder(new RestTokenFilter());
      filterHolder.setInitParameter("token", token);
      servletContextHandler.addFilter(filterHolder, "/metadata/*",
          EnumSet.of(DispatcherType.REQUEST));
      servletContextHandler.addFilter(filterHolder, "/recalltable/*",
          EnumSet.of(DispatcherType.REQUEST));
    }

    NamedInstrumentedSelectChannelConnector connector = new NamedInstrumentedSelectChannelConnector(
        "rest-connector", restServicePort, METRIC_REGISTRY.getRegistry());

    server.addConnector(connector);

    // Configure thread pool
    NamedInstrumentedThreadPool tp =
        new NamedInstrumentedThreadPool("rest", METRIC_REGISTRY.getRegistry());


    tp.setMaxThreads(maxThreads);
    tp.setMaxQueued(maxQueueSize);
    server.setThreadPool(tp);

    LOG.info("RESTful services threadpool configured: maxThreads={}, maxQueueSize={}", maxThreads,
        maxQueueSize);

    InstrumentedHandler ih = new InstrumentedHandler(METRIC_REGISTRY.getRegistry(),
        servletContextHandler, "rest-handler");

    server.setHandler(ih);
  }

  /**
   * Starts the server.
   * 
   * @throws Exception
   */
  public synchronized void start() throws Exception {

    if (!running) {

      LOG.info("Starting StoRM RESTful services");

      JettyThread thread = new JettyThread(server);
      thread.start();

      running = true;

      LOG.info("StoRM RESTful services started.");
    }

  }

  /**
   * Stops the server.
   * 
   * @throws Exception
   */
  public synchronized void stop() throws Exception {

    LOG.info("Stopping StoRM RESTful services");

    if (server != null) {

      try {

        server.stop();

      } catch (Exception e) {

        LOG.info("Could not stop StoRM RESTful services");

        return;
      }

    }

    running = false;

    LOG.info("StoRM RESTful services is not running");

  }

  /**
   * Returns if server is running.
   * 
   */
  public boolean isRunning() {

    return running;
  }
}

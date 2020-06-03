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

import it.grid.storm.authz.remote.resource.AuthorizationResource;
import it.grid.storm.authz.remote.resource.AuthorizationResourceCompat_1_0;
import it.grid.storm.config.Configuration;
import it.grid.storm.ea.remote.resource.StormEAResource;
import it.grid.storm.info.remote.resources.Ping;
import it.grid.storm.info.remote.resources.SpaceStatusResource;
import it.grid.storm.metrics.NamedInstrumentedSelectChannelConnector;
import it.grid.storm.metrics.NamedInstrumentedThreadPool;
import it.grid.storm.namespace.remote.resource.VirtualFSResource;
import it.grid.storm.namespace.remote.resource.VirtualFSResourceCompat_1_0;
import it.grid.storm.namespace.remote.resource.VirtualFSResourceCompat_1_1;
import it.grid.storm.namespace.remote.resource.VirtualFSResourceCompat_1_2;
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
public class RestService {

  private static final Logger LOG = LoggerFactory.getLogger(RestService.class);

  public static final int DEFAULT_PORT = 9998;

  public static final int DEFAULT_MAX_THREADS = 100;

  public static final int DEFAULT_MAX_QUEUE_SIZE = 1000;

  /**
   * The Jetty {@link Server} object.
   */
  private static Server server;

  /**
   * Get the port on which the server will bind from the service configuration.
   * 
   * @return the port on which the server will bind
   */
  private static int getPort() {

    int restServicePort = Configuration.getInstance().getRestServicesPort();

    LOG.debug("RESTFul services will be listening on port {}", restServicePort);

    return restServicePort;
  }


  /**
   * Configure the {@link Server}. Install the Jersey {@link ServletContainer} and configure it to
   * with resources locations.
   * 
   * @throws Exception
   * 
   */
  private static void configureServer() throws Exception {

    ResourceConfig resourceConfig = new ResourceConfig();
    /* Register resources: */
    resourceConfig.register(TaskResource.class);
    resourceConfig.register(TasksResource.class);
    resourceConfig.register(TasksCardinality.class);
    resourceConfig.register(TapeRecallTOListMessageBodyWriter.class);
    resourceConfig.register(AuthorizationResource.class);
    resourceConfig.register(AuthorizationResourceCompat_1_0.class);
    resourceConfig.register(VirtualFSResource.class);
    resourceConfig.register(VirtualFSResourceCompat_1_0.class);
    resourceConfig.register(VirtualFSResourceCompat_1_1.class);
    resourceConfig.register(VirtualFSResourceCompat_1_2.class);
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
    if (Configuration.getInstance().getXmlRpcTokenEnabled()) {

      LOG.info("Enabling security filter for rest server requests");
      String token = Configuration.getInstance().getXmlRpcToken();
      if (token == null || token.isEmpty()) {
        LOG.error("Rest server security token enabled, but token not found");
        throw new Exception("Rest server security token enabled, but token not found");
      }
      FilterHolder filterHolder = new FilterHolder(new RestTokenFilter());
      filterHolder.setInitParameter("token", token);
      servletContextHandler.addFilter(filterHolder, "/metadata/*",
          EnumSet.of(DispatcherType.REQUEST));
      servletContextHandler.addFilter(filterHolder, "/recalltable/*",
          EnumSet.of(DispatcherType.REQUEST));
    }

    server = new Server();

    NamedInstrumentedSelectChannelConnector connector = new NamedInstrumentedSelectChannelConnector(
        "rest-connector", getPort(), METRIC_REGISTRY.getRegistry());

    server.addConnector(connector);

    // Configure thread pool
    NamedInstrumentedThreadPool tp =
        new NamedInstrumentedThreadPool("rest", METRIC_REGISTRY.getRegistry());


    tp.setMaxThreads(Configuration.getInstance().getRestServicesMaxThreads());
    tp.setMaxQueued(Configuration.getInstance().getRestServicesMaxQueueSize());
    server.setThreadPool(tp);

    LOG.info("RESTful services threadpool configured: maxThreads={}, maxQueueSize={}",
        Configuration.getInstance().getRestServicesMaxThreads(),
        Configuration.getInstance().getRestServicesMaxQueueSize());

    InstrumentedHandler ih = new InstrumentedHandler(METRIC_REGISTRY.getRegistry(),
        servletContextHandler, "rest-handler");

    server.setHandler(ih);
  }

  /**
   * Starts the server.
   * 
   * @throws Exception
   */
  public static void startServer() throws Exception {

    configureServer();
    JettyThread thread = new JettyThread(server);
    thread.start();
    LOG.info("StoRM RESTful services started.");

  }

  /**
   * Stops the server.
   * 
   * @throws Exception
   */
  public static void stop() throws Exception {
    server.stop();
    LOG.info("StoRM RESTful services stopped.");
  }
}

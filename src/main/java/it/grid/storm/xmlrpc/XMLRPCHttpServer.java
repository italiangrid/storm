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
 * This class represents the Synchronous Call xmlrpc Server . This class hava a
 * set of Handler that manage the FE call invoking the right BackEnd manager.
 * 
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.xmlrpc;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.config.Configuration;
import it.grid.storm.rest.JettyThread;

public final class XMLRPCHttpServer {

  /**
   * Logger
   */
  private static final Logger LOG = LoggerFactory.getLogger(XMLRPCHttpServer.class);

  /**
   * The Jetty server hosting the Apache XML-RPC machinery
   */
  private final Server server;

  /**
   * True if a web server has been started
   */
  private boolean running = false;

  public static final int DEFAULT_MAX_THREAD_NUM = 256;
  public static final int DEFAULT_MAX_QUEUE_SIZE = 1000;

  /**
   * @param port
   * @param maxThreadNum
   * @throws XmlRpcException
   */
  public XMLRPCHttpServer(int port, int maxThreadNum, int maxQueueSize)
      throws StoRMXmlRpcException {

    server = buildWebServer(port, maxThreadNum, maxQueueSize);
  };


  private void configureThreadPool(Server s, int maxThreadNum, int maxQueueSize) {
    int threadNumber = maxThreadNum;

    if (threadNumber <= 0) {
      threadNumber = DEFAULT_MAX_THREAD_NUM;
    }

    int queueSize = maxQueueSize;

    if (queueSize <= 0) {
      queueSize = DEFAULT_MAX_QUEUE_SIZE;
    }

    QueuedThreadPool queuedThreadPool = new QueuedThreadPool();
    queuedThreadPool.setName("xmlrpc");
    queuedThreadPool.setMaxThreads(threadNumber);
    queuedThreadPool.setMaxQueued(queueSize);

    s.setThreadPool(queuedThreadPool);

    LOG.info("Configured XMLRPC server threadpool: maxThreads={}, maxQueueSize={}", threadNumber,
        queueSize);
  }


  private Server buildWebServer(int port, int maxThreadNum, int maxQueueSize)
      throws StoRMXmlRpcException {

    Server server = new Server(port);
    configureThreadPool(server, maxThreadNum, maxQueueSize);

    XmlRpcServlet servlet = new XmlRpcServlet();

    ServletContextHandler servletContextHandler = new ServletContextHandler();
    servletContextHandler.addServlet(new ServletHolder(servlet), "/");

    Boolean isTokenEnabled = Configuration.getInstance().getXmlRpcTokenEnabled();

    if (isTokenEnabled) {

      LOG.info("Enabling security filter for XML-RPC requests");

      String token = Configuration.getInstance().getXmlRpcToken();

      if (token == null || token.isEmpty()) {

        LOG.error("XML-RPC requests token enabled, but token not found");

        throw new StoRMXmlRpcException("XML-RPC requests token enabled, but token not found");
      }

      Filter filter = new XmlRpcTokenFilter(token);

      FilterHolder filterHolder = new FilterHolder(filter);

      servletContextHandler.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
    }

    server.setHandler(servletContextHandler);

    return server;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.grid.storm.xmlrpc.XMLRPCServerInterface#createServer()
   */
  public synchronized void start() {

    if (!running) {

      LOG.info("Starting Jetty server hosting the XML-RPC machinery");

      JettyThread thread = new JettyThread(server);
      thread.start();

      running = true;

      LOG.info("Jetty server hosting the XML-RPM machinery is running");
    }

  }

  /**
   * @throws Exception
   * 
   */
  public synchronized void stop() {

    LOG.info("Stopping Jetty server hosting the XML-RPC machinery");

    if (server != null) {

      try {

        server.stop();

      } catch (Exception e) {

        LOG.info("Could not stop the Jetty server hosting the XML-RPC machinery");

        return;
      }

    }

    running = false;

    LOG.info("Jetty server hosting the XML-RPM machinery is not running");

  }

}

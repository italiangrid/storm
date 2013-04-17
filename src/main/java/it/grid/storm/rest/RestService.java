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

import it.grid.storm.config.Configuration;
import it.grid.storm.info.InfoService;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * This class provides static methods for starting and stopping the
 * storm-backend restful services.
 * 
 * @author zappi
 * @author valerioventuri
 */
public class RestService {

	private static final Logger log = LoggerFactory.getLogger(RestService.class);

	/**
	 * Object holding the service configuration.
	 */
	private static Configuration config = Configuration.getInstance();

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

		int restServicePort = config.getRestServicesPort();

		log.debug("RESTFul services will be listening on port " + restServicePort);

		return restServicePort;
	}

	/**
	 * Configure the {@link Server} object to bind on localhost and on the port
	 * taken from the service configuration.
	 */
	private static void configureServerConnector() {

		Connector connector = new SelectChannelConnector();

		connector.setPort(RestService.getPort());

		server.addConnector(connector);
	}

	/**
	 * Configure the {@link Server}. Install the Jersey {@link ServletContainer}
	 * and configure it to with resources locations.
	 * 
	 */
	private static void configureServer() {

		ServletContainer servlet = new ServletContainer();

		ServletHolder holder = new ServletHolder(servlet);
		holder.setInitParameter(
			"com.sun.jersey.config.property.packages",
			"it.grid.storm.tape.recalltable.resources,"
				+ "it.grid.storm.authz.remote.resource,"
				+ "it.grid.storm.namespace.remote.resource,"
				+ "it.grid.storm.ea.remote.resource,"
				+ InfoService.getResourcePackage());

		ServletContextHandler servletContextHandler = new ServletContextHandler(
			ServletContextHandler.SESSIONS);
		servletContextHandler.setContextPath("/");
		servletContextHandler.addServlet(holder, "/*");

		server.setHandler(servletContextHandler);
	}

	/**
	 * Starts the server.
	 * 
	 * @throws Exception
	 */
	public static void startServer() throws Exception {

		server = new Server();

		configureServerConnector();
		configureServer();

		log.info("Starting RESTFul services ... ");

		JettyThread thread = new JettyThread(server);
		thread.start();

		log.info(" ... started");
	}

	/**
	 * Stops the server.
	 * 
	 * @throws Exception
	 */
	public static void stop() throws Exception {

		log.info("Starting RESTFul services ... ");

		server.stop();

		log.info("... stopped");

	}
}

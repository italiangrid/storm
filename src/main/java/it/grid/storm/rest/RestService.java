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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.authz.remote.resource.AuthorizationResource;
import it.grid.storm.authz.remote.resource.AuthorizationResourceCompat_1_0;
import it.grid.storm.config.Configuration;
import it.grid.storm.ea.remote.resource.StormEAResource;
import it.grid.storm.info.remote.resources.Metadata;
import it.grid.storm.info.remote.resources.Ping;
import it.grid.storm.info.remote.resources.SpaceStatusResource;
import it.grid.storm.namespace.remote.resource.VirtualFSResource;
import it.grid.storm.namespace.remote.resource.VirtualFSResourceCompat_1_0;
import it.grid.storm.namespace.remote.resource.VirtualFSResourceCompat_1_1;
import it.grid.storm.namespace.remote.resource.VirtualFSResourceCompat_1_2;
import it.grid.storm.tape.recalltable.providers.TapeRecallTOListMessageBodyWriter;
import it.grid.storm.tape.recalltable.providers.TapeRecallTOMessageBodyReader;
import it.grid.storm.tape.recalltable.resources.TaskResource;
import it.grid.storm.tape.recalltable.resources.TasksCardinality;
import it.grid.storm.tape.recalltable.resources.TasksResource;

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

		log.debug("RESTFul services will be listening on port {}", 
		  restServicePort);

		return restServicePort;
	}

	/**
	 * Configure the {@link Server}. Install the Jersey {@link ServletContainer}
	 * and configure it to with resources locations.
	 * 
	 */
	private static void configureServer() {

		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.registerClasses(TaskResource.class, TasksResource.class, TasksCardinality.class,
				TapeRecallTOListMessageBodyWriter.class, TapeRecallTOMessageBodyReader.class,
				AuthorizationResource.class, AuthorizationResourceCompat_1_0.class, VirtualFSResource.class,
				VirtualFSResourceCompat_1_0.class, VirtualFSResourceCompat_1_1.class,
				VirtualFSResourceCompat_1_2.class, StormEAResource.class, Metadata.class, Ping.class,
				SpaceStatusResource.class, JacksonFeature.class);
		ServletHolder holder = new ServletHolder(new ServletContainer(resourceConfig));

		server = new Server(RestService.getPort());

		ServletContextHandler servletContextHandler =
				new ServletContextHandler(ServletContextHandler.SESSIONS);
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

		log.info("Stopping RESTFul services ... ");

		server.stop();

		log.info("... stopped");

	}
}

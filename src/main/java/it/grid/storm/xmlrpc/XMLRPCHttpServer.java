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

import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.webserver.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class XMLRPCHttpServer {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory
		.getLogger(XMLRPCHttpServer.class);

	private final static int DEFAULT_PORT = 8080;

	private final static int DEFAULT_THREAD_NUM = 100;

	/**
	 * The web server running the XML-RPC server
	 */
	private final WebServer webServer;

	/**
	 * True if a web server has been started
	 */
	private boolean running = false;

	/**
	 * @param port
	 * @param maxThreadNum
	 * @throws XmlRpcException
	 */
	public XMLRPCHttpServer(int port, int maxThreadNum)
		throws StoRMXmlRpcException {

		log.info("[xmlrpc server] Creating server on port: " + port
			+ " using at most " + maxThreadNum + " threads");
		this.webServer = buildWebServer(port, maxThreadNum);
	};

	/**
	 * @param port
	 * @throws XmlRpcException
	 */
	public XMLRPCHttpServer(int port) throws StoRMXmlRpcException {

		this(port, DEFAULT_THREAD_NUM);
	};

	/**
	 * @throws XmlRpcException
	 * 
	 */
	public XMLRPCHttpServer() throws StoRMXmlRpcException {

		this(DEFAULT_PORT);
	};

	private WebServer buildWebServer(int port, int maxThreadNum)
		throws StoRMXmlRpcException {

		WebServer server = new WebServer(port);
		server.getXmlRpcServer().setMaxThreads(maxThreadNum);
		PropertyHandlerMapping phm = new PropertyHandlerMapping();
		try {
			phm.addHandler("synchcall", XMLRPCMethods.class);
		} catch (XmlRpcException e) {
			log
				.error("Unable to create synchcall PropertyHandlerMapping on XMLRPCMethods class . XmlRpcException"
					+ e.getMessage());
			throw new StoRMXmlRpcException("Unable to initialize the XmlRpcServer");
		}
		server.getXmlRpcServer().setHandlerMapping(phm);

		/*
		 * IP filtering is disabled because dynamic DNS for the FE machine. Where a
		 * dynamic DNS is used to load balancing on the FE machines, the hostname IP
		 * lookup above cannot be used. In that case the configuration properties
		 * files must contains the set of the specific IPs for the frontend
		 * machines. In that way, the xmlrpc server can be configured to accept
		 * request from each particulare FE machine.
		 * 
		 * HostLookup hl = new HostLookup(); for(String hostname :
		 * Configuration.getInstance().getListOfMachineNames()) { try { String IP =
		 * hl.lookup(hostname); log.info("[xmlrpc server] Accepting requests from "
		 * + IP + " (" + hostname + ")"); server.getXmlRpcServer().acceptClient(IP);
		 * } catch (UnknownHostException ex) {
		 * log.warn("Synchserver: IP for hostname: " + hostname +
		 * " cannot be resolved."); } }
		 */
		return server;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.xmlrpc.XMLRPCServerInterface#createServer()
	 */
	public synchronized void start() throws StoRMXmlRpcException {

		if (!running) {
			log.info("[xmlrpc server] Starting server...");
			try {
				this.webServer.start();
			} catch (IOException e) {
				log.error("xmlrpcServer web server start failure. IOException"
					+ e.getMessage());
				throw new StoRMXmlRpcException(
					"Unable to create the xmlRPC server. IOException: " + e.getMessage());
			}
			running = true;
			log.info("[xmlrpc server] Server running...");
		}
	}

	/**
     * 
     */
	public synchronized void stop() {

		if (this.webServer != null) {
			this.webServer.shutdown();
		}
		running = false;
	}
}

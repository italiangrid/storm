/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm;

import it.grid.storm.config.Configuration;
import it.grid.storm.space.gpfsquota.GPFSQuotaManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents a Multithreaded Server listening for administration commands to be sent to
 * StoRM.
 * 
 * If it receives the string START, the picker, the spaceReservationServer and the xmlrpcServer get
 * started; if it receives the string STOP, the picker stops; any other string interrupts the
 * connection to this command server leaving StoRM in whatever state is was on.
 * 
 * @author EGRID - ICTP Trieste; INFN - CNAF Bologna
 * @version 3.0
 * @Date July 2005
 */
public class StoRMCommandServer {

	private enum Command {
		START("START"), STOP("STOP"), SHUTDOWN("SHUTDOWN"), STATUS("STATUS"), EXIT("EXIT"), UNKNOW(
				"UNKNOW");

		private final String name;

		private Command(String name) {

			this.name = name;
		}

		public static Command getCommand(String name) {

			if (name != null) {
				for (Command command : Command.values()) {
					if (command.getName().equals(name.trim().toUpperCase())) {
						return command;
					}
				}
			}
			return UNKNOW;
		}

		private String getName() {

			return name;
		}

		@Override
		public String toString() {

			return this.name;
		}
	}

	private enum StormStatus {
		BOOTSTRAPPING("BOOTSTRAPPING"), RUNNING("RUNNING"), STOPPED("STOPPED"), STARTING(
				"STARTING"), STOPPING("STOPPING"), SHUTTING_DOWN("SHUTTING_DOWN"), UNKNOW("UNKNOW");

		private final String statusMessage;

		private StormStatus(String name) {

			this.statusMessage = name;
		}

		private String getStatusMessage() {

			return statusMessage;
		}

		@Override
		public String toString() {

			return this.statusMessage;
		}
	}

	private StoRM storm; // only StoRM object that the command server administers!
	private int listeningPort; // command server binding port
	private ServerSocket server = null; // server socket of command server!
	private static Logger log = LoggerFactory.getLogger(StoRMCommandServer.class);
	private boolean shutdownInProgress = false;

	/**
	 * Default constructor.
	 * 
	 * @param storm the storm instance. that should be initialized before anything else, as is where
	 *        configuration is loaded.
	 * 
	 */
	public StoRMCommandServer(StoRM storm) {

		this.storm = storm;

		this.listeningPort = Configuration.getInstance().getCommandServerBindingPort();

		startCommandServer();
	}

	/**
	 * Private method that starts a listening ServerSocket, and handles multiple requests by
	 * spawning different CommandExecuterThreads!
	 * 
	 * @param storm
	 */
	private void startCommandServer() {

		try {

			InetAddress loopbackAddress = InetAddress.getByName("localhost");
			server = new ServerSocket(listeningPort, 0, loopbackAddress);

		} catch (IOException e) {

			log.error("Could not bind to port {}: {}", listeningPort, e.getMessage(), e);

			System.exit(1);
		}


		new Thread() {

			@Override
			public void run() {

				try {
					while (true) {

						new CommandExecuterThread(server.accept(), storm).start();
					}
				} catch (IOException e) {

					log.error(
							"UNEXPECTED ERROR! Something went wrong with " + "server.accept(): {}",
							e.getMessage(), e);

					System.exit(1);
				}
			}
		}.start();
	}

	/**
	 * Private class that represents a thread that gets started when a new connection to this
	 * CommandServer is made: each client connecting to this CommandServer gets its own thread for
	 * processing the commands sent to StoRM
	 */
	private class CommandExecuterThread extends Thread {

		private static final String REQUEST_SUCCESS_RESPONSE = "SUCCESS";
		private static final String REQUEST_FAILURE_RESPONSE = "FAILURE";
		private static final String REQUEST_WARNING_RESPONSE = "WARNING";
		private Socket socket; // socket receiving the communication with the client!

		/**
		 * Constructor that requires the StoRM object to command, and the socket through which the
		 * client sends the commands to execute.
		 */
		private CommandExecuterThread(Socket socket, StoRM storm) {

			this.socket = socket;
		}

		@Override
		public void run() {

			BufferedReader in;
			try {
				// input stream from client
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {

				log.error("UNEXPECTED ERROR! Unable to get a reader for the client "
						+ "socket. IOException: {}", e.getMessage(), e);
				return;
			}
			BufferedWriter out;
			try {
				// output stream to the client
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (IOException e) {

				log.error("UNEXPECTED ERROR! Unable to get a writer for the client "
						+ "socket. IOException: {}", e.getMessage(), e);
				return;
			}
			String response = REQUEST_FAILURE_RESPONSE;
			boolean acceptCommands = true;
			String inputLine;
			try {
				inputLine = in.readLine();
			} catch (IOException e) {

				log.error("UNEXPECTED ERROR! Unable to read from the client socket. "
						+ "IOException: {}", e.getMessage(), e);
				return;
			}
			if (inputLine != null) {
				do {
					switch (Command.getCommand(inputLine)) {
						case START:
							// manage START command!
							log.info("StoRM: starting Backend services...");
							if (startServices()) {
								response = REQUEST_SUCCESS_RESPONSE;
								log.info("StoRM: Backend services successfully started.");
							} else {
								log.error("StoRM: error starting storm services.");
							}
							break;
						case STOP:
							// manage STOP command!
							log.info("StoRM: stopping Backend services...");
							if (stopServices()) {
								response = REQUEST_SUCCESS_RESPONSE;
								log.info("StoRM: Backend successfully stopped.");
							} else {
								log.error("StoRM: error stopping storm services.");
							}
							break;
						case SHUTDOWN:
							if (!shutdownInProgress) {
								shutdownInProgress = true;
								log.info("StoRM: Backend shutdown...");
								log.info("StoRM: stopping Backend services...");
								stopServices();
								response = REQUEST_SUCCESS_RESPONSE;
								sendOutputAndClose(response, out, in, socket);
								log.info("StoRM: Backend shutdown complete.");
								System.exit(0);
							}
							log.info("StoRM: Backend shutdown already in progress");
							break;
						case STATUS:
							switch (getCurrentStatus()) {
								case RUNNING:
									response = StormStatus.RUNNING.getStatusMessage();
									break;
								case STOPPED:
									response = StormStatus.STOPPED.getStatusMessage();
									break;
								case STARTING:
									response = StormStatus.STARTING.getStatusMessage();
									break;
								case STOPPING:
									response = StormStatus.STOPPING.getStatusMessage();
									break;
								case SHUTTING_DOWN:
									response = StormStatus.SHUTTING_DOWN.getStatusMessage();
									break;
								case UNKNOW:
									response = REQUEST_WARNING_RESPONSE;
									break;
								default:
									response = REQUEST_WARNING_RESPONSE;
									break;
							}
							break;
						case EXIT:
							// sequence of commands completed
							break;
						case UNKNOW:
							log.warn("Received an unknown command: {}", inputLine);
							acceptCommands = false;
							// any other command breaks the connection, but the command server
							// remains on!
							break;
						default:
							// any other command breaks the connection, but the command server
							// remains on!
							acceptCommands = false;
							log.warn("Received an unknown command: {}", inputLine);
							break;
					}
					try {
						inputLine = in.readLine();
					} catch (IOException e) {

						log.error("UNEXPECTED ERROR! Unable to read from the client socket. "
								+ "IOException : {}", e.getMessage(), e);
						return;
					}
				} while (inputLine != null && acceptCommands);
			}
			sendOutputAndClose(response, out, in, socket);
		}

		/**
		 * @return
		 */
		private boolean startServices() {

			boolean response = true;
			if (!storm.pickerIsRunning()) {
				storm.startPicker();
			}
			try {
				if (!storm.xmlRpcServerIsRunning()) {
					storm.startXmlRpcServer();
				}
			} catch (Exception e) {

				log.error("Unable to start the xmlrpc server. Exception: {}", e.getMessage(), e);

				stopServices();
				return false;
			}
			try {
				if (!storm.restServerIsRunning()) {
					storm.startRestServer();
				}
			} catch (Exception e) {

				log.error("Unable to start the Rest server. Exception: {}", e.getMessage(), e);

				stopServices();
				return false;
			}
			if (!storm.spaceGCIsRunning()) {
				storm.startSpaceGC();
			}
			if (!storm.isExpiredAgentRunning()) {
				storm.startExpiredAgent();
			}
			return response;
		}

		/**
		 * @return
		 */
		private boolean stopServices() {


			storm.stopPicker();
			storm.stopXmlRpcServer();
			storm.stopRestServer();
			storm.stopSpaceGC();
			storm.stopExpiredAgent();

			GPFSQuotaManager.INSTANCE.shutdown();

			return true;
		}

		/**
		 * @param response
		 * @param out
		 * @param in
		 * @param socket
		 */
		private void sendOutputAndClose(String response, BufferedWriter out, BufferedReader in,
				Socket socket) {

			try {
				try {
					out.write(response, 0, response.length());
					out.newLine();
				} catch (IOException e) {

					log.error("UNEXPECTED ERROR! Unable to write on the client socket. "
							+ "IOException : {}", e.getMessage(), e);

				}
				try {
					out.close();
					in.close();
				} catch (IOException e) {

					log.error("UNEXPECTED ERROR! Unable to close client socket streams. "
							+ "IOException : {}", e.getMessage(), e);

				}
			} finally {
				try {
					socket.close();
				} catch (IOException e) {

					log.error("UNEXPECTED ERROR! Unable to close client socket. "
							+ "IOException : {}", e.getMessage(), e);

				}
			}
		}

		/**
		 * @return
		 */
		private StormStatus getCurrentStatus() {

			if (bootstrapInProgress()) {
				return StormStatus.BOOTSTRAPPING;
			}
			if (shutdownInProgress()) {
				return StormStatus.SHUTTING_DOWN;
			}
			if (servicesRunning()) {
				return StormStatus.RUNNING;
			}
			if (servicesStopped()) {
				return StormStatus.STOPPED;
			}
			if (servicesStarting()) {
				return StormStatus.STARTING;
			}
			if (servicesStopping()) {
				return StormStatus.STOPPING;
			}
			return StormStatus.UNKNOW;
		}

		private boolean bootstrapInProgress() {

			return false;
		}

		/**
		 * @return
		 */
		private boolean shutdownInProgress() {

			return shutdownInProgress;
		}

		private boolean servicesRunning() {

			return storm.pickerIsRunning() && storm.xmlRpcServerIsRunning()
					&& storm.restServerIsRunning() && storm.spaceGCIsRunning()
					&& storm.isExpiredAgentRunning();
		}

		private boolean servicesStopped() {

			return !storm.pickerIsRunning() && !storm.xmlRpcServerIsRunning()
					&& !storm.restServerIsRunning() && !storm.spaceGCIsRunning()
					&& !storm.isExpiredAgentRunning();
		}

		private boolean servicesStarting() {

			return false;
		}

		private boolean servicesStopping() {

			return false;
		}
	}

	/**
	 * Method that automatically starts a CommandServer listening on the port specified in the
	 * configuration file.
	 * 
	 * The command line accepts two parameters, both of which must be specified or else the command
	 * line is completely ignored:
	 * 
	 * StoRMCommandServer pathname_of_configuration_file refresh_rate_in_seconds
	 * 
	 * For example: StoRMCommandServer /home/storm/backend/etc/storm.properties 5
	 * 
	 * It starts the command server with configuration file found in
	 * /home/storm/backend/etc/storm.properties, and refresh rate for checking changes in the
	 * configuration file of 5 seconds. A value of 0 disables refresh.
	 * 
	 * If no command line parameters are specified, the behaviour is dictated by the StoRM Class.
	 * Please refer there for further information.
	 */
	public static void main(String[] args) {

		Thread.setDefaultUncaughtExceptionHandler(new StoRMDefaultUncaughtExceptionHandler());

		String configurationPathname = "";
		int refresh = 0;

		if (args.length == 0) {

			log.info("StoRMCommandServer invoked without any command line parameter.");

		} else if (args.length == 2) {

			configurationPathname = args[0];

			log.info("StoRMCommandServer invoked with two parameters.");
			log.info("Configuration file: {}", configurationPathname);

			try {

				refresh = Integer.parseInt(args[1]);
				log.info("Configuration file refresh rate: {} seconds", refresh);

			} catch (NumberFormatException e) {

				log.error("Configuration file refresh rate: NOT an integer! "
						+ "Disabling refresh by default! {}", e.getMessage(), e);
			}
		} else {

			log.warn("StoRMCommandServer invoked with an invalid number of parameters. ");
			log.warn("Ignoring all: continuing as though as none were present.");
		}

		log.info("Now booting StoRM...");

		new StoRMCommandServer(new StoRM(configurationPathname, refresh));
	}
}

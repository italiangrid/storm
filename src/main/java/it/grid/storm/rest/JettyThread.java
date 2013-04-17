package it.grid.storm.rest;

import org.eclipse.jetty.server.Server;

/**
 * Thread that starts a Jetty server. The thread is passed an instance of
 * {@link Server}, on which start and join are called upon starting the thread.
 * This is needed as the join method is blocking, and would hang a thread
 * calling it directly.
 * 
 * @author valerioventuri
 */
public class JettyThread extends Thread {

	/**
	 * The {@link Server} object.
	 */
	private Server server;

	/**
	 * Constructor.
	 * 
	 * @param server
	 *          the server to start
	 */
	public JettyThread(Server server) {

		this.server = server;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {

		try {

			server.start();
			server.join();

		} catch (Exception e) {

			e.printStackTrace();
			System.exit(1);
		}
	}
}

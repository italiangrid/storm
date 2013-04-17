package it.grid.storm.balancer.ftp;

import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.locks.*;

/**
 * Simplistic telnet client.
 */
public class TelnetClient {

	private static final Logger logger = LoggerFactory
		.getLogger(TelnetClient.class);
	private static final int maxStatusUpdateRetryAttempts = 3;
	private static final long statusUpdateMaxWait = 1; // 1 second

	private static class HandlerObserver implements Observer {

		private Boolean response = null;

		private final ReentrantLock lock = new ReentrantLock();
		private final Condition updated = lock.newCondition();

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		@Override
		public void update(Observable o, Object arg) {

			lock.lock();
			try {
				logger.debug("Managing an update");
				GridFtpConnectionStatus s = (GridFtpConnectionStatus) o;
				this.response = s.isGridFtpConnectionValid();
				updated.signalAll();
			} catch (Exception e) {
				this.response = false;
			} finally {
				lock.unlock();
			}
		}

		/**
		 * @param timeToWait
		 */
		public void waitForUpdates(long secondsToWait) {

			lock.lock();
			try {
				if (!hasResponse()) {
					logger.debug("Waiting for updates");
					updated.await(secondsToWait, TimeUnit.SECONDS);
				} else {
					logger.debug("No need to wait, response available");
				}
			} catch (InterruptedException e) {
			} finally {
				lock.unlock();
			}
		}

		/**
		 * @return
		 */
		public boolean hasResponse() {

			return this.response != null;
		}

		/**
		 * @return
		 * @throws IllegalStateException
		 */
		public boolean getResponse() throws IllegalStateException {

			if (this.response == null) {
				throw new IllegalStateException(
					"Observer not yet modified, you have to wait");
			}
			return this.response;
		}
	}

	/**
	 * @param address
	 * @return
	 * @throws Exception
	 */
	public static boolean check(InetSocketAddress address) throws Exception {

		ClientBootstrap bootstrap = new ClientBootstrap(
			new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		HandlerObserver observer = new HandlerObserver();
		bootstrap.setPipelineFactory(new TelnetClientPipelineFactory(observer));

		bootstrap.setOption("connectTimeoutMillis", 5000); // 5 seconds

		logger.debug("Connecting to Gridftp at " + address.toString());
		ChannelFuture channelFuture = bootstrap.connect(address);

		Channel channel = channelFuture.awaitUninterruptibly().getChannel();

		if (!channelFuture.isSuccess()) {
			logger.info("GridFTP Telnet connection to server " + address.toString()
				+ "failed. Cause : " + channelFuture.getCause());
			bootstrap.releaseExternalResources();
			return false;
		}
		logger.debug("Channel created successfully");
		if (!channel.isConnected()) {
			logger.info("GridFTP Telnet connection failed " + address.toString()
				+ "failed. Cause : " + channelFuture.getCause());
			bootstrap.releaseExternalResources();
			return false;
		}
		logger.debug("Connection succeeded");
		// check what has been sent by the server on the socket
		int failedAttempts = 0;
		boolean failed = false;
		boolean response = false;
		do {
			failed = false;
			try {
				observer.waitForUpdates(statusUpdateMaxWait);
				if (observer.hasResponse()) {
					response = observer.getResponse();
				} else {
					failedAttempts++;
					failed = true;
				}
			} catch (Exception e) {
				logger.warn("Unable to check handler response. Exception : "
					+ e.getMessage());
				failedAttempts++;
				failed = true;
			}
		} while (failed && failedAttempts <= maxStatusUpdateRetryAttempts);

		// Close the connection. Make sure the close operation ends because
		// all I/O operations are asynchronous in Netty.
		channel.close().awaitUninterruptibly();

		// Shut down all thread pools to exit.
		bootstrap.releaseExternalResources();

		return response;
	}
}

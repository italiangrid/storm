package it.grid.storm.balancer.ftp;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponsivenessCacheEntry {

	private static final Logger log = LoggerFactory.getLogger(ResponsivenessCacheEntry.class);
	
	public enum Responsiveness {
		RESPONSIVE, UNRESPONSIVE, UNKNOWN
	}

	private String hostname;
	private int port;
	private InetSocketAddress remoteService;
	private long timeToRensponse;
	private long checkTime;
	private Responsiveness status = Responsiveness.UNKNOWN;
	private boolean resolvedAddress = false;
	
	public ResponsivenessCacheEntry(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;	
		remoteService = new InetSocketAddress(hostname, port);
		if (remoteService.isUnresolved()) {
			resolvedAddress = false;
		} else {
			resolvedAddress = true;
		}
		this.timeToRensponse = -1;
		this.checkTime = -1;
		this.status = Responsiveness.UNKNOWN;;		
	}
	
	public ResponsivenessCacheEntry(String hostname, int port, long timeToResp, long checkTime, Responsiveness status) {
		this.hostname = hostname;
		this.port = port;
		this.timeToRensponse = timeToResp;
		this.checkTime = checkTime;
		this.status = status;
		try {
		  remoteService = new InetSocketAddress(hostname, port);
		} catch (IllegalArgumentException iae) {
			log.error("Illegal argument :"+iae.getMessage());
		} catch (SecurityException se) {
			log.error("Security Exception :"+se.getMessage());
		}
		if (remoteService.isUnresolved()) {
			resolvedAddress = false;
		} else {
			resolvedAddress = true;
		}
	}

	/**
	 * @return the checkTime
	 */
	public final long getCheckTime() {
		return checkTime;
	}

	/**
	 * @param checkTime the checkTime to set
	 */
	public final void setCheckTime(long checkTime) {
		this.checkTime = checkTime;
	}

	/**
	 * @return the hostname
	 */
	public final String getHostname() {
		return hostname;
	}

	/**
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * @return the remoteService
	 */
	public final InetSocketAddress getRemoteService() {
		return remoteService;
	}

	/**
	 * @return the timeToRensponse
	 */
	public final long getTimeToRensponse() {
		return timeToRensponse;
	}

	/**
	 * @return the status
	 */
	public final Responsiveness getStatus() {
		return status;
	}


	/**
	 * @param timeToRensponse the timeToRensponse to set
	 */
	private final void setTimeToRensponse(long timeToRensponse) {
		this.timeToRensponse = timeToRensponse;
		if (timeToRensponse<0) {
			setStatus(Responsiveness.UNRESPONSIVE);
		} else {
			setStatus(Responsiveness.RESPONSIVE);
		}
	}

	/**
	 * @param status the status to set
	 */
	private final void setStatus(Responsiveness status) {
		this.status = status;
		this.checkTime = System.currentTimeMillis();
	}

	public boolean isExpired(long lifetime) {
		if (System.currentTimeMillis() - getCheckTime() > lifetime) {
			return true;
		} else {
			return false;
		}
	}
	
	public final Responsiveness refresh() {
		long time = CheckControlChannel.checkGFtpServer(getHostname(), getPort());
		setTimeToRensponse(time);
		return getStatus();
	}
}

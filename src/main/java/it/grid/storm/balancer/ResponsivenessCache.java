package it.grid.storm.balancer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponsivenessCache {

	private static final Logger log = LoggerFactory
		.getLogger(ResponsivenessCache.class);
	private final Map<Node, ResponsivenessCacheEntry> cache = new HashMap<Node, ResponsivenessCacheEntry>();
	private long entryLifetime;

	public enum Responsiveness {
		RESPONSIVE, UNRESPONSIVE, UNKNOWN
	}

	public ResponsivenessCache(long entryLifetimeMillisec) {

		this.entryLifetime = entryLifetimeMillisec;
	}

	public ResponsivenessCacheEntry addEntry(Node node) {

		log.debug("Adding cache entry for {}", node);
		ResponsivenessCacheEntry newEntry = this.new ResponsivenessCacheEntry(node,
			Responsiveness.UNKNOWN);
		cache.put(node, newEntry);
		return newEntry;
	}

	/**
	 * @param hostname
	 * @param port
	 * @return
	 * @throws Exception
	 */
	public Responsiveness getResponsiveness(Node node) throws Exception {

		Responsiveness resp = Responsiveness.UNKNOWN;
		ResponsivenessCacheEntry entry = getEntry(node);
		if (entry != null) {
			if (isExpired(entry)) {
				log.debug("Cache entry {} expired. Refreshing..", entry.toString());
				resp = entry.refresh();
			} else {
				log.debug("Found valid cache entry for {}", entry.toString());
				resp = entry.getStatus();
			}
		} else {
			log.debug("Missing cache entry for {}. Adding and refreshing..", node);
			entry = addEntry(node);
			resp = entry.refresh();
		}
		return resp;
	}

	/**
	 * @param entry
	 * @return
	 */
	public boolean isExpired(ResponsivenessCacheEntry entry) {

		if (entry != null) {
			return entry.isExpired(entryLifetime);
		}
		return true;
	}

	/**
	 * @param hostname
	 * @param port
	 * @return null if there is no entry for the provided parameters
	 */
	public ResponsivenessCacheEntry getEntry(Node node) {

		return cache.get(node);
	}

	private class ResponsivenessCacheEntry {

		private final Node cachedNode;
		private long checkTime = -1;
		private Responsiveness status = Responsiveness.UNKNOWN;

		public ResponsivenessCacheEntry(Node node, Responsiveness status) {

			this.cachedNode = node;
			this.status = status;
		}

		/**
		 * @return the status
		 */
		public final Responsiveness getStatus() {

			return status;
		}

		/**
		 * @param status
		 *          the status to set
		 */
		private final void setStatus(Responsiveness status) {

			this.status = status;
		}

		/**
		 * @param lifetime
		 * @return
		 */
		public boolean isExpired(long lifetime) {

			return System.currentTimeMillis() - checkTime >= lifetime;
		}

		/**
		 * @return
		 * @throws Exception
		 */
		public final Responsiveness refresh() throws Exception {

			Responsiveness respness = Responsiveness.UNKNOWN;
			if (cachedNode.checkServer()) {
				respness = Responsiveness.RESPONSIVE;
			} else {
				respness = Responsiveness.UNRESPONSIVE;
			}
			checkTime = new Date().getTime();
			setStatus(respness);
			return status;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			StringBuilder builder = new StringBuilder();
			builder.append("ResponsivenessCacheEntry [cachedNode=");
			builder.append(cachedNode);
			builder.append(", checkTime=");
			builder.append(checkTime);
			builder.append(", status=");
			builder.append(status);
			builder.append("]");
			return builder.toString();
		}
	}
}

package it.grid.storm.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class HostLookup {
	
	private static final Logger log = Logger.getLogger("synch_xmlrpc_server");
	
	public HostLookup() {}
	
	public String lookup(String hostname) throws UnknownHostException {
		log.debug("Lookup:looking for hostname: "+hostname);
		InetAddress ia = InetAddress.getByName(hostname);
		return ia.getHostAddress();
	}

}

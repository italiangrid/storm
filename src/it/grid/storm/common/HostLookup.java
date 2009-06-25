package it.grid.storm.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostLookup {

    private static final Logger log = LoggerFactory.getLogger(HostLookup.class);

    public HostLookup() {}

    public String lookup(String hostname) throws UnknownHostException {
        log.debug("Lookup:looking for hostname: "+hostname);
        InetAddress ia = InetAddress.getByName(hostname);
        return ia.getHostAddress();
    }

}

package it.grid.storm.balancer.ftp;

import it.grid.storm.balancer.Balancer;
import it.grid.storm.balancer.BalancerStrategyType;
import it.grid.storm.balancer.Node;
import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.model.Protocol;

import java.io.IOException;
import java.util.logging.Logger;

public class CheckControlChannel {

    private static final Logger logger = Logger.getLogger(CheckControlChannel.class.getName()); 
	
	public static long checkGFtpServer(String host, int port) {
		int result = -1;
		try {
			return TelnetClient.check(host, port);
		} catch (IOException e) {
			logger.info("Encountered a I/O problem during the check of The GFTP ("+host+","+port+")");
		}
		return result;
	}
	
	public static void setSmartPool(CapabilityInterface capabilities) {
        Balancer<? extends Node> balancer = capabilities.getPoolByScheme(Protocol.GSIFTP);
        if (balancer.getStrategy()==BalancerStrategyType.SMART_RR) {
        	
        }
	}
	
    /*
     * USED to Test the Telnet connection
     */
    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + CheckControlChannel.class.getSimpleName() +
                    " <host> <port>");
            return;
        }

        // Parse options.
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        long time = CheckControlChannel.checkGFtpServer(host, port);
        logger.info("Time to complete : "+time);
        
    }
	
}

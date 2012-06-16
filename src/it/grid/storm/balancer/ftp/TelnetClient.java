package it.grid.storm.balancer.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 * Simplistic telnet client.
 */
public class TelnetClient {


    private static final Logger logger = Logger.getLogger(TelnetClient.class.getName());   
    
    public TelnetClient() {
    }

    public static long check(String host, int port) throws IOException {
    
    	long time = System.currentTimeMillis();
    	
    	// Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new TelnetClientPipelineFactory());
        
        //Configure the timeout of the connections
        bootstrap.setOption("connectTimeoutMillis", 5000); //2 seconds
        
        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        		
        // Wait until the connection attempt succeeds or fails.
        Channel channel = future.awaitUninterruptibly().getChannel();
        
        //Check the result
        if (!future.isSuccess()) {
            //Log the failure
            bootstrap.releaseExternalResources();
            return -1;
        }
        
       if (channel.isConnected()) {
    	   logger.info("Connected with :"+channel.getRemoteAddress());
       } else {
    	   logger.info("NOT CONNECTED!!");
       }
        
        ChannelFuture lastWriteFuture = channel.write("quit" + "\r\n");
        //lastWriteFuture = channel.write("close" + "\r\n");
        //channel.close();
        
        // Wait until all messages are flushed before closing the channel.
        if (lastWriteFuture != null) {
				lastWriteFuture.awaitUninterruptibly();//awaitUninterruptibly(1000, TimeUnit.MILLISECONDS);
        }
        
        // Wait until the connection is closed or the connection attempt fails.
        channel.getCloseFuture().awaitUninterruptibly();
        
        // Close the connection.  Make sure the close operation ends because
        // all I/O operations are asynchronous in Netty.
        channel.close().awaitUninterruptibly();

        // Shut down all thread pools to exit.
        bootstrap.releaseExternalResources();
           
        // Wait until the connection is closed or the connection attempt fails.
        future.getChannel().getCloseFuture().awaitUninterruptibly();
    
        time = System.currentTimeMillis() - time;
        return time;
    }

    
    /*
     * USED to Test the Telnet connection
     */
    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + TelnetClient.class.getSimpleName() +
                    " <host> <port>");
            return;
        }

        // Parse options.
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        for (int i = 0; i < 1; i++) {
        	long time = TelnetClient.check(host, port);
            logger.info("TIME : "+time);
            Thread.sleep(1000);
		}
        
    }
}

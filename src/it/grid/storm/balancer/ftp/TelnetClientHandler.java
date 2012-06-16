package it.grid.storm.balancer.ftp;


import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Handles a client-side channel.
 */
public class TelnetClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(TelnetClientHandler.class.getName());


    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // Print out the line received from the server.
    	//logger.info("SERVER MESSAGE - " + e.getMessage());
    	FtpReplyMessage msg = new FtpReplyMessage(e.getMessage().toString());
    	logger.info("SERVER MESSAGE - Is a FTP? "+ msg.isRecognizedAsFTP());
    	logger.info("SERVER MESSAGE - ReplyCode : "+ msg.getReplyCode());
    	logger.info("SERVER MESSAGE - Message : "+ msg.getMessage());
    	
    	logger.info("CHANNEL Remote-IP:  "+ctx.getChannel().getRemoteAddress());
    	
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
       // logger.info("Server unreachable " + e.getCause().getMessage());
        e.getChannel().close();
    }
}

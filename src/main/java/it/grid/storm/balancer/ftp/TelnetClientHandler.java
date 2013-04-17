package it.grid.storm.balancer.ftp;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a client-side channel.
 */
public class TelnetClientHandler extends SimpleChannelUpstreamHandler {

	private static final Logger log = LoggerFactory
		.getLogger(TelnetClientHandler.class);
	private final GridFtpConnectionStatus state = new GridFtpConnectionStatus();

	public TelnetClientHandler() {

	}

	/**
	 * @return
	 */
	public GridFtpConnectionStatus getState() {

		return this.state;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
		throws Exception {

		FtpReplyMessage msg = new FtpReplyMessage(e.getMessage().toString());
		state.setMessageParsingResponse(msg.isRecognizedAsFTP());
		state.messageReceived();
		state.updated();
		super.messageReceived(ctx, e);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {

		log.warn("Server unreachable " + e.getCause().getMessage());
		this.state.setError();
		state.updated();
	}
}

package it.grid.storm.balancer.ftp;


import static org.jboss.netty.channel.Channels.*;
import java.util.Observer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class TelnetClientPipelineFactory implements ChannelPipelineFactory
{

    private final Observer o;
    
    public TelnetClientPipelineFactory(Observer o)
    {
        this.o = o;
    }
    
    /* (non-Javadoc)
     * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
     */
    public ChannelPipeline getPipeline() throws Exception
    {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        // Add the text line codec combination first,
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        // and then business logic.
        TelnetClientHandler hander = new TelnetClientHandler();
        pipeline.addLast("handler", hander);
        hander.getState().addObserver(o);
        return pipeline;
    }
}

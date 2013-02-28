package it.grid.storm.balancer.ftp;


import java.util.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 */
public class GridFtpConnectionStatus extends Observable
{
    private static final Logger log = LoggerFactory.getLogger(GridFtpConnectionStatus.class);

    private Boolean messageParsingResponse = null;

    private boolean operationFailed = false;

    public boolean messageReceived()
    {
        return (!operationFailed) && (messageParsingResponse != null);
    }

    /**
     * @return
     * @throws Exception
     */
    public boolean isGridFtpConnectionValid() throws Exception
    {
        if (messageParsingResponse == null)
        {
            log.error("Message received from the server but no response computed");
            throw new Exception("No valid response computed from server message");
        }
        return messageParsingResponse;
    }

    public void setMessageParsingResponse(boolean response)
    {
        this.messageParsingResponse = new Boolean(response);

    }

    public void setError()
    {
        this.operationFailed = true;
    }

    public void updated()
    {
        this.setChanged();
        this.notifyObservers();
    }
}

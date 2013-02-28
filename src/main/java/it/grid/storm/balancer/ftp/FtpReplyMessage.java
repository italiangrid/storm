package it.grid.storm.balancer.ftp;


public class FtpReplyMessage
{

    private String serverMessage;
    private FtpReplyCode replyCode = FtpReplyCode.UNKNOWN_ERROR;
    private boolean recognizedAsFTP = false;

    /**
     * @param serverMessage
     */
    public FtpReplyMessage(String serverMessage)
    {
        this.serverMessage = serverMessage;
        parse(serverMessage);
    }

    /**
     * @param serverMessage
     */
    private void parse(String serverMessage)
    {
        if ((serverMessage == null) || (serverMessage.length() < 3))
        {
            return;
        }
        else
        {
            // Try to extract the Return Code
            String returnCode = serverMessage.substring(0, 3);
            try
            {
                int retCode = Integer.parseInt(returnCode);
                replyCode = FtpReplyCode.find(retCode);
            } catch(NumberFormatException nfe)
            {
                return;
            }
            if (FtpReplyCode.SERVICE_READY.equals(replyCode))
            {
                recognizedAsFTP = true;
            }
        }
    }

    /**
     * @return the serverMessage
     */
    public final String getServerMessage()
    {
        return serverMessage;
    }

    /**
     * @return the replyCode
     */
    public final FtpReplyCode getReplyCode()
    {
        return replyCode;
    }

    /**
     * @return the recognizedAsFTP
     */
    public final boolean isRecognizedAsFTP()
    {
        return recognizedAsFTP;
    }
}

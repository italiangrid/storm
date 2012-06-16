package it.grid.storm.balancer.ftp;

public class FtpReplyMessage {

	private String serverMessage;
	private FtpReplyCode replyCode = FtpReplyCode.UNKNOWN_ERROR;
	private String message;
	private boolean recognizedAsFTP = false;
	
	
	public FtpReplyMessage(String serverMessage) {
		super();
		this.serverMessage = serverMessage;
		parse(serverMessage);
	}


	private void parse(String serverMessage) {
		if ((serverMessage==null) || (serverMessage.length()<3)) {
			recognizedAsFTP = false;
			return;
		} else {
			//Try to extract the Return Code
			String returnCode = serverMessage.substring(0, 3);
			try { 
			  int retCode = Integer.parseInt(returnCode);
			  replyCode = FtpReplyCode.find(retCode);
			} catch (NumberFormatException nfe) {
				recognizedAsFTP = false;
				return;
			}
			
			message = serverMessage.substring(3);
			recognizedAsFTP = true;

		}
		
	}


	/**
	 * @return the serverMessage
	 */
	public final String getServerMessage() {
		return serverMessage;
	}


	/**
	 * @return the replyCode
	 */
	public final FtpReplyCode getReplyCode() {
		return replyCode;
	}



	/**
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}


	/**
	 * @return the recognizedAsFTP
	 */
	public final boolean isRecognizedAsFTP() {
		return recognizedAsFTP;
	}
	
}

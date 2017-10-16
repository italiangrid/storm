package it.grid.storm.asynch;

import java.util.ArrayList;
import java.util.Date;
import it.grid.storm.asynch.Copy.Result;
import it.grid.storm.asynch.Copy.ResultType;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;

public class PushCopyPutVisitor implements CopyVisitor {

	private final TSizeInBytes getFileSize;

	@SuppressWarnings("unused")
	private PushCopyPutVisitor() {

		// forbidden;
		this.getFileSize = null;
	}

	public PushCopyPutVisitor(TSizeInBytes getFileSize) {

		this.getFileSize = getFileSize;
	}

	@Override
	public Result visit(VisitableCopy copy) {

		try {
			TLifeTimeInSeconds retryTime = TLifeTimeInSeconds.make(Configuration
				.getInstance().getSRMClientPutTotalRetryTime(), TimeUnit.SECONDS);
			try {
				copy.getLog().debug("PUSH COPY CHUNK: getting SRM client...");
				SRMClient srmClient = SRMClientFactory.getInstance().client();
				copy.getLog().debug("... got it!");
				// Invoke prepareToPut functionality of SRMClient
				copy.getLog().debug(
					"PUSH COPY CHUNK: Invoking prepareToPut functionality...");
				SRMPrepareToPutReply reply = srmClient.prepareToPut(copy.getGu(), copy
					.getRequestData().getDestinationSURL(), copy.getRequestData()
					.getLifetime(), copy.getRequestData().getFileStorageType(), copy
					.getRequestData().getSpaceToken(), getFileSize,
					TransferProtocol.GSIFTP, "StoRM Remote PtP for (push) srmCopy", copy
						.getRequestData().getOverwriteOption(), retryTime);
				copy.getLog().debug("... got it! Reply was: {}", reply);
				// Polling...
				long timeOut = new Date().getTime()
					+ Configuration.getInstance().getSRMClientPutTimeOut() * 1000;
				long sleepTime = Configuration.getInstance().getSRMClientPutSleepTime() * 1000;
				boolean timedOut = false;
				SRMStatusOfPutRequestReply statusOfPutRequestReply = null;
				TStatusCode replyCode = null;
				try {
					do {
						copy.getLog().debug("PUSH COPY CHUNK: Going to sleep...");
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
						}
						;
						copy.getLog().debug(
							"PUSH COPY CHUNK: Waking up and verifying status...");
						statusOfPutRequestReply = srmClient.statusOfPutRequest(reply
							.requestToken(), copy.getGu(), copy.getRequestData()
							.getDestinationSURL());
						replyCode = statusOfPutRequestReply.returnStatus().getStatusCode();
						timedOut = (new Date().getTime() > timeOut);
						copy.getLog().debug("PUSH COPY CHUNK: reply was {}; the reply code "
							+ "was: {}; timedOut is: {}", statusOfPutRequestReply, replyCode, 
							timedOut);
					} while (((replyCode == TStatusCode.SRM_REQUEST_QUEUED)
						|| (replyCode == TStatusCode.SRM_REQUEST_INPROGRESS) 
						|| (replyCode == TStatusCode.SRM_INTERNAL_ERROR))
						&& !timedOut);
				} catch (SRMClientException e2) {
					// The SRMClient statusOfPutRequest functionality failed!
					copy.getLog().error("ERROR IN PushCopyChunk! PutOperation failed: "
						+ "SRMClient could not do an srmStatusOfPutRequest! {}", e2);
					StringBuilder sb = new StringBuilder();
					sb.append("Parameters passed to client: ");
					sb.append("requestToken: ");
					sb.append(reply.requestToken().toString());
					sb.append(", ");
					sb.append("GridUser: ");
					sb.append(copy.getGu().toString());
					sb.append(", ");
					sb.append("toSURL: ");
					sb.append(copy.getRequestData().getDestinationSURL().toString());
					sb.append(".");
					copy.getLog().debug(sb.toString());
					return copy.buildOperationResult(
						"SRMClient failure! Could not do an srmStatusOfPutRequest! " + e2,
						ResultType.PUT);
				}
				// Handle all possible states...
				copy.getLog().debug("PUSH COPY CHUNK: out of loop ...");
				if (timedOut) {
					// Reached time out!
					copy.getLog().warn(
						"ATTENTION IN PushCopyChunk! PutOperation timed out!");
					return copy.buildOperationResult("PutOperation timed out!",
						ResultType.PUT);
				}
				// The remote operation completed!!!
				copy.getLog().debug("PushCopyChunk! The PutOperation completed! {}", 
					statusOfPutRequestReply.returnStatus());
				ArrayList<Object> parameters = new ArrayList<Object>(3);
				parameters.add(1, statusOfPutRequestReply.returnStatus());
				parameters.add(2, statusOfPutRequestReply.toTURL());
				parameters.add(3, reply.requestToken());
				return copy.buildOperationResult(parameters, ResultType.PUT);
			} catch (SRMClientException e1) {
				// The SRMClient prepareToPut functionality failed!
				copy.getLog().error("ERROR IN PushCopyChunk! PutOperation failed: "
					+ "SRMClient could not do an srmPrepareToPut! {}", e1);
				StringBuilder sb = new StringBuilder();
				sb.append("Parameters passed to client: ");
				sb.append("GridUser:");
				sb.append(copy.getGu().toString());
				sb.append(", ");
				sb.append("toSURL:");
				sb.append(copy.getRequestData().getDestinationSURL().toString());
				sb.append(", ");
				sb.append("lifetime:");
				sb.append(copy.getRequestData().getLifetime().toString());
				sb.append(", ");
				sb.append("fileStorageType:");
				sb.append(copy.getRequestData().getFileStorageType().toString());
				sb.append(", ");
				sb.append("spaceToken:");
				sb.append(copy.getRequestData().getSpaceToken().toString());
				sb.append(", ");
				sb.append("fileSize:");
				sb.append(copy.getRequestData().toString());
				sb.append(", ");
				sb.append("transferProtocol:");
				sb.append(TransferProtocol.GSIFTP.toString());
				sb.append(", ");
				sb.append("description:");
				sb.append("StoRM Remote PtP for (push) srmCopy");
				sb.append(", ");
				sb.append("overwriteOption:");
				sb.append(copy.getRequestData().getOverwriteOption().toString());
				sb.append(", ");
				sb.append("retryTime:");
				sb.append(retryTime.toString());
				sb.append(".");
				copy.getLog().debug(sb.toString());
				return copy.buildOperationResult(
					"SRMClient failure! Could not do an srmPrepareToPut! " + e1,
					ResultType.PUT);
			} catch (NoSRMClientFoundException e1) {
				copy.getLog().error("ERROR IN PushCopyChunk! Cannot call remote SRM "
					+ "server because no SRM client could be loaded! {}", e1, ResultType.PUT);
				return copy
					.buildOperationResult(
						"Cannot talk to other SRM server because no SRM client could be loaded!",
						ResultType.PUT);
			}
		} catch (IllegalArgumentException e3) {
			// Cannot create TLifeTimeInSeconds! This is a programming bug and should
			// not occur!!!
			copy.getLog().error("ERROR IN PushCopyChunk! Cannot create "
				+ "TLifeTimeInSeconds! {}", e3.getMessage(), e3);
			return copy.buildOperationResult(e3.toString(), ResultType.PUT);
		}
	}

}

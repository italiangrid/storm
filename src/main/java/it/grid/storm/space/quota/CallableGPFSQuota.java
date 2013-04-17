package it.grid.storm.space.quota;

import java.util.concurrent.Callable;

public class CallableGPFSQuota implements Callable<GPFSQuotaCommandResult> {

	private boolean test;
	private long timeout;
	private long creationTime;

	/**
	 * @return the creationTime
	 */
	public final long getCreationTime() {

		return creationTime;
	}

	private GPFSMMLSQuotaCommand quotaCmd;

	/**
	 * @return the quotaCmd
	 */
	public final GPFSMMLSQuotaCommand getQuotaCmd() {

		return quotaCmd;
	}

	public CallableGPFSQuota() {

		this.test = false;
		this.timeout = GPFSMMLSQuotaCommand.DEFAULT_TIMEOUT;
		this.creationTime = System.currentTimeMillis();
		this.quotaCmd = new GPFSMMLSQuotaCommand(this.timeout);
	}

	public CallableGPFSQuota(long timeout) {

		this.test = false;
		this.timeout = timeout;
		this.creationTime = System.currentTimeMillis();
		this.quotaCmd = new GPFSMMLSQuotaCommand(this.timeout);
	}

	public CallableGPFSQuota(long timeout, boolean test) {

		this.test = test;
		this.timeout = timeout;
		this.creationTime = System.currentTimeMillis();
		this.quotaCmd = new GPFSMMLSQuotaCommand(this.timeout);
	}

	public CallableGPFSQuota(boolean poisonPill) {

		this.test = false;
		this.creationTime = System.currentTimeMillis();
		this.quotaCmd = new GPFSMMLSQuotaCommand(this.timeout);
	}

	@Override
	public GPFSQuotaCommandResult call() throws Exception {

		// This command take notes of starting time
		GPFSQuotaCommandResult quotaCmdResult = quotaCmd.executeGetQuotaInfo(test);

		return quotaCmdResult;
	}

}

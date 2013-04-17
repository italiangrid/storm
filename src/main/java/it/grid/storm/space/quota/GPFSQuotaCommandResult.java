package it.grid.storm.space.quota;

import java.util.ArrayList;
import java.util.List;

import it.grid.storm.space.ExitCode;

public class GPFSQuotaCommandResult implements Cloneable {

	private List<GPFSQuotaInfo> quotaResults;
	private long durationTime;
	private long startTime;
	private ExitCode cmdResult;

	public GPFSQuotaCommandResult() {

		this.startTime = System.currentTimeMillis();
		this.quotaResults = new ArrayList<GPFSQuotaInfo>();
		this.cmdResult = ExitCode.UNDEFINED;
	}

	public GPFSQuotaCommandResult(List<GPFSQuotaInfo> quotaRes, long startTime,
		long durTime, ExitCode cmdRes) {

		this.quotaResults = quotaRes;
		this.startTime = startTime;
		this.durationTime = durTime;
		this.cmdResult = cmdRes;
	}

	/**
	 * 
	 * @param quotaRes
	 */
	public void setQuotaInfos(List<GPFSQuotaInfo> quotaRes) {

		this.quotaResults = quotaRes;
		cmdResult = ExitCode.UNDEFINED;
		int nrFailure = 0;
		int nrNotProcessed = 0;
		int totalQuotas = quotaResults.size();
		for (GPFSQuotaInfo gpfsQuotaInfo : quotaRes) {
			if (gpfsQuotaInfo.isFailure()) {
				nrFailure++;
			} else if (!gpfsQuotaInfo.isInitializated()) {
				nrNotProcessed++;
			}
		}
		if ((nrFailure > 0) && (nrFailure + nrNotProcessed) < totalQuotas) {
			cmdResult = ExitCode.PARTIAL_SUCCESS;
		} else if ((nrFailure + nrNotProcessed) >= totalQuotas) {
			cmdResult = ExitCode.FAILURE;
		}
	}

	/**
	 * @return the durationTime
	 */
	public final long getDurationTime() {

		return durationTime;
	}

	/**
	 * @return the startTime
	 */
	public final long getStartTime() {

		return startTime;
	}

	/**
	 * @return the cmdResult
	 */
	public final ExitCode getCmdResult() {

		return cmdResult;
	}

	/**
     * 
     */
	public final void endOfExecution() {

		this.durationTime = System.currentTimeMillis() - startTime;
	}

	/**
	 * @param cmdResult
	 *          the cmdResult to set
	 */
	public final void setCmdResult(ExitCode cmdResult) {

		this.cmdResult = cmdResult;
	}

	/**
	 * @return the quotaResults
	 */
	public final List<GPFSQuotaInfo> getQuotaResults() {

		return quotaResults;
	}

	/**
	 * @param quotaResults
	 *          the quotaResults to set
	 */
	public final void setQuotaResults(List<GPFSQuotaInfo> quotaResults) {

		this.quotaResults = quotaResults;
	}

	public final void addQuotaResult(GPFSQuotaInfo quotaResult) {

		this.quotaResults.add(quotaResult);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {

		return new GPFSQuotaCommandResult(this.getQuotaResults(), this.startTime,
			this.durationTime, this.cmdResult);
	}

}

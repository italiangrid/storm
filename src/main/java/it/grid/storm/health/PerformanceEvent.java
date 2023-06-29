/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.health;

/**
 * @author zappi
 * 
 */
public class PerformanceEvent {

	private OperationType opType = OperationType.UNDEF;
	private int numberOfOperation = 0;
	private long minExecutionDuration = Long.MAX_VALUE;
	private long maxExecutionDuration = Long.MIN_VALUE;
	private long meanExecutionDuration = 0;
	private long totExecutionDuration = 0;

	public PerformanceEvent(OperationType op) {

		opType = op;
	}

	public void addLogEvent(LogEvent logEvent) {

		if (logEvent.getOperationType().equals(this.opType)) {
			this.numberOfOperation++;
			long executionDuration = logEvent.getDuration();
			if (executionDuration < minExecutionDuration) {
				this.minExecutionDuration = executionDuration;
			}
			if (executionDuration > maxExecutionDuration) {
				this.maxExecutionDuration = executionDuration;
			}
			this.totExecutionDuration = totExecutionDuration + executionDuration;
			this.meanExecutionDuration = totExecutionDuration / numberOfOperation;
		}
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(opType.toString());
		sb.append(" [ ");
		sb.append("#" + this.numberOfOperation);
		sb.append(", ");
		sb.append(" min:" + this.minExecutionDuration);
		sb.append(", ");
		sb.append(" Max:" + this.maxExecutionDuration);
		sb.append(", ");
		sb.append(" Mean:" + this.meanExecutionDuration);
		sb.append(", ");
		sb.append(" TOT:" + this.totExecutionDuration);
		sb.append(" ]");
		return sb.toString();
	}
}

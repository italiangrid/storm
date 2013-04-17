package it.grid.storm.space;

import java.util.Date;

public class DUResult implements Cloneable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {

		return new DUResult(this.getSize(), this.absRootPath, this.startTime,
			this.durationTime, this.cmdResult);
	}

	private long size;
	private String absRootPath;
	private long durationTime;
	private Date startTime;
	private ExitCode cmdResult;

	/**
	 * @param size
	 * @param absRootPath
	 * @param startTime
	 * @param durationTime
	 * @param cmdResult
	 */
	public DUResult(long size, String absRootPath, Date startTime,
		long durationTime, ExitCode cmdResult) {

		super();
		this.size = size;
		this.absRootPath = absRootPath;
		this.durationTime = durationTime;
		this.startTime = startTime;
		this.cmdResult = cmdResult;
	}

	/**
	 * @return the size
	 */
	public final long getSize() {

		return size;
	}

	/**
	 * @return the absRootPath
	 */
	public final String getAbsRootPath() {

		return absRootPath;
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
	public final Date getStartTime() {

		return startTime;
	}

	public boolean isSuccess() {

		return (this.cmdResult.equals(ExitCode.SUCCESS));
	}

	public boolean isPoisoned() {

		return (this.cmdResult.equals(ExitCode.POISON_PILL));
	}

	/**
	 * @return the cmdResult
	 */
	public final ExitCode getCmdResult() {

		return cmdResult;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("DUResult [size=");
		builder.append(size);
		builder.append(", absRootPath=");
		builder.append(absRootPath);
		builder.append(", durationTime=");
		builder.append(durationTime);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", cmdResult=");
		builder.append(cmdResult);
		builder.append("]");
		return builder.toString();
	}

}

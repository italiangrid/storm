package it.grid.storm.space;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallableDU implements Callable<DUResult> {

	private static final Logger LOG = LoggerFactory.getLogger(CallableDU.class);
	private String rootPath;
	private long timeout;
	private Date creationTime;
	private Date startTime;
	private Date endTime;
	private ExitCode cmdResult;
	private static final String POISON_PILL = "poison-pill";

	public CallableDU(String rootPath, long timeOutInSeconds) {

		super();
		this.rootPath = rootPath;
		this.timeout = timeOutInSeconds * 1000;
		this.creationTime = new Date(System.currentTimeMillis());
	}

	public static CallableDU getPoisonPill() {

		return new CallableDU(POISON_PILL, 1000);
	}

	private boolean isWindows() {

		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);
	}

	private String getDULocalCommand() {

		String result = "du"; // default is Linux
		if (isWindows()) {
			result = "C:\\cygwin\\bin\\du.exe";
			File cf = new File(result);
			if (!(cf.exists())) {
				LOG.error("Unable to find DU command.");
				result = null;
			}
		}
		return result;
	}

	/**
	 * @return the rootPath
	 */
	public final String getRootPath() {

		return rootPath;
	}

	/**
	 * @return the creationTime
	 */
	public final Date getCreationTime() {

		return creationTime;
	}

	/**
	 * @return the startTime
	 */
	public final Date getStartTime() {

		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public final Date getEndTime() {

		return endTime;
	}

	/**
	 * @return the cmdResult
	 */
	public final ExitCode getCmdResult() {

		return cmdResult;
	}

	public DUResult call() throws Exception {

		long size = -1; // Undefined
		long startT = System.currentTimeMillis();
		this.startTime = new Date(startT);
		String command = getDULocalCommand();
		DUResult result;

		if (this.getRootPath().equals(POISON_PILL)) {
			result = new DUResult(0, POISON_PILL, startTime, 0, ExitCode.POISON_PILL);
			return result;
		}

		List<String> commandList = new ArrayList<String>();
		commandList.add(command);
		commandList.add("-s");
		commandList.add("-b");
		commandList.add(this.rootPath);
		ExecCommand ec = new ExecCommand(commandList, this.timeout);

		cmdResult = ExitCode.getExitCode(ec.runCommand());
		LOG.debug("Command result: {}" , cmdResult);
		String output = ec.getOutput();
		LOG.debug(" Output: '{}'" , output);
		if (output != null) {
			String[] outputArray = output.split("\\s");
			for (int i = 0; i < outputArray.length; i++) {
				LOG.trace("outputArray[{}]={}" , i , outputArray[i]);
			}
			try {
				size = Long.valueOf(outputArray[0]).longValue();
			} catch (NumberFormatException nfe) {
				LOG.error("Unable to retrieve the disk usage of '{}'. {}" , this.rootPath
					, nfe.getMessage(),nfe);
			}
		}

		// Checking special case IO_ERROR due to a "du: cannot access ..."
		if (cmdResult.equals(ExitCode.IO_ERROR)) {
			// Size is present?
			if (size > 0) {
				cmdResult = ExitCode.PARTIAL_SUCCESS;
				LOG.info("IO Error occurred 'du: cannot access '{}': No such file or ..' but SUCCESSFully managed." , this.rootPath);
			}
		}

		// Checking special case SUCCESS, but unable to retrieve output of DU
		if (cmdResult.equals(ExitCode.SUCCESS)) {
			// Size is yet undefined?
			if (size < 0) {
				cmdResult = ExitCode.IO_ERROR;
				LOG.warn("DU of {} successfully ended, but an IO_ERROR occurred retrieving command output." , this.rootPath);
			}
		}

		long endT = System.currentTimeMillis();
		long durationT = endT - startT;
		this.endTime = new Date(endT);

		result = new DUResult(size, rootPath, startTime, durationT, cmdResult);
		ec.stopExecution();
		return result;
	}

}

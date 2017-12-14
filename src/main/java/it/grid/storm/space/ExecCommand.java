package it.grid.storm.space;

import it.grid.storm.concurrency.NamedThreadFactory;
import it.grid.storm.concurrency.TimingThreadPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecCommand {

	private static final Logger LOG = LoggerFactory.getLogger(ExecCommand.class);

	private static final long DEFAULT_TIMEOUT = 10; // 10 sec as default Timeout;

	private long timeout = 0;
	private final List<String> command;

	private Future<String> outputFuture;
	private Future<String> errorFuture;
	ExecutorService executorService = null;

	/**
	 * 
	 * @author ritz
	 * 
	 */
	private class StreamReader implements Callable<String> {

		private InputStream is;

		public StreamReader(InputStream inputStream) {

			is = inputStream;
		}

		public String call() {

			String output = "";
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			try {
				StringBuilder readBuffer = new StringBuilder();
				String buff;
				String eol = System.getProperty("line.separator");
				while ((buff = br.readLine()) != null) {
					readBuffer.append(buff).append(eol);
					LOG.debug(" - output-reader: {}" , buff);
				}

				output = readBuffer.toString();

				IOUtils.closeQuietly(br);
				IOUtils.closeQuietly(isr);

			} catch (IOException e) {
				LOG
					.warn("IO Exception occours when retrieve output from the execution on a native command. {}"
						, e.getMessage());
			} finally {
				IOUtils.closeQuietly(br);
				IOUtils.closeQuietly(isr);
			}
			return output;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param command
	 * @param timeout
	 */
	public ExecCommand(final List<String> commandInformation, final long timeout) {

		if (commandInformation == null)
			throw new NullPointerException("The commandInformation is required.");
		this.command = commandInformation;
		this.timeout = timeout;
	}

	/**
	 * Constructor
	 * 
	 * @param command
	 */
	public ExecCommand(final List<String> commandInformation) {

		if (commandInformation == null)
			throw new NullPointerException("The commandInformation is required.");
		this.command = commandInformation;
		this.timeout = -1;
	}

	public String getCommand() {

		String result = "";
		for (String cmdElement : this.command) {
			result += cmdElement + " ";
		}
		return result;
	}

	/**
	 * 
	 */
	public int runCommand() {

		int result = 0; // zero means success
		try {
			ProcessBuilder pb = new ProcessBuilder(this.command);
			Process process = pb.start();
			LOG.debug("Running command: '{}'" , command);

			int corePoolSize = 3;
			int maxPoolSize = 3;
			int keepAliveTime = 10;
			BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();

			executorService = new TimingThreadPool(corePoolSize, maxPoolSize,
				keepAliveTime, TimeUnit.SECONDS, workQueue, new NamedThreadFactory(
					"ExecCommand"));

			outputFuture = executorService.submit(new StreamReader(process
				.getInputStream()));
			errorFuture = executorService.submit(new StreamReader(process
				.getErrorStream()));

			final Process finalProcess = process;
			Callable<Integer> call = new Callable<Integer>() {

				public Integer call() throws Exception {

					finalProcess.waitFor();
					return finalProcess.exitValue();
				}
			};

			Future<Integer> ft = executorService.submit(call);
			LOG.debug("START WaitFor the result of Native Command.");
			if (timeout > 0) {
				result = ft.get(timeout, TimeUnit.SECONDS);
			} else {
				result = ft.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
			}
			LOG.debug("END WaitFor the result of Native Command.");

		} catch (IOException e) {
			LOG.warn("IO Exception occours during the execution of a native command {}. {}"
				, command , e.getMessage());
			result = 1;
		} catch (InterruptedException e) {
			LOG
				.warn("Interrupted Exception occours during the execution of a native command {}. {}"
					, command , e.getMessage());
			result = 2;
		} catch (TimeoutException e) {
			LOG.warn("Native command {}  was in TimeOut." , command);
			result = 3;
		} catch (NullPointerException npe) {
			LOG.error("The command to execute is NULL!" , npe);
			result = 4;
		} catch (SecurityException se) {
			LOG.error("The program have Security limitation to execute the command '{}'"
				, command , se);
			result = 5;
		} catch (IllegalArgumentException iae) {
			LOG.error("The command to execute is EMPTY or ILLEGAL {}" , command
				, iae);
			result = 6;
		} catch (ExecutionException e) {
			LOG.error("Execution Exception during the execution of {}" , command
			 , e);
			result = 7;
		}
		return result;
	}

	public String getOutput() {

		String outputResult = "";
		try {
			LOG.debug("Get Output message ... ");
			if (outputFuture != null) {
				if (timeout > 0) {
					outputResult = outputFuture.get(timeout, TimeUnit.SECONDS);
				} else {
					outputResult = outputFuture.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
				}
				LOG.debug(" .. :{}" , outputResult);
			} else {
				LOG.warn("Unable to get outputResult value. outputResult is NULL");
			}
		} catch (ExecutionException e) {
			LOG
				.warn("ExecutionException occours when retrieving OUTPUT stream returned by native command: {}"
					, e.getMessage());
		} catch (TimeoutException e) {
			LOG
				.warn("Timeout occours when retrieving OUTPUT stream returned by native command: {}"
					, e.getMessage());
		} catch (InterruptedException e) {
			LOG
				.warn("Interrupt occours when retrieving OUTPUT stream returned by native command: {}"
					, e.getMessage());
		}
		return outputResult;
	}

	public String getError() {

		String errorResult = "";
		try {
			LOG.debug("Get Error Message .. ");
			if (timeout > 0) {
				errorResult = errorFuture.get(timeout, TimeUnit.SECONDS);
			} else {
				errorResult = outputFuture.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
			}
			LOG.debug(" .. :{}" , errorResult);

		} catch (ExecutionException e) {
			LOG
				.warn("ExecutionException occours when retrieving error stream returned by native command: {}"
					, e.getMessage());
		} catch (TimeoutException e) {
			LOG
				.warn("Timeout occours when retrieving error stream returned by native command: {}"
					, e.getMessage());
		} catch (InterruptedException e) {
			LOG
				.warn("Interrupt occours when retrieving error stream returned by native command: {}"
					, e.getMessage());
		}
		return errorResult;
	}

	/**
     * 
     */
	public void stopExecution() {

		long millisecToWait = 1000;
		if (executorService != null) {
			try {
				LOG.debug("Shutting down..");
				executorService.awaitTermination(millisecToWait, TimeUnit.MILLISECONDS);
				LOG.debug("Shutted down!");
			} catch (InterruptedException ie) {
				LOG.debug("Interrupted excep." , ie);
			} finally {
				LOG.debug("Tasks killed: {}" , executorService.shutdownNow().size());
			}
		}
	}

}

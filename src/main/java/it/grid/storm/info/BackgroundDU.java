package it.grid.storm.info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.concurrency.NamedThreadFactory;
import it.grid.storm.concurrency.TimingThreadPool;
import it.grid.storm.space.CallableDU;
import it.grid.storm.space.ExitCode;
import it.grid.storm.space.DUResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BackgroundDU {

	private static final Logger log= LoggerFactory.getLogger(BackgroundDU.class);

	private List<CallableDU> todoTasks = new ArrayList<CallableDU>();

	// Futures for all submitted Callables that have not yet been checked
	private Set<Future<DUResult>> toCheckTasks = new HashSet<Future<DUResult>>();
	private Set<Future<DUResult>> completedTasks = new HashSet<Future<DUResult>>();
	private Set<DUResult> successResults = new HashSet<DUResult>();
	private Set<DUResult> failureResults = new HashSet<DUResult>();

	// Timeout value used in CallableDU
	private long timeoutInSec = 3600; // 60 min

	// Executor Service used within CompletionService
	private int poolSize = 2;
	private int maxPoolSize = 5;
	private long keepAliveTime = 10;
	private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
	private final ExecutorService exec = new TimingThreadPool(poolSize,
		maxPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue,
		new NamedThreadFactory("DUExecutors"));

	/**
	 * CompletionService for running submitted tasks. All tasks are submitted
	 * through this CompletionService to provide blocking, queued access to
	 * completion information.
	 */
	private final CompletionService<DUResult> completionService = new ExecutorCompletionService<DUResult>(
		exec);

	private ExecutorService singleExec = Executors.newFixedThreadPool(1,
		new NamedThreadFactory("CompletionTask"));
	private Future<?> completionTaskHandle = null;

	public BackgroundDU(long timeOutDuration, TimeUnit timeUnit) {

		this.timeoutInSec = timeUnit.toSeconds(timeOutDuration);
	}

	/**
	 * @param saRoot
	 */
	public void addStorageArea(String saRoot, int taskId) {

		CallableDU task = new CallableDU(saRoot, timeoutInSec);
		todoTasks.add(task);
	}

	public int howManyTaskToComplete() {

		return toCheckTasks.size();
	}

	public int howManyTaskSuccess() {

		return successResults.size();
	}

	public int howManyTaskFailure() {

		return failureResults.size();
	}

	public Set<DUResult> getSuccessTasks() {

		return successResults;
	}

	public Set<DUResult> getFailureTasks() {

		return failureResults;
	}

	/**
     * 
     */
	public void startExecution() {

		for (Callable<DUResult> tasks : todoTasks) {
			toCheckTasks.add(completionService.submit(tasks));
		}
		// Clean todo Task list;
		todoTasks.clear();
		completionTaskHandle = singleExec.submit(new CompletionTask());
	}

	public void poisonCompletionService() {

		completionService.submit(CallableDU.getPoisonPill());
	}

	/**
     * 
     */
	public void stopExecution(boolean sweetDead) {

		// Clean todo Task list;
		todoTasks.clear();
		long millisecToWait = 1000;
		if (sweetDead) {
			poisonCompletionService();
			millisecToWait = 10000;
		}
		try {
			completionTaskHandle.get(millisecToWait, TimeUnit.MILLISECONDS);
		} catch (Throwable e){
		  log.debug(e.getMessage(),e);
		} finally {
			completionTaskHandle.cancel(true);
			log.debug("Completion task forced to be canceled.");
		}
		try {
			log.debug("Shutting down..");
			shutdown(false, 1000);
			log.debug("Shutted down!");
		} catch (InterruptedException ie) {
			log.debug("Interrupted excep. " + ie);
		}
	}

	/**
	 * @param interrupt
	 *          <tt>true</tt> if the threads executing tasks task should be
	 *          interrupted; otherwise, in-progress tasks are allowed to complete
	 *          normally.
	 * @param waitMillis
	 *          maximum amount of time to wait for tasks to complete.
	 * @return <tt>true</tt> if this all the running tasks terminated and
	 *         <tt>false</tt> if the some running task did not terminate.
	 * @throws InterruptedException
	 *           if interrupted while waiting.
	 */
	private void shutdown(boolean interrupt, long waitMillis)
		throws InterruptedException {

		if (interrupt) {
			log.debug("Tasks killed: {}", exec.shutdownNow().size());
			log.debug("Tasks killed: {}",singleExec.shutdownNow().size());
		} else {
			exec.shutdown();
			singleExec.shutdown();
		}
		try {
			exec.awaitTermination(waitMillis, TimeUnit.MILLISECONDS);
			singleExec.awaitTermination(waitMillis, TimeUnit.MILLISECONDS);
		} finally {
			log.debug("Tasks killed: {}", exec.shutdownNow().size());
			log.debug("Tasks killed: {}", singleExec.shutdownNow().size());
		}
		log.debug("EXEC is terminated? : {}" , exec.isTerminated());
		log.debug("SingleEXEC is terminated? : {}" , singleExec.isTerminated());
	}

	/**
	 * @param duResult
	 */
	private void processCompletedTask(DUResult duResult) {

	  log.trace("BackgroundDU.processCompletedTask. duResult={}", duResult);

		log.info(duResult.toString());
		if (duResult.getCmdResult().equals(ExitCode.SUCCESS)) {
			successResults.add(duResult);
		} else {
			failureResults.add(duResult);
		}
		SpaceInfoManager.getInstance().updateSA(duResult);
	}

	/**
	 * Inner class implementing the checking and taking of completed task. Every
	 * task completed are then passed to the processCompletedTask.
	 */
	private class CompletionTask implements Runnable {

		private boolean completeTask() throws InterruptedException {

		  log.trace("BackgroundDU.CompletionTask.completeTask");
			boolean poison = false;
			Future<DUResult> completedTask;
			DUResult duResult = null;
			try {
				// block until a callable completes
				completedTask = completionService.take();
				// Add the processed task.
				completedTasks.add(completedTask);
				log.debug("Completed Tasks: {}", completedTasks.size());
				// Wait 1 second to check if there is a result
				duResult = completedTask.get(1, TimeUnit.SECONDS);
				log.debug("Completed Task : {}", duResult.toString());
				if (duResult.isPoisoned()) {
					log.debug("POISONED the DU Completion Service!!!");
					poison = true;
				} else {
					toCheckTasks.remove(completedTask);
				}
			} catch (ExecutionException e) {
				Throwable cause = e.getCause();
				if (!(cause instanceof InterruptedException)) {
					log.error("Completion Task failed with unhandled exception", cause);
				}
			} catch (TimeoutException e) {
				log.info("Completion Task terminated due to a TimeOut. Cause: {}"
					, e.getCause().getMessage());
			} catch (CancellationException e) {
				log.info("Completion Task was cancelled. Cause: {}"
					, e.getCause().getMessage());
			}

			if (duResult != null) {
				// a DUResult was created, store the results.
				processCompletedTask(duResult);
			} else {
				log
					.warn("DU completed but unable to manage the result (something wrong was happen).");
			}
			return poison;
		}

		public void run() {

			try {
				boolean poison_pill = false;
				while ((!Thread.currentThread().isInterrupted()) && (!(poison_pill))) {
					poison_pill = completeTask();
				}
			} catch (InterruptedException ie) {
				log.info("CompletionTask has been terminated by an interruption. ");
				Thread.currentThread().interrupt();
			}
		}
	}

}

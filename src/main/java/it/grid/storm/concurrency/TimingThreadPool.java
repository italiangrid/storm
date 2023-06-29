/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimingThreadPool extends ThreadPoolExecutor {

	public TimingThreadPool(int corePoolSize, int maximumPoolSize,
		long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
		ThreadFactory threadFactory) {

		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
			threadFactory);
	}

	private final ThreadLocal<Long> startTime = new ThreadLocal<>();
	private static final Logger log = LoggerFactory
		.getLogger(TimingThreadPool.class);
	private final AtomicLong numTasks = new AtomicLong();
	private final AtomicLong totalTime = new AtomicLong();

	@Override
	protected void beforeExecute(Thread t, Runnable r) {

		super.beforeExecute(t, r);
		log.debug("Thread {}: start {}", t, r);
		startTime.set(System.nanoTime());
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {

		try {
			long endTime = System.nanoTime();
			long taskTime = endTime - startTime.get();
			startTime.remove();
			numTasks.incrementAndGet();
			totalTime.addAndGet(taskTime);
			if (t == null && r instanceof Future<?>) {
				try {
					Object result = ((Future<?>) r).get();
					log.debug("Thread ended with result: {}", result);
				} catch (CancellationException ce) {
					t = ce;
				} catch (ExecutionException ee) {
					t = ee.getCause();
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt(); // ignore/reset
				}
				log.debug("Throwable {}. end {}, time={}ns",
				  t,r,taskTime);
			} else {
			  log.debug("Throwable {}", t);

			}
		} finally {
			super.afterExecute(r, t);
		}
	}
}

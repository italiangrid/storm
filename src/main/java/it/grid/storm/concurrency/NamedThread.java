package it.grid.storm.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamedThread extends Thread {

	public static final String DEFAULT_NAME = "StoRM-Thread";
	public static volatile boolean traceLevel = false;
	private static final AtomicInteger created = new AtomicInteger();
	private static final AtomicInteger alive = new AtomicInteger();
	private static final Logger log = LoggerFactory.getLogger(NamedThread.class);

	/**
	 * @param target
	 */
	public NamedThread(Runnable target) {

		this(target, DEFAULT_NAME);
	}

	/**
	 * @param target
	 * @param name
	 */
	public NamedThread(Runnable target, String name) {

		super(target, name + "-" + created.incrementAndGet());
		boolean debug = traceLevel;
		if (debug)
			log.trace("Created " + getName());
		setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			public void uncaughtException(Thread t, Throwable e) {

				log.error("UNCAUGHT in thread " + t.getName(), e);
			}
		});

	}

	public void run() {

		boolean debug = traceLevel;
		if (debug)
			log.debug("Running " + getName());
		try {
			alive.incrementAndGet();
			super.run();
		} finally {
			alive.decrementAndGet();
			if (debug)
				log.debug("Exiting " + getName());
		}
	}

	public static int getThreadsCreated() {

		return created.get();
	}

	public static int getThreadAlive() {

		return alive.get();
	}

	public static boolean getDebugStatus() {

		return traceLevel;
	}

	public static void setTrace(boolean debug) {

		traceLevel = debug;
	}

}

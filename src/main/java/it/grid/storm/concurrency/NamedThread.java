package it.grid.storm.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamedThread extends Thread {

	public static final String DEFAULT_NAME = "StoRM-Thread";
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

		log.trace("Created  thread {}", getName());
		
		setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			public void uncaughtException(Thread t, Throwable e) {

				log.error("UNCAUGHT in thread {}", t.getName(), e);
			}
		});

	}

	public void run() {

	  log.trace("NamedThread.run name={}", getName());

		try {
			alive.incrementAndGet();
			super.run();
		} finally {
			alive.decrementAndGet();
			log.trace("NamedThread.run name={} done.", getName());
		}
	}

	public static int getThreadsCreated() {

		return created.get();
	}

	public static int getThreadAlive() {

		return alive.get();
	}

}

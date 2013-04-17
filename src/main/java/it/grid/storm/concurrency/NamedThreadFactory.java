package it.grid.storm.concurrency;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

	private final String factoryName;

	public NamedThreadFactory(String name) {

		this.factoryName = name;
	}

	public Thread newThread(Runnable r) {

		return new NamedThread(r, factoryName);
	}

}

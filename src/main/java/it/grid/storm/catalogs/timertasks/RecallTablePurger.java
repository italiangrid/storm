package it.grid.storm.catalogs.timertasks;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.config.Configuration;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;


public class RecallTablePurger extends TimerTask {

	private static final Logger log = LoggerFactory
		.getLogger(RecallTablePurger.class);
	
	private final Configuration config = Configuration.getInstance();
	private Timer handler;
	
	public RecallTablePurger(Timer handlerTimer) {
		
		handler = handlerTimer;
	}
	
	
	@Override
	public void run() {

		int n = new TapeRecallCatalog().purgeCatalog(Configuration.getInstance()
			.getPurgeBatchSize());
		
		if (n == 0) {
			log.trace("No entries have been purged from tape_recall table");
		} else {
			log.info("{} entries have been purged from tape_recall table", n);
		}
		
		handler.schedule(new RecallTablePurger(handler), config.getTransitTimeInterval() * 1000);

	}

}

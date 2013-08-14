package it.grid.storm;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StoRMDefaultUncaughtExceptionHandler implements
	UncaughtExceptionHandler {

	public static final Logger log = LoggerFactory
		.getLogger(StoRMDefaultUncaughtExceptionHandler.class);

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		
		String errorMessage = String.format(
			"Thread (%d - '%s') uncaught exception: %s at line %d (%s)\n",
			t.getId(), t.getName(), e.toString(), e.getStackTrace()[0]
					.getLineNumber(), e.getStackTrace()[0].getFileName());

		log.error(errorMessage, e);

	}

}

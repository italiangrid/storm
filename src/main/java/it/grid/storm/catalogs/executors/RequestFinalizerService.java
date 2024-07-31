package it.grid.storm.catalogs.executors;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import it.grid.storm.catalogs.executors.threads.BoLFinalizer;
import it.grid.storm.catalogs.executors.threads.PtGFinalizer;
import it.grid.storm.catalogs.executors.threads.PtPFinalizer;
import it.grid.storm.config.StormConfiguration;

public class RequestFinalizerService {

  private final long delay;
  private final long period;

  private ScheduledExecutorService executor;
  private PtPFinalizer ptpTask;
  private BoLFinalizer bolTask;
  private PtGFinalizer ptgTask;

  public RequestFinalizerService(StormConfiguration config) {

    delay = config.getTransitInitialDelay() * 1000L;
    period = config.getTransitTimeInterval() * 1000L;
    executor = Executors.newScheduledThreadPool(3);
    ptpTask = new PtPFinalizer(config.getInProgressPutRequestExpirationTime());
    bolTask = new BoLFinalizer(config.getInProgressBolRequestExpirationTime());
    ptgTask = new PtGFinalizer();

  }

  public void start() {

    executor.scheduleAtFixedRate(ptpTask, delay, period, SECONDS);
    executor.scheduleAtFixedRate(bolTask, delay, period, SECONDS);
    executor.scheduleAtFixedRate(ptgTask, delay, period, SECONDS);

  }

  public void stop() {

    executor.shutdown();
  }
}

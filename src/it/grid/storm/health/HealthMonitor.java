package it.grid.storm.health;

import org.apache.log4j.Logger;
import java.util.Timer;

public class HealthMonitor {

  private Logger log;
  private static Timer healtTimer = null;
  private static BookKeeper bookKeeper;


  public HealthMonitor(int delay, int period) {
    log = HealthDirector.getHealthLogger();
    healtTimer = new Timer();
    //Set monitoring the HEARTHBEAT
    this.heartbeat(delay*1000,period*1000);

    //Create the Book Keeper
    bookKeeper = new SimpleBookKeeper();

    //Other vital parameter to log
    // ...

    log.info("HEART MONITOR Initialized");
  }

  /**
   *
   * @return BookKeeper
   */
  public BookKeeper getBookKeeper() {
    return HealthMonitor.bookKeeper;
  }

  /**
   * heartbeat
   *
   */
  public void heartbeat(int delay, int period) {
    healtTimer.scheduleAtFixedRate(new Hearthbeat(), delay, period );
    log.info("Set HEARTHBEAT in Timer Task (DELAY:"+delay+" PERIOD:"+period+")");
  }
}

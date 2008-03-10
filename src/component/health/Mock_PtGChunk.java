package component.health;

import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.health.HealthDirector;
import org.apache.log4j.Logger;
import it.grid.storm.scheduler.Streets;

public class Mock_PtGChunk implements Delegable, Chooser {

  private static Logger log = HealthDirector.getHealthLogger();

  private String name;
  private long duration;

  public Mock_PtGChunk(String name, long duration)
  {
    this.name = name;
    this.duration = duration;
  }

  /**
   * doIt
   *
   */
  public void doIt() {
    try {
      Thread.sleep(this.duration);
    }
    catch (InterruptedException ex) {
       ex.printStackTrace();
    }
  }


  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return this.name;
  }


  public void choose(Streets s) {
    s.ptgStreet(this);
  }

}

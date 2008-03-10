package component.scheduler;


import it.grid.storm.scheduler.Delegable;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.scheduler.Streets;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class Mock_COPYChunk implements Delegable, Chooser {

  private static Log log = LogFactory.getLog(TestMemoryProfiler.class);
  private String name;
  private long duration;

  public Mock_COPYChunk(String name, long duration)
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

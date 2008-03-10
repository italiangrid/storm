package component.scheduler;

import java.util.TimerTask;
import it.grid.storm.scheduler.Scheduler;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import it.grid.storm.scheduler.SchedulerStatus;
/**
 *
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
public class PeeperTask extends TimerTask {

  private Scheduler sched;
  private int schedulerType;
  private static Log log = LogFactory.getLog(TestMemoryProfiler.class);
  private SchedulerStatus status;


  public PeeperTask(Scheduler sched, int schedtype) {
    this.sched = sched;
    this.schedulerType = schedtype;
  }

  private long heapSize(){
    // Get current size of heap in bytes
     long heapSize = Runtime.getRuntime().totalMemory();
     return heapSize;
  }

  private long heapMaxSize(){
     // Get maximum size of heap in bytes. The heap cannot grow beyond this size.
     // Any attempt will result in an OutOfMemoryException.
     long heapMaxSize = Runtime.getRuntime().maxMemory();
     return heapMaxSize;
  }

  private long heapFreeSize(){
     // Get amount of free memory within the heap in bytes. This size will increase
     // after garbage collection and decrease as new objects are created.
     long heapFreeSize = Runtime.getRuntime().freeMemory();
     return heapFreeSize;
  }


  public void run() {
    log.debug(" The glange of peeper..");
    status = sched.getStatus(schedulerType);
    int activecount = status.getActiveCount();
    long completed = status.getCompletedTaskCount();
    long taskcount = status.getTaskCount();
    log.debug("Taskcount:"+taskcount+" | ActiveCount:"+activecount+" | Completed:"+completed);
    log.debug("Heap size:"+heapSize()+" | Heap MAX size:"+heapMaxSize()+" | Heap Free Size:"+heapFreeSize());
  }

  }

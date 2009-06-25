package component.scheduler;

import it.grid.storm.scheduler.Scheduler;
import it.grid.storm.scheduler.SchedulerStatus;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
    private static Logger log = LoggerFactory.getLogger(PeeperTask.class);
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


    @Override
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

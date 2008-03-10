package component.scheduler;


import it.grid.storm.scheduler.Scheduler;
import it.grid.storm.asynch.SchedulerFactory;
import it.grid.storm.asynch.SchedulerFacade;
import org.apache.commons.logging.LogFactory;
import component.namespace.config.AdHocTest;
import org.apache.commons.logging.Log;
import it.grid.storm.scheduler.SchedulerStatus;
import it.grid.storm.scheduler.*;
import org.apache.commons.logging.impl.Jdk14Logger;
import java.io.File;
import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.logging.impl.Log4JLogger;
import java.util.Timer;


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
public class TestMemoryProfiler {

  private Scheduler crusherSched = null;
  private SchedulerStatus ptgStatus = null;
  private SchedulerStatus ptpStatus = null;
  private SchedulerStatus copyStatus = null;
  private Timer timer = null;
  int statusDelay = 1000; // delay for 5 sec.
  int statusPeriod = 1000; // repeat every sec.

  private static Log log = LogFactory.getLog(TestMemoryProfiler.class);

  public static void main(String[] args) throws InterruptedException {
    TestMemoryProfiler testmemoryprofiler = new TestMemoryProfiler();
    testmemoryprofiler.init();
    //testmemoryprofiler.logStatus(Scheduler.PtG_WorkerPoolType);
    testmemoryprofiler.test1();
    Thread.sleep(60*1000);
    testmemoryprofiler.test1();
  }


  private void test1() {
    Mock_PTGChunk ptg;
    for (int i = 0; i < 300; i++) {
      ptg = new Mock_PTGChunk("PTG_" + i, 3000);
      try {
        crusherSched.schedule(ptg);
        //Between one job and next one elapse 1 sec.
        try {
          Thread.sleep(100);
        }
        catch (InterruptedException ex1) {
           ex1.printStackTrace();
        }
      }
      catch (SchedulerException ex) {
        ex.printStackTrace();
        log.error(ex);
      }
    }
  }


  private void init() {
    boolean jdk14Logger = (log instanceof Jdk14Logger);
    boolean log4jlog = (log instanceof Log4JLogger);

    if (jdk14Logger) {
      System.out.println("Using Jdk14Logger = " + jdk14Logger);
    }
    if (log4jlog) {
      System.out.println("Using Log14Logger = " + log4jlog);
      String logConfigFile = System.getProperty("user.dir") + File.separator +
          "unittest" + File.separator + "log4j_for_testing.properties";
      System.out.println("config file = " + logConfigFile);
      PropertyConfigurator.configure(logConfigFile);
    }

    crusherSched = SchedulerFacade.getInstance().chunkScheduler();
    updateStatus();

    timer = new Timer();
    timer.scheduleAtFixedRate(new PeeperTask(crusherSched,Scheduler.PtG_WorkerPoolType),
                              statusDelay, statusPeriod );

  }


  private void updateStatus() {
    ptgStatus = crusherSched.getStatus(Scheduler.PtG_WorkerPoolType); //PtG worker pool
    ptpStatus = crusherSched.getStatus(Scheduler.PtP_WorkerPoolType); //PtG worker pool
    copyStatus = crusherSched.getStatus(Scheduler.Copy_WorkerPoolType); //PtG worker pool
  }


  private void logStatus(int type) {
    switch (type) {
      case 0: {
        log.debug("Active count in PTG = " + ptgStatus.getActiveCount());
        log.debug("Completed task count in PTG = " + ptgStatus.getCompletedTaskCount());
        log.debug("Task count in PTG = " + ptgStatus.getTaskCount());
      }
      case 1: {
        log.debug("Active count in PTP = " + ptpStatus.getActiveCount());
        log.debug("Completed task count in PTP = " + ptpStatus.getCompletedTaskCount());
        log.debug("Task count in PTP = " + ptpStatus.getTaskCount());
      }
      case 2: {
        log.debug("Active count in COPY = " + copyStatus.getActiveCount());
        log.debug("Completed task count in COPY = " + copyStatus.getCompletedTaskCount());
        log.debug("Task count in COPY = " + copyStatus.getTaskCount());
      }
    }
  }

  private void logAllStatus() {
     logStatus(Scheduler.PtG_WorkerPoolType);
     logStatus(Scheduler.PtP_WorkerPoolType);
     logStatus(Scheduler.Copy_WorkerPoolType);
  }

  }

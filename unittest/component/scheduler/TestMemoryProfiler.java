package component.scheduler;


import it.grid.storm.scheduler.Scheduler;
import it.grid.storm.scheduler.SchedulerException;
import it.grid.storm.scheduler.SchedulerStatus;

import java.io.File;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;

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

    private static Logger log = LoggerFactory.getLogger(TestMemoryProfiler.class);

    private static void init() {
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);
    }


    public static void main(String[] args) throws InterruptedException {
        TestMemoryProfiler.init();
        TestMemoryProfiler testmemoryprofiler = new TestMemoryProfiler();
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
                log.error(ex.toString(), ex);
            }
        }
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

package component.health;


import it.grid.storm.health.HealthDirector;
import it.grid.storm.scheduler.Scheduler;
import it.grid.storm.scheduler.SchedulerException;

import org.slf4j.Logger;


public class HealthMonitoringTest {

    private static Logger log = HealthDirector.getHealthLogger();
    private Scheduler crusherSched = null;

    public HealthMonitoringTest() {
        super();
    }


    private void init() {
        HealthDirector.initializeDirector(true);
    }



    private void test1() {
        Mock_PtGChunk ptg;
        for (int i = 0; i < 300; i++) {
            ptg = new Mock_PtGChunk("PTG_" + i, 3000);
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
                log.error(ex.getMessage(),ex);
            }
        }
    }



    public static void main(String[] args) throws InterruptedException {
        HealthMonitoringTest testHealthMonitor = new HealthMonitoringTest();
        testHealthMonitor.init();
        testHealthMonitor.test1();
        Thread.sleep(60 * 1000);
    }


}

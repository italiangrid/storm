package it.grid.storm.info;

import it.grid.storm.space.DUResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class TestBackgroundDU {
    private static final Logger LOG = LoggerFactory.getLogger(TestBackgroundDU.class);
    private static BackgroundDU bDu = new BackgroundDU(60, TimeUnit.SECONDS);
    private static TestBackgroundDU tBDU = new TestBackgroundDU();

    public void initSA(List<String> paths) {

        int count = 0;
        for (String path : paths) {
            LOG.debug("Adding :'" + path + "'");
            bDu.addStorageArea(path, count);
            count++;
        }
    }

    public void startComputation() {
        LOG.debug("Starting Computation.. in background");
        bDu.startExecution();
        LOG.debug(".. computation started");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        LOG.debug("arg.length : " + args.length);
        if (args.length == 0) {
            List<String> paths = new ArrayList<String>();
            paths.add(System.getProperty("user.dir"));
            paths.add(System.getProperty("user.dir")+File.separator+"..");
            paths.add(System.getProperty("user.dir")+File.separator+".."+File.separator+"TreeTraversal");
            tBDU.initSA(paths);
        } else {
            List<String> paths = Arrays.asList(args);
            tBDU.initSA(paths);
        }
        LOG.debug("how many? :" + bDu.howManyTaskToComplete());
        tBDU.startComputation();
        int howTodos = bDu.howManyTaskToComplete();
        while (howTodos > 0) {
            howTodos = bDu.howManyTaskToComplete();
            LOG.debug("Task to complete: " + howTodos);
            LOG.debug("Task success    : " + bDu.howManyTaskSuccess());
            LOG.debug("Task failed     : " + bDu.howManyTaskFailure());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        LOG.debug("***********************************");
        LOG.debug("***********************************");
        LOG.debug("***********************************");

        bDu.stopExecution(false);
        Set<DUResult> results = bDu.getSuccessTasks();
        for (DUResult duResult : results) {
            LOG.debug("duResult : " + duResult);
        }
        Set<DUResult> failedTasks = bDu.getFailureTasks();
        for (DUResult duResult : failedTasks) {
            LOG.debug("failed duResult : " + duResult);
        }
        bDu = null;

    }

}

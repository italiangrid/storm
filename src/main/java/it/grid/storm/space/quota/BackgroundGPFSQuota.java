package it.grid.storm.space.quota;

import it.grid.storm.concurrency.NamedThreadFactory;
import it.grid.storm.concurrency.TimingThreadPool;
import it.grid.storm.config.Configuration;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class BackgroundGPFSQuota {

    private static final Logger LOG = LoggerFactory.getLogger(BackgroundGPFSQuota.class);
    
    private static BackgroundGPFSQuota instance = new BackgroundGPFSQuota();
    
    //Singleton
    private BackgroundGPFSQuota() {
        int waitRefreshPeriod = Configuration.getInstance().getGPFSQuotaRefreshPeriod();
        RefreshQuota refreshQuota = new RefreshQuota(waitRefreshPeriod);
        refreshQuota.start();

    }
    
    public static BackgroundGPFSQuota getInstance() {
        return instance;
    }
    
    private long lastExecutionTime = -1;
    private long creationTime = -1;
    
    private static final long RELAX_PERIOD = 15000; //between two GPFSQuota at least 15 seconds
    private static final long WAIT_TIMEOUT_DURATION = 10000; //The executor can wait until to 10 seconds  
    
    // Executor Service used within CompletionService
    private int poolSize = 2;
    private int maxPoolSize = 2;
    private long keepAliveTime = 10;
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
    
    private final ExecutorService exec = new TimingThreadPool(poolSize,
                                                              maxPoolSize,
                                                              keepAliveTime,
                                                              TimeUnit.SECONDS,
                                                              workQueue,
                                                              new NamedThreadFactory("GPFSQuotaExecutors"));    
    
    private final CompletionService<GPFSQuotaCommandResult> completionService = new ExecutorCompletionService<GPFSQuotaCommandResult>(exec);
    private ExecutorService singleExec = Executors.newFixedThreadPool(1, new NamedThreadFactory("CompletionTask"));

    
    
    /**
     * 
     * @return boolean: true if the submission is accepted. False otherwise.
     */
    public boolean submitGPFSQuota() {
        boolean result = false;
        long currTime = System.currentTimeMillis();
        
        if (((lastExecutionTime > 0 )) &&(currTime-lastExecutionTime)<RELAX_PERIOD) {
            //It is not the first execution && I'm in relax , then "Refuse submission"
            long remainngRelaxTime = RELAX_PERIOD - (currTime-lastExecutionTime);
            LOG.debug("GPFS Quota Background submission. Refused because relax period is not ended ("+remainngRelaxTime+" ms yet.)");
        } else {
            lastExecutionTime = currTime;
            LOG.debug("GPFS Quota Background submission.");
            execute();
            result = true;
        }
        return result;
    }
    
    
    /**
     * 
     */
    private void execute() {
        CallableGPFSQuota bgGPFSQuota = new CallableGPFSQuota();
        this.creationTime = bgGPFSQuota.getCreationTime();
        
        completionService.submit(bgGPFSQuota);
        singleExec.submit(new CompletionTask());
    }
    
    
    
    /**
     * @param quotaResult
     */
    private boolean processCompletedTask(GPFSQuotaCommandResult quotaResult) {
        boolean result = false;
        int nrFailires = QuotaManager.getInstance().updateSAwithQuotaResult(quotaResult);
 
        Date startDate = new Date(quotaResult.getStartTime());
        Date endDate = new Date(quotaResult.getStartTime() + quotaResult.getDurationTime());
        Date creationDate = new Date(creationTime);
        LOG.debug("GPFS Quota execution: Submitted ["+creationDate+"], Start ["+startDate+"], End["+endDate+"], wait.duration = "+(quotaResult.getStartTime()-creationTime)+",exec.duration = "+quotaResult.getDurationTime());
        
        result = (nrFailires!=0?false:true);
        if (!result) {
            LOG.debug("GPFS Quota execution is failed.");
        }
        return result;
    }

    
    
    /*
    * Inner class implementing the checking and taking of completed task. Every task completed are then passed to the
    * processCompletedTask.
    */
   private class CompletionTask implements Runnable {

       private boolean completeTask() throws InterruptedException {
           LOG.debug("Checking for complete a task..");
           boolean failure = false;
           GPFSQuotaCommandResult quotaResult = null;
           
           // block until a callable completes
           Future<GPFSQuotaCommandResult> completedTask = completionService.take();
           
           try {
               // Wait 1 second to check if there is a result
               quotaResult = completedTask.get(WAIT_TIMEOUT_DURATION, TimeUnit.MILLISECONDS);
               LOG.debug("Completed Task : " + quotaResult.toString());
               
           } catch (ExecutionException e) {
               Throwable cause = e.getCause();
               if (!(cause instanceof InterruptedException)) {
                   LOG.error("Completion Task failed with unhandled exception", cause);
               }
           } catch (TimeoutException e) {
               LOG.info("Completion Task terminated due to a TimeOut. Cause: " + e.getCause().getMessage());
           } catch (CancellationException e) {
               LOG.info("Completion Task was cancelled. Cause: " + e.getCause().getMessage());
           } finally {
               completedTask.cancel(true);
           }

           if (quotaResult != null) {
               // a DUResult was created, store the results.
               processCompletedTask(quotaResult);
           } else {
               LOG.warn("DU completed but unable to manage the result (something wrong was happen).");
               failure = true;
           }
           return failure;
       }

       public void run() {
           try {
               while (!Thread.currentThread().isInterrupted()) {
                   completeTask();
               }
           } catch (InterruptedException ie) {
               LOG.info("CompletionTask has been terminated by an interruption. ");
               Thread.currentThread().interrupt();
           }
       }
   }

   
   /**
    * 
    *
    */
   public class RefreshQuota {
       int delay = 20000;   // delay for 20 sec.
       int periodInSeconds;
       Timer timer;

       public RefreshQuota(int period) {
           timer = new Timer();
           periodInSeconds = period;
       }
       
       public void start() {
           timer.scheduleAtFixedRate(new RefreshTask(), delay, periodInSeconds*1000);
       }
       
       class RefreshTask extends TimerTask {
           public void run() {
               LOG.debug("Refresh Quota Timer task is executed");
               BackgroundGPFSQuota.getInstance().submitGPFSQuota();
           }
       }
       
   }

 


}



package it.grid.storm.space.quota;

import java.util.concurrent.Callable;


public class CallableGPFSQuota implements Callable<GPFSQuotaCommandResult> {

    private boolean test;
    private long timeout;
    private long creationTime;
 
    
    /**
     * @return the creationTime
     */
    public final long getCreationTime() {
        return creationTime;
    }


    private GPFSLsQuotaCommand quotaCmd;
    
    /**
     * @return the quotaCmd
     */
    public final GPFSLsQuotaCommand getQuotaCmd() {
        return quotaCmd;
    }

    public CallableGPFSQuota() {
        this.test = false;
        this.timeout = GPFSLsQuotaCommand.DEFAULT_TIMEOUT;
        this.creationTime = System.currentTimeMillis();
        this.quotaCmd = new GPFSLsQuotaCommand(this.timeout);
    }
    
    public CallableGPFSQuota(long timeout) {
        this.test = false;
        this.timeout = timeout;
        this.creationTime = System.currentTimeMillis();
        this.quotaCmd = new GPFSLsQuotaCommand(this.timeout);
    }

   public CallableGPFSQuota(long timeout, boolean test) {
        this.test = test;
        this.timeout = timeout;
        this.creationTime = System.currentTimeMillis();
        this.quotaCmd = new GPFSLsQuotaCommand(this.timeout);
    }

   public CallableGPFSQuota(boolean poisonPill) {
       this.test = false;
       this.creationTime = System.currentTimeMillis();
       this.quotaCmd = new GPFSLsQuotaCommand(this.timeout);
   }
   
   
	@Override
	public GPFSQuotaCommandResult call() throws Exception {

	    //This command take notes of starting time
	    GPFSQuotaCommandResult quotaCmdResult = quotaCmd.executeGetQuotaInfo(test);
		
		return quotaCmdResult;
	}


}

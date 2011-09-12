package it.grid.storm.space.quota;

import it.grid.storm.space.quota.GPFSQuotaCommand.ExitCode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallableGPFSQuota implements Callable<List<GPFSQuotaInfo>> {

	private static final Logger LOG = LoggerFactory.getLogger(CallableGPFSQuota.class);
    private boolean test;

    public CallableGPFSQuota(long timeout) {
        this.test = false;
        this.timeout = timeout;
    }

   public CallableGPFSQuota(long timeout, boolean test) {
        this.test = test;
        this.timeout = timeout;
    }

	private ExitCode cmdResult;
	private long timeout;

	@Override
	public List<GPFSQuotaInfo> call() throws Exception {

		long startT = System.currentTimeMillis();
		GPFSRepQuotaCommand quotaCmd = new GPFSRepQuotaCommand();
		
		ArrayList<GPFSQuotaInfo> result = quotaCmd.executeGetQuotaInfo(test);

		long endT = System.currentTimeMillis();
		long durationT = endT - startT;

		// result = new GPFSQuotaInfo(size, , startTime, durationT, cmdResult);
		
		LOG.info("Execution of '" + quotaCmd + "'. Duration: " + durationT);
		return result;
	}


	

    
    
    

   
    
}

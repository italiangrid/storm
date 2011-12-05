package component.initSA;

import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.QuotaType;
import it.grid.storm.space.quota.GPFSLsQuotaCommand;
import it.grid.storm.space.quota.GPFSQuotaCommandResult;
import it.grid.storm.space.quota.GPFSQuotaInfo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockGPFSQuotaResultTest {

    private static final Logger log = LoggerFactory.getLogger(MockGPFSQuotaResultTest.class);
    
    @Test
    public void testExecuteGetQuotaInfo() {
        GPFSLsQuotaCommand quotaCmd = new GPFSLsQuotaCommand();
        quotaCmd.executeGetQuotaInfo(true);
    }
    
    @Test
    public void testExecuteGetQuotaInfo2() {
        GPFSLsQuotaCommand quotaCmd = new GPFSLsQuotaCommand();
        Quota quota = new Quota();
        quota.setDevice("gemss_test");
        quota.setEnabled(true);
        QuotaType qt = QuotaType.FILESET;
        qt.setValue("data1");
        quota.setQuotaType(qt);
        GPFSQuotaCommandResult qResult = quotaCmd.executeGetQuotaInfo(quota, true);
        log.debug(qResult.toString());
        log.debug("How many quotas: "+qResult.getQuotaResults().size());
        GPFSQuotaInfo qInfo = qResult.getQuotaResults().get(0);
        if (qInfo!=null){
            log.debug("Space Used (KB): "+qInfo.getCurrentBlocksUsage());
        }
        
    }

}

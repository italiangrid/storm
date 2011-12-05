package it.grid.storm.space.quota;

import it.grid.storm.namespace.model.Quota;

import java.util.ArrayList;


public abstract class GPFSQuotaCommand {

    protected long timeout;
    
    
    public GPFSQuotaCommand(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Constructor without timeout. It'll use default timeout of 10 sec.
     */
    public GPFSQuotaCommand() {
        this.timeout = -1;
    }
    
    
    public abstract String getQuotaCommandString();
    
    public abstract GPFSQuotaCommandResult executeGetQuotaInfo(boolean test);
    
    public abstract GPFSQuotaCommandResult executeGetQuotaInfo(Quota quotaElement, boolean test) throws QuotaException;
    
    
}
